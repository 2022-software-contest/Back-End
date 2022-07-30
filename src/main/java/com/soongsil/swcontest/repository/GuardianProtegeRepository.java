package com.soongsil.swcontest.repository;

import com.soongsil.swcontest.entity.GuardianProtege;
import com.soongsil.swcontest.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuardianProtegeRepository extends JpaRepository<GuardianProtege, Long> {
    List<GuardianProtege> findAllByGuardian(UserInfo userInfo);
}
