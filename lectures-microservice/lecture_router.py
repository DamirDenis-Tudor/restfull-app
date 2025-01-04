import base64
from datetime import datetime
from typing import List, Dict

from fastapi import APIRouter, Request

import auth_pb2
from database import db_wrapper
from fastapi import HTTPException, status

from files_router import validate_id
from models import *
from validator import role_validator

router = APIRouter(tags=["Lecture Router"])


def generate_hateoas_links(lecture_id: str) -> Dict[str, Link]:
    return {
        "self": Link(href=f"/lectures/{lecture_id}"),
        "delete": Link(href=f"/lectures/{lecture_id}"),
        "update_assessments": Link(href=f"/lectures/{lecture_id}/assessments"),
        "create_course": Link(href=f"/lectures/{lecture_id}")
    }


def extract_authorization_header(request: Request):
    authorization_header = request.headers.get("authorization")
    if not authorization_header:
        raise HTTPException(status_code=400, detail="Authorization header is missing")

    print(authorization_header.split(" ")[1])
    print(base64.b64decode(authorization_header.split(" ")[1]).decode('utf-8'))

    return authorization_header



@router.put("/lectures", status_code=status.HTTP_201_CREATED, responses={
    status.HTTP_409_CONFLICT: {"description": "Course already exists"},
    status.HTTP_201_CREATED: {"description": "Course created successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"}
}, response_model=CreateCourseResponse)
@role_validator(role=auth_pb2.Role.PROFESSOR)
async def create_course(request_body: LectureRequestBody):
    validate_id(request_body.lecture_id)

    existing_course = db_wrapper.get_database().lectures.find_one({"_id": str(request_body.lecture_id)})
    if existing_course:
        raise HTTPException(status_code=409, detail="Course already exists")

    lecture_id = str(request_body.lecture_id)

    course_data = {
        "_id": lecture_id,
        "created_at": datetime.utcnow().isoformat(),
    }

    db_wrapper.get_database().lectures.insert_one(course_data)

    links = generate_hateoas_links(lecture_id)

    return CreateCourseResponse(
        message="Course created successfully",
        id=lecture_id,
        created_at=datetime.utcnow().isoformat(),
        _links=links
    )


@router.get("/lectures/{lecture_id}/assessments/", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Lecture or assessments not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=AssessmentTestResponse)
@role_validator(role=auth_pb2.Role.PROFESSOR)
async def get_assessments(lecture_id: int):
    validate_id(lecture_id)

    lecture = db_wrapper.get_database().lectures.find_one({"_id": str(lecture_id)})

    if not lecture:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Lecture not found")

    assessment_tests = lecture.get("assessment_tests", [])
    if not assessment_tests:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Assessments not found for this lecture")

    assessment_tests: List[AssessmentTest] = [
        AssessmentTest(type=test["type"], weight=float(test["weight"]))
        for test in assessment_tests
    ]

    links: Dict[str, Link] = {
        "self": Link(href=f"/lectures/{lecture_id}/assessments")
    }

    return AssessmentTestResponse(
        embedded={"assessment_tests": assessment_tests},
        links=links
    )

@role_validator(role=auth_pb2.Role.PROFESSOR)
@router.post("/lectures/{lecture_id}/assessments", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Lecture not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"},
    status.HTTP_200_OK: {"description": "Assessment tests replaced successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
}, response_model=AssessmentTestResponse)
async def replace_assessment_tests(lecture_id: int, new_tests: List[AssessmentTest]) -> AssessmentTestResponse:
    validate_id(lecture_id)

    lecture = db_wrapper.get_database().lectures.find_one({"_id": str(lecture_id)})
    if not lecture:
        raise HTTPException(status_code=404, detail="Lecture not found")

    total_weight = sum(test.weight for test in new_tests)
    if total_weight != 100:
        raise HTTPException(status_code=416, detail="Total weight must be 100%")

    db_wrapper.get_database().lectures.update_one(
        {"_id": str(lecture_id)},
        {"$set": {"assessment_tests": [{"type": test.type, "weight": test.weight} for test in new_tests]}}
    )

    return AssessmentTestResponse(
        embedded={"assessment_tests": new_tests},
        links=generate_hateoas_links(str(lecture_id))
    )

@role_validator(role=auth_pb2.Role.PROFESSOR)
@router.delete("/lectures/{lecture_id}", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Course not found"},
    status.HTTP_200_OK: {"description": "Course deleted successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
}, response_model=DeleteCourseResponse)
async def delete_course(lecture_id: int) -> DeleteCourseResponse:
    validate_id(lecture_id)

    course = db_wrapper.get_database().lectures.find_one({"_id": str(lecture_id)})
    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    db_wrapper.get_database().lectures.delete_one({"_id": str(lecture_id)})

    return DeleteCourseResponse(
        message=f"Course {lecture_id} deleted successfully",
        links={
            "create_course": Link(href=f"/lectures/{lecture_id}")
        }
    )
