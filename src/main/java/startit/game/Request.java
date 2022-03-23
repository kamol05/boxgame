package startit.game;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {
    private int id;
    private String action;
    private int block;
    private int kick;

    public static synchronized boolean verify(String message){
        Request request = new Request();
        try {
            request = new Gson().fromJson(message, Request.class);
        }catch (Exception e){
            return false;
        }
//        if (block < 0 || block > 3) {
//            throw new Error("Invalid block value");
//        }
//        if (kick < 0 || kick > 3) {
//            throw new Error("Invalid kick value");
//        }
        throw new Error("Unsupported action: " + request.action);

    }



}
