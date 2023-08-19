package com.example.messanger.payload.response;

import com.example.messanger.payload.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private EStatus status;
    private String message;


}
