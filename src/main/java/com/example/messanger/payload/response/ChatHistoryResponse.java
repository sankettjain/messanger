package com.example.messanger.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChatHistoryResponse {

    private String userName;
    private String message;
}
