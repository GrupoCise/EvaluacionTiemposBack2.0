package com.web.back.services;

import com.web.back.model.entities.Profile;
import com.web.back.model.entities.User;
import com.web.back.model.requests.RegisterUserRequest;
import com.web.back.model.requests.UserUpdateRequest;
import com.web.back.repositories.ProfileRepository;
import com.web.back.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    public final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = {Exception.class})
    public User register(RegisterUserRequest request) {

        var error = validateUserRegisterRequest(request);
        if (error != null) {
            throw new RuntimeException(error);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setUserLevel(request.getUserLevel());
        user.setActive(true);

        userRepository.save(user);

        saveUserProfiles(user, request.getProfiles());

        return user;
    }

    @Transactional(rollbackFor = {Exception.class})
    protected void saveUserProfiles(User user, List<Integer> profileIds) {
        Set<Profile> profiles = new HashSet<>();
        profileIds.forEach(profileId -> {
            Profile profile = profileRepository.findById(profileId).get();

            profiles.add(profile);
        });

        user.setProfiles(profiles);
    }

    @Transactional(rollbackFor = {Exception.class})
    public User update(Integer id, UserUpdateRequest userUpdate) {
        User user = userRepository.findById(id).get();
        user = userUpdate.changeUser(user);

        saveUserProfiles(user, userUpdate.getProfiles());

        return user;
    }

    @Transactional
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(rollbackFor = {Exception.class})
    public User updateStatus(String userName, boolean isActive) {
        Optional<User> userOptional = userRepository.findByUsername(userName);

        userOptional.ifPresent(user -> user.setActive(isActive));

        userRepository.save(userOptional.get());

        return userOptional.get();
    }

    @Transactional(rollbackFor = {Exception.class})
    public String updatePassword(String userName, String newPassword) {
        User user = userRepository.findByUsername(userName).orElseThrow();

        user.setPassword(newPassword);
        userRepository.save(user);

        return "Password Changed";
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteUser(Integer id) {
        var user = userRepository.findById(id).orElseThrow();

        userRepository.delete(user);
    }

    @Transactional
    public Optional<User> getByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Transactional
    public Optional<User> getById(int userId) {
        return userRepository.findById(userId);
    }

    private String validateUserRegisterRequest(RegisterUserRequest request) {
        if(request.allFilled()){
            return "Informacion faltante!";
        }

        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            return "El usuario ya existe!";
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            return "El email ya esta en uso!";
        }

        return null;
    }
}
