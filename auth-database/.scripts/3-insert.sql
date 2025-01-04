-- User Roles: 1 - Admin, 2 - Professor, 3 - Student
INSERT INTO users (email, password, role)
VALUES
    -- Admin
    ('admin.admin@example.com', 'admin', 1),

    -- Students
    ( 'alice.johnson@example.com', 'password123', 2),
    ( 'bob.brown@example.com', 'password456', 2),
    ( 'charlie.davis@example.com', 'password789', 2),
    ( 'david.garcia@example.com', 'password321', 2),
    ( 'sophia.martinez@example.com', 'password654', 2),
    ( 'liam.hernandez@example.com', 'password987', 2),
    ( 'olivia.lopez@example.com', 'password111', 2),
    ( 'emma.anderson@example.com', 'password222', 2),
    ( 'james.thomas@example.com', 'password333', 2),
    ( 'isabella.jackson@example.com', 'password444', 2),
    ( 'noah.white@example.com', 'password555', 2),
    ( 'ava.harris@example.com', 'password666', 2),
    ( 'liam.clark@example.com', 'password777', 2),
    ( 'mia.lewis@example.com', 'password888', 2),
    ( 'ethan.robinson@example.com', 'password999', 2),

    -- Professors
    ( 'john.doe@example.com', 'securepass1', 3),
    ( 'jane.smith@example.com', 'securepass2', 3),
    ( 'emily.wilson@example.com', 'securepass3', 3),
    ( 'michael.taylor@example.com', 'securepass4', 3),
    ( 'sarah.moore@example.com', 'securepass5', 3);