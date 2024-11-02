-- Insert into Teacher
INSERT INTO professor (first_name, last_name, email, affiliation, grader_type, association_type)
VALUES
    ('John', 'Doe', 'john.doe@example.com', 'Computer Science Department', 'Profesor', 'Titular'),
    ('Jane', 'Smith', 'jane.smith@example.com', 'Mathematics Department', 'Asistent', 'Asociat'),
    ('Emily', 'Wilson', 'emily.wilson@example.com', 'Physics Department', 'Conferentiar', 'Titular'),
    ('Michael', 'Taylor', 'michael.taylor@example.com', 'Chemistry Department', 'Profesor', 'Extern'),
    ('Sarah', 'Moore', 'sarah.moore@example.com', 'Biology Department', 'Asistent', 'Asociat');

-- Insert into Discipline
INSERT INTO lecture (id, lecture_name, study_year, lecture_type, category_type, examination_type, professor_id)
VALUES
    (101,'Data Structures', 1, 'Impusa', 'Domeniu', 'Examen', 1),
    (102,'Algorithms', 1, 'Impusa', 'Specialitate', 'Cologviu', 1),
    (103,'Linear Algebra', 2, 'Optionala', 'Domeniu', 'Examen', 2),
    (104,'Machine Learning', 2, 'LiberAleasa', 'Specialitate', 'Examen', 2),
    (105,'Quantum Mechanics', 2, 'Optionala', 'Specialitate', 'Examen', 3),
    (106,'Organic Chemistry', 1, 'Impusa', 'Domeniu', 'Cologviu', 4),
    (107,'Cell Biology', 2, 'LiberAleasa', 'Specialitate', 'Examen', 5),
    (108,'Software Engineering', 1, 'Impusa', 'Adiacenta', 'Examen', 1),
    (109,'Advanced Mathematics', 2, 'Optionala', 'Domeniu', 'Examen', 2);

-- Insert into Student
INSERT INTO student (first_name, last_name, cycle_type, email, study_year, student_group)
VALUES
    ('Alice', 'Johnson', 'Licenta', 'alice.johnson@example.com', 1, 101),
    ('Bob', 'Brown', 'Licenta', 'bob.brown@example.com', 1, 102),
    ('Charlie', 'Davis', 'Master', 'charlie.davis@example.com', 2, 201),
    ('David', 'Garcia', 'Licenta', 'david.garcia@example.com', 1, 103),
    ('Sophia', 'Martinez', 'Licenta', 'sophia.martinez@example.com', 1, 104),
    ('Liam', 'Hernandez', 'Master', 'liam.hernandez@example.com', 2, 202),
    ('Olivia', 'Lopez', 'Master', 'olivia.lopez@example.com', 2, 203),
    ('Emma', 'Anderson', 'Licenta', 'emma.anderson@example.com', 1, 105),
    ('James', 'Thomas', 'Licenta', 'james.thomas@example.com', 1, 106),
    ('Isabella', 'Jackson', 'Master', 'isabella.jackson@example.com', 2, 204),
    ('Noah', 'White', 'Master', 'noah.white@example.com', 2, 205),
    ('Ava', 'Harris', 'Licenta', 'ava.harris@example.com', 1, 107),
    ('Liam', 'Clark', 'Licenta', 'liam.clark@example.com', 1, 108),
    ('Mia', 'Lewis', 'Master', 'mia.lewis@example.com', 2, 206),
    ('Ethan', 'Robinson', 'Master', 'ethan.robinson@example.com', 2, 207);

-- Insert into student_discipline (junction table)
INSERT INTO student_lecture (student_id, lecture_id)
VALUES
    ((SELECT id FROM student WHERE email = 'alice.johnson@example.com'), 101), -- Alice enrolls in Data Structures
    ((SELECT id FROM student WHERE email = 'alice.johnson@example.com'), 102), -- Alice enrolls in Algorithms
    ((SELECT id FROM student WHERE email = 'bob.brown@example.com'), 101), -- Bob enrolls in Data Structures
    ((SELECT id FROM student WHERE email = 'charlie.davis@example.com'), 103), -- Charlie enrolls in Linear Algebra
    ((SELECT id FROM student WHERE email = 'charlie.davis@example.com'), 104), -- Charlie enrolls in Machine Learning
    ((SELECT id FROM student WHERE email = 'david.garcia@example.com'), 106), -- David enrolls in Organic Chemistry
    ((SELECT id FROM student WHERE email = 'sophia.martinez@example.com'), 104), -- Sophia enrolls in Machine Learning
    ((SELECT id FROM student WHERE email = 'liam.hernandez@example.com'), 105), -- Liam enrolls in Quantum Mechanics
    ((SELECT id FROM student WHERE email = 'olivia.lopez@example.com'), 105), -- Olivia enrolls in Quantum Mechanics
    ((SELECT id FROM student WHERE email = 'emma.anderson@example.com'), 101), -- Emma enrolls in Data Structures
    ((SELECT id FROM student WHERE email = 'emma.anderson@example.com'), 102), -- Emma enrolls in Algorithms
    ((SELECT id FROM student WHERE email = 'james.thomas@example.com'), 103), -- James enrolls in Linear Algebra
    ((SELECT id FROM student WHERE email = 'isabella.jackson@example.com'), 104), -- Isabella enrolls in Machine Learning
    ((SELECT id FROM student WHERE email = 'noah.white@example.com'), 105), -- Noah enrolls in Quantum Mechanics
    ((SELECT id FROM student WHERE email = 'ava.harris@example.com'), 106), -- Ava enrolls in Organic Chemistry
    ((SELECT id FROM student WHERE email = 'liam.clark@example.com'), 108), -- Liam enrolls in Software Engineering
    ((SELECT id FROM student WHERE email = 'mia.lewis@example.com'), 107), -- Mia enrolls in Cell Biology
    ((SELECT id FROM student WHERE email = 'ethan.robinson@example.com'), 109); -- Ethan enrolls in Advanced Mathematics
