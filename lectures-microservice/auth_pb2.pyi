from google.protobuf.internal import enum_type_wrapper as _enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Mapping as _Mapping, Optional as _Optional, Union as _Union

ADMIN: Role
DESCRIPTOR: _descriptor.FileDescriptor
PROFESSOR: Role
STUDENT: Role
UNKNOWN: Role

class ErrorResponse(_message.Message):
    __slots__ = ["exception", "message"]
    EXCEPTION_FIELD_NUMBER: _ClassVar[int]
    MESSAGE_FIELD_NUMBER: _ClassVar[int]
    exception: str
    message: str
    def __init__(self, exception: _Optional[str] = ..., message: _Optional[str] = ...) -> None: ...

class InvalidateTokenResponse(_message.Message):
    __slots__ = ["error", "success"]
    ERROR_FIELD_NUMBER: _ClassVar[int]
    SUCCESS_FIELD_NUMBER: _ClassVar[int]
    error: ErrorResponse
    success: str
    def __init__(self, success: _Optional[str] = ..., error: _Optional[_Union[ErrorResponse, _Mapping]] = ...) -> None: ...

class LoginRequest(_message.Message):
    __slots__ = ["password", "username"]
    PASSWORD_FIELD_NUMBER: _ClassVar[int]
    USERNAME_FIELD_NUMBER: _ClassVar[int]
    password: str
    username: str
    def __init__(self, username: _Optional[str] = ..., password: _Optional[str] = ...) -> None: ...

class LoginResponse(_message.Message):
    __slots__ = ["error", "success"]
    ERROR_FIELD_NUMBER: _ClassVar[int]
    SUCCESS_FIELD_NUMBER: _ClassVar[int]
    error: ErrorResponse
    success: TokenLoginResponse
    def __init__(self, success: _Optional[_Union[TokenLoginResponse, _Mapping]] = ..., error: _Optional[_Union[ErrorResponse, _Mapping]] = ...) -> None: ...

class TokenLoginResponse(_message.Message):
    __slots__ = ["message", "token"]
    MESSAGE_FIELD_NUMBER: _ClassVar[int]
    TOKEN_FIELD_NUMBER: _ClassVar[int]
    message: str
    token: str
    def __init__(self, token: _Optional[str] = ..., message: _Optional[str] = ...) -> None: ...

class TokenRequest(_message.Message):
    __slots__ = ["token"]
    TOKEN_FIELD_NUMBER: _ClassVar[int]
    token: str
    def __init__(self, token: _Optional[str] = ...) -> None: ...

class TokenValid(_message.Message):
    __slots__ = ["id", "role"]
    ID_FIELD_NUMBER: _ClassVar[int]
    ROLE_FIELD_NUMBER: _ClassVar[int]
    id: str
    role: Role
    def __init__(self, role: _Optional[_Union[Role, str]] = ..., id: _Optional[str] = ...) -> None: ...

class TokenValidResponse(_message.Message):
    __slots__ = ["error", "success"]
    ERROR_FIELD_NUMBER: _ClassVar[int]
    SUCCESS_FIELD_NUMBER: _ClassVar[int]
    error: ErrorResponse
    success: TokenValid
    def __init__(self, success: _Optional[_Union[TokenValid, _Mapping]] = ..., error: _Optional[_Union[ErrorResponse, _Mapping]] = ...) -> None: ...

class Role(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
    __slots__ = []
