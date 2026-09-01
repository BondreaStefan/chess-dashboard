package com.bond.chess_dashboard.game;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "game")
class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private GameSource source;

    @Column(name = "pgn", nullable = false, columnDefinition = "TEXT")
    private String pgn;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_color", nullable = false, length = 10)
    private Color studentColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_result", nullable = false, length = 10)
    private GameResult studentResult;

    @Column(name = "played_at")
    private OffsetDateTime playedAt;

    @Column(name = "opponent_name", length = 255)
    private String opponentName;

    @Column(name = "result", length = 7)
    private String result;

    @Column(name = "eco_code", length = 3)
    private String ecoCode;

    @Column(name = "time_control", length = 50)
    private String timeControl;

    @Column(name = "white_elo")
    private Integer whiteElo;

    @Column(name = "black_elo")
    private Integer blackElo;

    @Column(name = "move_count")
    private Integer moveCount;

    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    @Generated(event = EventType.INSERT)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    protected Game() {
        
    }

    public Game(Long studentId, GameSource source, String pgn, Color studentColor, GameResult studentResult) {
        this.studentId = studentId;
        this.source = source;
        this.pgn = pgn;
        this.studentColor = studentColor;
        this.studentResult = studentResult;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public GameSource getSource() {
        return source;
    }

    public String getPgn() {
        return pgn;
    }

    public String getExternalId() {
        return externalId;
    }

    public Color getStudentColor() {
        return studentColor;
    }

    public GameResult getStudentResult() {
        return studentResult;
    }

    public OffsetDateTime getPlayedAt() {
        return playedAt;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getResult() {
        return result;
    }

    public String getEcoCode() {
        return ecoCode;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public Integer getWhiteElo() {
        return whiteElo;
    }

    public Integer getBlackElo() {
        return blackElo;
    }

    public Integer getMoveCount() {
        return moveCount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    void applyMetadata(ParsedGame parsedGame) {
        this.playedAt = parsedGame.playedAt();
        this.opponentName = studentColor == Color.WHITE 
                    ? parsedGame.blackName() 
                    : parsedGame.whiteName();
        this.result = parsedGame.result();
        this.ecoCode = parsedGame.ecoCode();
        this.timeControl = parsedGame.timeControl();
        this.whiteElo = parsedGame.whiteElo();
        this.blackElo = parsedGame.blackElo();
        this.moveCount = parsedGame.moveCount();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", source='" + source + '\'' +
                ", externalId='" + externalId + '\'' +
                ", studentColor='" + studentColor + '\'' +
                ", studentResult='" + studentResult + '\'' +
                ", playedAt=" + playedAt +
                ", opponentName='" + opponentName + '\'' +
                ", result='" + result + '\'' +
                ", ecoCode='" + ecoCode + '\'' +
                ", timeControl='" + timeControl + '\'' +
                ", whiteElo=" + whiteElo +
                ", blackElo=" + blackElo +
                ", moveCount=" + moveCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (!(o instanceof Game)) 
            return false;

        Game game = (Game) o;

        return id != null && id.equals(game.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}   
