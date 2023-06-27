package learn.poker.controllers;

import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GameController {

//    @Autowired
//    SimpMessagingTemplate simpMessagingTemplate;

    // this is a placeholder method for testing, will need to be modified according to dev plan spec once rest of backend is complete
//    @MessageMapping("/welcome")
//    @SendTo("/topic/room")
//    public void welcomeSubscribers(@Payload String message) throws Exception {
//        Thread.sleep(1000);
//        this.simpMessagingTemplate.convertAndSend("/topic/room", message);
//    }

    @SubscribeMapping("/subscribe")
    public String sendOneTimeMessage() {
        return "server one-time message via the application";
    }
}
