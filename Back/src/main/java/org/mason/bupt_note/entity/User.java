package org.mason.bupt_note.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

//Entity注解表示这是一个实体类
@Entity
//Table注解表示这是一个数据库表
@Setter
@Getter
@Table(name = "users")
public class User implements Serializable {
    //将对象转换为字节流，Java的内置机制会处理序列化和反序列化的具体细节
    @Serial
    private static final long serialVersionUID = 1L;

    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", role=" + role +
                ", studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", authProvider=" + authProvider +
                ", githubId='" + githubId + '\'' +
                ", githubUsername='" + githubUsername + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

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

    // GitHub 相关字段
    @Column(length = 255)
    private String githubId;

    @Column(length = 255)
    private String githubUsername;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum AuthProvider {
        PASSWORD, GITHUB
    }

    public enum Role {
        USER, ADMIN
    }

}
