package com.soongsil.swcontest.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class JobService {
    public Boolean registerJob(Scheduler scheduler, String id, String body, LocalDateTime time, String usage) {
        boolean result = false;
        try {
            JobDetail jobDetail = JobBuilder.newJob(SendPushAlarmJob.class)
                    .withIdentity(id).withDescription(body).build();
            result = setJobSchedule(scheduler, jobDetail, id, body, time, usage);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("스케쥴 성공 "+ id + "번 유저");
        return result;
    }

    public void deleteJob(Scheduler scheduler, String id) {
        try {
            scheduler.unscheduleJob(new TriggerKey(id));
            log.info("스케쥴 삭제 완료 id : {}", id);
        }
        catch (Exception e) {
            log.error("[[ERROR]] 잡 삭제 에러 !!! | jobId: {}| msg: {}", id, e.getMessage());
        }
    }

    public Boolean setJobSchedule(Scheduler scheduler, JobDetail jobDetail, String id, String body, LocalDateTime time, String usage) {
        try {
            String cronExpression = time.format(DateTimeFormatter.ofPattern("ss mm HH dd MM ? yyyy"));
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id).withDescription(usage)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            scheduler.scheduleJob(jobDetail, trigger);
            log.info(id +" 유저 에게 " + time + " 에 푸시알림을 보냅니다.");
        }
        catch (SchedulerException e) {
            log.error(
                    "[[ERROR]] 잡 추가 에러 !!! | jobId: {} | msg: {}",
                    id,
                    e.getMessage()
            );
            return false;
        }
        return true;
    }
}
