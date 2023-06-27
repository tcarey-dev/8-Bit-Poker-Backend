package learn.poker.data.mappers;

import learn.poker.models.Room;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomMapper implements RowMapper<Room> {
    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Room(
                rs.getInt("room_id"),
                rs.getDouble("stake"),
                rs.getInt("seats"));
    }
}
