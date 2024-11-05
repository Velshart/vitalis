package me.mmtr.vitalis.controller;

import jakarta.validation.Valid;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.enums.Specialization;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.ClinicService;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/clinic")
public class ClinicController {

    private final ClinicService CLINIC_SERVICE;
    private final UserService USER_SERVICE;
    private final UserRepository USER_REPOSITORY;


    public ClinicController(ClinicService CLINIC_SERVICE, UserService USER_SERVICE, UserRepository USER_REPOSITORY) {
        this.CLINIC_SERVICE = CLINIC_SERVICE;
        this.USER_SERVICE = USER_SERVICE;
        this.USER_REPOSITORY = USER_REPOSITORY;
    }

    @GetMapping("/owned")
    public String ownedClinics(Model model, Principal principal) {
        User user = USER_SERVICE.findUserByUsername(principal.getName());

        model.addAttribute("clinicsOwned", CLINIC_SERVICE.getAll()
                .stream()
                .filter(clinic -> clinic.getOwner() != null && clinic.getOwner().equals(user))
                .collect(Collectors.toList())
        );
        return "clinics-owned";
    }

    @GetMapping("/employed")
    public String employedClinics(Model model, Principal principal) {
        User user = USER_SERVICE.findUserByUsername(principal.getName());

        model.addAttribute("clinicsEmployed", CLINIC_SERVICE.getAll()
                .stream()
                .filter(clinic -> clinic.getEmployees().contains(user))
                .collect(Collectors.toList())
        );
        return "clinics-employed";
    }

    @GetMapping("/add")
    public String add(Model model, Principal principal) {
        Clinic clinic = new Clinic();
        clinic.setOwner(USER_SERVICE.findUserByUsername(principal.getName()));

        model.addAttribute("clinic", new Clinic());
        model.addAttribute("specializations", Specialization.values());

        model.addAttribute("doctors", USER_REPOSITORY
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
        clinic.setOwner(USER_SERVICE.findUserByUsername(principal.getName()));

        if (bindingResult.hasErrors()) {
            model.addAttribute("clinic", clinic);
            model.addAttribute("specializations", Specialization.values());
            return "clinic";
        }

        this.CLINIC_SERVICE.saveOrUpdate(clinic);
        return "redirect:/clinic/owned";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        Optional<Clinic> clinicOptional = CLINIC_SERVICE.getById(id);

        if (clinicOptional.isEmpty()) {
            return "redirect:/clinic/owned";
        }
        model.addAttribute("clinic", clinicOptional.get());
        model.addAttribute("specializations", Specialization.values());
        return "clinic";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute @Valid Clinic clinic,
                         BindingResult bindingResult,
                         Model model,
                         Principal principal) {

        clinic.setOwner(USER_SERVICE.findUserByUsername(principal.getName()));

        if (bindingResult.hasErrors()) {
            model.addAttribute("clinic", clinic);
            model.addAttribute("specializations", Specialization.values());
            return "clinic";
        }

        this.CLINIC_SERVICE.saveOrUpdate(clinic);
        return "redirect:/clinic/owned";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        this.CLINIC_SERVICE.delete(id);
        return "redirect:/clinic/owned";
    }

}
