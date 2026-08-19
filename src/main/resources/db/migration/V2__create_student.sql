CREATE TABLE student (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    coach_id BIGINT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    lichess_username VARCHAR(100) UNIQUE,
    chess_com_username VARCHAR(100) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,

    CONSTRAINT fk_student_coach FOREIGN KEY (coach_id) REFERENCES coach(id) ON DELETE SET NULL
);

CREATE INDEX idx_student_coach_id ON student(coach_id);