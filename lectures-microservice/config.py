import os

study_ms_host_address = os.getenv('STUDY_MS_HOST_ADDRESS', "http://0.0.0.0:8080")
auth_ms_host_address = os.getenv('AUTH_MS_HOST_ADDRESS', "0.0.0.0:50051")



host = os.getenv('LECTURES_DB_HOST', "0.0.0.0")
port = os.getenv('LECTURES_DB_PORT', "27017")
user = os.getenv('LECTURES_DB_USER', "user")
password = os.getenv('LECTURES_DB_PASSWORD', "password")
mongodb_uri = os.getenv("MONGO_URI", f"mongodb://{user}:{password}@{host}:{port}")