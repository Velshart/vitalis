package me.mmtr.vitalis.controller;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import me.mmtr.vitalis.service.interfaces.ClinicService;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ClinicService clinicService;

    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService, UserRepository userRepository, ClinicService clinicService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.clinicService = clinicService;
    }

    @GetMapping("/all")
    public String all(Model model, Principal principal) {
        User patient = userRepository.findByUsername(principal.getName());

        List<Appointment> scheduledAppointments = appointmentService.getAll().stream()
                .filter(appointment -> Objects.equals(appointment.getPatient().getId(), patient.getId()))
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.SCHEDULED))
                .toList();

        model.addAttribute("scheduledAppointments", scheduledAppointments);

        List<Appointment> pendingAppointments = appointmentService.getAll().stream()
                .filter(appointment -> Objects.equals(appointment.getPatient().getId(), patient.getId()))
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.PENDING))
                .toList();

        model.addAttribute("pendingAppointments", pendingAppointments);

        List<Appointment> otherAppointments = appointmentService.getAll().stream()
                .filter(appointment -> Objects.equals(appointment.getPatient().getId(), patient.getId()))
                .filter(appointment -> !appointment.getStatus().equals(AppointmentStatus.PENDING) &&
                        !appointment.getStatus().equals(AppointmentStatus.SCHEDULED))
                .toList();

        model.addAttribute("otherAppointments", otherAppointments);

        return "patient-appointments";
    }

    @GetMapping("/choose-clinic")
    public String addAppointment(@ModelAttribute Appointment appointment, Model model) {

        model.addAttribute("appointment", appointment);
        model.addAttribute("clinics", clinicService.getAll());
        return "appointment-clinic-choose";
    }

    @PostMapping("/choose-clinic")
    public String chooseClinic(@ModelAttribute Appointment appointment,
                               @RequestParam Long clinicId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        User patient = userRepository.findByUsername(principal.getName());
        Clinic clinic = clinicService.getById(clinicId).orElseThrow();

        appointment.setPatient(patient);
        appointment.setClinic(clinic);

        redirectAttributes.addFlashAttribute("patient", appointment.getPatient());
        redirectAttributes.addFlashAttribute("clinic", appointment.getClinic());

        redirectAttributes.addFlashAttribute("appointment", appointment);
        return "redirect:/appointment/add";
    }

    @GetMapping("/patient-view/{id}")
    public String showPatientView(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.findById(id).orElseThrow();
        model.addAttribute("appointment", appointment);

        return "appointment-patient-view";
    }

    @GetMapping("/add")
    public String addAppointment(Model model) {

        Appointment appointment = (Appointment) model.asMap().get("appointment");

        User patient = (User) model.asMap().get("patient");
        Clinic clinic = (Clinic) model.asMap().get("clinic");

        List<User> doctors = userRepository.findAll().stream()
                .filter(User::getIsDoctor)
                .filter(user -> appointment.getClinic().getEmployees().contains(user) ||
                        appointment.getClinic().getOwner().equals(user))
                .toList();

        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctors);

        model.addAttribute("patient", patient);
        model.addAttribute("clinic", clinic);

        return "appointment";
    }

    @PostMapping("/add")
    public String saveAppointment(@ModelAttribute Appointment appointment,
                                  @RequestParam Long doctorId,
                                  @RequestParam Long patientId,
                                  @RequestParam Long clinicId) {

        User doctor = userRepository.findById(doctorId).orElseThrow();
        appointment.setDoctor(doctor);

        User patient = userRepository.findById(patientId).orElseThrow();
        Clinic clinic = clinicService.getById(clinicId).orElseThrow();

        appointment.setPatient(patient);
        appointment.setClinic(clinic);

        appointmentService.saveAppointment(appointment);
        return "redirect:/patient";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        Optional<Appointment> noteOptional = appointmentService.findById(id);
        if (noteOptional.isEmpty()) {
            return "redirect:/home";
        }
        model.addAttribute("note", noteOptional.get());
        return "appointment";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Appointment appointment) {
        appointmentService.saveAppointment(appointment);
        return "redirect:/patient-home";
    }


    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        appointmentService.delete(id);
        return "redirect:/patient-home";
    }
}
