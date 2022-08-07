package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.entity.Log;
import com.soongsil.swcontest.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping("/logs")
    public Page<Log> getAllLogs(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return logService.getAllLogs(pageable);
    }
}
