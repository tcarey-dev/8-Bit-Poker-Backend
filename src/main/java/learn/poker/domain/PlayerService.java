package learn.poker.domain;

import learn.poker.models.Player;
import learn.poker.security.Credential;
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
    public Player loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = repository.findByUsername(username);

        if (player == null) {
            throw new UsernameNotFoundException(username + " not found.");
        }

        return player;
    }

    public Result<Player> create(Credential credential) {
        Result<Player> result = validate(credential);
        if (!result.isSuccess()) {
            return result;
        }

        String hashedPassword = encoder.encode(credential.getPassword());

        Player player = new Player(0, credential.getUsername(),
                hashedPassword, true, List.of("USER"));

        try {
            player = repository.create(player);
            result.setPayload(player);
        } catch (DuplicateKeyException e) {
            result.addMessage("The provided username already exists", ResultType.INVALID);
        }

        return result;
    }

    public Result<Player> update(Player player) {
        Result<Player> result = validate(player);
        if (!result.isSuccess()) {
            return result;
        }

        boolean success = repository.update(player);

        if (success) {
            result.setPayload(player);
        } else {
            result.addMessage(String.format("Player %s not found.", player.getUsername()), ResultType.NOT_FOUND);
        }

        return result;

    }

    private Result<Player> validate(Player player) {
        Result<Player> result = new Result<>();

        if (player == null) {
            result.addMessage("Player cannot be null.", ResultType.INVALID);
            return result;
        }

        Credential playerCredential = new Credential();
        playerCredential.setUsername(player.getUsername());
        playerCredential.setPassword(player.getPassword());
        result = validate(playerCredential);

        if (!result.isSuccess()) {
            return result;
        }

        if (player.getPlayerId() <= 0) {
            result.addMessage(String.format("Player %s not found.", player.getUsername()), ResultType.NOT_FOUND);
        }

        if (player.getAccountBalance() < 0) {
            result.addMessage("Account balance cannot be negative", ResultType.INVALID);
        }

        if (player.getAuthorities().isEmpty()) {
            result.addMessage("Player must be either User or Admin", ResultType.INVALID);
        }

        if (player.getHoleCards().size() != 0 || player.getHoleCards().size() != 2) {
            result.addMessage("Player must have either zero or exactly two hole cards", ResultType.INVALID);
        }

        return result;
    }

    private Result<Player> validate(Credential credential) {
        Result<Player> result = new Result<>();
        if (credential.getUsername() == null || credential.getUsername().isBlank()) {
            result.addMessage("username is required", ResultType.INVALID);
            return result;
        }

        if (credential.getPassword() == null) {
            result.addMessage("password is required", ResultType.INVALID);
            return result;
        }

        if (credential.getUsername().length() > 50) {
            result.addMessage("username must be less than 50 characters", ResultType.INVALID);
        }

        if (!isValidPassword(credential.getPassword())) {
            result.addMessage(
                    "password must be at least 8 character and contain a digit," +
                            " a letter, and a non-digit/non-letter", ResultType.INVALID);
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