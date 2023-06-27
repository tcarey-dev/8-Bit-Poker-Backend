package learn.poker.data;

import learn.poker.data.mappers.BoardMapper;
import learn.poker.data.mappers.PlayerMapper;
import learn.poker.data.mappers.RoomMapper;
import learn.poker.models.Board;
import learn.poker.models.Game;
import learn.poker.models.Player;
import learn.poker.models.Room;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import learn.poker.data.mappers.GameMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class GameJdbcTemplateRepository implements GameRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Game> rowMapper = new GameMapper();

    public GameJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Game findById(int gameId) {
        final String sql = "select game_id, pot, winner from game where game_id = ?;";

        Game game = jdbcTemplate.query(sql, new GameMapper(), gameId).stream().findFirst().orElse(null);

        if(game != null) {
            addRoom(game);
            addBoard(game);
            addPlayers(game);
        }

        return game;
    }

    @Override
    public Game create(Game game) {
        return null;
    }

    @Override
    public boolean update(Game game) {
        return false;
    }

    @Override
    public boolean delete(int gameId) {
        return false;
    }

    private void addRoom(Game game) {
        final String sql = "select " +
                "r.room_id, r.stake, r.seats " +
                "from room r " +
                "inner join game g on r.room_id = g.room_id " +
                "where g.game_id = ?;";

        Room room = jdbcTemplate.query(sql, new RoomMapper(), game.getGameId()).stream()
                .findFirst().orElse(null);

        game.setRoom(room);
    }

    private void addBoard(Game game) {
        final String sql = "select " +
                "b.board_id, b.flop, b.turn, b.river " +
                "from board b " +
                "inner join game g on b.board_id = g.board_id " +
                "where g.game_id = ?;";

        Board board = jdbcTemplate.query(sql, new BoardMapper(), game.getGameId()).stream().findFirst().orElse(null);

        game.setBoard(board);
    }

    @Transactional
    private void addPlayers(Game game) {
        final String player1sql = "select " +
                "p.player_id, p.username, p.password_hash, p.enabled  " +
                "from player p " +
                "inner join game g on p.player_id = g.player_one_id " +
                "where g.game_id = ?;";

        final String player2sql = "select " +
                "p.player_id, p.username, p.password_hash, p.enabled  " +
                "from player p " +
                "inner join game g on p.player_id = g.player_two_id " +
                "where g.game_id = ?;";
        Player player1 = jdbcTemplate.query(player1sql, new PlayerMapper(), game.getGameId()).stream().findFirst().orElse(null);
        Player player2 = jdbcTemplate.query(player2sql, new PlayerMapper(), game.getGameId()).stream().findFirst().orElse(null);

        List<Player> players = List.of(player1, player2);

        game.setPlayers(players);
    }

}
