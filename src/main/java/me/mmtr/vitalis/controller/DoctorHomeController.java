package me.mmtr.vitalis.controller;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorHomeController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    public DoctorHomeController(AppointmentService appointmentService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String doctor(Principal principal, Model model) {
        model.addAttribute("scheduledAppointments", getAppointments(principal, AppointmentStatus.SCHEDULED));
        return "doctor-home";
    }

    @GetMapping("/pending")
    public String pending(Principal principal, Model model) {
        model.addAttribute("pendingAppointments", getAppointments(principal, AppointmentStatus.PENDING));
        return "doctor-pending-appointments";
    }

    @GetMapping("/appointment-doctor-view/{id}")
    public String appointmentDoctorView(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.findById(id).orElseThrow());
        model.addAttribute("statuses", Arrays.stream(AppointmentStatus.values())
                .filter(status -> status != AppointmentStatus.PENDING));

        return "appointment-doctor-view";
    }

    @PostMapping("/appointment-doctor-view/{id}")
    public String appointmentDoctorViewSubmit(@RequestParam(name = "appointmentStatus") AppointmentStatus appointmentStatus,
                                              @RequestParam(name = "reasonForChange") String reasonForChange,
                                              @PathVariable Long id) {

        Appointment appointment = appointmentService.findById(id).orElseThrow();
        appointment.setStatus(appointmentStatus);
        appointment.setReasonForChange(reasonForChange);

        appointmentService.saveAppointment(appointment);
        return "redirect:/doctor/home";
    }

    private List<Appointment> getAppointments(Principal principal, AppointmentStatus status) {
        return appointmentService.getAll().stream()
                .filter(appointment -> appointment.getDoctor().equals(userService.findUserByUsername(principal.getName())))
                .filter(appointment -> appointment.getStatus().equals(status))
                .toList();
    }
}
