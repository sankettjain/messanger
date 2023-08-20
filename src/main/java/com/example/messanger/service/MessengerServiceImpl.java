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

    /**
     * gets all UserNames
     *
     * @return
     */
    @Override
    public MessageResponse getUsers() {

        //finds all users
        List<User> userList = userRepository.findAll();

        //filtering only userName
        List<String> userName = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userList)) {
            userName = userList.stream().map(p -> p.getUsername()).collect(Collectors.toList());
        }

        return new MessageResponse(EStatus.SUCCESS, userName);
    }

    /**
     * gets Users unread messages for a given chat
     *
     * @param userName
     * @return
     * @throws Exception
     */
    @Override
    public MessageResponse getUnreadMessages(String userName) throws Exception {

        //get User's data
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new Exception("User not found with userName " + userName));

        //gets user's unread messages from chatReadBy table
        List<ChatReadBy> chatReadBIES = chatReadByRepository.getChatByUserAndStatus(user.getId(), false);

        //Map of chatToMessages -> telling user unread messages
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
        //returning based on unreadMessages
        if (chatToMessages.size() > 0) {
            return new MessageResponse(EStatus.SUCCESS, "You have message(s)", chatToMessages);
        }
        return new MessageResponse(EStatus.SUCCESS, "No new Messages");
    }

    /**
     * sends message based on chatId
     *
     * @param sendTextUserRequest
     * @return
     * @throws Exception
     */
    @Override
    public MessageResponse sendMessage(SendTextUserRequest sendTextUserRequest) throws Exception {

        //fetches user's
        User fromUser = userRepository.findByUsername(sendTextUserRequest.getFromUserName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + sendTextUserRequest.getFromUserName()));
        User toUser = userRepository.findByUsername(sendTextUserRequest.getToUserName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + sendTextUserRequest.getToUserName()));

        //if chatId is null considering new chat
        if (Objects.nonNull(sendTextUserRequest) && sendTextUserRequest.getChatId() == null) {

            //adding chat entry
            Chat chatEntry = Chat.builder().
                    lastSeenMessage(sendTextUserRequest.getText()).
                    lastSeenUser(fromUser).type(ChatType.ONE_ONE).
                    build();
            chatEntry = chatRepository.save(chatEntry);

            //TODO:: can move to common place
            //storing message with chatId
            Message message = Message.builder().
                    chat(chatEntry).user(fromUser).text(sendTextUserRequest.getText()).build();
            messageRepository.save(message);

            //storing chatread status as false for toUser
            ChatReadBy chatReadBy = ChatReadBy.builder().
                    message(message).user(toUser).isRead(false).build();
            chatReadByRepository.save(chatReadBy);

            //creating a entry of chatToUser for loading inbox
            ChatParticipants fromChatParticipant = ChatParticipants.builder().
                    chat(chatEntry).user(fromUser).
                    build();

            ChatParticipants toChatParticipant = ChatParticipants.builder().
                    chat(chatEntry).user(toUser).
                    build();

            chatParticipantsRepository.save(fromChatParticipant);
            chatParticipantsRepository.save(toChatParticipant);

        } else {
            //same flow as above for exisitng chat
            Chat chatEntry = chatRepository.findById(sendTextUserRequest.getChatId()).orElseThrow(() -> new Exception("Chat Not Found with chatId: " + sendTextUserRequest.getChatId()));
            chatEntry.setLastSeenMessage(sendTextUserRequest.getText());
            chatEntry.setLastSeenUser(fromUser);

            //TODO:: can move to common place
            Message message = Message.builder().
                    chat(chatEntry).user(fromUser).text(sendTextUserRequest.getText()).build();
            messageRepository.save(message);

            ChatReadBy chatReadBy = ChatReadBy.builder().
                    message(message).user(toUser).isRead(false).build();
            chatReadByRepository.save(chatReadBy);
        }
        return new MessageResponse(EStatus.SUCCESS, "");
    }

    /**
     * gets History based on chatId and user
     *
     * @param chatId
     * @param fromUserName
     * @return
     * @throws Exception
     */
    @Override
    public MessageResponse getHistory(Long chatId, String fromUserName) throws Exception {

        //gets fromUser
        User user = userRepository.findByUsername(fromUserName).orElseThrow(() -> new Exception("fromUserNotFound"));
        //finds all the messages for user based on a given chat
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId);
        // traversing through messages for desired response and marking messages a read
        List<ChatHistoryResponse> chatHistoryResponses = new ArrayList<>();
        List<Long> messageId = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messages)) {
            for (Message message : messages) {
                messageId.add(message.getId());
                chatHistoryResponses.add(new ChatHistoryResponse(message.getUser().getUsername(), message.getText()));
            }
        }
        //marking messages a read
        if (!CollectionUtils.isEmpty(messageId)) {
            List<ChatReadBy> chatReadBIES = chatReadByRepository.getChatReadByMessageAndUser(messageId, user.getId());
            if (!CollectionUtils.isEmpty(chatReadBIES)) {
                for (ChatReadBy chatReadBy : chatReadBIES) {
                    chatReadBy.setIsRead(true);
                    chatReadByRepository.save(chatReadBy);
                }
            }
        }
        MessageResponse<List<ChatHistoryResponse>> messageResponse = new MessageResponse<>(EStatus.SUCCESS, chatHistoryResponses);
        return messageResponse;
    }
}
