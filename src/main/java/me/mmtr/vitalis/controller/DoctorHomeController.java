package me.mmtr.vitalis.controller;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.Invitation;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.data.enums.InvitationStatus;
import me.mmtr.vitalis.data.enums.Specialization;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import me.mmtr.vitalis.service.interfaces.ClinicService;
import me.mmtr.vitalis.service.interfaces.InvitationService;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class DoctorHomeController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final ClinicService clinicService;
    private final InvitationService invitationService;
    private final UserRepository userRepository;

    public DoctorHomeController(AppointmentService appointmentService,
                                UserService userService,
                                ClinicService clinicService,
                                InvitationService invitationService,
                                UserRepository userRepository) {

        this.appointmentService = appointmentService;
        this.userService = userService;
        this.clinicService = clinicService;
        this.invitationService = invitationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/scheduled-appointments")
    public String scheduledAppointments(Principal principal, Model model) {
        List<Appointment> scheduledAppointmentsForToday = getAppointments(principal, AppointmentStatus.SCHEDULED)
                .stream()
                .filter(appointment -> appointment.getDate().equals(LocalDate.now()))
                .toList();

        List<Appointment> otherAppointments = getAppointments(principal, AppointmentStatus.SCHEDULED)
                .stream()
                .filter(appointment -> !appointment.getDate().equals(LocalDate.now()))
                .toList();

        model.addAttribute("scheduledAppointmentsForToday", scheduledAppointmentsForToday);
        model.addAttribute("otherAppointments", otherAppointments);
        return "doctor-scheduled-appointments";
    }

    @GetMapping("/choose-specializations")
    public String chooseSpecializations(Model model, Principal principal) {
        model.addAttribute("doctor", userService.findUserByUsername(principal.getName()));

        model.addAttribute("specializations", Arrays.stream(Specialization.values()));
        return "doctor-specializations-choose";
    }

    @PostMapping("/choose-specializations")
    public String chooseSpecializations(@ModelAttribute User doctor, Principal principal) {
        User currentDoctor = userService.findUserByUsername(principal.getName());
        currentDoctor.setSpecializations(doctor.getSpecializations());

        userRepository.save(currentDoctor);

        return "redirect:/doctor/home";
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
        model.addAttribute("name", principal.getName());

        model.addAttribute("scheduledAppointments", getAppointments(principal, AppointmentStatus.SCHEDULED)
                .stream()
                .sorted(Comparator.comparing(Appointment::getDate).thenComparing(Appointment::getTime)).toList());
        return "doctor-home";
    }

    @GetMapping("/pending-appointments")
    public String pending(Principal principal, Model model) {
        model.addAttribute("pendingAppointments", getAppointments(principal, AppointmentStatus.PENDING));
        return "doctor-pending-appointments";
    }

    @GetMapping("/pending-invitations")
    public String pendingInvitations(Principal principal, Model model) {
        model.addAttribute("pendingInvitations", getInvitations(principal));
        return "doctor-pending-invitations";
    }

    @GetMapping("/appointment-doctor-view/{id}")
    public String appointmentDoctorView(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.findById(id).orElseThrow());
        model.addAttribute("statuses", Arrays.stream(AppointmentStatus.values())
                .filter(status -> status != AppointmentStatus.PENDING && status != AppointmentStatus.EXPIRED));

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

    @PostMapping("/invitation-accept/{id}")
    public String invitationAccept(@PathVariable Long id) {
        Invitation invitation = invitationService.getById(id).orElseThrow();

        invitationService.delete(id);
        clinicService.addEmployeeToClinic(invitation.getClinic().getId(), invitation.getDoctor().getId());

        return "redirect:/doctor/pending-invitations";
    }

    @PostMapping("/invitation-deny/{id}")
    public String invitationDeny(@PathVariable Long id) {
        invitationService.delete(id);
        return "redirect:/doctor/pending-invitations";
    }

    @PostMapping("/invitation-cancel/{id}")
    public String invitationCancel(@PathVariable Long id, @RequestParam Long clinicId) {
        invitationService.delete(id);
        return "redirect:/clinic/employees/" + clinicId;
    }

    private List<Appointment> getAppointments(Principal principal, AppointmentStatus status) {
        return appointmentService.getAll().stream()
                .filter(appointment -> appointment.getDoctor().equals(userService.findUserByUsername(principal.getName())))
                .filter(appointment -> appointment.getStatus().equals(status))
                .toList();
    }

    private List<Invitation> getInvitations(Principal principal) {
        return invitationService.getAll().stream()
                .filter(invitation -> invitation.getDoctor().equals(userService.findUserByUsername(principal.getName())))
                .filter(invitation -> invitation.getStatus().equals(InvitationStatus.PENDING))
                .toList();
    }
}
