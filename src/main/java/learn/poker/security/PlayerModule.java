package learn.poker.security;

import com.fasterxml.jackson.databind.module.SimpleModule;
import learn.poker.models.Player;

public class PlayerModule extends SimpleModule {

    public PlayerModule() {
        super();

        this.addSerializer(Player.class, new PlayerSerializer());
    }
}
