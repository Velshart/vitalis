package me.mmtr.vitalis.service;

import me.mmtr.vitalis.data.Role;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.UserDTO;
import me.mmtr.vitalis.repository.RoleRepository;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDTO userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        boolean isDoctor = userDto.getIsDoctor();
        user.setIsDoctor(isDoctor);

        Role role;
        if (isDoctor) {
            role = roleRepository.findByName("DOCTOR");
        } else {
            role = roleRepository.findByName("PATIENT");
        }

        if (role == null) {
            role = createRole(isDoctor);
        }

        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToUserDto)
                .toList();
    }

    private UserDTO mapToUserDto(User user) {
        UserDTO userDto = new UserDTO();
        String[] str = user.getUsername().split(" ");
        userDto.setUsername(str[0]);
        return userDto;
    }

    private Role createRole(boolean isDoctor) {
        Role role = new Role();
        if (isDoctor) {
            role.setName("DOCTOR");
        } else {
            role.setName("PATIENT");
        }
        return roleRepository.save(role);
    }
}
