package me.mmtr.vitalis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class PatientHomeController {

    @GetMapping("/home")
    public String patient() {
        return "patient-home";
    }
}
