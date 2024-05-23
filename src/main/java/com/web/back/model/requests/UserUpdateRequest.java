package com.web.back.model.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.web.back.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    String username;
    String name;
    String email;
    Boolean isActive;
    List<Integer> profiles;

    @JsonIgnore
    public boolean isLongEnough(){
        return username.length()>=8 && name.length()>=8 && email.length()>=8;
    }

    public User changeUser(User user){
        user.setName(this.name);
        user.setEmail(this.email);
        user.setUsername(this.username);
        user.setActive(this.isActive);
        return user;
    }
}