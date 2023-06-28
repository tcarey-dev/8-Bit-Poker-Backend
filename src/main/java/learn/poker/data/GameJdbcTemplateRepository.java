package learn.poker.data;

import learn.poker.data.mappers.BoardMapper;
import learn.poker.data.mappers.GameMapper;
import learn.poker.data.mappers.PlayerMapper;
import learn.poker.models.Board;
import learn.poker.models.Card;
import learn.poker.models.Game;
import learn.poker.models.Player;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GameJdbcTemplateRepository implements GameRepository {
    private final JdbcTemplate jdbcTemplate;
    private PlayerRepository playerRepository;

    public GameJdbcTemplateRepository(JdbcTemplate jdbcTemplate, PlayerRepository playerRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.playerRepository = playerRepository;
    }


    @Override
    public Game findById(int gameId) {
        final String sql = "select game_id, pot, winner from game where game_id = ?;";

        Game game = jdbcTemplate.query(sql, new GameMapper(), gameId).stream().findFirst().orElse(null);

        if(game != null) {
            addBoard(game);
            addPlayers(game);
        }

        return game;
    }

    @Override
    @Transactional
    public Game create(Game game) {
        if (game.getPlayers().size() < 2) {
            return null;
        }

        final String boardSql = "insert into board " +
                "(flop, turn, river) " +
                "values " +
                "(?, ?, ?);";

        KeyHolder boardKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(boardSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, game.getBoard().getFlop().stream()
                    .map(Card::getAbbr).collect(Collectors.joining()));
            ps.setString(2, game.getBoard().getTurn().getAbbr());
            ps.setString(3, game.getBoard().getRiver().getAbbr());
            return ps;
        }, boardKeyHolder);

        Player player1 = playerRepository.create(game.getPlayers().get(0));
        Player player2 = playerRepository.create(game.getPlayers().get(1));

        final String sql = "insert into game " +
                "(pot, winner, board_id, player_one_id, player_two_id) " +
                "values "+
                "(?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, game.getPot());
            ps.setString(2, game.getWinner());
            ps.setInt(3, boardKeyHolder.getKey().intValue());
            ps.setInt(4, player1.getPlayerId());
            ps.setInt(5, player2.getPlayerId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        game.setGameId(keyHolder.getKey().intValue());
        return game;
    }

    @Override
    public boolean update(Game game) {

        final String sql = "update game set " +
                "pot = ?, " +
                "winner = ?, " +
                "board_id = ?, " +
                "player_one_id = ?, " +
                "player_two_id = ? " +
                "where game_id = ?;";

        return jdbcTemplate.update(sql,
                game.getPot(),
                game.getWinner(),
                game.getBoard().getBoardId(),
                game.getPlayers().get(0).getPlayerId(),
                game.getPlayers().get(1).getPlayerId(),
                game.getGameId()) > 0;
    }

    @Override
    @Transactional
    public boolean delete(int gameId) {
        jdbcTemplate.update("delete from room where game_id = ?;", gameId);
        return jdbcTemplate.update("delete from game where game_id = ?;", gameId) > 0;
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
