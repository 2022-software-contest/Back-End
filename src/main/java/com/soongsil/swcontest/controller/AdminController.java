package com.soongsil.swcontest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping("/v1/index")
    public String index() {
        return "html/index.html";
    }
}
