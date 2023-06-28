package learn.poker.data;

import learn.poker.data.mappers.GameMapper;
import learn.poker.data.mappers.RoomMapper;
import learn.poker.models.Game;
import learn.poker.models.Room;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class RoomJdbcTemplateRepository implements RoomRepository {

    private final JdbcTemplate jdbcTemplate;
    private final GameRepository gameRepository;
    private final RowMapper<Room> rowMapper = new RoomMapper();

    public RoomJdbcTemplateRepository(JdbcTemplate jdbcTemplate, GameRepository gameRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.gameRepository = gameRepository;
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
                " seats," +
                " game_id" +
                " from room" +
                " where room_id = ?;";

        Room room = jdbcTemplate.query(sql, rowMapper, roomId)
                .stream()
                .findFirst()
                .orElse(null);

        if (room != null) {
            addGame(room);
        }
        return room;
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
                "seats = ?, " +
                "game_id = ?;";

        int rowsUpdated = jdbcTemplate.update(sql,
                room.getStake(),
                room.getSeats(),
                room.getGame().getGameId());

        return rowsUpdated > 0;
    }

    @Override
    public boolean deleteById(int roomId) {
        return jdbcTemplate.update("delete from room where room_id = ?;", roomId) > 0;
    }

    private void addGame(Room room) {
        final String sql = "select " +
                "g.game_id, g.pot, g.winner, g.board_id, g.player_one_id, g.player_two_id " +
                "from game g " +
                "inner join room r on g.game_id = r.game_id " +
                "where r.room_id = ?;";
        Game game = jdbcTemplate.query(sql, new GameMapper(), room.getRoomId()).stream()
                .findFirst().orElse(null);
        game = gameRepository.findById(game.getGameId());
        room.setGame(game);
    }

}
