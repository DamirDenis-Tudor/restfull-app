import grpc
from fastapi import HTTPException, Request, status
import httpx
from proto import auth_pb2_grpc, auth_pb2

channel = grpc.insecure_channel('localhost:50051')
stub = auth_pb2_grpc.AuthServiceStub(channel)

def validate_id(lecture_id: int):
    if lecture_id < 1 or lecture_id > 999:
        raise HTTPException(status_code=status.HTTP_416_REQUESTED_RANGE_NOT_SATISFIABLE,
                            detail="Lecture ID must be between 1 and 999 digits")
    return str(lecture_id)

def roles_validator(roles: list[auth_pb2.Role]):
    def validator(request: Request):
        token = request.headers.get("Authorization")

        if not token or not token.startswith("Bearer "):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Missing or invalid Authorization Token"
            )

        token = token.split(" ")[1]
        try:
            token_request = auth_pb2.TokenRequest(token=token)

            response = stub.ValidateToken(token_request)

            if response.HasField("error"):
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid or expired token"
                )

            if response.success.role not in roles:
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="Insufficient role"
                )

            return response.success.id, response.success.role

        except grpc.RpcError as e:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail=str(e)
            )

    return validator


def validate_professor_owner_of_lecture(roles: list[auth_pb2.Role]):
    async def validator(lecture_id: int, request: Request):
        _, r = roles_validator(roles)(request)
        if r is auth_pb2.PROFESSOR:
            async with httpx.AsyncClient() as client:
                try:
                    url = f"http://localhost:8080/api/academia/lectures/{lecture_id}/professors/owner"
                    headers = {
                        "Authorization": request.headers.get("Authorization")
                    }
                    response = await client.get(url = url, headers = headers)

                    if response.status_code != 200:
                        raise HTTPException(
                            status_code=response.status_code,
                            detail=response.text
                        )

                    data = response.json()

                    if not bool(data):
                        raise HTTPException(
                            status_code=status.HTTP_403_FORBIDDEN,
                            detail="The professor is not the owner of the lecture"
                        )

                except httpx.RequestError as e:
                    raise HTTPException(
                        status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                        detail=f"Error during request: {str(e)}"
                    )
    return validator

def validate_student_enrolled_in_lecture(roles: list[auth_pb2.Role]):
    async def validator(lecture_id: int, request: Request):
        _, r = roles_validator(roles)(request)
        if r is auth_pb2.STUDENT:
            async with httpx.AsyncClient() as client:
                try:
                    url = f"http://localhost:8080/api/academia/lectures/{lecture_id}/students/enrolled"
                    headers = {
                        "Authorization": request.headers.get("Authorization")
                    }
                    response = await client.get(url = url, headers = headers)

                    if response.status_code != 200:
                        raise HTTPException(
                            status_code=response.status_code,
                            detail=response.text
                        )

                    data = response.json()

                    if not bool(data):
                        raise HTTPException(
                            status_code=status.HTTP_403_FORBIDDEN,
                            detail="The student is not enrolled in the lecture"
                        )

                except httpx.RequestError as e:
                    raise HTTPException(
                        status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                        detail=f"Error during request: {str(e)}"
                    )
    return validator