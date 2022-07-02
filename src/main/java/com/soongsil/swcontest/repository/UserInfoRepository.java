package com.soongsil.swcontest.repository;

import com.soongsil.swcontest.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    boolean existsByEmail(String email);
    UserInfo findByEmail(String email);
}