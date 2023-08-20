package com.example.messanger.service;

import com.example.messanger.payload.request.SendTextUserRequest;
import com.example.messanger.payload.response.MessageResponse;

public interface MessengerService {

    MessageResponse getUsers();

    MessageResponse getUnreadMessages();

    MessageResponse sendMessage(SendTextUserRequest sendTextUserRequest) throws Exception;

    MessageResponse getHistory(Long chatId);
}
