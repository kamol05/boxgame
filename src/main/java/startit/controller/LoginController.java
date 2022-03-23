package startit.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import startit.game.Player;
import startit.game.SessionClass;
import startit.game.Setups;
import startit.service.PlayerService;
import java.io.IOException;
import java.util.Map;


@RestController
public class LoginController {
    private final PlayerService service;
    @Autowired
    public LoginController(PlayerService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> create(@RequestBody User user){
        return service.getMapResponseEntity(user.username, user.password);
    }

    @Setter
    @Getter
     static class User{
        private String username;
        private String password;
    }

}
