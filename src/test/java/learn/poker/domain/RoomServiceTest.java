package learn.poker.domain;

import learn.poker.data.RoomRepository;
import learn.poker.models.Game;
import learn.poker.models.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RoomServiceTest {

    @Autowired
    RoomService service;

    @MockBean
    RoomRepository repository;

    //happy path -- findAll
    @Test
    void shouldFindAll() {
        Game game1 = new Game();
        Game game2 = new Game();
        Game game3 = new Game();

        when(repository.findAll()).thenReturn(List.of(
                new Room(1, 0.25, 2, game1),
                new Room(2, 0.5,2, game2),
                new Room(3, 0.75, 2, game3)
        ));

        List<Room> rooms = service.findAll();

        assertEquals(3, rooms.size());
    }

    //unhappy path -- findById
    @Test
    void shouldNotFindNonExistingId(){
        Room room = service.findById(999);
        assertNull(room);
    }

    //happy path -- findById
    @Test
    void shouldFindById(){
        when(repository.findById(3)).thenReturn(new Room());
        Room room = service.findById(3);
        assertNotNull(room);
    }

    //unhappy paths -- add
    @Test
    void shouldNotAddNull(){
        Result<Room> result = service.add(null);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Room cannot be null", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddWhenSeatsAreEqualToZero(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(0);
        room.setStake(1.00);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.add(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Number of seats must be greater than 0", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddWhenSeatsAreLessThanZero(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(-2);
        room.setStake(1.00);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.add(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Number of seats must be greater than 0", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddWhenStakesAreEqualToTwoCents(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(2);
        room.setStake(0.02);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.add(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Stakes must be greater than 0.02", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddWhenStakesAreLessThanTwoCents(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(2);
        room.setStake(0.00);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.add(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Stakes must be greater than 0.02", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddWhenStakesInCentsAreNotAnEvenNumber(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(2);
        room.setStake(0.75);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.add(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Stakes must be an even number", result.getMessages().get(0));
    }

    //happy path -- add
    @Test
    void shouldAdd(){
        Room room = new Room();
        room.setRoomId(0);
        room.setSeats(2);
        room.setStake(1.00);

        Room mockOut = new Room();
        mockOut.setRoomId(1);
        mockOut.setSeats(2);
        mockOut.setStake(1.00);

        when(repository.create(room)).thenReturn(mockOut);

        Result<Room> result = service.add(room);

        assertTrue(result.isSuccess());
        assertEquals(mockOut, result.getPayload());
    }

    //unhappy path -- update
    @Test
    void shouldNotUpdateNull(){
        Result<Room> result = service.update(null);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Room cannot be null", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdateWhenSeatsAreEqualToZero(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(0);
        room.setStake(1.00);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.update(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Number of seats must be greater than 0", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdateWhenSeatsAreLessThanZero(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(-2);
        room.setStake(1.00);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.update(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Number of seats must be greater than 0", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddUpdateStakesAreEqualToTwoCents(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(2);
        room.setStake(0.02);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.update(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Stakes must be greater than 0.02", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddUpdateStakesAreLessThanTwoCents(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(2);
        room.setStake(0.00);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.add(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Stakes must be greater than 0.02", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddUpdateStakesInCentsAreNotAnEvenNumber(){
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(2);
        room.setStake(0.75);

        Game game = new Game();
        room.setGame(game);

        Result<Room> result = service.add(room);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Stakes must be an even number", result.getMessages().get(0));
    }

    //happy path -- update
    @Test
    void shouldUpdate() {
        Room room = new Room();
        room.setRoomId(1);
        room.setSeats(2);
        room.setStake(1.00);

        when(repository.update(room)).thenReturn(true);

        Result<Room> result = service.update(room);

        assertEquals(ResultType.SUCCESS, result.getType());
    }

    //unhappy path -- delete
    @Test
    void shouldNotDeleteNonExistentRoom(){
        assertFalse(service.deleteById(999));
    }

    //happy path -- delete
    @Test
    void shouldDelete(){
        when(repository.deleteById(1)).thenReturn(true);
        assertTrue(service.deleteById(1));
    }

}
