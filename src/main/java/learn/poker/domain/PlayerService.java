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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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

    private Result<Player> validate(Credential credential) {
        Result<Player> result = new Result<>();
        if (credential.getUsername() == null || credential.getUsername().isBlank()) {
            result.addMessage("username is required");
            return result;
        }

        if (credential.getPassword() == null) {
            result.addMessage("password is required");
            return result;
        }

        if (credential.getUsername().length() > 50) {
            result.addMessage("username must be less than 50 characters");
        }

        if (!isValidPassword(credential.getPassword())) {
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