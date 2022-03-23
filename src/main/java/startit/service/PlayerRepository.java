package startit.service;

import org.springframework.data.jpa.repository.JpaRepository;
import startit.game.Player;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
        Optional<Player> getPlayerByUsername(String username);
        Optional<Player> getPlayerByToken(String token);
}
