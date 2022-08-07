package com.soongsil.swcontest.service;

import com.soongsil.swcontest.entity.Log;
import com.soongsil.swcontest.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogService {
    private final LogRepository logRepository;

    public Page<Log> getAllLogs(Pageable pageable) {
        return logRepository.findAll(pageable);
    }
}
