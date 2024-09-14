package com.pigeon_stargram.sns_clone.dto.chat.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ResponseUserChatDto {

    /**
     * 사용자 정보 필드
     */
    private Long id;
    @JsonProperty("work_email")
    private String workEmail;
    private String role;

    private String name;
    private String company;
    private String avatar;
    @JsonProperty("personal_phone")
    private String personalPhone;
    @JsonProperty("work_phone")
    private String workPhone;
    @JsonProperty("personal_email")
    private String personalEmail;
    private String location;
    @JsonProperty("birthdayText")
    private String birthdayText;
    @JsonProperty("online_status")
    private String onlineStatus;

    /**
     * 채팅 서비스 정보
     */
    @JsonProperty("unReadChatCount")
    private int unReadChatCount;
    @JsonProperty("lastMessage")
    private String lastMessage;
    private String status;
    // 0: 나를 팔로우 중인 상태 (<-)
    // 1: 내가 팔로우 중인 상태 (->)
    // 2: 서로 팔로우 중인 상태 (<- && ->)
    private Integer state;

}
