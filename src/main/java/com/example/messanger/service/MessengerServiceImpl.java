package com.example.messanger.service;

import com.example.messanger.models.Chat;
import com.example.messanger.models.ChatParticipants;
import com.example.messanger.models.Message;
import com.example.messanger.models.User;
import com.example.messanger.payload.enums.ChatType;
import com.example.messanger.payload.enums.EStatus;
import com.example.messanger.payload.request.SendTextUserRequest;
import com.example.messanger.payload.response.ChatHistoryResponse;
import com.example.messanger.payload.response.MessageResponse;
import com.example.messanger.repository.ChatParticipantsRepository;
import com.example.messanger.repository.ChatRepository;
import com.example.messanger.repository.MessageRepository;
import com.example.messanger.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessengerServiceImpl implements MessengerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatParticipantsRepository chatParticipantsRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public MessageResponse getUsers() {

        List<User> userList = userRepository.findAll();

        List<String> userName = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userList)) {
            userName = userList.stream().map(p -> p.getUsername()).collect(Collectors.toList());
        }

        return new MessageResponse(EStatus.SUCCESS, new Gson().toJson(userName));
    }

    @Override
    public MessageResponse getUnreadMessages() {
        return null;
    }

    @Override
    public MessageResponse sendMessage(SendTextUserRequest sendTextUserRequest) throws Exception {


        User fromUser = userRepository.findByUsername(sendTextUserRequest.getFromUserName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + sendTextUserRequest.getFromUserName()));
        User toUser = userRepository.findByUsername(sendTextUserRequest.getToUserName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + sendTextUserRequest.getToUserName()));

        if (Objects.nonNull(sendTextUserRequest) && sendTextUserRequest.getChatId() == null) {

            Chat chatEntry = Chat.builder().
                    lastSeenMessage(sendTextUserRequest.getText()).
                    lastSeenUser(fromUser).type(ChatType.ONE_ONE).
                    build();
            chatEntry = chatRepository.save(chatEntry);

            Message message = Message.builder().
                    chat(chatEntry).user(fromUser).text(sendTextUserRequest.getText()).build();
            messageRepository.save(message);

            ChatParticipants fromChatParticipant = ChatParticipants.builder().
                    chat(chatEntry).user(fromUser).
                    build();

            ChatParticipants toChatParticipant = ChatParticipants.builder().
                    chat(chatEntry).user(toUser).
                    build();

            chatParticipantsRepository.save(fromChatParticipant);
            chatParticipantsRepository.save(toChatParticipant);

        } else {
            Chat chatEntry = chatRepository.findById(sendTextUserRequest.getChatId()).orElseThrow(() -> new Exception("Chat Not Found with chatId: " + sendTextUserRequest.getChatId()));
            chatEntry.setLastSeenMessage(sendTextUserRequest.getText());
            chatEntry.setLastSeenUser(fromUser);
            Message message = Message.builder().
                    chat(chatEntry).user(fromUser).text(sendTextUserRequest.getText()).build();
            messageRepository.save(message);
        }
        return new MessageResponse(EStatus.SUCCESS, "");
    }

    @Override
    public MessageResponse getHistory(Long chatId) {

        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId);
        List<ChatHistoryResponse> chatHistoryResponses= new ArrayList<>();
        if(!CollectionUtils.isEmpty(messages)){
            for(Message message:messages){
                chatHistoryResponses.add(new ChatHistoryResponse(message.getUser().getUsername(),message.getText()));
            }
        }
        MessageResponse<List<ChatHistoryResponse>> messageResponse = new MessageResponse<>(EStatus.SUCCESS,chatHistoryResponses);
        return messageResponse;
    }
}
