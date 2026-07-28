package com.qeat.dto.auth;

import com.qeat.domain.Role;
import com.qeat.domain.User;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private Long id;
    private String studentNumber;
    private String name;
    private String major;
    private Integer grade;
    private Role role;

    public LoginResponseDto(Long id, String studentNumber, String name, String major, Integer grade, Role role) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.name = name;
        this.major = major;
        this.grade = grade;
        this.role = role;
    }

    public static LoginResponseDto from(User user) {
        return new LoginResponseDto(
                user.getId(),
                user.getStudentNumber(),
                user.getName(),
                user.getMajor(),
                user.getGrade(),
                user.getRole()
        );
    }
}