from datetime import datetime

from fastapi import APIRouter, Depends
from fastapi import HTTPException, status

from config import lectures_ms_host_address
from database import db_wrapper
from proto import auth_pb2
from routers.files_router import validate_id
from routers.models import *
from routers.validator import roles_validator, student_enrolled, validate_user, professor_owner

router = APIRouter(tags=["Lecture Router"])


@router.put("/lectures", status_code=status.HTTP_201_CREATED, responses={
    status.HTTP_201_CREATED: {"description": "Course created successfully"},
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_409_CONFLICT: {"description": "Course already exists"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_502_BAD_GATEWAY: {"description": "Authorization service unavailable"},
}, response_model=CreateCourseResponse)
async def create_course(
        request_body: LectureRequestBody,
        _=Depends(roles_validator([auth_pb2.PROFESSOR]))):
    lecture_id = validate_id(request_body.lecture_id)

    existing_course = db_wrapper.get_database().lectures.find_one({"_id": lecture_id})
    if existing_course:
        raise HTTPException(status_code=409, detail="Course already exists")

    lecture_id = str(request_body.lecture_id)

    course_data = {
        "_id": lecture_id,
        "created_at": datetime.utcnow().isoformat(),
    }

    db_wrapper.get_database().lectures.insert_one(course_data)

    return CreateCourseResponse(
        message="Course created successfully",
        id=lecture_id,
        created_at=datetime.utcnow().isoformat(),
        _links={
            "self": Link(href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}", type="GET"),
        }
    )


@router.get("/lectures/{lecture_id}/assessments", responses={
    status.HTTP_200_OK: {"description": "Get requested resource"},
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_502_BAD_GATEWAY: {"description": "Authorization service unavailable"}
}, response_model=AssessmentTestResponse)
async def get_assessments(
        lecture_id: str = Depends(validate_id),
        owner=Depends(validate_user(
            roles=[auth_pb2.PROFESSOR, auth_pb2.STUDENT],
            execute_for=[
                (auth_pb2.STUDENT, student_enrolled()),
                (auth_pb2.PROFESSOR, professor_owner(throw_on_false=False)),
            ],
        ))
):
    lecture = db_wrapper.get_database().lectures.find_one(
        {"_id": str(lecture_id)},
    )

    links = {
        "self": Link(href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/assessments", type="GET")
    }
    if owner:
        links = {
            "self": Link(href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/assessments", type="GET"),
            "update": Link(href=f"{lectures_ms_host_address}/api/academia/lectures/{lecture_id}/assessments", type="POST")
        }

    if not lecture:
        return AssessmentTestResponse(
            _embedded={"assessment_tests": []},
            _links=links
        )

    assessment_tests = lecture.get("assessment_tests", [])
    if not assessment_tests:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Assessments not found for this lecture")

    assessment_tests: List[AssessmentTest] = [
        AssessmentTest(type=test["type"], weight=float(test["weight"]))
        for test in assessment_tests
    ]

    return AssessmentTestResponse(
        _embedded={"assessment_tests": assessment_tests},
        _links=links
    )


@router.post("/lectures/{lecture_id}/assessments", responses={
    status.HTTP_200_OK: {"description": "Assessment tests replaced successfully"},
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_404_NOT_FOUND: {"description": "Lecture not found"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_502_BAD_GATEWAY: {"description": "Authorization service unavailable"},
}, response_model=AssessmentTestResponse)
async def replace_assessment_tests(
        new_tests: List[AssessmentTest],
        lecture_id: str = Depends(validate_id),
        _=Depends(validate_user(
            roles=[auth_pb2.PROFESSOR],
            execute_for=[
                (auth_pb2.PROFESSOR, professor_owner()),
            ],
        ))
) -> AssessmentTestResponse:

    total_weight = sum(test.weight for test in new_tests)
    if total_weight != 100:
        raise HTTPException(status_code=422, detail="Total weight must be 100%")

    db_wrapper.get_database().lectures.update_one(
        {"_id": str(lecture_id)},
        {"$set": {"assessment_tests": [{"type": test.type, "weight": test.weight.real} for test in new_tests]}},
        upsert=True
    )

    return AssessmentTestResponse(
        _embedded={"assessment_tests": new_tests},
        _links={}
    )


@router.delete("/lectures/{lecture_id}", responses={
    status.HTTP_200_OK: {"description": "Course deleted successfully"},
    status.HTTP_400_BAD_REQUEST: {"description": "Invalid content"},
    status.HTTP_401_UNAUTHORIZED: {"description": "Unauthorized, token invalid or missing"},
    status.HTTP_403_FORBIDDEN: {"description": "Forbidden, user does not have the necessary permissions"},
    status.HTTP_404_NOT_FOUND: {"description": "Course not found"},
    status.HTTP_422_UNPROCESSABLE_ENTITY: {"description": "Invalid data or missing required information"},
    status.HTTP_500_INTERNAL_SERVER_ERROR: {"description": "An error has occurred"},
    status.HTTP_502_BAD_GATEWAY: {"description": "Authorization service unavailable"},

}, response_model=DeleteCourseResponse)
async def delete_course(
        lecture_id: str = Depends(validate_id),
        _=Depends(validate_user(
            roles=[auth_pb2.PROFESSOR],
            execute_for=[
                (auth_pb2.PROFESSOR, professor_owner())
            ],
        ))
) -> DeleteCourseResponse:
    course = db_wrapper.get_database().lectures.find_one({"_id": str(lecture_id)})
    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    db_wrapper.get_database().lectures.delete_one({"_id": str(lecture_id)})

    return DeleteCourseResponse(
        message=f"Course {lecture_id} deleted successfully",
        _links={
            "self": Link(href=f"/lectures/{lecture_id}", type="DELETE")
        }
    )
