package me.mmtr.vitalis.controller;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.enums.Specialization;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import me.mmtr.vitalis.service.interfaces.ClinicService;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final ClinicService clinicService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService,
                                 UserRepository userRepository, ClinicService clinicService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.clinicService = clinicService;
        this.userService = userService;
    }

    @GetMapping("/choose-clinic")
    public String addAppointment(@ModelAttribute Appointment appointment, Model model) {
        List<Clinic> clinics = clinicService.getAll();
        Map<String, String> clinicSpecializations = new HashMap<>();

        for (Clinic clinic : clinics) {
            String specializations = clinic.getSpecializations().stream()
                    .map(Specialization::getName)
                    .collect(Collectors.joining(", "));

            clinicSpecializations.put(clinic.getName(), specializations);
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("clinics", clinics);
        model.addAttribute("clinicSpecializations", clinicSpecializations);
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

    @GetMapping("/add")
    public String addAppointment(Model model) {
        Appointment appointment = (Appointment) model.asMap().get("appointment");

        List<User> doctors = userRepository.findAll().stream()
                .filter(User::getIsDoctor)
                .filter(user -> appointment.getClinic() != null && (appointment.getClinic().getEmployees().contains(user) ||
                        appointment.getClinic().getOwner().equals(user)))
                .toList();

        Map<String, String> doctorSpecializations = new HashMap<>();

        for (User doctor : doctors) {
            String specializations = doctor.getSpecializations().stream()
                    .map(Specialization::getName)
                    .collect(Collectors.joining(", "));
            doctorSpecializations.put(doctor.getUsername(), specializations);
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctors);
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("doctorSpecializations", doctorSpecializations);
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
        return "redirect:/patient/home";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        Optional<Appointment> appointmentOptional = appointmentService.findById(id);
        if (appointmentOptional.isEmpty()) {
            return "redirect:/home";
        }
        model.addAttribute("note", appointmentOptional.get());
        return "appointment";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Appointment appointment) {
        appointmentService.saveAppointment(appointment);
        return "redirect:/patient/home";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        appointmentService.delete(id);
        return "redirect:/patient/all-appointments";
    }

    @GetMapping("/confirm-delete/{appointmentId}")
    public String deleteConfirm(@PathVariable String appointmentId) {
        return "appointment-delete-confirm";
    }

    @GetMapping("/exists")
    @ResponseBody
    public boolean checkAppointmentExists(@RequestParam Long doctorId,
                                          @RequestParam LocalDate date,
                                          @RequestParam LocalTime time) {
        return appointmentService.existsByDoctorAndDateAndTime(doctorId, date, time);
    }
}