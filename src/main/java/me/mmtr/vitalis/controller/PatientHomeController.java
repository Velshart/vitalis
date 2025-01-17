package me.mmtr.vitalis.controller;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/patient")
public class PatientHomeController {
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public PatientHomeController(AppointmentService appointmentService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/all-appointments")
    public String all(Model model, Principal principal) {
        User patient = userRepository.findByUsername(principal.getName());

        model.addAttribute("scheduledAppointments", getAllAppointments(patient, AppointmentStatus.SCHEDULED));

        model.addAttribute("pendingAppointments", getAllAppointments(patient, AppointmentStatus.PENDING));

        List<Appointment> otherAppointments = appointmentService.getAll().stream()
                .filter(appointment -> Objects.equals(appointment.getPatient().getId(), patient.getId()))
                .filter(appointment -> !appointment.getStatus().equals(AppointmentStatus.PENDING) &&
                        !appointment.getStatus().equals(AppointmentStatus.SCHEDULED))
                .toList();

        model.addAttribute("otherAppointments", otherAppointments);

        return "patient-appointments";
    }

    @GetMapping("/appointment-patient-view/{id}")
    public String showPatientView(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.findById(id).orElseThrow();
        model.addAttribute("appointment", appointment);

        return "appointment-patient-view";
    }

    @GetMapping("/home")
    public String patient(Model model, Principal principal) {
        model.addAttribute("name", principal.getName());
        return "patient-home";
    }

    private List<Appointment> getAllAppointments(User patient, AppointmentStatus status) {
        return appointmentService.getAll().stream()
                .filter(appointment -> Objects.equals(appointment.getPatient().getId(), patient.getId()))
                .filter(appointment -> appointment.getStatus().equals(status))
                .toList();
    }
}
