package learn.poker.data.mappers;

import learn.poker.models.Card;
import learn.poker.models.Player;
import learn.poker.models.Position;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PlayerMapper implements RowMapper<Player> {
    private List<String> roles;

    public PlayerMapper(List<String> roles) {
        this.roles = roles;
    }

    public PlayerMapper(){

    }

    @Override
    public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
        Player player = new Player(
                rs.getInt("player_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getBoolean("enabled"));

        player.setDisplayName("");
        player.setAccountBalance(rs.getInt("account_balance"));

        if(roles != null) {
            player.setAuthorities(roles);
        }

        List<Card> holeCards = Arrays.stream(rs.getString("hole_cards").split(","))
                .map(Card::getCardFromAbbreviation).toList();
        player.setHoleCards(holeCards);

        player.setPosition(Position.valueOf(rs.getString("position")));
        player.setPlayersAction(rs.getBoolean("is_player_action"));
        return player;
    }
}
