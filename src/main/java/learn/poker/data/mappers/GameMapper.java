package learn.poker.data.mappers;

import learn.poker.models.Action;
import learn.poker.models.Game;
import learn.poker.models.Room;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GameMapper implements RowMapper<Game> {
    @Override
    public Game mapRow(ResultSet rs, int rowNum) throws SQLException {
        Game game = new Game();
        game.setGameId(rs.getInt("game_id"));
        game.setPot(rs.getInt("pot"));
        game.setWinner(rs.getString("winner"));
        String action = rs.getString("last_action");
        if (action != null){
            game.setLastAction(Action.valueOf(action));
        }
        return game;
    }
}
