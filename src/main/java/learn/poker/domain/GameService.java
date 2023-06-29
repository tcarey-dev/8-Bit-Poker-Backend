package learn.poker.domain;

import learn.poker.data.GameRepository;
import learn.poker.models.Board;
import learn.poker.models.Card;
import learn.poker.models.Game;
import learn.poker.models.Player;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GameService {

    private final GameRepository repository;


    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    public Game findById(int gameId) {
        return repository.findById(gameId);
    }

    public Player getWinner(List<Player> players, Board board) {
        //TODO call pokerApi to determine winner
        return null;
    }

    public Result<Game> add(Game game) {
        Result<Game> result = validate(game);

        if (!result.isSuccess()) {
            return result;
        }

        if (game.getGameId() > 0) {
            result.addMessage("Cannot create an existing game.");
            return result;
        }

        Board board = new Board();
        board.setFlop(List.of(Card.EMPTY, Card.EMPTY, Card.EMPTY));
        board.setTurn(Card.EMPTY);
        board.setRiver(Card.EMPTY);
        game.setBoard(board);

        game = repository.create(game);
        result.setPayload(game);
        return result;
    }

    public Result<Game> update(Game game) {
        Result<Game> result = validate(game);

        if (!result.isSuccess()) {
            return result;
        }

        boolean updated = repository.update(game);
        if (!updated) {
            result.addMessage("Game doesn't exist", ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean deleteById(int gameId) {
        Result<Game> result = new Result<>();
        if (!repository.delete(gameId)) {
            result.addMessage(String.format("Game: %s doesn't exist", gameId), ResultType.NOT_FOUND);
        }

        return repository.delete(gameId);
    }

    private Result<Game> validate(Game game) {
        Result<Game> result = new Result<>();
        if (game.getPot() < 0) {
            result.addMessage("pot must be greater than or equal to zero", ResultType.INVALID);
            return result;
        }

        if (game.getPlayers().size() != 2) {
            result.addMessage("two players are required to start a game", ResultType.INVALID);
            return result;
        }

        return result;
    }


}
