package learn.poker.data;

import learn.poker.models.Player;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlayerRepository {
    @Transactional
    Player findByUsername(String username);

    @Transactional
    Player create(Player user);

    @Transactional
    boolean update(Player user);

    public List<String> getRolesByUsername(String username);
}
