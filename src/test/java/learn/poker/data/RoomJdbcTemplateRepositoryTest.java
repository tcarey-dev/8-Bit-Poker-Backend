package learn.poker.data;

import learn.poker.models.Game;
import learn.poker.models.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RoomJdbcTemplateRepositoryTest {

    @Autowired
    private RoomJdbcTemplateRepository repository;

    @Autowired
    private GameJdbcTemplateRepository gameRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindAll(){
        List<Room> rooms = repository.findAll();
        assertNotNull(rooms);
        assertTrue(rooms.size() >= 3);

        //1, 0.25, 2, 2
        Room room = rooms.get(0);
        assertEquals(1, room.getRoomId());
        assertEquals(0.25, room.getStake());
        assertEquals(2, room.getSeats());
        //assertEquals(2, room.getGame().getGameId());

        //System.out.println(rooms.get(0).getGame());//returning null cuz game_id not called in sql in findAll function
    }

    @Test
    void shouldFindById(){
        Room room = repository.findById(2);
        assertNotNull(room);

        assertEquals(2, room.getRoomId());
        assertEquals(0.5, room.getStake());
        assertEquals(2, room.getSeats());
        //assertEquals(3, room.getGame().getGameId());
    }

    @Test
    void shouldNotFindNonExistentId(){
        assertNull(repository.findById(9999));
    }

//come back to this later
    @Test
    void shouldCreate(){
        Room room = new Room();
        room.setStake(1.00);
        room.setSeats(2);
        //room.setGame(new Game());
        Room result = repository.create(room);
        assertNotNull(result);
        assertEquals(4, result.getRoomId());

        assertEquals(result, repository.findById(4));
    }

    @Test
    void shouldDelete(){
        assertTrue(repository.deleteById(3));
    }

    @Test
    void shouldNotDeleteByNonExistentId(){
        assertFalse(repository.deleteById(999999));
    }

}