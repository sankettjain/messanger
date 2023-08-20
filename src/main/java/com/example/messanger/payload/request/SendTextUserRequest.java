package com.example.messanger.payload.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendTextUserRequest {

    private String fromUserName;
    private String toUserName;
    private Long chatId;
    private String text;
}
