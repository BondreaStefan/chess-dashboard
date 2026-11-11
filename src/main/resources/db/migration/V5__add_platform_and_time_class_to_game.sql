ALTER TABLE game
    ADD COLUMN platform VARCHAR(20) NOT NULL DEFAULT 'OTHER',
    ADD COLUMN time_class VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN';

CREATE INDEX idx_game_student_platform_class_played
    ON game (student_id, platform, time_class, played_at);