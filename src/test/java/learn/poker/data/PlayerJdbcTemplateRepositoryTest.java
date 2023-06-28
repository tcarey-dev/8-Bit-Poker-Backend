package learn.poker.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

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

    }

    @Test
    void shouldNotFindUnknownUsername() {

    }

    @Test
    void shouldCreate() {

    }

    @Test
    void shouldUpdate() {

    }

    @Test
    void shouldNotUpdateUnknownId() {

    }


}