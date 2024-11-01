package me.mmtr.vitalis.controller;

import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.UserDTO;
import me.mmtr.vitalis.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {
    private final UserService USER_SERVICE;

    public AuthenticationController(UserService userService) {
        this.USER_SERVICE = userService;
    }

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) {
            model.addAttribute("error", "Poniższe dane nie pasują do żadnego istniejącego konta.");
        }
        if (logout != null) {
            model.addAttribute("logout", logout);
        }
        return "login";
    }

    @GetMapping("/patient")
    public String home() {
        return "patient-home";
    }

    @GetMapping("/doctor")
    public String admin() {
        return "doctor-home";
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        UserDTO userDTO = new UserDTO();
        model.addAttribute("user", userDTO);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, Model model) {
        User existingUser = USER_SERVICE.findUserByUsername(userDTO.getUsername());

        if (existingUser != null && existingUser.getUsername() != null && !existingUser.getUsername().isEmpty()) {
            bindingResult.rejectValue("username", "exists", "Użytkownik o takiej nazwie już istnieje.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDTO);
            return "register";
        }

        USER_SERVICE.saveUser(userDTO);
        return "redirect:/login";
    }
}
