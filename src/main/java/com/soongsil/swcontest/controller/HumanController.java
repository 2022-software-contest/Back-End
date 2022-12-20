package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.service.HumanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/human")
@RequiredArgsConstructor
@Slf4j
public class HumanController {
    private final HumanService humanService;

    @GetMapping("/decrease")
    public String decreaseMoney(@RequestParam(value = "name") String name, @RequestParam(value = "money") int money) {
        String result;
        try{
            humanService.decreaseMoney(name, money);
            result =  "현재 남은돈 : " + humanService.currentMoney(name);
        } catch (Exception e) {
            log.info(e.toString());
            result = "에러났어";
        }
        log.info(result);
        return result;
    }
}