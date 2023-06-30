package learn.poker.domain;

import learn.poker.data.GameRepository;
import learn.poker.models.*;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GameService {

    private final GameRepository repository;
    private final RoomService roomService;
    private final DeckService deckService;
    private final PlayerService playerService;


    public GameService(GameRepository repository, RoomService roomService, DeckService deckService, PlayerService playerService) {
        this.repository = repository;
        this.roomService = roomService;
        this.deckService = deckService;
        this.playerService = playerService;
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

        return result;
    }

    public Result<Room> init(Room room) {
        Result<Room> roomResult = new Result<>();

        Game game = new Game(0, null, null, null);
        Result<Game> gameResult = add(game);

        if (!gameResult.isSuccess()){
            roomResult.addMessage("Something went wrong", ResultType.INVALID);
            return roomResult;
        } else {
            room.setGame(gameResult.getPayload());
            return roomService.update(room);
        }
    }

    public Result<Room> start(Room room) {
        Game game = room.getGame();

        double smallBlind = room.getStake()/2;
        double bigBlind = room.getStake();

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        player1.setPosition(Position.SMALLBLIND);
        player1.setPlayersAction(true);

        player2.setPosition(Position.BIGBLIND);
        player2.setPlayersAction(false);

        player1.setAccountBalance(player1.getAccountBalance() - smallBlind);
        player2.setAccountBalance(player2.getAccountBalance() - bigBlind);
        game.setPot(smallBlind + bigBlind);

        Deck deck = deckService.drawCards(4);
        List<PokerApiCard> playerCards = deck.getCards();

        List<Card> cards = playerCards.stream().map(pokerApiCard -> {
            String code = pokerApiCard.getCode();
            return Card.getCardFromAbbreviation(code);
        }).toList();

        player1.setHoleCards(cards.subList(0,1));
        player2.setHoleCards(cards.subList(2,3));

        playerService.update(player1);
        playerService.update(player2);

        game.setPlayers(List.of(player1, player2));
        Result<Game> gameResult = update(game);

        if (!gameResult.isSuccess()) {
            // TODO error
        }

        room.setGame(game);
        return roomService.update(room);
    }

    /**
     *
     handleAction(Game game) {

     if current action == FOLD
     set winner to the non-folding player
     resetState(game)

     // small blind opening call or raise
     if lastAction == null && player.position == SMALLBLIND && currentAction == CALL || RAISE
     update players balance and pot,
     flip playersAction

     // big blind check behind (terminal action)
     if lastAction == CALL && player.position == BIGBLIND && currentAction == CHECK
     flip playersAction and position

     // big blind call behind (terminal action)
     if lastAction == RAISE && player.position == BIGBLIND && currentAction == CALL
     update players balance and pot,
     flip playersAction and position

     // raise
     if lastAction == RAISE || CHECK && currentAction == RAISE
     update players balance and pot,
     flip playersAction

     // terminating call (terminal action)
     if lastAction == RAISE && current action == CALL (terminal action)
     dealNext(game)
     }

     dealNext(game){
     if (board.flop == null)
     deal flop
     else if (board.turn == null)
     deal turn
     else if (board.river == null)
     deal river
     else
     determine winner
     reset state(game)
     flip playersAction and position
     }

     resetState() {
     add pot to winning player's balance
     set pot to zero
     set winner, lastAction, board, and holeCards to null
     }


     /////////////////// UI layer rendering logic
     if lastAction == CHECK && my playersAction == true
     render BET/CHECK/FOLD

     if lastAction == BET || RAISE && my playersAction == true
     render CALL/RAISE/FOLD
     */

}
