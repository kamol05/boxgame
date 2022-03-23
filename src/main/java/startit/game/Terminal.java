package startit.game;

import com.google.gson.Gson;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


@Configuration
@EnableScheduling
public class Terminal {
    private AtomicInteger gameCounter = new AtomicInteger();
    private static SessionClass sessionClass;

    public static synchronized void init(SessionClass sessionc, String payload) throws IOException {
        sessionClass = sessionc;
        Request request = new Gson().fromJson(payload, Request.class);
//        boolean isCorrect = Request.verify(payload);

        if (request.getAction().equals("join")) {
            joinGame();
        }else if (payload.contains("undo_join")) {
            undoJoinGame();
        } else if (payload.contains( "game_set_block")) {
            setBlock(request);
        } else if (payload.contains( "game_set_kick")) {
            setKick(request);
        } else if (payload.contains( "logout")) {
            sessionc.getSession().close();
        }
    }

    @Scheduled(fixedDelay = 15000, initialDelay = 10000)
    public void randomGame() throws IOException {
        if (Player.playersWaiting.size() >= 2){
            Game game = new Game(Player.getRandom(), Player.getRandom());
            Thread thread = new Thread(game);
            thread.setName("BOX-" + gameCounter.incrementAndGet() + " " + game.getMine().getUsername() +" & " + game.getEnemy().getUsername());
            thread.start();
        }
    }

    public static synchronized void joinGame() throws IOException {
        sessionClass.getPlayer().changeStateToWaiting();
        sessionClass.sendMessage(Message.getStateWait());
    }

    public static synchronized void undoJoinGame() throws IOException {
        sessionClass.getPlayer().changeStateToInit();
        sessionClass.sendMessage(Message.getStateInit());
    }

    public static synchronized void setBlock(Request request){
        sessionClass.getPlayer().setBlock(request.getBlock());
    }

    public static synchronized void setKick(Request request){
        sessionClass.getPlayer().setKick(request.getKick());
    }

}
