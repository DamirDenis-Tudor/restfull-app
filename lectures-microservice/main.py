from fastapi import FastAPI

import files_router
import lecture_router

app = FastAPI(
    title="Lecture Management API",
    description="API for managing lectures and associated files.",
    version="1.0.0",
)
app.include_router(lecture_router.router, prefix="/api/academia")
app.include_router(files_router.router, prefix="/api/academia")
