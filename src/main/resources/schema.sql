BEGIN;

CREATE TABLE student (
    id BIGSERIAL PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text NOT NULL,
    phone_number text NOT NULL,
    date_of_birth date NOT NULL,
    gpa decimal,
    major text,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE course (
    id bigserial PRIMARY KEY,
    course_name text NOT NULL,
    department_name text NOT NULL,
    semester text NOT NULL,
    course_year int NOT NULL,
    credits int NOT NULL,
    professor_name text NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_course (
    student_id bigint REFERENCES student(id),
    course_id bigint REFERENCES course(id),
    CONSTRAINT pk_student_course PRIMARY KEY (student_id, course_id)
);

COMMIT;