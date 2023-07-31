package learn.poker.data;

import learn.poker.data.mappers.BoardMapper;
import learn.poker.data.mappers.GameMapper;
import learn.poker.data.mappers.PlayerMapper;
import learn.poker.models.Board;
import learn.poker.models.Card;
import learn.poker.models.Game;
import learn.poker.models.Player;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final PlayerRepository playerRepository;

    public GameJdbcTemplateRepository(JdbcTemplate jdbcTemplate, PlayerRepository playerRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.playerRepository = playerRepository;
    }


    @Override
    public Game findById(int gameId) {
        final String sql = "select game_id, pot, winner, bet_amount, last_action from game where game_id = ?;";

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

        final String sql = "insert into game " +
                "(pot, winner) " +
                "values "+
                "(?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, game.getPot());
            ps.setString(2, game.getWinner());
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

        if (game.getBoard() != null) {
            Board board = createBoard(game.getBoard());

            if (board != null){
                game.setBoard(board);
            }
        }


        if (game.getBoard() != null && game.getPlayers().size() == 2){

            final String sql = "update game set " +
                    "pot = ?, " +
                    "winner = ?, " +
                    "bet_amount = ?, " +
                    "board_id = ?, " +
                    "player_one_id = ?, " +
                    "player_two_id = ? " +
                    "where game_id = ?;";

            return jdbcTemplate.update(sql,
                    game.getPot(),
                    game.getWinner(),
                    game.getBetAmount(),
                    game.getBoard().getBoardId(),
                    game.getPlayers().get(0).getPlayerId(),
                    game.getPlayers().get(1).getPlayerId(),
                    game.getGameId()) > 0;
        }

        if (game.getBoard() == null && game.getPlayers().size() == 2){
            final String sql = "update game set " +
                    "pot = ?, " +
                    "winner = ?, " +
                    "bet_amount = ?, " +
                    "player_one_id = ?, " +
                    "player_two_id = ? " +
                    "where game_id = ?;";

            return jdbcTemplate.update(sql,
                    game.getPot(),
                    game.getWinner(),
                    game.getBetAmount(),
                    game.getPlayers().get(0).getPlayerId(),
                    game.getPlayers().get(1).getPlayerId(),
                    game.getGameId()) > 0;
        }

        if (game.getBoard() != null && game.getPlayers().size() == 1){
            final String sql = "update game set " +
                    "pot = ?, " +
                    "winner = ?, " +
                    "bet_amount = ?, " +
                    "board_id = ?, " +
                    "player_one_id = ? " +
                    "where game_id = ?;";

            return jdbcTemplate.update(sql,
                    game.getPot(),
                    game.getWinner(),
                    game.getBetAmount(),
                    game.getBoard().getBoardId(),
                    game.getPlayers().get(0).getPlayerId(),
                    game.getGameId()) > 0;
        }

        if (game.getBoard() == null && game.getPlayers().size() == 1){
            final String sql = "update game set " +
                    "pot = ?, " +
                    "winner = ?, " +
                    "bet_amount = ?, " +
                    "player_one_id = ? " +
                    "where game_id = ?;";

            return jdbcTemplate.update(sql,
                    game.getPot(),
                    game.getWinner(),
                    game.getBetAmount(),
                    game.getPlayers().get(0).getPlayerId(),
                    game.getGameId()) > 0;
        }

        if (game.getBoard() != null && game.getPlayers().size() == 0){
            final String sql = "update game set " +
                    "pot = ?, " +
                    "winner = ?, " +
                    "bet_amount = ?, " +
                    "board_id = ? " +
                    "where game_id = ?;";

            return jdbcTemplate.update(sql,
                    game.getPot(),
                    game.getWinner(),
                    game.getBetAmount(),
                    game.getBoard().getBoardId(),
                    game.getGameId()) > 0;
        }

        if (game.getBoard() == null && game.getPlayers().size() == 0){
            final String sql = "update game set " +
                    "pot = ?, " +
                    "winner = ? " +
                    "bet_amount = ?, " +
                    "where game_id = ?;";

            return jdbcTemplate.update(sql,
                    game.getPot(),
                    game.getWinner(),
                    game.getBetAmount(),
                    game.getGameId()) > 0;
        }

        return false;
    }

    @Override
    public boolean delete(int gameId) {
        Game game = findById(gameId);
        jdbcTemplate.update("UPDATE game SET player_one_id = NULL WHERE player_one_id = ?;", game.getPlayers().get(0).getPlayerId());
        jdbcTemplate.update("UPDATE game SET player_two_id = NULL WHERE player_two_id = ?;", game.getPlayers().get(1).getPlayerId());
        jdbcTemplate.update("UPDATE game SET board_id = NULL WHERE board_id = ?;", game.getBoard().getBoardId());
        jdbcTemplate.update("UPDATE room SET game_id = NULL WHERE game_id = ?;", gameId);
        return jdbcTemplate.update("delete from game where game_id = ?;", gameId) > 0;
    }

    private Board createBoard(Board board) {
        final String sql = """
                insert into board
                	(flop, turn, river)
                    values
                    (?, ?, ?);
                """;

        String flop = board.getFlop().stream()
                    .map(Card::getAbbr).collect(Collectors.joining(","));

        String turn = board.getTurn() != null ? board.getTurn().getAbbr() : "";
        String river = board.getRiver() != null ? board.getTurn().getAbbr() : "";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, flop);
            ps.setString(2, turn);
            ps.setString(3, river);
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        board.setBoardId(keyHolder.getKey().intValue());
        return board;
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
                "p.player_id, p.username, p.password_hash, p.enabled, p.display_name, " +
                "p.account_balance, p.roles, p.hole_cards, p.position, p.is_player_action  " +
                "from player p " +
                "inner join game g on p.player_id = g.player_one_id " +
                "where g.game_id = ?;";

        final String player2sql = "select " +
                "p.player_id, p.username, p.password_hash, p.enabled,  p.display_name, " +
                "p.account_balance, p.roles, p.hole_cards, p.position, p.is_player_action  " +
                "from player p " +
                "inner join game g on p.player_id = g.player_two_id " +
                "where g.game_id = ?;";


        List<Player> players = new ArrayList<>();

        Player player1 = jdbcTemplate.query(player1sql, new PlayerMapper(), game.getGameId()).stream().findFirst().orElse(null);
        if (player1 != null){
            List<String> player1Roles = playerRepository.getRolesByUsername(player1.getUsername());
            player1.setAuthorities(player1Roles);
            players.add(player1);
        }

        Player player2 = jdbcTemplate.query(player2sql, new PlayerMapper(), game.getGameId()).stream().findFirst().orElse(null);
        if (player2 != null) {
            List<String> player2Roles = playerRepository.getRolesByUsername(player2.getUsername());
            player2.setAuthorities(player2Roles);
            players.add(player2);
        }

        game.setPlayers(players);
    }

}
