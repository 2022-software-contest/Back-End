package com.soongsil.swcontest.repository;

import com.soongsil.swcontest.entity.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface HumanRepository extends JpaRepository<Human, Long> {

//    @Lock(LockModeType.OPTIMISTIC)
    Human findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE) //여기
    @Query("select h from Human h where h.name = :name")
    Human findWithNameForUpdate(@Param("name") String name);
}
