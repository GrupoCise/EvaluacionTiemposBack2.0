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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Transactional
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

    @Transactional
    public CustomResponse<AuthResponse> getTokenForUser(String loggedUserName, String userName){
        User user = userService.getByUserName(userName).orElseThrow();
        User loggedUser = userService.getByUserName(loggedUserName).orElseThrow();

        if (loggedUser.getUserLevel() == null || user.getUserLevel() == null) {
            loggedUser.setUserLevel(loggedUser.getUserLevel() == null ? 0 : loggedUser.getUserLevel());
            user.setUserLevel(user.getUserLevel() == null ? 0 : user.getUserLevel());
        }

        if (loggedUser.getUserLevel() < user.getUserLevel()) {
            user.setUserLevel(loggedUser.getUserLevel());
        }

        return new CustomResponse<AuthResponse>().ok(buildAuthResponse(user));
    }


    @Transactional
    public CustomResponse<String> changePassword(LoginRequest request) {
        try {
            var result = userService.updatePassword(request.getUsername(), passwordEncoder.encode(request.getPassword()));
            return new CustomResponse<String>().ok(result);
        } catch (NoSuchElementException e) {
            return new CustomResponse<String>().badRequest("User doesn't exist");
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
            userPermissions.add(PermissionsEnum.UPDATE_TIMESHEET.name());
            userPermissions.add(PermissionsEnum.UPDATE_RG.name());
            userPermissions.add(PermissionsEnum.UPDATE_HE.name());
            userPermissions.add(PermissionsEnum.APPROVE.name());
            userPermissions.add(PermissionsEnum.SEND_TO_SAP.name());
        }

        String token = jwtService.getToken(user, String.join("|", userPermissions));

        var profiles = user.getProfiles().stream()
                .filter(f -> f.getDescription() != null)
                .map(p -> p.getDescription().toUpperCase(Locale.ROOT)
                        .replaceAll("\\s+","")).toList();

        return AuthResponse.builder()
                .token(token)
                .permissions(userPermissions)
                .userLevel(user.getUserLevel())
                .roles(profiles)
                .fullName(user.getName())
                .username(user.getUsername())
                .build();
    }
}
