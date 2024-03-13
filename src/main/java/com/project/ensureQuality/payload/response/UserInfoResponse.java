package com.project.ensureQuality.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class UserInfoResponse {
    private Integer id;
    private String username;
    private String email;

    private String phoneNumber;

    private List<String> roles;

    public UserInfoResponse(Integer id, String username, String email, String phoneNumber, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }
}
