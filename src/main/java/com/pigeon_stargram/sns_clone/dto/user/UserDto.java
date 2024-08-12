package com.pigeon_stargram.sns_clone.dto.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.pigeon_stargram.sns_clone.domain.user.Role;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDto {
    private int id;
    private String name;
    private String company;
    private String role;

    @JsonProperty("work_email")
    private String workEmail;

    @JsonProperty("personal_email")
    private String personalEmail;

    @JsonProperty("work_phone")
    private String workPhone;

    @JsonProperty("personal_phone")
    private String personalPhone;

    private String location;
    private String avatar;
    private String status;

    @JsonProperty("lastMessage")
    private String lastMessage;

    @JsonProperty("birthdayText")
    private String birthdayText;

    @JsonProperty("unReadChatCount")
    private int unReadChatCount;

    @JsonProperty("online_status")
    private String onlineStatus;

    @Builder.Default
    private String password = "test";

    public User toEntity() {
        return User.builder()
                .name(name)
                .company(company)
                .role(Role.USER)
                .workEmail(workEmail)
                .personalEmail(personalEmail)
                .workPhone(workPhone)
                .personalPhone(personalPhone)
                .location(location)
                .avatar(avatar)
                .birthdayText(birthdayText)
                .onlineStatus(onlineStatus)
                .password(password)
                .build();
    }
}