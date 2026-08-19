package com.bond.chess_dashboard.student;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "coach_id")
    private Long coachId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "lichess_username", length = 100, unique = true)
    private String lichessUsername;

    @Column(name = "chess_com_username", length = 100, unique = true)
    private String chessComUsername;

    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    @Generated(event = EventType.INSERT) 
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    protected Student() {

    }

    public Student(String firstName, String lastName, String email, Long coachId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.coachId = coachId;
    }

     public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public String getLichessUsername() {
        return lichessUsername;
    }

    public void setLichessUsername(String lichessUsername) {
        this.lichessUsername = lichessUsername;
    }

    public String getChessComUsername() {
        return chessComUsername;
    }

    public void setChessComUsername(String chessComUsername) {
        this.chessComUsername = chessComUsername;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", coachId=" + coachId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", lichessUsername='" + lichessUsername + '\'' +
                ", chessComUsername='" + chessComUsername + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (!(o instanceof Student)) 
            return false;

        Student student = (Student) o;

        return id != null && id.equals(student.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
