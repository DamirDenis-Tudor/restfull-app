import os
from datetime import datetime

from fastapi import File, UploadFile, HTTPException, APIRouter
from fastapi.responses import JSONResponse
from starlette.responses import FileResponse

from database import db_wrapper

router = APIRouter(prefix="/lectures/{lecture_id}", tags=["Files Controller"])


def generate_file_hateoas_links(lecture_id: str, cat: str, file_name: str = None):
    base_link = f"/lectures/{lecture_id}/files"
    links = {
        "upload": {"href": base_link},
        "delete": {"href": f"{base_link}/{file_name}"} if file_name else None,
        "get_file": {"href": f"{base_link}/{file_name}?category={cat}"} if file_name else None,
    }
    return {k: v for k, v in links.items() if v}


@router.get("/files/{file_name}")
async def get_file(lecture_id: str, category: str, file_name: str):
    if category not in ["course", "lab"]:
        raise HTTPException(status_code=416, detail="Category must be 'course' or 'lab'")

    file_metadata = db_wrapper.get_database().lectures.find_one(
        {"_id": lecture_id, f"{category}-files.file_name": file_name},
        {f"{category}-files.$": 1}
    )
    if not file_metadata:
        raise HTTPException(status_code=404, detail="File metadata not found in database")

    file_path = os.path.join("files", category, file_name)
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="File not found on disk")

    return FileResponse(file_path, media_type="application/octet-stream", filename=file_name)


@router.post("/files")
async def upload_file(lecture_id: str, category: str, file: UploadFile = File(...)):
    if category not in ["course", "lab"]:
        raise HTTPException(status_code=416, detail="Category must be 'course' or 'lab'")

    course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})
    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    course_dir = os.path.join("files", category)
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
        {"$push": {f"{category}-files": file_metadata}},
    )

    return JSONResponse(content={
        "message": "File uploaded or updated successfully",
        "file_metadata": file_metadata,
        "_links": generate_file_hateoas_links(lecture_id, category, file.filename)
    })


@router.delete("/files/{file_name}")
async def delete_file(lecture_id: str, category: str, file_name: str):
    file_path = os.path.join("files", category, file_name)

    if os.path.exists(file_path):
        os.remove(file_path)

    file_exist = db_wrapper.get_database().lectures.find_one(
        {"_id": lecture_id, f"{category}-files.file_name": file_name},
        {"_id": 1}
    )
    if not file_exist:
        raise HTTPException(status_code=404, detail="File not found")

    db_wrapper.get_database().lectures.update_one(
        {"_id": lecture_id},
        {"$pull": {f"{category}-files": {"file_name": file_name}}}
    )

    return JSONResponse(content={
        "message": f"File '{file_name}' deleted successfully",
        "_links": generate_file_hateoas_links(lecture_id, category)
    })
