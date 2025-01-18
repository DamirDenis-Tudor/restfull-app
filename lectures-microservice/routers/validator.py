import socket

import grpc
import httpx
from fastapi import HTTPException, Request, status

from config import auth_ms_host_address, study_ms_host_address
from proto import auth_pb2_grpc, auth_pb2

channel = grpc.insecure_channel(f"{socket.gethostbyname(auth_ms_host_address)}:50051")
stub = auth_pb2_grpc.AuthServiceStub(channel)

def validate_id(lecture_id: int):
    if lecture_id < 1 or lecture_id > 999:
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                            detail="Lecture ID must be between 1 and 999 digits")
    return str(lecture_id)

def roles_validator(roles: list[auth_pb2.Role]):
    def validator(request: Request):
        global channel
        global stub

        token = request.headers.get("Authorization")

        if not token or not token.startswith("Bearer "):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Missing or invalid Authorization Token"
            )

        token = token.split(" ")[1]
        try:
            response = stub.ValidateToken(auth_pb2.TokenRequest(token=token))

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
            channel = grpc.insecure_channel(auth_ms_host_address)
            stub = auth_pb2_grpc.AuthServiceStub(channel)

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail=str(e)
            )

    return validator

def professor_owner(throw_on_false = True):
    async def validator(lecture_id: int, request: Request,):
        async with httpx.AsyncClient() as client:
            try:
                url = f"{study_ms_host_address}/api/academia/lectures/{lecture_id}/professors/owner"
                headers = {
                    "Authorization": request.headers.get("Authorization")
                }
                response = await client.get(url = url, headers = headers)

                if response.status_code != 200:
                    raise HTTPException(
                        status_code=response.status_code,
                        detail=response.text
                    )

                if throw_on_false and not bool(response.json()):
                    raise HTTPException(
                        status_code=status.HTTP_403_FORBIDDEN,
                        detail="The professor is not the owner of the lecture"
                    )

                if throw_on_false:
                    return None

                return bool(response.json())

            except httpx.RequestError as e:
                raise HTTPException(
                    status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                    detail=f"Error during request: {str(e)}"
                )
    return validator

def student_enrolled( throw_on_false = True):
    async def validator(lecture_id: int, request: Request):
        async with httpx.AsyncClient() as client:
            try:
                url = f"{study_ms_host_address}/api/academia/lectures/{lecture_id}/students/isEnrolled"
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

                if throw_on_false and not bool(data):
                    raise HTTPException(
                        status_code=status.HTTP_403_FORBIDDEN,
                        detail="The student is not enrolled in the lecture"
                    )

                if throw_on_false:
                    return None

                return bool(data)

            except httpx.RequestError as e:
                raise HTTPException(
                    status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                    detail=f"Error during request: {str(e)}"
                )
    return validator


def validate_user(roles: list[auth_pb2.Role], execute_for:list[tuple[auth_pb2.Role, callable]]):
    async def validator(lecture_id: int, request: Request):
        _, r = roles_validator(roles)(request)
        for execute in execute_for:
            if r is execute[0]:
                return await execute[1](lecture_id, request)

    return validator