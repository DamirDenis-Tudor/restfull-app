from enum import Enum
from typing import List, Dict

from pydantic import BaseModel, Field


class Link(BaseModel):
    href: str
    type: str

    class Config:
        exclude_none = True

class Category(str, Enum):
    course = "course"
    lab = "lab"


class FileResponseSchema(BaseModel):
    file_name: str
    category: str
    uploaded_at: str
    size: int
    links: Dict[str, Link] = Field(..., alias="_links")

    class Config:
        populate_by_name = True

class FileListResponse(BaseModel):
    embedded: Dict[str, List[FileResponseSchema]] = Field(..., alias="_embedded")
    links: Dict[str, Link] = Field(..., alias="_links")

    class Config:
        populate_by_name = True


class UploadFileResponseSchema(BaseModel):
    message: str
    links: Dict[str, Link] = Field(..., alias="_links")

    class Config:
        populate_by_name = True


class DeleteFileResponseSchema(BaseModel):
    message: str
    links: Dict[str, Link] = Field(..., alias="_links")

    class Config:
        populate_by_name = True


class AssessmentTest(BaseModel):
    type: str = Field(...)
    weight: float = Field(..., ge=0, le=100)

class CreateCourseResponse(BaseModel):
    id: str
    created_at: str
    message: str
    links: Dict[str, Link] = Field(..., alias="_links")

    class Config:
        populate_by_name = True
        exclude_none = True


class AssessmentTestResponse(BaseModel):
    embedded: Dict[str, List[AssessmentTest]] = Field(..., alias="_embedded")
    links: Dict[str, Link] = Field(..., alias="_links")

    class Config:
        populate_by_name = True


class DeleteCourseResponse(BaseModel):
    message: str
    links: Dict[str, Link] = Field(..., alias="_links")

    class Config:
        populate_by_name = True

class LectureRequestBody(BaseModel):
    lecture_id: int
