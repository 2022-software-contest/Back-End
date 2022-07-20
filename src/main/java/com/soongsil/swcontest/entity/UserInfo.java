package com.soongsil.swcontest.entity;

import com.soongsil.swcontest.enums.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class UserInfo extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String username;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private String refreshToken;

    @Cascade(value = CascadeType.ALL)
    @OneToMany(mappedBy = "userInfo")
    private List<Image> images = new ArrayList<>();

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserInfo(Long id, String email, String password, String username, RoleType role, String refreshToken) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.refreshToken = refreshToken;
    }
}
