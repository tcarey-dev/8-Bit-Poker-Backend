package learn.poker.data;

import learn.poker.models.Card;
import learn.poker.data.mappers.PlayerMapper;
import learn.poker.models.Position;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import learn.poker.models.Player;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PlayerJdbcTemplateRepository implements PlayerRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlayerJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Player findByUsername(String username) {
        List<String> roles = getRolesByUsername(username);

        final String sql = """
                select player_id, username, password_hash, enabled, display_name,
                account_balance, roles, hole_cards, position, is_player_action
                from player
                where username = ?;
                """;

        Player player = jdbcTemplate.query(sql, new PlayerMapper(roles), username)
                .stream()
                .findFirst().orElse(null);

        if(player != null){
            player.setAuthorities(roles);
        }
        return player;
    }

    @Override
    @Transactional
    public Player create(Player player) {

        final String sql = "insert into player (username, password_hash, enabled) values (?, ?, ?);";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getPassword());
            ps.setBoolean(3, player.isEnabled());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        player.setPlayerId(keyHolder.getKey().intValue());

        updateRoles(player);

        return player;
    }

    @Override
    @Transactional
    public boolean update(Player player) {

        final String sql = """
                update player set
                    username = ?,
                    enabled = ?,
                    display_name = ?,
                    account_balance = ?,
                    roles = ?,
                    hole_cards = ?,
                    position = ?,
                    is_player_action = ?
                where player_id = ?
                """;

        String serializedCards = "";
        if (player.getHoleCards() != null){
            List<String> cards = player.getHoleCards().stream().map(Card::getAbbr).toList();
            String serialized = "";
            for (String card : cards){
                serialized += card + ",";
            }
            serializedCards = serialized.substring(0, serialized.length() -1);
        }
        final String holeCards = serializedCards;


        String stringifiedPosition = "";
        if (player.getPosition() != null) {
            stringifiedPosition = player.getPosition().toString();
        }

        final String position = stringifiedPosition;

        int rowsReturned = jdbcTemplate.update(sql,
                player.getUsername(),
                player.isEnabled(),
                player.getDisplayName(),
                player.getAccountBalance(),
                player.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.joining()),
                holeCards,
                position,
                player.isPlayersAction(),
                player.getPlayerId());

        if (rowsReturned > 0) {
            updateRoles(player);
            return true;
        }

        return false;
    }

    private void updateRoles(Player user) {
        // delete all roles, then re-add
        jdbcTemplate.update("delete from player_role where player_id = ?;", user.getPlayerId());

        Collection<GrantedAuthority> authorities = user.getAuthorities();

        if (authorities == null) {
            return;
        }

        for (GrantedAuthority role : authorities) {
            String sql = """
                    insert into player_role (player_id, role_id)
                        select ?, role_id from role where `name` = ?;
                    """;
            jdbcTemplate.update(sql, user.getPlayerId(), role.getAuthority());
        }
    }

    @Override
    public List<String> getRolesByUsername(String username) {
        final String sql = """
                select r.name
                from player_role ur
                inner join role r on ur.role_id = r.role_id
                inner join player pl on ur.player_id = pl.player_id
                where pl.username = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowId) -> rs.getString("name"), username);
    }
}