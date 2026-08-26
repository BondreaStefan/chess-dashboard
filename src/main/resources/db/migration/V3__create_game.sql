CREATE TABLE game (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id BIGINT NOT NULL,
    source VARCHAR(20) NOT NULL,
    pgn TEXT NOT NULL,
    external_id VARCHAR(100),
    student_color VARCHAR(10) NOT NULL,
    student_result VARCHAR(10) NOT NULL,
    played_at TIMESTAMPTZ,
    opponent_name VARCHAR(255),
    result VARCHAR(7),
    eco_code VARCHAR(3),
    time_control VARCHAR(50),
    white_elo INTEGER,
    black_elo INTEGER,
    move_count INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,


    CONSTRAINT fk_game_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    CONSTRAINT uq_game_source_external UNIQUE (source, external_id)
);

CREATE INDEX idx_game_student_played ON game(student_id, played_at DESC);
