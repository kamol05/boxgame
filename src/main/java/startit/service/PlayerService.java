package startit.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import startit.game.Player;
import startit.game.SessionClass;
import startit.game.Setups;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public String getToken(String username) {
        SessionClass sc = new SessionClass();
        String token = UUID.randomUUID().toString();
        Optional<Player> optional = repository.getPlayerByUsername(username);
        if(optional.isPresent()){
            Player player = optional.get();
            player.setToken(token);
            repository.save(player);
            sc.setPlayer(player);
            return token;
        }
        Player player = new Player();
        player.setUsername(username);
        player.setToken(token);
        sc.setPlayer(player);
        repository.save(player);
        return token;
    }
    public ResponseEntity<Map<String, String>> getMapResponseEntity(String username, String password) {
        boolean passwordCorrect = password.equals(String.valueOf(Setups.USERS_PASSWORD.getValue()));
        if (username.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","true" ,"errorMessage","Absent or invalid username"));
        }
        if (!passwordCorrect){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","true" ,"errorMessage","Password mismatch"));
        }
        SessionClass.checkAndCloseSession(username); //close session if user logged in
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", getToken(username)));
    }

}
