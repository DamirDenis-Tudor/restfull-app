import os
import re
from datetime import datetime
from typing import Optional

from fastapi import File, UploadFile, HTTPException, APIRouter, status, Depends
from fastapi.responses import FileResponse

from config import lectures_ms_host_address
from database import db_wrapper
from proto import auth_pb2
from routers.models import *
from routers.validator import validate_professor_owner_of_lecture, roles_validator, validate_id, \
    validate_student_enrolled_in_lecture

router = APIRouter(prefix="/lectures/{lecture_id}", tags=["Files Controller"])


def generate_file_hateoas_links(lecture_id: str, cat: Category, file_name: Optional[str] = None) -> Dict[str, Link]:
    base_link = f"/lectures/{lecture_id}/files"
    return {
        "upload": Link(href=base_link),
        "delete": Link(href=f"{base_link}/{file_name}") if file_name else None,
        "get_file": Link(href=f"{base_link}/{file_name}?category={cat}") if file_name else None
    }


@router.get("/files", responses={
    status.HTTP_404_NOT_FOUND: {"description": "No files found for the given lecture and category"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Category must be 'course' or 'lab'"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=FileListResponse)
async def list_files(
        lecture_id: str = Depends(validate_id),
        _ = Depends(validate_student_enrolled_in_lecture([auth_pb2.PROFESSOR, auth_pb2.STUDENT]))):

    course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    file_list = []
    for category in Category.__members__.values():
        files = db_wrapper.get_database().lectures.find_one(
            {"_id": lecture_id},
            {f"{category.value}-files": 1}
        )

        if f"{category.value}-files" not in files:
            continue

        for file_metadata in files[f"{category.value}-files"]:
            file_name = file_metadata["file_name"]
            file_list.append(FileResponseSchema(
                file_metadata=FileMetadata(
                    file_name=file_name,
                    category=category.value,
                    uploaded_at=file_metadata["uploaded_at"],
                    size=file_metadata["size"]
                ),
                _links={
                    "download": Link(
                        href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/files/{file_name}?category={category.value}"
                    )
                }
        ))

    if len(file_list) == 0:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail="No files found for the given lecture")

    return FileListResponse(
        _embedded={
            "files": file_list
        },
        _links={
            "self": Link(href=f"/lectures/{lecture_id}/files"),
        }
    )


@router.post("/files", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Course not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Category must be 'course' or 'lab'"},
    status.HTTP_200_OK: {"description": "File uploaded or updated successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=UploadFileResponseSchema)
async def upload_file(
        category: Category,
        lecture_id: str = Depends(validate_id),
        file: UploadFile = File(...),
        _ = Depends(validate_professor_owner_of_lecture([auth_pb2.PROFESSOR]))):

    course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    course_dir = os.path.join("../files", category.value)
    os.makedirs(course_dir, exist_ok=True)

    file.filename = re.sub(r'\s+', '_', file.filename)

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


@router.get("/files/{file_name}", responses={
    status.HTTP_404_NOT_FOUND: {"description": "File metadata or file not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Category must be 'course' or 'lab'"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=FileResponseSchema)
async def get_file(
        category: Category,
        file_name: str,
        lecture_id: str = Depends(validate_id),
        _ = Depends(validate_student_enrolled_in_lecture([auth_pb2.PROFESSOR, auth_pb2.STUDENT]))):
    file_metadata = db_wrapper.get_database().lectures.find_one(
        {"_id": lecture_id, f"{category.value}-files.file_name": file_name},
        {f"{category.value}-files.$": 1}
    )
    if not file_metadata:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File metadata not found in database")

    file_path = os.path.join("../files", category.value, file_name)

    if not os.path.exists(file_path):
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File not found on disk")

    return FileResponse(
        file_path,
        media_type="application/octet-stream",
        headers={"Content-Disposition": f"attachment; filename={file_name}"},
        status_code=status.HTTP_200_OK
    )


@router.delete("/files/{file_name}", responses={
    status.HTTP_200_OK: {"description": "File deleted successfully"},
    status.HTTP_404_NOT_FOUND: {"description": "File not found or not found in database"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=DeleteFileResponseSchema)
async def delete_file(
        category: Category,
        file_name: str,
        lecture_id: str = Depends(validate_id),
        _ = Depends(validate_professor_owner_of_lecture([auth_pb2.PROFESSOR]))):

    file_path = os.path.join("../files", category.value, file_name)
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