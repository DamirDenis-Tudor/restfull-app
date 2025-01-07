from datetime import datetime

from fastapi import APIRouter, Depends
from fastapi import HTTPException, status

from database import db_wrapper
from proto import auth_pb2
from routers.files_router import validate_id
from routers.models import *
from routers.validator import roles_validator, validate_student_enrolled_in_lecture, validate_professor_owner_of_lecture

router = APIRouter(tags=["Lecture Router"])


def generate_hateoas_links(lecture_id: str) -> Dict[str, Link]:
    return {
        "self": Link(href=f"/lectures/{lecture_id}"),
        "delete": Link(href=f"/lectures/{lecture_id}"),
        "update_assessments": Link(href=f"/lectures/{lecture_id}/assessments"),
        "create_course": Link(href=f"/lectures/{lecture_id}")
    }

@router.put("/lectures", status_code=status.HTTP_201_CREATED, responses={
    status.HTTP_409_CONFLICT: {"description": "Course already exists"},
    status.HTTP_201_CREATED: {"description": "Course created successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"}
}, response_model=CreateCourseResponse)
async def create_course(
    request_body: LectureRequestBody,
    _ = Depends(roles_validator([auth_pb2.PROFESSOR]))):

    lecture_id = validate_id(request_body.lecture_id)

    existing_course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id })
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


@router.get("/lectures/{lecture_id}/assessments", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Lecture or assessments not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"}
}, response_model=AssessmentTestResponse)
async def get_assessments(
        lecture_id: str = Depends(validate_id),
        _ = Depends(validate_student_enrolled_in_lecture([auth_pb2.PROFESSOR, auth_pb2.STUDENT]))):

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
        _embedded={"assessment_tests": assessment_tests},
        _links=links
    )

@router.post("/lectures/{lecture_id}/assessments", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Lecture not found"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"},
    status.HTTP_200_OK: {"description": "Assessment tests replaced successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
}, response_model=AssessmentTestResponse)
async def replace_assessment_tests(
        new_tests: List[AssessmentTest],
        lecture_id: str = Depends(validate_id),
        _ = Depends(validate_professor_owner_of_lecture([auth_pb2.PROFESSOR]))) -> AssessmentTestResponse:

    lecture = db_wrapper.get_database().lectures.find_one({"_id": str(lecture_id)})
    if not lecture:
        raise HTTPException(status_code=404, detail="Lecture not found")

    total_weight = sum(test.weight for test in new_tests)
    if total_weight != 100:
        raise HTTPException(status_code=416, detail="Total weight must be 100%")

    db_wrapper.get_database().lectures.update_one(
        {"_id": str(lecture_id)},
        {"$set": {"assessment_tests": [{"type": test.type, "weight": test.weight.real} for test in new_tests]}}
    )

    return AssessmentTestResponse(
        _embedded={"assessment_tests": new_tests},
        _links=generate_hateoas_links(str(lecture_id))
    )

@router.delete("/lectures/{lecture_id}", responses={
    status.HTTP_404_NOT_FOUND: {"description": "Course not found"},
    status.HTTP_200_OK: {"description": "Course deleted successfully"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_503_SERVICE_UNAVAILABLE: {"description": "Authorization service unavailable"},
    status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE: {"description": "Lecture ID must be between 1 and 999 digits"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
}, response_model=DeleteCourseResponse)
async def delete_course(
        lecture_id: str = Depends(validate_id),
        _ = Depends(validate_professor_owner_of_lecture([auth_pb2.PROFESSOR]))
) -> DeleteCourseResponse:

    course = db_wrapper.get_database().lectures.find_one({"_id": str(lecture_id)})
    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    db_wrapper.get_database().lectures.delete_one({"_id": str(lecture_id)})

    return DeleteCourseResponse(
        message=f"Course {lecture_id} deleted successfully",
        _links={
            "create_course": Link(href=f"/lectures/{lecture_id}")
        }
    )
