package com.web.back.services;

import com.web.back.model.entities.Permission;
import com.web.back.model.entities.User;
import com.web.back.model.enumerators.PermissionsEnum;
import com.web.back.model.responses.AuthResponse;
import com.web.back.model.requests.LoginRequest;
import com.web.back.model.responses.CustomResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserService userService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
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

    public CustomResponse<AuthResponse> getTokenForUser(String userName){
        User user = userService.getByUserName(userName).orElseThrow();

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
        var userPermissions = new ArrayList<String>();
        var userPermissionsTemp = user.getProfiles().stream()
                .map(profile -> {
                    var profilePermissions = profile.getPermissions();

                    return profilePermissions.stream().map(Permission::getKeyName).toList();
                }).toList();


        for (List<String> list : userPermissionsTemp) {
            userPermissions.addAll(list);
        }

        if(Objects.equals(user.getUsername(), "00000000")){
            userPermissions.add(PermissionsEnum.SUPER.name());
        }

        String token = jwtService.getToken(user, String.join("|", userPermissions));

        return AuthResponse.builder()
                .token(token)
                .permissions(userPermissions)
                .userLevel(user.getUserLevel())
                .build();
    }
}
