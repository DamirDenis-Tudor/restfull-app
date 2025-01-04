use admin;

db.createUser({
    user: "user",
    pwd: "password",
    roles: [
        {
            role: "readWrite",
            db: "lectures-db"
        }
    ]
});
