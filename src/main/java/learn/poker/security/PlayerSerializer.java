package learn.poker.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import learn.poker.models.Player;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerSerializer extends StdSerializer<Player> {

    public PlayerSerializer() {
        super(Player.class);
    }

    @Override
    public void serialize(Player player, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("playerId", player.getPlayerId());
        jsonGenerator.writeStringField("username", player.getUsername());
        jsonGenerator.writeStringField("displayName", player.getDisplayName());
        jsonGenerator.writeBooleanField("enabled", player.isEnabled());
        jsonGenerator.writeNumberField("accountBalance", player.getAccountBalance());

        List<GrantedAuthority> authorities = (List<GrantedAuthority>) player.getAuthorities();
        if (authorities != null && !authorities.isEmpty()) {
            jsonGenerator.writeObjectField("authorities", authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        } else {
            jsonGenerator.writeObjectField("authorities", new ArrayList<>());
        }

        jsonGenerator.writeObjectField("holeCards", player.getHoleCards());
        jsonGenerator.writeObjectField("position", player.getPosition());
        jsonGenerator.writeBooleanField("playersAction", player.isPlayersAction());

        jsonGenerator.writeEndObject();
    }
}


