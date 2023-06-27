package learn.poker.controllers.mappers;

import learn.poker.models.Player;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayerMapper implements RowMapper<Player> {
    private final List<String> roles;

    public PlayerMapper(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Player(
                rs.getInt("player_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getBoolean("enabled"),
                roles);
    }
}
