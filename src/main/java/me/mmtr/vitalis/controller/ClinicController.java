package me.mmtr.vitalis.controller;

import jakarta.validation.Valid;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.Invitation;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.enums.InvitationStatus;
import me.mmtr.vitalis.data.enums.Specialization;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import me.mmtr.vitalis.service.interfaces.ClinicService;
import me.mmtr.vitalis.service.interfaces.InvitationService;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/clinic")
public class ClinicController {

    private final ClinicService clinicService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final InvitationService invitationService;
    private final AppointmentService appointmentService;


    public ClinicController(ClinicService clinicService,
                            UserService userService,
                            UserRepository userRepository,
                            InvitationService invitationService,
                            AppointmentService appointmentService) {
        this.clinicService = clinicService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.invitationService = invitationService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/add")
    public String add(@ModelAttribute(name = "clinic") Clinic clinic, Model model, Principal principal) {
        clinic.setOwner(userService.findUserByUsername(principal.getName()));

        model.addAttribute("specializations", Specialization.values());

        model.addAttribute("doctors", userRepository
                .findAll()
                .stream()
                .filter(User::getIsDoctor)
                .filter(user -> !user.getUsername().equals(principal.getName()))
                .collect(Collectors.toList())
        );
        return "clinic";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute @Valid Clinic clinic,
                      BindingResult bindingResult,
                      Model model,
                      Principal principal
    ) {
        clinic.setOwner(userService.findUserByUsername(principal.getName()));

        if (bindingResult.hasErrors()) {
            model.addAttribute("clinic", clinic);
            model.addAttribute("specializations", Specialization.values());

            return "clinic";
        }

        this.clinicService.saveOrUpdate(clinic);
        return "redirect:/doctor/owned-clinics";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        Optional<Clinic> clinicOptional = clinicService.getById(id);
        Clinic clinic = clinicOptional.orElseThrow();

        if (clinicOptional.isEmpty()) {
            return "redirect:/doctor/owned-clinics";
        }
        model.addAttribute("clinic", clinicOptional.get());
        model.addAttribute("specializations", Specialization.values());
        model.addAttribute("encodedURL", URLEncoder.encode(
                "https://www.google.com/maps/embed/v1/place?key=AIzaSyBWVBP2SeZv0I1Bk4XHdf1XgdvyoDnWG9c&q="
                        + clinic.getStreet() + "+" + clinic.getBuildingNumber() + "," + clinic.getCity(),
                StandardCharsets.UTF_8));
        return "clinic-doctor-view";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute @Valid Clinic clinic,
                         BindingResult bindingResult,
                         Model model,
                         Principal principal) {

        clinic.setOwner(userService.findUserByUsername(principal.getName()));

        if (bindingResult.hasErrors()) {
            model.addAttribute("clinic", clinic);
            model.addAttribute("specializations", Specialization.values());

            return "clinic-doctor-view";
        }

        this.clinicService.saveOrUpdate(clinic);
        return "redirect:/doctor/owned-clinics";
    }

    @PostMapping("/employees/add/{employeeId}")
    public String addEmployee(@ModelAttribute Invitation invitation,
                              @PathVariable Long employeeId,
                              @RequestParam Long clinicId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {

        updateEmployeeList(invitation, clinicId, employeeId, redirectAttributes, principal, true);

        return "redirect:/clinic/employees/" + clinicId;
    }
    @GetMapping("/employee/confirm-remove")
    public String confirmDeleteEmployee(@RequestParam Long employeeId, @RequestParam Long clinicId, Model model) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("clinicId", clinicId);
        return "clinic-emp-delete-confirm";
    }
    @PostMapping("/employees/remove/{employeeId}")
    public String removeEmployee(@ModelAttribute Invitation invitation,
                                 @PathVariable Long employeeId,
                                 @RequestParam Long clinicId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        updateEmployeeList(invitation, clinicId, employeeId, redirectAttributes, principal, false);

        return "redirect:/clinic/employees/" + clinicId;
    }

    @GetMapping("/employees/{id}")
    public String showEmployees(@ModelAttribute Invitation invitation,
                                @PathVariable Long id,
                                Model model,
                                Principal principal) {

        Clinic clinic = clinicService.getById(id).orElseThrow();

        model.addAttribute("clinic", clinic);
        model.addAttribute("employees", clinic.getEmployees());
        model.addAttribute("others", getUnemployedDoctors(userRepository.findAll(), clinic.getId(), principal));
        model.addAttribute("invitation", invitation);
        model.addAttribute("pendingInvitations", getPendingInvitations(clinic.getId()));

        return "clinic-employees";
    }
    @GetMapping("/confirm-delete")
    public String confirmDelete(@RequestParam Long clinicId, Model model) {
        model.addAttribute("clinicId", clinicId);
        return "clinic-delete-confirm";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        this.invitationService.deleteByClinicId(id);
        this.appointmentService.deleteByClinicId(id);
        this.clinicService.delete(id);
        return "redirect:/doctor/owned-clinics";

    }

    private void updateEmployeeList(@ModelAttribute Invitation invitation,
                                    Long clinicId,
                                    Long employeeId,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal,
                                    boolean add) {
        Clinic clinic = clinicService.getById(clinicId).orElseThrow(() ->
                new RuntimeException("Clinic not found with id: " + clinicId));
        User user = userRepository.findById(employeeId).orElseThrow(() ->
                new RuntimeException("User not found with id: " + employeeId));

        if (add) {
            invitation.setClinic(clinic);
            invitation.setDoctor(user);
            invitation.setDate(java.time.LocalDate.now());
            invitation.setTime(java.time.LocalTime.now());

            invitationService.saveInvitation(invitation);
        } else {
            invitationService.delete(invitation.getId());
            clinicService.removeEmployeeFromClinic(clinicId, employeeId);
        }
        addRedirectAttributes(redirectAttributes, clinicId, principal);
    }

    private void addRedirectAttributes(RedirectAttributes redirectAttributes, Long clinicId, Principal principal) {
        Clinic clinic = clinicService.getById(clinicId).orElseThrow();

        redirectAttributes.addFlashAttribute("employees", clinic.getEmployees());

        redirectAttributes.addFlashAttribute("others", getUnemployedDoctors(userRepository.findAll(),
                clinicId, principal));
    }

    private List<User> getUnemployedDoctors(List<User> others, Long clinicId, Principal principal) {
        Clinic clinic = clinicService.getById(clinicId).orElseThrow();

        return others.stream()
                .filter(User::getIsDoctor)
                .filter(user -> !user.getUsername().equals(principal.getName()))
                .filter(user -> !clinic.getEmployees().contains(user))
                .filter(user -> invitationService.getAll().stream()
                        .noneMatch(invitation -> invitation.getDoctor().equals(user)
                                && invitation.getClinic().equals(clinic)
                                && invitation.getStatus().equals(InvitationStatus.PENDING)))
                .collect(Collectors.toList());
    }

    private List<Invitation> getPendingInvitations(Long clinicId) {

        return invitationService.getAll().stream()
                .filter(invitation -> invitation.getStatus().equals(InvitationStatus.PENDING))
                .filter(invitation -> invitation.getClinic().getId().equals(clinicId))
                .toList();
    }
}


