package com.cambofreelance.websiteservice.model.dto.users;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private String id;
    private String username;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String status;
    private String applicationCode;
    private String applicationName;

    private List<RoleDto> roles;

    @Data
    public static class RoleDto {
        private String id;
        private String code;
        private String name;
    }
}