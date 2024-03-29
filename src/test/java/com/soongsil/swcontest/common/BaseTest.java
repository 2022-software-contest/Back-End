package com.soongsil.swcontest.common;

import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.swcontest.controller.ImageController;
import com.soongsil.swcontest.dto.response.AddProtegeResponseDto;
import com.soongsil.swcontest.dto.response.SignInResponseDto;
import com.soongsil.swcontest.enums.RoleType;
import com.soongsil.swcontest.jwt.JwtTokenProvider;
import com.soongsil.swcontest.repository.*;
import com.soongsil.swcontest.schedule.JobService;
import com.soongsil.swcontest.service.*;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    public ImageController imageController;

    @Autowired
    public ImageService imageService;

    @Autowired
    public ImageRepository imageRepository;

    @MockBean
    public AmazonS3Client amazonS3Client;

    @Autowired
    public GuardianProtegeService guardianProtegeService;
    @Autowired
    public GuardianProtegeRepository guardianProtegeRepository;

    @Autowired
    public PillService pillService;

    @Autowired
    public PillRepository pillRepository;

    @Autowired
    public PushTokenService pushTokenService;

    @Autowired
    public PushTokenRepository pushTokenRepository;

    @MockBean
    public FirebaseCloudMessageService firebaseCloudMessageService;

    @Autowired
    public JobService jobService;

    @Autowired
    public Scheduler scheduler;

    @PersistenceContext
    public EntityManager em;

    public String email = "testuser1@naver.com";
    public String password = "password";
    public String username = "testusername1";
    public RoleType role = RoleType.USER;

    public String phoneNumber = "01011112222";

    public Boolean isGuardian = false;

    public void makeUser(String email) {
        userService.signUp(email, password, username, phoneNumber, isGuardian, role);
    }

    public void makeUser(String email, Boolean isGuardian) {
        userService.signUp(email, password, username, phoneNumber, isGuardian, role);
    }

    public SignInResponseDto signInUser(String email) {
        return userService.signIn(email, password);
    }

    public List<MockMultipartFile> makeTwoImages() {
        List<MockMultipartFile> mockMultipartFiles = new ArrayList<>();
        MockMultipartFile image1 = new MockMultipartFile("image", "testImage1.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image2 = new MockMultipartFile("image", "testImage2.jpg", MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE.getBytes(StandardCharsets.UTF_8));
        mockMultipartFiles.add(image1);
        mockMultipartFiles.add(image2);
        return mockMultipartFiles;
    }

    public AddProtegeResponseDto addProtege(String guardianEmail, String protegeEmail, String protegePhoneNumber) {
        return guardianProtegeService.addProtege(guardianEmail, protegeEmail, protegePhoneNumber);
    }
}
