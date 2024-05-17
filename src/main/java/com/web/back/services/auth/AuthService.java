package com.web.back.services.auth;

import com.web.back.model.entities.Profile;
import com.web.back.model.entities.User;
import com.web.back.model.responses.AuthResponse;
import com.web.back.model.requests.LoginRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.repositories.ProfilePermissionRepository;
import com.web.back.repositories.ProfileRepository;
import com.web.back.services.jwt.JwtService;
import com.web.back.services.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ProfilePermissionRepository profilePermissionRepository;
    private final ProfileRepository profileRepository;

    public AuthService(UserService userService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       ProfilePermissionRepository profilePermissionRepository, ProfileRepository profileRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.profilePermissionRepository = profilePermissionRepository;
        this.profileRepository = profileRepository;
    }

    public CustomResponse<AuthResponse> login(LoginRequest request) {
        User user;

        try {
            user = userService.getByUserName(request.getUsername()).orElseThrow();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (NoSuchElementException e) {
            return new CustomResponse<AuthResponse>().badRequest("User doesn't exist");
        } catch (AuthenticationException e) {
            return new CustomResponse<AuthResponse>().badRequest("Incorrect password");
        }

        return new CustomResponse<AuthResponse>().ok(buildAuthResponse(user));
    }

    public Mono<CustomResponse<String>> changePassword(LoginRequest request) {
        try {
            return userService.updatePassword(request.getUsername(), passwordEncoder.encode(request.getPassword()))
                    .map(result -> new CustomResponse<String>().ok(result));
        } catch (NoSuchElementException e) {
            return Mono.just(new CustomResponse<String>().badRequest("User doesn't exist"));
        }
    }

    private AuthResponse buildAuthResponse(User user) {

        var userPermissions = user.getProfiles().stream()
                .map(Profile::getId)
                .map(profileId -> {
                    var profile = profileRepository.findById(profileId);
                    var profilePermissions = profilePermissionRepository.getByProfile(profile.get());

                    return profilePermissions.stream().map(item -> item.getPermission().getKeyName()).toList();
                }).flatMap(List::stream)
                .collect(Collectors.joining("|"));

        String token = jwtService.getToken(user, userPermissions);
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
