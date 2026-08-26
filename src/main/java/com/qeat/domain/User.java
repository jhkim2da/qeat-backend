package com.qeat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String major;

    @Column(name = "student_number", nullable = false, unique = true)
    private String studentNumber;

    private String name;

    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {
    }

    public static User create(String studentNumber, String name, String major, Integer grade, Role role) {
        User user = new User();
        user.studentNumber = studentNumber;
        user.name = name;
        user.major = major;
        user.grade = grade;
        user.role = role;
        return user;
    }

    public void changeRole(Role role) {
        this.role = role;
    }
}
