import os

study_ms_host_address = os.getenv('STUDY_MS_HOST_ADDRESS', "https://0.0.0.0:8080")
auth_ms_host_address = os.getenv('AUTH_MS_HOST_ADDRESS', "0.0.0.0:50051")
mongodb_uri = os.getenv("MONGO_URI", "mongodb://user:password@0.0.0.0")