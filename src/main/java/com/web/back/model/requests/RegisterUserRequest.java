package com.web.back.model.requests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    String username;
    String password;
    String name;
    String email;
    List<Integer> profiles;
    Integer userLevel;

    public boolean allFilled() {
        return username == null ||
                password == null ||
                name == null ||
                profiles == null ||
                email == null;
    }
}
