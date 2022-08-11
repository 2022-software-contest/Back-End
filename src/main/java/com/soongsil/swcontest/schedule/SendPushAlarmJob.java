package com.soongsil.swcontest.schedule;

import com.soongsil.swcontest.repository.PillRepository;
import com.soongsil.swcontest.service.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendPushAlarmJob implements Job {
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final PillRepository pillRepository;

    @Override
    public void execute(JobExecutionContext context) {
        firebaseCloudMessageService.sendMessageTo(
                context.getJobDetail().getDescription(),
                "약먹을 시간",
                "꿀꺽");
        pillRepository.deleteById(Long.valueOf(context.getJobDetail().getKey().getName()));
    }
}
