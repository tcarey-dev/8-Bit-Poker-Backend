package learn.poker.data;

import learn.poker.data.mappers.RoomMapper;
import learn.poker.models.Room;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class RoomJdbcTemplateRepository implements RoomRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Room> rowMapper = new RoomMapper();

    public RoomJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Room> findAll() {
       final String sql = "select room_id" +
                " stake," +
                " seats" +
                " from room;";

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Room findById(int roomId) {
        final String sql = "select room_id" +
                " stake," +
                " seats" +
                " from room" +
                " where room_id = ?;";

        return jdbcTemplate.query(sql, rowMapper, roomId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Room create(Room room) {
        final String sql = "insert into room " +
                "(stake, seats) values (?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, room.getStake());
            ps.setInt(2, room.getSeats());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        room.setRoomId(keyHolder.getKey().intValue());
        return room;
    }

    @Override
    public boolean update(Room room) {
        final String sql = "update room set " +
                "stake = ?, " +
                "seats = ?;";

        int rowsUpdated = jdbcTemplate.update(sql,
                room.getStake(),
                room.getSeats());

        return rowsUpdated > 0;
    }

    @Override
    public boolean deleteById(int roomId) {
        return jdbcTemplate.update("delete from room where room_id = ?;", roomId) > 0;
    }





}
