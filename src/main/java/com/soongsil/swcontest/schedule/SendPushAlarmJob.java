package com.soongsil.swcontest.schedule;

import com.soongsil.swcontest.entity.Pill;
import com.soongsil.swcontest.entity.PushToken;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.repository.PillRepository;
import com.soongsil.swcontest.repository.PushTokenRepository;
import com.soongsil.swcontest.repository.UserInfoRepository;
import com.soongsil.swcontest.service.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendPushAlarmJob implements Job {
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final PillRepository pillRepository;

    private final UserInfoRepository userInfoRepository;
    private final PushTokenRepository pushTokenRepository;

    @Override
    public void execute(JobExecutionContext context) {
        String[] split_id = context.getJobDetail().getKey().toString().split("DEFAULT.");
        if(context.getTrigger().getDescription().equals("eat")) {
            Optional<UserInfo> userInfo = userInfoRepository.findById(Long.valueOf(split_id[1]));
            if (userInfo.isPresent()) {
                PushToken pushToken = pushTokenRepository.findByUserInfo(userInfo.get());
                if (pushToken!=null) {
                    firebaseCloudMessageService.sendMessageTo(
                            pushToken.getToken(),
                            "약먹을 시간",
                            context.getJobDetail().getDescription(),
                            context.getTrigger().getDescription());
                }
                List<Pill> pills = pillRepository.findByUserInfo(userInfo.get());
                for (Pill pill : pills) {
                    if(pill.getTime().isBefore(LocalDateTime.now())) {
                        pillRepository.delete(pill);
                    }
                }
            }
        }
    }
}
