package com.web.back.model.requests.user;

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


    public boolean allFilled() {
        return username == null ||
                password == null ||
                name == null ||
                profiles == null ||
                email == null;
    }

    public boolean valuesMatchesFieldsLength() {
        return !(username.length() >= 8 && password.length() >= 8 && name.length() >= 8 && email.length() >= 10);
    }

}
