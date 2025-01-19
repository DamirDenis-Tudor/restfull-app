-- Create the "professor" table
CREATE TABLE professor
(
    professor_id     INT AUTO_INCREMENT PRIMARY KEY,
    first_name       VARCHAR(255)                                                      NOT NULL,
    last_name        VARCHAR(255)                                                      NOT NULL,
    email            VARCHAR(255)                                                      NOT NULL UNIQUE,
    affiliation      VARCHAR(255),
    grader_type      ENUM ('Asistent', 'Conferentiar', 'Profesor', 'TitularLaborator') NOT NULL,
    association_type ENUM ('Titular', 'Asociat', 'Extern')                             NOT NULL
);

-- Create the "lecture" table
CREATE TABLE lecture
(
    id               VARCHAR(255) PRIMARY KEY,
    lecture_name     VARCHAR(20)                                   NOT NULL UNIQUE,
    study_year       INT                                           NOT NULL,
    lecture_type     ENUM ('Impusa', 'Optionala', 'LiberAleasa')   NOT NULL,
    category_type    ENUM ('Domeniu', 'Specialitate', 'Adiacenta') NOT NULL,
    examination_type ENUM ('Examen', 'Cologviu')                   NOT NULL,
    professor_id     INT,
    CONSTRAINT fk_lecture_professor FOREIGN KEY (professor_id) REFERENCES professor (professor_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Create the "student" table
CREATE TABLE student
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    cycle_type    ENUM ('Licenta', 'Master'),
    email         VARCHAR(255) NOT NULL UNIQUE,
    study_year    INT          NOT NULL,
    student_group INT          NOT NULL
);

-- Create the "student_lecture" table for the many-to-many relationship
CREATE TABLE student_lecture
(
    student_id BIGINT       NOT NULL,
    lecture_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (student_id, lecture_id),
    CONSTRAINT fk_student_lecture_student FOREIGN KEY (student_id) REFERENCES student (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_student_lecture_lecture FOREIGN KEY (lecture_id) REFERENCES lecture (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
