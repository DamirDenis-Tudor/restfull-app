import os

from pymongo import MongoClient


client = None

def get_database():
    global client

    if client is None:
        mongodb_uri = os.getenv("MONGO_URI", "mongodb://user:password@localhost/")
        client = MongoClient(mongodb_uri)

    return client['lectures-db']