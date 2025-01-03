//package com.capstone.project.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kh.springJpa241217.dto.ChatMessageDto;
//import com.kh.springJpa241217.service.ChatService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@RequiredArgsConstructor  // 롬복 어노테이션으로 생성자 만들기
//@Slf4j  //  롬복 어노테이션으로 log 메시지 사용
//@Component  // 스프링 컨테이너에 빈 등록
//public class WebSocketHandler extends TextWebSocketHandler {
//    private final ObjectMapper objectMapper;
//    private final ChatService chatService;
//
//    // 세션과 채팅방을 매핑하는 데 사용 (사용자가 어떤 채팅방에 속해 있는지 등록)
//    private final Map<WebSocketSession, String>
//            sessionRoomIdMap = new ConcurrentHashMap<>();
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message)
//            throws Exception {
//        String payload = message.getPayload(); // 클라이언트가 전송한 메시지
//        log.info("{}", payload);
//        // JSON 문자열을 ChatMessageDto 변환 작업
//        ChatMessageDto chatMessage = objectMapper.readValue(payload, ChatMessageDto.class);
//        String roomId = chatMessage.getRoomId();
//        if(chatMessage.getType() == ChatMessageDto.MessageType.ENTER) {
//            sessionRoomIdMap.put(session, chatMessage.getRoomId());
//            chatService.addSessionAndHandleEnter(roomId, session, chatMessage);
//        } else if (chatMessage.getType() == ChatMessageDto.MessageType.CLOSE) {
//            chatService.removeSessionAndHandleExit(roomId, session, chatMessage);
//        } else {
//            chatService.sendMessageToAll(roomId, chatMessage);
//        }
//
//    }
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
//            throws Exception {
//        log.info("연결 해제 이후 동작 : {}", session);
//        String roomId = sessionRoomIdMap.remove(session);
//        if (roomId != null) {
//            ChatMessageDto chatMessage = new ChatMessageDto();
//            chatMessage.setType(ChatMessageDto.MessageType.CLOSE);
//            chatService.removeSessionAndHandleExit(roomId, session, chatMessage);
//        }
//    }
//}
