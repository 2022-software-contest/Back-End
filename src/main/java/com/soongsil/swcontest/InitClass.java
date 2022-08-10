package com.soongsil.swcontest;

import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.repository.PillRepository;
import com.soongsil.swcontest.repository.PushTokenRepository;
import com.soongsil.swcontest.schedule.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitClass {
    private final PillRepository pillRepository;
    private final PushTokenRepository pushTokenRepository;
    private final JobService jobService;
    private final Scheduler scheduler;

    @PostConstruct
    @Scheduled(cron = "0 0 0 * * *")
    public void init() throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                String jobName = jobKey.getName();
                jobService.deleteJob(scheduler, jobName);
            }
        }

        List<Pill> pills = pillRepository.findAll();
        for (Pill pill : pills) {
            if(pill.getTime().isBefore(LocalDateTime.now())) {
                pillRepository.delete(pill);
            }
            else {
                PushToken pushToken = pushTokenRepository.findByUserInfo(pill.getUserInfo());
                LocalDateTime convertToday = LocalDateTime.of(
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonth(),
                        LocalDateTime.now().getDayOfMonth(),
                        pill.getTime().getHour(),
                        pill.getTime().getMinute(),
                        pill.getTime().getSecond());
                jobService.registerJob(scheduler, pill.getId().toString(), pushToken.getToken(), convertToday);
            }
        }
    }
}
