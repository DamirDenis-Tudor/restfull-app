from fastapi import FastAPI, APIRouter

from routers import lecture_router, files_router

app = FastAPI(
    servers=[]
)
sub_api = FastAPI(
    title="Lecture Management API",
    description="API for managing lectures and associated files.",
    version="1.0.0",
    servers=[
        {
            "url": "http://localhost:8000/api/academia",
            "description": "Local Server"
        },
        {
            "url": "/api/academia",
        }
    ]
)
sub_api.include_router(lecture_router.router)
sub_api.include_router(files_router.router)

app.mount("/api/academia", sub_api)
