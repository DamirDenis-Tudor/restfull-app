import base64
from datetime import datetime
from typing import List

from fastapi import APIRouter, HTTPException, Request, Depends
from pydantic import BaseModel, Field

from database import db_wrapper

router = APIRouter(tags=["Lecture Router"])

class AssessmentTest(BaseModel):
    type: str = Field(...)
    weight: float = Field(..., ge=0, le=100)

def generate_hateoas_links(lecture_id: str):
    return {
        "self": {"href": f"/lectures/{lecture_id}"},
        "delete": {"href": f"/lectures/{lecture_id}"},
        "update_assessments": {"href": f"/lectures/{lecture_id}/assessments"},
        "create_course": {"href": f"/lectures/{lecture_id}"}
    }

def extract_authorization_header(request: Request):
    authorization_header = request.headers.get("authorization")
    if not authorization_header:
        raise HTTPException(status_code=400, detail="Authorization header is missing")

    print(authorization_header.split(" ")[1])
    print(base64.b64decode(authorization_header.split(" ")[1]).decode('utf-8'))

    return authorization_header

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

    return {

        "message": "Course created successfully",
        "course": discipline_data,
        "_links": generate_hateoas_links(lecture_id)
    }

@router.post("/lectures/{lecture_id}/assessments")
async def replace_assessment_tests(lecture_id: str, new_tests: List[AssessmentTest]):
    # TODO: verify user identity

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

    return {
        "message": "Assessment tests replaced successfully",
        "assessment_tests": new_tests,
        "links": generate_hateoas_links(lecture_id)
    }

@router.delete("/lectures/{lecture_id}")
async def delete_course(lecture_id: str, authorization: str = Depends(extract_authorization_header)):
    # TODO: verify user identity (authentication)

    # Print the Authorization header (you may use this for token validation logic)
    print(f"Authorization header: {authorization}")

    course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})

    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    db_wrapper.get_database().lectures.delete_one({"_id": lecture_id})

    return {
        "message": f"Course {lecture_id} deleted successfully",
        "links": {
            "create_course": {"href": f"/lectures/{lecture_id}"}
        }
    }
