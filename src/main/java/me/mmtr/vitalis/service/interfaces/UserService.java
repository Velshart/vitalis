package me.mmtr.vitalis.service.interfaces;

import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.data.UserDTO;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDto);

    User findUserByUsername(String username);

    List<UserDTO> findAllUsers();
}
