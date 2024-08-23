package com.pigeon_stargram.sns_clone.dto.chat.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseUserChatDto {
    private Long id;
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

    // 0이면 나만 팔로우 (<-)
    // 1이면 나만 팔로잉 (->)
    // 2이면 서로 맞팔 (<- && ->)
    private Integer state;

    public ResponseUserChatDto(User user, Integer unReadChatCount, LastMessageDto lastMessage, Integer state) {
        this.id = user.getId();
        this.name = user.getName();
        this.company = user.getCompany();
        this.workEmail = user.getWorkEmail();
        this.personalEmail = user.getPersonalEmail();
        this.workPhone = user.getWorkPhone();
        this.personalPhone = user.getPersonalPhone();
        this.location = user.getLocation();
        this.avatar = user.getAvatar();
        this.status = lastMessage.getLastMessage();
        this.lastMessage = lastMessage.getTime();
        this.birthdayText = user.getBirthdayText();
        //temp
        this.unReadChatCount = unReadChatCount;
        this.onlineStatus = user.getOnlineStatus();
        this.state = state;
    }

}