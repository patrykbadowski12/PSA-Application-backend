package net.respekto.psawebapi;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;



@RestController
public class SessionController {

    @GetMapping("/api")
    String dupa(WebSession session){
            session.start();
        return "cokolwiek";
    }


}