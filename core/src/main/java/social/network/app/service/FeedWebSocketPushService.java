package social.network.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import social.network.app.dto.PostDto;
import social.network.app.websocket.session.WebSocketSessionRegistry;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedWebSocketPushService {

    private final WebSocketSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public void sendPostToUser(String userId, PostDto message) {
        Set<WebSocketSession> sessions = sessionRegistry.getSessions(userId);
        log.info("Sessions: {}", sessions);
        if (sessions.isEmpty()) {
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(payload);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    synchronized (session) {
                        session.sendMessage(textMessage);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to send post to user {}", userId, e);
        }
    }
}
