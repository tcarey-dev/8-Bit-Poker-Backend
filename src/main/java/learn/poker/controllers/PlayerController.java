package learn.poker.controllers;

import learn.poker.domain.PlayerService;
import learn.poker.domain.Result;
import learn.poker.models.Player;
import learn.poker.models.Room;
import learn.poker.security.Credential;
import learn.poker.security.JwtConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins ={"http://localhost:3000"})
@ConditionalOnWebApplication
public class PlayerController {

    private final AuthenticationManager authenticationManager;
    private final JwtConverter jwtConverter;
    private final PlayerService service;


    public PlayerController(AuthenticationManager authenticationManager, JwtConverter jwtConverter, PlayerService service) {
        this.authenticationManager = authenticationManager;
        this.jwtConverter = jwtConverter;
        this.service = service;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody Credential credential){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credential.getUsername(), credential.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authToken);

            if(authentication.isAuthenticated()) {
                Player player = (Player) authentication.getPrincipal();
                return new ResponseEntity<>(makePlayerTokenMap(player), HttpStatus.OK);
            }

        } catch (AuthenticationException ex) {
            System.out.println(ex);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@AuthenticationPrincipal Player player){
        return new ResponseEntity<>(makePlayerTokenMap(player), HttpStatus.OK);
    }

    @PostMapping("/create-account")
    public ResponseEntity<Object> create(@RequestBody Credential credential){
        Result<Player> result = service.create(credential);

        if(!result.isSuccess()){
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }
        HashMap<String, Integer> map = new HashMap<>();
        map.put("playerId", result.getPayload().getPlayerId());

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getPlayerByUserName(@PathVariable String username){
        Player player = service.loadUserByUsername(username);
        if(player == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(player);

    }

//    @PostMapping
//    public ResponseEntity<?> create(@RequestBody Credential credential){
//        Result<Player> result = service.create(credential);
//        if(result.isSuccess()){
//            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
//        }
//        return ErrorResponse.build(result);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Player player){
        Result<Player> result = service.update(player);
        if(result.isSuccess()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> delete(@PathVariable int id){
//        if(service.deleteById(id)){
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    private HashMap<String, String> makePlayerTokenMap(Player player){
        HashMap<String, String> map = new HashMap<>();
        String jwtToken = jwtConverter.getTokenFromUser(player);
        map.put("jwt_token", jwtToken);
        return map;
    }
}
