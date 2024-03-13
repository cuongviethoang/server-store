package com.project.ensureQuality.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignupRequest {

    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 11)
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private List<String> roles;
}
