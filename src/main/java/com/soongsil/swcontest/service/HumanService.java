package com.soongsil.swcontest.service;

import com.soongsil.swcontest.entity.Human;
import com.soongsil.swcontest.repository.HumanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HumanService {
    private final HumanRepository humanRepository;

    public int currentMoney(String name) {
        Human human = humanRepository.findByName(name);
        return human.getMoney();
    }

    @Transactional
    public int decreaseMoney(String name, int money) {
        Human human = humanRepository.findByName(name);
        human.decreaseMoney(money);
        return human.getMoney();
    }
}
