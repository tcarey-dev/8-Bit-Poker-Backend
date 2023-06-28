package learn.poker.domain;

import learn.poker.data.RoomRepository;
import learn.poker.models.Room;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public List<Room> findAll(){
        return repository.findAll();
    }

    public Room findById(int roomId){
        return repository.findById(roomId);
    }

    public Result<Room> add(Room room){
        Result<Room> result = validate(room);
        if(!result.isSuccess()){
            return result;
        }

        if(room.getRoomId() != 0 ){
            result.addMessage("roomId should not be set for add", ResultType.INVALID);
        }

        room = repository.create(room);
        result.setPayload(room);
        return result;
    }

    public Result<Room> update(Room room){
        Result<Room> result = validate(room);
        if(!result.isSuccess()){
            return result;
        }

        if(room.getRoomId() <= 0){
            result.addMessage("roomId must be set for update", ResultType.INVALID);
            return result;
        }

        if(!repository.update(room)){
            String msg = String.format("roomId %s not found", room.getRoomId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean deleteById(int roomId){
        return repository.deleteById(roomId);
    }

    private Result<Room> validate(Room room){
        Result<Room> result = new Result<>();

        if(room == null){
            result.addMessage("Room cannot be null", ResultType.INVALID);
            return result;
        }
        if(room.getSeats() <= 0){
            result.addMessage("Number of seats must be greater than 0", ResultType.INVALID);
        }
        if(room.getStake() <= 0.02) {
            result.addMessage("Stakes must be greater than 0.02", ResultType.INVALID);
        }
        double stakeInCents = room.getStake() * 100;
        if(stakeInCents % 2 != 0){
            result.addMessage("Stakes must be an even number", ResultType.INVALID);
        }
        return result;
    }
}

