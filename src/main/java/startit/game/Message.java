package startit.game;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private int id;
    private int waitingCount;
    private int requestId;
    private String username;
    private String data;
    private Type type;
    private State state;
    private Game game;

    public static String getStateGame(Game game){
        Message message = new Message();
        message.type = Type.state;
        message.state = State.game;
        message.data = "ok";
        message.game = game;
        return new Gson().toJson(message);
    }

    public static String getStateWait(){
        Message message = new Message();
        message.type = Type.state;
        message.state = State.wait;
        message.data = "ok";
        message.waitingCount = Player.playersWaiting.size();
        return new Gson().toJson(message);
    }

    public static String getStateInit(){
        Message message = new Message();
        message.setType(Type.state);
        message.setState(State.init);
        message.setWaitingCount(Player.playersWaiting.size());
        return new Gson().toJson(message);
    }

    public static String getJoinMessage(){
        Request request = new Request();
        request.setAction("join");
        request.setId(0);
        return new Gson().toJson(request);
    }

    public static Message getStateResponse(){
        Message message = new Message();
        message.setType(Type.response);
        message.setData("ok");
        message.setWaitingCount(Player.playersWaiting.size());
        return message;
    }
}
