package com.soongsil.swcontest.repository;

import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PillRepository extends JpaRepository<Pill,Long> {
    List<Pill> findByUserInfo(UserInfo userInfo);
}
