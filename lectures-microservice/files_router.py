import os
from datetime import datetime
from typing import Optional

from fastapi import File, UploadFile, HTTPException, APIRouter, status

import auth_pb2
from database import db_wrapper
from models import *
from validator import role_validator

router = APIRouter(prefix="/lectures/{lecture_id}", tags=["Files Controller"])


class Category(str, Enum):
    course = "course"
    lab = "lab"


def generate_file_hateoas_links(lecture_id: str, cat: Category, file_name: Optional[str] = None) -> Dict[str, Link]:
    base_link = f"/lectures/{lecture_id}/files"
    return {
        "upload": Link(href=base_link),
        "delete": Link(href=f"{base_link}/{file_name}") if file_name else None,
        "get_file": Link(href=f"{base_link}/{file_name}?category={cat}") if file_name else None
    }


def validate_id(lecture_id: int):
    if lecture_id < 1 or lecture_id > 999:
        raise HTTPException(status_code=status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE,
                            detail="Lecture ID must be between 1 and 999 digits")


@role_validator(role=auth_pb2.Role.PROFESSOR)
@router.get("/files", responses={
    status.HTTP_404_NOT_FOUND: {"description": "No files found for the given lecture and category"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Category must be 'course' or 'lab'"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=FileListResponse)
async def list_files(lecture_id: int, category: Category):
    validate_id(lecture_id)

    lecture_id = str(lecture_id)

    course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    files = db_wrapper.get_database().lectures.find_one(
        {"_id": lecture_id},
        {f"{category.value}-files": 1}
    )

    if not files or f"{category.value}-files" not in files:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail="No files found for the given lecture and category")

    file_list = []
    for file_metadata in files[f"{category.value}-files"]:
        file_name = file_metadata["file_name"]
        file_list.append(FileResponseSchema(
            file_metadata=FileMetadata(
                file_name=file_name,
                uploaded_at=file_metadata["uploaded_at"],
                size=file_metadata["size"]
            ),
            _links=generate_file_hateoas_links(lecture_id, category, file_name)
        ))

    # Construct response with _embedded and _links
    return FileListResponse(
        _embedded={
            "files": file_list
        },
        _links={
            "self": Link(href=f"/lectures/{lecture_id}/files?category={category.value}")
        }
    )

@role_validator(role=auth_pb2.Role.PROFESSOR)
@router.post("/files", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Course not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Category must be 'course' or 'lab'"},
    status.HTTP_200_OK: {"description": "File uploaded or updated successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=UploadFileResponseSchema)
async def upload_file(lecture_id: int, category: Category, file: UploadFile = File(...)):
    validate_id(lecture_id)

    lecture_id = str(lecture_id)

    course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    course_dir = os.path.join("files", category.value)
    os.makedirs(course_dir, exist_ok=True)

    file_path = os.path.join(course_dir, file.filename)
    with open(file_path, "wb") as f:
        content = await file.read()
        f.write(content)

    file_metadata = {
        "file_name": file.filename,
        "uploaded_at": datetime.utcnow().isoformat(),
        "size": len(content),
    }

    db_wrapper.get_database().lectures.update_one(
        {"_id": lecture_id},
        {"$push": {f"{category.value}-files": file_metadata}},
    )

    return UploadFileResponseSchema(
        message="File uploaded or updated successfully",
        file_metadata=FileMetadata(
            file_name=file.filename,
            uploaded_at=file_metadata["uploaded_at"],
            size=file_metadata["size"]
        ),
        _links=generate_file_hateoas_links(lecture_id, category, file.filename)
    )

@role_validator(role=auth_pb2.Role.PROFESSOR)
@router.get("/files/{file_name}", responses={
    status.HTTP_404_NOT_FOUND: {"description": "File metadata or file not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Category must be 'course' or 'lab'"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=FileResponseSchema)
async def get_file(lecture_id: int, category: Category, file_name: str):
    validate_id(lecture_id)

    lecture_id = str(lecture_id)

    file_metadata = db_wrapper.get_database().lectures.find_one(
        {"_id": lecture_id, f"{category.value}-files.file_name": file_name},
        {f"{category.value}-files.$": 1}
    )
    if not file_metadata:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File metadata not found in database")

    file_path = os.path.join("files", category.value, file_name)
    if not os.path.exists(file_path):
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File not found on disk")

    return FileResponseSchema(
        file_metadata=FileMetadata(
            file_name=file_name,
            uploaded_at=file_metadata.get("uploaded_at", ""),
            size=os.path.getsize(file_path)
        ),
        _links=generate_file_hateoas_links(lecture_id, category, file_name)
    )

@role_validator(role=auth_pb2.Role.PROFESSOR)
@router.delete("/files/{file_name}", responses={
    status.HTTP_200_OK: {"description": "File deleted successfully"},
    status.HTTP_404_NOT_FOUND: {"description": "File not found or not found in database"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=DeleteFileResponseSchema)
async def delete_file(lecture_id: int, category: Category, file_name: str):
    validate_id(lecture_id)

    lecture_id = str(lecture_id)

    file_path = os.path.join("files", category.value, file_name)
    if os.path.exists(file_path):
        os.remove(file_path)

    file_exist = db_wrapper.get_database().lectures.find_one(
        {"_id": lecture_id, f"{category.value}-files.file_name": file_name},
        {"_id": 1}
    )
    if not file_exist:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File not found")

    db_wrapper.get_database().lectures.update_one(
        {"_id": lecture_id},
        {"$pull": {f"{category.value}-files": {"file_name": file_name}}}
    )

    return DeleteFileResponseSchema(
        message=f"File '{file_name}' deleted successfully",
        _links=generate_file_hateoas_links(lecture_id, category)
    )
