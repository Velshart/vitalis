package me.mmtr.vitalis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PatientHomeController {

    @GetMapping("/patient")
    public String patient() {
        return "patient-home";
    }
}
