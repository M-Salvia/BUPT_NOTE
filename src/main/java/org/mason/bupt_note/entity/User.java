package org.mason.bupt_note.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//Entity注解表示这是一个实体类
@Entity
//Table注解表示这是一个数据库表
@Setter
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    //将枚举值以string而不是数字的形式存在数据库中
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = true, nullable = false, length = 10)
    private String studentId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 255)
    private String avatarUrl;

    @Column(length = 255)
    private String backgroundImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Column(length = 255)
    private String authProviderId;

    // GitHub 相关字段
    @Column(length = 255)
    private String githubId;

    @Column(length = 255)
    private String githubUsername;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


//    public String getPassword() {
//        return password;
//    }
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setStudentId(String studentId) {
//        this.studentId = studentId;
//    }
//
//    public void setAuthProvider(String authProvider) {
//        this.authProvider = AuthProvider.valueOf(authProvider);
//    }
//
//    public void setRole(String user) {
//        this.role = Role.valueOf(user);
//    }
//
//    public void setCreatedAt(LocalDateTime now) {
//        this.createdAt = now;
//    }
//
//    public void setUpdatedAt(LocalDateTime now) {
//        this.updatedAt = now;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public enum AuthProvider {
        PASSWORD, GITHUB
    }

    public enum Role {
        USER, ADMIN
    }

}
