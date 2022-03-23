package startit.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import startit.game.Player;
import startit.service.PlayerRepository;


import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final PlayerRepository repository;

    @Autowired
    public WebSocketConfiguration(PlayerRepository repository) {
        this.repository = repository;
    }

    @Bean
    public WebSocketHandler handler() { return new SocketHandler(); }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(handler(), "/websocket").setAllowedOrigins("*")
                .addInterceptors(new HttpHandshakeInterceptor());
    }

    class HttpHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request,ServerHttpResponse response,WebSocketHandler wsHandler,Map attributes )
        {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");
            Optional<Player> player = repository.getPlayerByToken(token);
            if (player.isPresent()){
                return true;
                }
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        }
    }
}