package startit.game;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;

@JsonSerialize
@Getter
@Setter
public class Game implements Runnable{
    transient
    private SessionClass player1Session;
    transient
    private SessionClass player2Session;
    private int round;
    private int timeout;
    private int timeoutPassed;
    private boolean completed;
    private boolean roundCompleted;
    private Player mine;
    private Player enemy;

    @SneakyThrows
    @Override
    public void run() {
        while (round < Setups.ROUNDS.getValue() && !completed){
            roundStart();
            sendMessageToPlayers();
            sleep(Setups.ROUND_TIMEOUT);
            roundEnd();
            sendMessageToPlayers();
            sleep(Setups.RESULT_TIMEOUT);
        }
        gameEnd();
        sendMessageToPlayers();
        sleep(Setups.RESULT_TIMEOUT);
        player1Session.joinGameOrClose();
        player2Session.joinGameOrClose();
    }

    public void roundStart(){
        round++;
        roundCompleted = false;
        mine.setBlock(0);
        mine.setKick(0);
        enemy.setBlock(0);
        enemy.setKick(0);
        mine.setHit(false);
        enemy.setHit(false);
        timeout = Setups.ROUND_TIMEOUT.getValue();
    }

    public void roundEnd(){
        this.roundCompleted = true;
        if (mine.getKick() != 0 && mine.getKick() != enemy.getBlock()) {
            mine.setScore(mine.getScore() +1);
            mine.setHit(true);
        }
        if (enemy.getKick() != 0 && enemy.getKick() != mine.getBlock()) {
            enemy.setScore(enemy.getScore() +1);
            enemy.setHit(true);
        }
    }

    public void gameEnd(){
        completed = true;
        roundCompleted = true;
        if (mine.getScore() > enemy.getScore()) {
            mine.setWinner(true);
        } else if (enemy.getScore() > mine.getScore()) {
            enemy.setWinner(true);
        }
    }

    private void sendMessageToPlayers() throws IOException {
        if ( !player1Session.isOpen() || !player2Session.isOpen() ){ completed = true; }
        player1Session.sendMessage(Message.getStateGame(this));
        player2Session.sendMessage(Message.getStateGame(reversePlayer()));
    }

    public void sleep(Setups timeout){
        try {
            Thread.sleep(timeout.getValue());
        } catch (InterruptedException e)
        { System.out.println( "The " + Thread.currentThread().getName() + " is closed"); }
    }

    public Game reversePlayer(){
        Game game = new Game();
        game.setTimeout(timeout);
        game.setTimeoutPassed(timeoutPassed);
        game.setCompleted(completed);
        game.setRoundCompleted(roundCompleted);
        game.setRound(round);
        game.setMine(enemy);
        game.setEnemy(mine);
        return game;
    }

    public Game(){}

    public Game (Player user1, Player user2){
        mine = user1;
        enemy = user2;
        player1Session = SessionClass.getSessionClass(user1.getUsername());
        player2Session = SessionClass.getSessionClass(user2.getUsername());
    }

}
