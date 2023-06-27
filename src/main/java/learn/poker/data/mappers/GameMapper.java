package learn.poker.data.mappers;

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
        RoomMapper roomMapper = new RoomMapper();
        game.setRoom(roomMapper.mapRow(rs, rowNum));

        return new Game(

                rs.getInt("board_id"),
                List.of(rs.getInt("player_one_id"),
                rs.getInt("player_two_id"));
    }


}
