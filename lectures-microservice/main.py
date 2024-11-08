from fastapi import FastAPI
from routers import lecture_router, files_router


app = FastAPI()
app.include_router(lecture_router.router)
app.include_router(files_router.router)
