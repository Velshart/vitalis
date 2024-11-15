package me.mmtr.vitalis.controller;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import me.mmtr.vitalis.service.interfaces.ClinicService;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class DoctorHomeController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final ClinicService clinicService;

    public DoctorHomeController(AppointmentService appointmentService, UserService userService, ClinicService clinicService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.clinicService = clinicService;
    }

    @GetMapping("/owned-clinics")
    public String ownedClinics(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());

        model.addAttribute("clinicsOwned", clinicService.getAll()
                .stream()
                .filter(clinic -> clinic.getOwner() != null && clinic.getOwner().equals(user))
                .collect(Collectors.toList())
        );
        return "clinics-owned";
    }

    @GetMapping("/clinics-employed")
    public String employedClinics(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());

        model.addAttribute("clinicsEmployed", clinicService.getAll()
                .stream()
                .filter(clinic -> clinic.getEmployees().contains(user))
                .collect(Collectors.toList())
        );
        return "clinics-employed";
    }

    @GetMapping("/clinic-employee-view/{id}")
    public String showEmployeeView(@PathVariable Long id, Model model) {
        Clinic clinic = clinicService.getById(id).orElseThrow();
        model.addAttribute("clinic", clinic);
        model.addAttribute("specializations", clinic.getSpecializations());

        return "clinic-employee-view";
    }

    @GetMapping("/home")
    public String doctor(Principal principal, Model model) {
        model.addAttribute("scheduledAppointments", getAppointments(principal, AppointmentStatus.SCHEDULED));
        return "doctor-home";
    }

    @GetMapping("/pending-appointments")
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
