package learn.poker.data.mappers;

import learn.poker.models.Room;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomMapper implements RowMapper<Room> {
    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setStake(rs.getDouble("stake"));
        room.setSeats(rs.getInt("seats"));
        room.setPlayerCount(rs.getInt("player_count"));

        String deckId = rs.getString("deck_id");
        if (deckId != null) {
            room.setDeckId(deckId);
        }
        return room;
    }
}
