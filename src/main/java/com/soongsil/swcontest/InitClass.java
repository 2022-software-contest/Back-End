package com.soongsil.swcontest;

import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.repository.PillRepository;
import com.soongsil.swcontest.schedule.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitClass {
    private final PillRepository pillRepository;
    private final JobService jobService;
    private final Scheduler scheduler;

    @PostConstruct
    @Scheduled(cron = "0 4/5 * * * *")
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
        }

        //userInfoId로 나누고, 시간으로 나눈다음 같은 시간이라면 약이름 set에 넣기
        HashMap<UserInfo, HashMap<LocalDateTime, HashSet<String>>> map = new HashMap<>();
        for (Pill pill : pills) {
            UserInfo firstKey = pill.getUserInfo();
            LocalDateTime secondKey = LocalDateTime.of(
                    LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(),
                    pill.getTime().getHour(),
                    pill.getTime().getMinute(),
                    pill.getTime().getSecond());
            String pillName = pill.getPillName();

            map.put(firstKey, map.getOrDefault(firstKey, new HashMap<>()));

            map.get(firstKey).put(secondKey, map.get(firstKey).getOrDefault(secondKey, new HashSet<>()));
            map.get(firstKey).get(secondKey).add(pillName);
        }

        log.info("{}에 작동했다!", LocalDateTime.now());
        for (UserInfo userInfo : map.keySet()) {
            for (LocalDateTime dateTime : map.get(userInfo).keySet()) {
                System.out.println(userInfo.getEmail()+ " " + dateTime + " " + map.get(userInfo).get(dateTime));
            }
        }


        for (UserInfo userInfo : map.keySet()) {
            for (LocalDateTime dateTime : map.get(userInfo).keySet()) {
                    if (dateTime.isBefore(LocalDateTime.now().plusMinutes(5)) && dateTime.isAfter(LocalDateTime.now())) {
                        String body = map.get(userInfo).get(dateTime).toString();
                        jobService.registerJob(scheduler, userInfo.getId().toString(), body.substring(1, body.length()-1)+ "(정)을 먹을 시간 입니다.", dateTime, "eat");
                        log.info("{} 예약 완료", userInfo.getId());
                    }
            }
        }
    }
}
