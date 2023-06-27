package learn.poker.controllers;

import learn.poker.domain.Result;
import learn.poker.domain.RoomService;
import learn.poker.models.Room;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = {"http://localhost:3000"})
public class RoomController {
    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @GetMapping
    public List<Room> findAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int roomId){
        Room room = service.findById(roomId);
        if(room == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(room);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Room room){
        Result<Room> result = service.add(room);
        if(result.isSuccess()){
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Room room)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id)

}
