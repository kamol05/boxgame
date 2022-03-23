package startit.game;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SessionClass {
    public static List<SessionClass> classList = new ArrayList<>();

    public synchronized static SessionClass getSessionClass(String tokenOrUserNameOrSessionId){
        SessionClass s = null;
        for (SessionClass sc : classList){
            if (sc.player.getToken().equals(tokenOrUserNameOrSessionId)){
                s = sc;
            } else if (sc.player.getUsername().equals(tokenOrUserNameOrSessionId)){
                s= sc;
            } else if (sc.getSessionId().equals(tokenOrUserNameOrSessionId)){
                s= sc;
            }
        }
        return s;
    }

    public synchronized static void sendWaitingPlayersCount() throws IOException {
        for (SessionClass sc : classList){
            if (sc.player.getState().equals(State.init)){
                sc.sendMessage(Message.getStateInit());
            }
            else if (sc.player.getState().equals(State.wait)){
                sc.sendMessage(Message.getStateWait());
            }
        }
    }

    public synchronized static void clearOnExit(WebSocketSession session) throws IOException {
        for (SessionClass sc : classList){
            if (sc.getSession().equals(session)){
                sc.session.close();
                classList.remove(sc);
                sc.player.exitGame();
                break;
            }
        }
    }

    public static synchronized void checkAndCloseSession(String username)  {
        for (SessionClass sc : classList){
            if (sc.player.getUsername().equals(username)){
                if (sc.isOpen()){
                    try {
                        sc.session.close();
                    } catch (IOException e) {
                        System.out.println("Get Some error on close Session ");
                    }
                } break;
            }
        }
    }

    private WebSocketSession session;
    private String sessionId;
    private Player player;

    public void sendMessage(String message) throws IOException {
        if (this.isOpen()){
            this.session.sendMessage(new TextMessage(message));
        }
    }

    public boolean isOpen(){
        if (this.session != null){
            return session.isOpen();
        } else return false;
    }

    public void joinGameOrClose() throws IOException {
        player.changeStateToWaiting();
        this.sendMessage(Message.getStateWait());

    }

    public SessionClass() {
        classList.add(this);
    }
}
