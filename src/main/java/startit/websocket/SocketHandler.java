package startit.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import startit.game.*;

import java.io.IOException;
import java.util.Objects;

public class SocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println(message.getPayload());
        Terminal.init(SessionClass.getSessionClass(session.getId()), message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = Objects.requireNonNull(session.getUri()).getQuery().replaceFirst("token=","");
        SessionClass sessionc = SessionClass.getSessionClass(token);
        sessionc.setSession(session);
        sessionc.setSessionId(session.getId());
        session.sendMessage(new TextMessage(Message.getStateInit()));
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        SessionClass.clearOnExit(session);
        super.afterConnectionClosed(session, status);
    }




}
