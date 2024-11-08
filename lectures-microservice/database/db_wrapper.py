from pymongo import MongoClient


def get_database():
    client = MongoClient("mongodb://user:password@localhost/")

    return client['lectures-db']