import os

from pymongo import MongoClient

from config import mongodb_uri

client = None

def get_database():
    global client

    if client is None:
        client = MongoClient(mongodb_uri)

    return client['lectures-db']