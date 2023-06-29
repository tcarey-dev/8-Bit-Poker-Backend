package learn.poker.data;

import learn.poker.models.Player;
import learn.poker.models.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PlayerJdbcTemplateRepositoryTest {

    @Autowired
    PlayerJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindByUsername() {
        Player player = repository.findByUsername("fred@astair.com");
        assertNotNull(player);

        assertEquals(3, player.getPlayerId());
    }

    @Test
    void shouldNotFindUnknownUsername() {
        assertNull(repository.findByUsername("fake@fake.com"));
    }

    @Test
    void shouldCreate() {
        Player player = new Player(0,"miracle@test.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, List.of("USER"));

        Player result = repository.create(player);
        assertNotNull(result);
        assertEquals(7, result.getPlayerId());
        assertEquals(result, repository.findByUsername("miracle@test.com"));
    }

    @Test
    void shouldUpdate() {

    }

    @Test
    void shouldNotUpdateUnknownId() {

    }


    @Test
    void shouldGetRolesByUsername() {
        List<String> roles = repository.getRolesByUsername("billy@bob.com");
        assertNotNull(roles);
        assertTrue(roles.contains("ADMIN"));
    }

    @Test
    void shouldNotGetRolesByInvalidUsername() {

    }


}