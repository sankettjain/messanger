package com.example.messanger.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/messenger")
public class MessangerController {
    @GetMapping("/get/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUsers() {
        //todo getUsers
        return "";
    }

    @GetMapping("/get/unread")
    @PreAuthorize("hasRole('USER')")
    public String getUnread() {
        return "";
    }

}
