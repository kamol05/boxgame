package startit.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Entity
public class Player {
    transient
    public static Set<Player> playersWaiting = new HashSet<>();
    transient
    public static List<Player> playersGaming = new CopyOnWriteArrayList<>();
    public static Player getRandom () throws IOException {
        Player p =  playersWaiting.stream()
                .skip((int) (playersWaiting.size() * Math.random()))
                .findFirst()
                .get();
        p.changeStateToGaming();
        return p;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private String token;

    @Column(unique = true)
    private String username;
    private int block = 0;
    private int kick = 0;
    private int score = 0;
    private boolean winner = false;
    private boolean hit = false;
    private State state = State.init;

    public void changeStateToInit() throws IOException {
        this.score = 0;
        this.state = State.init;
        playersWaiting.remove(this);
        playersGaming.remove(this);
        SessionClass.sendWaitingPlayersCount();
    }
    public void changeStateToWaiting() throws IOException {
        this.score = 0;
        this.state = State.wait;
        playersWaiting.add(this);
        playersGaming.remove(this);
        SessionClass.sendWaitingPlayersCount();
    }
    public void changeStateToGaming() throws IOException {
        this.state = State.game;
        playersWaiting.remove(this);
        playersGaming.add(this);
        SessionClass.sendWaitingPlayersCount();
    }
    public void exitGame() throws IOException {
        this.state = State.init;
        playersWaiting.remove(this);
        playersGaming.remove(this);
        SessionClass.sendWaitingPlayersCount();
    }

    public Player(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return username.equals(player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
