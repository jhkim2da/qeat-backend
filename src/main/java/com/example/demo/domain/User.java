package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String major;

    @Column(name = "student_number", nullable = false, unique = true)
    private int studentNumber;

    private String name;

    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public Integer getGrade() {
        return grade;
    }

    public Role getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}