from datetime import datetime
from typing import List

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from pydantic import Field

from database import db_wrapper

router = APIRouter(tags=["Course Router"])

class AssessmentTest(BaseModel):
    type: str = Field(...)
    weight: float = Field(..., ge=0, le=100)

@router.put("/lectures/{lecture_id}")
async def create_course(lecture_id: str):
    discipline_data = {
        "_id": lecture_id,
        "created_at": datetime.utcnow().isoformat(),
    }

    existing_course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})
    if existing_course:
        raise HTTPException(status_code=409, detail="Course already exists")

    db_wrapper.get_database().lectures.insert_one(discipline_data)

    return {"message": "Course created successfully", "course": discipline_data}

@router.post("/lectures/{lecture_id}/assessments")
async def replace_assessment_tests(lecture_id: str, new_tests: List[AssessmentTest]):
    lecture = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})

    if not lecture:
        raise HTTPException(status_code=404, detail="Lecture not found")

    total_weight = sum(test.weight for test in new_tests)

    if total_weight != 100:
        raise HTTPException(status_code=416, detail="Total weight must be 100%")

    updated_lecture = db_wrapper.get_database().lectures.update_one(
        {"_id": lecture_id},
        {"$set": {"assessment_tests": [{"type": test.type, "weight": test.weight} for test in new_tests]}}
    )

    if updated_lecture.matched_count == 0:
        raise HTTPException(status_code=404, detail="Lecture not found")

    return {"message": "Assessment tests replaced successfully", "assessment_tests": new_tests}

@router.delete("/lectures/{lecture_id}")
async def delete_course(lecture_id: str):
    course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})

    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    db_wrapper.get_database().lectures.delete_one({"_id": lecture_id})

    return {"message": f"Course {lecture_id} deleted successfully"}
