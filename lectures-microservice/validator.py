import grpc
from functools import wraps
from fastapi import HTTPException, Request, status
from fastapi.concurrency import run_in_threadpool

import auth_pb2
import auth_pb2_grpc

channel = grpc.insecure_channel('localhost:50051')
stub = auth_pb2_grpc.AuthServiceStub(channel)

def role_validator(role: auth_pb2.Role):
    def decorator(func):
        @wraps(func)
        async def wrapper(request: Request, *args, **kwargs):
            token = request.headers.get("Authorization")
            if not token or not token.startswith("Bearer "):
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Missing or invalid Authorization Token"
                )
            token = token.split(" ")[1]
            try:
                token_request = auth_pb2.TokenRequest(token=token)
                response = await run_in_threadpool(stub.ValidateToken, token_request)

                if response.result.HasField("error"):
                    raise HTTPException(
                        status_code=status.HTTP_401_UNAUTHORIZED,
                        detail="Invalid or expired token"
                    )

                if response.result.success.role != role:
                    raise HTTPException(
                        status_code=status.HTTP_403_FORBIDDEN,
                        detail="Insufficient role"
                    )

            except grpc.RpcError as e:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail=e
                )

            return await func(request, *args, **kwargs)

        return wrapper
    return decorator
