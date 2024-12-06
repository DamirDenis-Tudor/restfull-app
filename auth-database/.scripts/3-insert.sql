-- User Roles: 1 - Admin, 2 - Professor, 3 - Student
INSERT INTO users (email, password, role)
VALUES
    -- Admin
    ('admin.admin@example.com', 'admin', 1),
    -- Professors
    ( 'john.doe@example.com', 'securepass1', 2),
    ( 'jane.smith@example.com', 'securepass2', 2),
    ( 'emily.wilson@example.com', 'securepass3', 2),
    ( 'michael.taylor@example.com', 'securepass4', 2),
    ( 'sarah.moore@example.com', 'securepass5', 2),
    -- Students
    ( 'alice.johnson@example.com', 'password123', 3),
    ( 'bob.brown@example.com', 'password456', 3),
    ( 'charlie.davis@example.com', 'password789', 3),
    ( 'david.garcia@example.com', 'password321', 3),
    ( 'sophia.martinez@example.com', 'password654', 3),
    ( 'liam.hernandez@example.com', 'password987', 3),
    ( 'olivia.lopez@example.com', 'password111', 3),
    ( 'emma.anderson@example.com', 'password222', 3),
    ( 'james.thomas@example.com', 'password333', 3),
    ( 'isabella.jackson@example.com', 'password444', 3),
    ( 'noah.white@example.com', 'password555', 3),
    ( 'ava.harris@example.com', 'password666', 3),
    ( 'liam.clark@example.com', 'password777', 3),
    ( 'mia.lewis@example.com', 'password888', 3),
    ( 'ethan.robinson@example.com', 'password999', 3);

