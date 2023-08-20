package com.example.messanger.payload.response;

import com.example.messanger.payload.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse<T> {
    private EStatus status;
    private T message;


}
