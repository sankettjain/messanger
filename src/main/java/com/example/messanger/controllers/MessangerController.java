package com.example.messanger.controllers;

import com.example.messanger.payload.request.SendTextUserRequest;
import com.example.messanger.payload.response.MessageResponse;
import com.example.messanger.service.MessengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/messenger")
public class MessangerController {

    @Autowired
    private MessengerService messengerService;

    @GetMapping("/get/users")
//    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse getUsers() {
        return messengerService.getUsers();
    }

    @GetMapping("/get/unread")
//    @PreAuthorize("hasRole('USER')")
    public String getUnread() {
        return "";
    }

    @PostMapping("/send/text/user")
//    @PreAuthorize("hasRole('USER')")
    public MessageResponse sendTextToUser(@RequestBody SendTextUserRequest sendTextUserRequest) throws Exception {

        return messengerService.sendMessage(sendTextUserRequest);
    }

    @GetMapping("/get/history/{chatId}")
//    @PreAuthorize("hasRole('USER')")
    public MessageResponse getHistory(@PathVariable Long chatId) {

        return messengerService.getHistory(chatId);
    }

}
