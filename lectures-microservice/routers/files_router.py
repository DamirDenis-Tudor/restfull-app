import os
from datetime import datetime

from fastapi import File, UploadFile, HTTPException, APIRouter
from fastapi.responses import JSONResponse

from database import db_wrapper

router = APIRouter(prefix="/lectures/{lecture_id}", tags=["Files Controller"])

@router.post("/files")
async def upload_file(
        lecture_id: str,
        category: str,
        file: UploadFile = File(...),
):
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

    return JSONResponse(content={"message": "File uploaded or updated successfully", "file_metadata": file_metadata})


@router.delete("/files/{file_name}")
async def delete_file(
        lecture_id: str,
        category: str,
        file_name: str,
):
    file_path = os.path.join("files", category, file_name)

    if os.path.exists(file_path):
        os.remove(file_path)

    file_exist = (
        db_wrapper.get_database().lectures.find_one(
            {"_id": lecture_id, f"{category}-files.file_name": file_name},
            {"_id": 1})
    )

    if not file_exist:
        raise HTTPException(status_code=404, detail="File not found")

    db_wrapper.get_database().lectures.update_one(
        {"_id": lecture_id},
        {"$pull": {f"{category}-files": {"file_name": file_name}}}
    )

    return JSONResponse(content={"message": f"File '{file_name}' deleted successfully"})