package com.soongsil.swcontest.repository;

import com.soongsil.swcontest.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    Page<Log> findAll(Pageable pageable);
}
