package com.example.messanger.service;

import com.example.messanger.models.*;
import com.example.messanger.payload.enums.ChatType;
import com.example.messanger.payload.enums.EStatus;
import com.example.messanger.payload.request.SendTextUserRequest;
import com.example.messanger.payload.response.ChatHistoryResponse;
import com.example.messanger.payload.response.MessageResponse;
import com.example.messanger.repository.*;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
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

    @Autowired
    private ChatReadByRepository chatReadByRepository;

    @Override
    public MessageResponse getUsers() {

        List<User> userList = userRepository.findAll();

        List<String> userName = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userList)) {
            userName = userList.stream().map(p -> p.getUsername()).collect(Collectors.toList());
        }

        return new MessageResponse(EStatus.SUCCESS, userName);
    }

    @Override
    public MessageResponse getUnreadMessages(String userName) throws Exception {


        User user = userRepository.findByUsername(userName).orElseThrow(() -> new Exception("User not found with userName " + userName));
        List<ChatReadBy> chatReadBIES = chatReadByRepository.getChatByUserAndStatus(user.getId(), false);
        Map<Long, List<String>> chatToMessages = new HashMap<>();
        if (!CollectionUtils.isEmpty(chatReadBIES)) {
            for (ChatReadBy chatReadBy : chatReadBIES) {

                Message message = chatReadBy.getMessage();
                Long chatId = message.getChat().getId();
                if (chatToMessages.containsKey(chatId)) {
                    List<String> tempMessageList = chatToMessages.get(chatId);
                    tempMessageList.add(message.getText());
                    chatToMessages.put(chatId, tempMessageList);
                } else {
                    chatToMessages.put(chatId, Arrays.asList(message.getText()));
                }


            }
        }
        if (chatToMessages.size() > 0) {
            return new MessageResponse(EStatus.SUCCESS, "You have message(s)", chatToMessages);
        }
        return new MessageResponse(EStatus.SUCCESS, "No new Messages");
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

            ChatReadBy chatReadBy = ChatReadBy.builder().
                    message(message).user(toUser).isRead(false).build();
            chatReadByRepository.save(chatReadBy);

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

            ChatReadBy chatReadBy = ChatReadBy.builder().
                    message(message).user(toUser).isRead(false).build();
            chatReadByRepository.save(chatReadBy);
        }
        return new MessageResponse(EStatus.SUCCESS, "");
    }

    @Override
    public MessageResponse getHistory(Long chatId, String fromUserName) throws Exception {

        User user = userRepository.findByUsername(fromUserName).orElseThrow(() -> new Exception("fromUserNotFound"));
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId);
        List<ChatHistoryResponse> chatHistoryResponses = new ArrayList<>();
        List<Long> messageId = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messages)) {
            for (Message message : messages) {
                messageId.add(message.getId());
                chatHistoryResponses.add(new ChatHistoryResponse(message.getUser().getUsername(), message.getText()));
            }
        }
        if (!CollectionUtils.isEmpty(messageId)) {
            List<ChatReadBy> chatReadBIES = chatReadByRepository.getChatReadByMessageAndUser(messageId, user.getId());
            if (!CollectionUtils.isEmpty(chatReadBIES)) {
                for(ChatReadBy chatReadBy:chatReadBIES) {
                    chatReadBy.setIsRead(true);
                    chatReadByRepository.save(chatReadBy);
                }
            }
        }
        MessageResponse<List<ChatHistoryResponse>> messageResponse = new MessageResponse<>(EStatus.SUCCESS, chatHistoryResponses);
        return messageResponse;
    }
}
