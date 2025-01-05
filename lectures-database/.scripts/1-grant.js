db = db.getSiblingDB("admin");

db.createUser({
    user: "user",
    pwd: "password",
    roles: [
        { role: "readWrite", db: "lectures-db" },
        { role: "dbAdmin", db: "lectures-db" }
    ]
});
