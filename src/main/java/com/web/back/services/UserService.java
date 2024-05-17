package com.web.back.services;

import com.web.back.model.entities.Profile;
import com.web.back.model.entities.User;
import com.web.back.model.entities.UserProfile;
import com.web.back.model.entities.UserProfileId;
import com.web.back.model.requests.RegisterUserRequest;
import com.web.back.model.requests.UserUpdateRequest;
import com.web.back.repositories.ProfileRepository;
import com.web.back.repositories.UserProfileRepository;
import com.web.back.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProfileRepository profileRepository;
    public final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<User> register(RegisterUserRequest request) {

        var error = validateUserRegisterRequest(request);
        if (error != null) {
            return Mono.error(new Throwable(error));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setActive(true);

        userRepository.save(user);

        saveUserProfiles(user, request.getProfiles());

        return Mono.just(user);
    }

    @Transactional(rollbackFor = {Exception.class})
    protected void saveUserProfiles(User user, List<Integer> profileIds) {
        profileIds.forEach(profileId -> {
            UserProfile userProfile = new UserProfile();

            Profile profile = profileRepository.findById(profileId).get();

            UserProfileId userProfileId = new UserProfileId();
            userProfileId.setProfileId(profileId);
            userProfileId.setUserId(user.getId());
            userProfile.setId(userProfileId);

            userProfile.setProfile(profile);
            userProfile.setUser(user);

            userProfileRepository.save(userProfile);
        });
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<User> update(String userName, UserUpdateRequest userUpdate) {
        if (!userUpdate.isLongEnough()) {
            return Mono.error(new Throwable("ERROR: The fields should be filled with 8 characters long"));
        }

        User user = userRepository.findByUsername(userName).get();
        user = userUpdate.changeUser(user);

        userRepository.save(user);

        userProfileRepository.deleteByUser(user);
        saveUserProfiles(user, userUpdate.getProfiles());

        return Mono.just(user);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<List<User>> getUsersByStatus(boolean status) {
        return Mono.just(userRepository.findAllByActive(status).get());
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<User> updateStatus(String userName, boolean isActive) {
        Optional<User> userOptional = userRepository.findByUsername(userName);

        userOptional.ifPresent(user -> {
            user.setActive(isActive);
        });

        userRepository.save(userOptional.get());

        return Mono.just(userOptional.get());
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<String> updatePassword(String userName, String newPassword) {
        User user = userRepository.findByUsername(userName).orElseThrow();

        user.setPassword(newPassword);
        userRepository.save(user);

        return Mono.just("Password Changed");
    }

    public Optional<User> getByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    private String validateUserRegisterRequest(RegisterUserRequest request) {
        if(request.allFilled()){
            return "ERROR: Fill All the Fields";
        }

        if(request.valuesMatchesFieldsLength()){
            return "ERROR: The fields should be filled with 8 characters long";
        }

        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            return "ERROR: The Username Already Exist";
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            return "ERROR: The Email is Already in Use";
        }

        return null;
    }
}
