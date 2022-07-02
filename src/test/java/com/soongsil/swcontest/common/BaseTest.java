package com.soongsil.swcontest.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.enums.RoleType;
import com.soongsil.swcontest.jwt.JwtTokenProvider;
import com.soongsil.swcontest.repository.UserInfoRepository;
import com.soongsil.swcontest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class BaseTest {
    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public UserService userService;

    @Autowired
    public UserInfoRepository userInfoRepository;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public JwtTokenProvider jwtTokenProvider;

    public String email = "testuser1@naver.com";
    public String password = "password";
    public String username = "testusername1";
    public RoleType role = RoleType.USER;

    public void makeUser() {
        userService.signUp(email, password, username, role);
    }

    public SignInResponseDto signInUser() {
        return userService.signIn(email, password);
    }
}
