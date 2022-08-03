package com.soongsil.swcontest.repository;

import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    PushToken findByUserInfo(UserInfo userInfo);
    PushToken findByToken(String token);
}
