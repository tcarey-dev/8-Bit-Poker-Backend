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
    }

    @Test
    void shouldFindById(){
        Room room = repository.findById(2);
        assertNotNull(room);

        assertEquals(2, room.getRoomId());
        assertEquals(0.5, room.getStake());
        assertEquals(2, room.getSeats());
    }

    @Test
    void shouldNotFindNonExistentId(){
        assertNull(repository.findById(9999));
    }

    @Test
    void shouldCreate(){
        Room room = new Room();
        room.setStake(1.00);
        room.setSeats(2);
        Room result = repository.create(room);
        assertNotNull(result);
        assertEquals(6, result.getRoomId());
        assertEquals(result, repository.findById(6));
    }

    @Test
    void shouldUpdate(){
        Room room = repository.findById(3);
        room.setStake(2.00);
        room.setSeats(8);
        assertTrue(repository.update(room));
        assertEquals(room, repository.findById(3));
    }

    @Test
    void willDelete(){
        assertTrue(repository.deleteById(3));
    }

    @Test
    void shouldNotDeleteByNonExistentId(){
        assertFalse(repository.deleteById(999999));
    }

}