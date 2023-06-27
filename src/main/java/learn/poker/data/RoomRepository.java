package learn.poker.data;

import learn.poker.models.Room;

import java.util.List;

public interface RoomRepository {
    List<Room> findAll();

    Room findById(int roomId);

    Room create(Room room);

    boolean update(Room room);

    boolean deleteById(int roomId);
}
