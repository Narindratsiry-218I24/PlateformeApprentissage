package com.plateforme_etudiant.demo.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping("/test-login")
    @ResponseBody
    public String test() {
        return "Application fonctionne correctement !";
    }
}