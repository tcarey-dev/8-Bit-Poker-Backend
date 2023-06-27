package learn.poker.domain;

import learn.poker.models.Player;
import learn.poker.security.Credentials;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import learn.poker.data.PlayerRepository;

import java.util.List;

@Service
public class PlayerService implements UserDetailsService {


    private final PlayerRepository repository;
    private final PasswordEncoder encoder;

    public PlayerService(PlayerRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = repository.findByUsername(username);

        if (player == null) {
            throw new UsernameNotFoundException(username + " not found.");
        }

        return player;
    }

    public Result<Player> create(Credentials credentials) {
        Result<Player> result = validate(credentials);
        if (!result.isSuccess()) {
            return result;
        }


        if(player.getPlayerId() != 0){
            result.addMessage("playerId should not be set for add", ResultType.INVALID);

        String hashedPassword = encoder.encode(credentials.getPassword());

        Player player = new Player(0, credentials.getUsername(),
                hashedPassword, true, List.of("USER"));

        try {
            player = repository.create(player);
            result.setPayload(player);
        } catch (DuplicateKeyException e) {
            result.addMessage("The provided username already exists", ResultType.INVALID);

        }

        return result;
    }

    private Result<Player> validate(Credentials credentials) {
        Result<Player> result = new Result<>();
        if (credentials.getUsername() == null || credentials.getUsername().isBlank()) {
            result.addMessage("username is required");
            return result;
        }


        if(player.getPlayerId() <= 0){
            result.addMessage("playerId must be set for update", ResultType.INVALID);
        }

        if(!repository.update(player)){
            String msg = String.format("playerId %s was not found", player.getPlayerId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean deleteById(int playerId){
        return repository.delete(playerId);
    }

    private Result<Player> validate(Player player){
        Result<Player> result = new Result<>();

        if(player == null){
            result.addMessage("Player cannot be null", ResultType.INVALID);
            return result;

        if (credentials.getPassword() == null) {
            result.addMessage("password is required");
            return result;
        }

        if (credentials.getUsername().length() > 50) {
            result.addMessage("username must be less than 50 characters");
        }

        if (!isValidPassword(credentials.getPassword())) {
            result.addMessage(
                    "password must be at least 8 character and contain a digit," +
                            " a letter, and a non-digit/non-letter");

        }

        return result;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        int digits = 0;
        int letters = 0;
        int others = 0;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isLetter(c)) {
                letters++;
            } else {
                others++;
            }
        }

        return digits > 0 && letters > 0 && others > 0;
    }
}