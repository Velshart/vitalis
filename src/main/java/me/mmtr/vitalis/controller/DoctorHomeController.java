package me.mmtr.vitalis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DoctorHomeController {

    @GetMapping("/doctor")
    public String doctor() {
        return "doctor-home";
    }
}
