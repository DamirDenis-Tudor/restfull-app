import os
import random
import re
from datetime import datetime
from typing import Optional

from fastapi import File, UploadFile, HTTPException, APIRouter, status, Depends
from fastapi.responses import FileResponse

from config import lectures_ms_host_address
from database import db_wrapper
from proto import auth_pb2
from routers.models import *
from routers.validator import validate_id, professor_owner, validate_user, student_enrolled

router = APIRouter(prefix="/lectures/{lecture_id}", tags=["Files Controller"])


@router.get("/files", responses={
    status.HTTP_200_OK: {"description": "A list with requested resource"},
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_404_NOT_FOUND: {"description": "No files found for the given lecture and category"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=FileListResponse)
async def list_files(
        lecture_id: str = Depends(validate_id),
        owner=Depends(validate_user(
            roles=[auth_pb2.PROFESSOR, auth_pb2.STUDENT],
            execute_for=[
                (auth_pb2.PROFESSOR, professor_owner(throw_on_false=False)),
                (auth_pb2.STUDENT, student_enrolled()),
            ],
        ))
):
    file_list = []
    for category in Category.__members__.values():
        files = db_wrapper.get_database().lectures.find_one(
            {"_id": lecture_id},
            {f"{category.value}-files": 1}
        )

        if not files or f"{category.value}-files" not in files:
            continue

        for file_metadata in files[f"{category.value}-files"]:
            file_name = file_metadata["file_name"]

            links = {
                'download': Link(
                    href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/files/{file_name}?category={category.value}",
                    type="GET"
                )
            }

            if owner:
                links = {
                    'download': Link(
                        href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/files/{file_name}?category={category.value}",
                        type="GET"
                    ),
                    'delete': Link(
                        href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/files/{file_name}?category={category.value}",
                        type="DELETE"
                    )
                }

            file_list.append(FileResponseSchema(
                file_name=file_name,
                category=category.value,
                uploaded_at=file_metadata["uploaded_at"],
                size=file_metadata["size"],
                _links=links
            ))

    links = {"self": Link(href=f"/lectures/{lecture_id}/files", type="GET")}
    if owner:
        links = {
            "self": Link(href=f"/lectures/{lecture_id}/files", type="GET"),
            'upload': Link(
                href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/files?category="+"{}",
                type="POST"
            )
        }

    if len(file_list) == 0:
        FileListResponse(
            _embedded={},
            _links=links
        )

    return FileListResponse(
        _embedded={
            "files": file_list
        },
        _links=links
    )


@router.post("/files", responses={
    status.HTTP_200_OK: {"description": "File uploaded or updated successfully"},
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_404_NOT_FOUND: {"description": "Course not found"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=UploadFileResponseSchema)
async def upload_file(
        category: Category,
        lecture_id: str = Depends(validate_id),
        file: UploadFile = File(...),
        _=Depends(validate_user(
            roles=[auth_pb2.PROFESSOR, auth_pb2.STUDENT],
            execute_for=[
                (auth_pb2.PROFESSOR, professor_owner()),
            ],
        ))
):
    course_dir = os.path.join(f"files/{lecture_id}", category.value)
    os.makedirs(course_dir, exist_ok=True)

    time = random.randint(1000, 9999)
    file.filename = re.sub(r'\s+', '_', file.filename)
    file.filename = f"{time}_{file.filename}"

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
        upsert=True
    )

    return UploadFileResponseSchema(
        message="File uploaded or updated successfully",
        _links={
            "self": Link(href="/lectures/files/{lecture_id}", type="GET")
        }
    )


@router.get("/files/{file_name}", responses={
    status.HTTP_200_OK: {"description": "The requested resource"},
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_404_NOT_FOUND: {"description": "File metadata or file not found"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=FileResponseSchema)
async def get_file(
        category: Category,
        file_name: str,
        lecture_id: str = Depends(validate_id),
        _=Depends(validate_user(
            roles=[auth_pb2.PROFESSOR, auth_pb2.STUDENT],
            execute_for=[
                (auth_pb2.STUDENT, student_enrolled()),
            ],
        ))
):
    file_metadata = db_wrapper.get_database().lectures.find_one(
        {"_id": lecture_id, f"{category.value}-files.file_name": file_name},
        {f"{category.value}-files.$": 1}
    )
    if not file_metadata:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File metadata not found in database")

    file_path = os.path.join(f"files/{lecture_id}", category.value, file_name)

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
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_404_NOT_FOUND: {"description": "File not found or not found in database"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=DeleteFileResponseSchema)
async def delete_file(
        category: Category,
        file_name: str,
        lecture_id: str = Depends(validate_id),
        _=Depends(validate_user(
            roles=[auth_pb2.PROFESSOR],
            execute_for=[
                (auth_pb2.PROFESSOR, professor_owner(throw_on_false=False)),
            ],
        ))
):
    file_path = os.path.join(f"files/{lecture_id}", category.value, file_name)
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
        _links={

        }
    )