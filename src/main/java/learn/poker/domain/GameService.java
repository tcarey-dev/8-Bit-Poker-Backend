package learn.poker.domain;

import learn.poker.data.GameRepository;
import learn.poker.models.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GameService {

    private final GameRepository repository;
    private final RoomService roomService;
    private final DeckService deckService;
    private final PlayerService playerService;
    private final WinnerService winnerService;

    public GameService(GameRepository repository, RoomService roomService, DeckService deckService, PlayerService playerService, WinnerService winnerService) {
        this.repository = repository;
        this.roomService = roomService;
        this.deckService = deckService;
        this.playerService = playerService;
        this.winnerService = winnerService;
    }

    public Game findById(int gameId) {
        return repository.findById(gameId);
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

        if(game.getPlayers() == null || game.getPlayers().size() < 1) {
            result.addMessage("Cannot update a game with no players", ResultType.INVALID);
            return result;
        }

        boolean updated = repository.update(game);
        if (!updated) {
            result.addMessage("Game doesn't exist", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(game);
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
            roomResult.addMessage("Something went wrong when initializing the game", ResultType.INVALID);
            return roomResult;
        } else {
            room.setGame(gameResult.getPayload());

            String deckId = deckService.getDeckId();

            if (deckId == null){
                roomResult.addMessage("Could not initialize deck id", ResultType.NOT_FOUND);
                return roomResult;
            }

            room.setDeckId(deckId);
            return roomService.update(room);
        }
    }

    public Result<Room> addPlayer(Room room) {
        Result<Room> result = new Result<>();

        if(room.getGame() == null || room.getGame().getPlayers().isEmpty()){
            result.addMessage("Cannot add empty players to game", ResultType.INVALID);
            return result;
        }

        List<Player> players = room.getGame().getPlayers();
        Game game = findById(room.getGame().getGameId());

        for (Player p : players) {
            Player match = game.getPlayers().stream().filter(player -> player.getUsername().equalsIgnoreCase(p.getUsername())).findFirst().orElse(null);
            if (match != null && players.size() == 1) {
                result.addMessage("Cannot add duplicate player to game", ResultType.INVALID);
                return result;
            }
        }

        game.setPlayers(players);

        Result<Game> gameResult = update(game);
        if (!gameResult.isSuccess()){
            result.addMessage("Was unable to update the game after adding new players", ResultType.INVALID);
            return result;
        }

        Game updatedGame = findById(game.getGameId());
        room.setGame(updatedGame);

        Result<Room> roomUpdateResult = roomService.update(room);
        if (!roomUpdateResult.isSuccess()){
            result.addMessage("Was unable to update room after updating game");
        }
        result.setPayload(room);
        return result;
    }

    public Result<Room> leaveGame(Room room, String username) {
        /**
         * remove them from the list of players from game
         * then check if the game has any players
         * if game doesn't have any player left then set back to null
         */

        Result<Room> roomResult = new Result<>();
        Game game = room.getGame();
        Player exitingPlayer = game.getPlayers().stream().filter(player -> player.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
        Player remainingPlayer = game.getPlayers().stream().filter(player -> !player.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);

        if(remainingPlayer != null) {
            game.setPlayers(List.of(remainingPlayer));
        } else {
            game.setPlayers(null);
            if(!deleteById(room.getGame().getGameId())){
                roomResult.addMessage("Sorry, unable to delete the game", ResultType.NOT_FOUND);
            }
            room.setGame(null);
            roomResult.setPayload(room);
            return roomResult;
        }

        remainingPlayer.setAccountBalance(remainingPlayer.getAccountBalance() + game.getPot());
        remainingPlayer.setHoleCards(List.of(Card.EMPTY, Card.EMPTY));

        game.setWinner(null);
        game.setPot(0);
        game.setLastAction(Action.LEAVE);
        game.setBoard(null);
        game.setBetAmount(0);

        if(!repository.update(game)){
            roomResult.addMessage("Unable to delete game ", ResultType.NOT_FOUND);
            return roomResult;
        }
        repository.update(game);
        roomResult.setPayload(room);

        return roomResult;
    }

    public Result<Room> start(Room room) {
        Result<Room> roomResult = new Result<>();
        Game game = room.getGame();

        if(game.getPlayers().size() < 2){
            roomResult.addMessage("Game must have two players to start.", ResultType.INVALID);
            return roomResult;
        }

        game.setLastAction(Action.NONE);

        double smallBlind = room.getStake()/2;
        double bigBlind = room.getStake();

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        player1.setPosition(Position.SMALLBLIND);
        player1.setPlayersAction(true);

        player2.setPosition(Position.BIGBLIND);
        player2.setPlayersAction(false);

        if(player1.getAccountBalance() <= 0 || player2.getAccountBalance() <= 0){
            roomResult.addMessage("Account Balance must be greater than 0 to start game.", ResultType.INVALID);
            return roomResult;
        }

        player1.setAccountBalance(player1.getAccountBalance() - smallBlind);
        player2.setAccountBalance(player2.getAccountBalance() - bigBlind);
        game.setPot(smallBlind + bigBlind);

        List<Card> cards = deckService.drawCards(4, room);

        player1.setHoleCards(cards.subList(0,2));
        player2.setHoleCards(cards.subList(2,4));

        playerService.update(player1);
        playerService.update(player2);

        setGameState(room, game, List.of(player1, player2));
        roomResult.setPayload(room);
        return roomResult;
    }

    public Result<Room> handleAction(Room room, Action action) {
        Result<Room> roomResult = new Result<>();
        Game game = room.getGame();
        Action lastAction = game.getLastAction();
        if (lastAction == null){
            lastAction = Action.NONE;
        }

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        Player currentPlayer = game.getPlayers().stream().filter(Player::isPlayersAction).findFirst().orElse(null);
        Player opponent = game.getPlayers().stream().filter(player -> !player.isPlayersAction()).findFirst().orElse(null);
        double bet = game.getBetAmount();

        if (lastAction.equals(Action.NONE) && game.getWinner() != null) {
            game.setWinner(null);
        }

        // Fold
        if (action.equals(Action.FOLD)) {
            String winner = null;
            for (Player player : game.getPlayers()){
                if (!player.isPlayersAction()) {
                    winner = player.getUsername();
                    room.getGame().setWinner(winner);
                    resetState(room, player);
                }
            }
            game = room.getGame();
            game.setWinner(winner);
            game.setLastAction(action);
            setGameState(room, game, List.of(player1, player2));
            roomResult.setPayload(room);
            return roomResult;
        }

        boolean isTerminal = ((lastAction.equals(Action.RAISE)
                || lastAction.equals(Action.BET))
                && action.equals(Action.CALL)
                || lastAction.equals(Action.CHECK)
                && action.equals(Action.CHECK)
                && currentPlayer.getPosition().equals(Position.BIGBLIND));

        // Final showdown
        if ((game.getBoard() != null
                && game.getBoard().getRiver() != null
                && game.getBoard().getRiver() != Card.EMPTY
                && isTerminal)) {
            Player winner = winnerService.determineWinner(room);
            Player winningPlayer = game.getPlayers().stream().filter(p -> Objects.equals(p.getUsername(), winner.getUsername())).findFirst().orElse(null);
            Player losingPlayer = game.getPlayers().stream().filter(p -> !Objects.equals(p.getUsername(), winner.getUsername())).findFirst().orElse(null);

            if (winningPlayer != null && losingPlayer != null) {
                winningPlayer.setAccountBalance(winningPlayer.getAccountBalance() + game.getPot());
                game.setPot(0);
                game.setBetAmount(0);
                Board board = new Board(List.of(Card.EMPTY, Card.EMPTY, Card.EMPTY), Card.EMPTY, Card.EMPTY);
                game.setBoard(board);
                game.setWinner(winningPlayer.getUsername());

                if (winningPlayer.isPlayersAction()) {
                    setGameState(room, game, List.of(losingPlayer, winningPlayer));
                } else {
                    setGameState(room, game, List.of(winningPlayer, losingPlayer));
                }
                return start(room);
            }
        }

        // Any raise, or Small Blind open Call
        if ((lastAction.equals(Action.NONE) && currentPlayer.getPosition().equals(Position.SMALLBLIND) && (action.equals(Action.CALL)))
                || action.equals(Action.RAISE) || action.equals(Action.BET)) {

            if(currentPlayer.getPosition().equals(Position.SMALLBLIND) && lastAction.equals(Action.NONE)){
                bet = room.getStake() / 2;
            }

            currentPlayer.setAccountBalance(currentPlayer.getAccountBalance() - bet);
            game.setPot(game.getPot() + bet);
            currentPlayer.setPlayersAction(false);
            opponent.setPlayersAction(true);
            game.setLastAction(action);
            setGameState(room, game, List.of(currentPlayer, opponent));
            roomResult.setPayload(room);
            return roomResult;
        }

        // Small Blind open Check
        if ((lastAction.equals(Action.CHECK) || lastAction.equals(Action.CALL))
                && currentPlayer.getPosition().equals(Position.SMALLBLIND)){
            currentPlayer.setPlayersAction(false);
            opponent.setPlayersAction(true);
            game.setLastAction(action);
            setGameState(room, game, List.of(currentPlayer, opponent));
            roomResult.setPayload(room);
            return roomResult;
        }

        // Big Blind Checks behind a Call (terminal)
        if ((lastAction.equals(Action.CALL) || lastAction.equals(Action.CHECK))
                && currentPlayer.getPosition().equals(Position.BIGBLIND) && action.equals(Action.CHECK)) {
            currentPlayer.setPlayersAction(false);
            opponent.setPlayersAction(true);
            game.setLastAction(action);
            dealNext(room, game, List.of(currentPlayer, opponent));
            roomResult.setPayload(room);
            return roomResult;
        }

        // Any Call behind a Raise (terminal)
        if (isTerminal) {
            currentPlayer.setAccountBalance(currentPlayer.getAccountBalance() - bet);
            game.setPot(game.getPot() + bet);
            currentPlayer.setPlayersAction(false);
            opponent.setPlayersAction(true);
            game.setLastAction(action);
            dealNext(room, game, List.of(currentPlayer, opponent));
            roomResult.setPayload(room);
            return roomResult;
        }

        return roomResult;
    }

    private void resetState(Room room, Player winner){
        Game game = room.getGame();
        winner.setAccountBalance(winner.getAccountBalance() + game.getPot());

        game.setPot(0);
        game.setWinner(null);
        game.setLastAction(Action.NONE);
        game.setBoard(null);
        game.setBetAmount(0);

        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        player1.setHoleCards(null);
        player2.setHoleCards(null);

        if (player1.isPlayersAction()) {
            player1.setPlayersAction(false);
            player2.setPlayersAction(true);
        } else {
            player1.setPlayersAction(true);
            player2.setPlayersAction(false);
        }

        if (player1.getPosition() == Position.SMALLBLIND) {
            player1.setPosition(Position.BIGBLIND);
            player2.setPosition(Position.SMALLBLIND);
        } else {
            player1.setPosition(Position.SMALLBLIND);
            player2.setPosition(Position.BIGBLIND);
        }

        // TODO throws connection refused error
        deckService.shuffle(room.getDeckId());

        setGameState(room, game, List.of(player1, player2));
    }

    private void dealNext(Room room, Game game, List<Player> players){
        Board board = game.getBoard();

        if (board == null
                || board.getFlop().isEmpty()
                || (board.getFlop().get(0).equals(Card.EMPTY)
                    && board.getFlop().get(1).equals(Card.EMPTY)
                    && board.getFlop().get(2).equals(Card.EMPTY))) {
            List<Card> flop = deckService.drawCards(3, room);

            board = new Board();
            board.setFlop(flop);
            game.setBoard(board);
            setGameState(room, game, players);

        }else if (board.getTurn() == null
                    || board.getTurn().equals(Card.EMPTY)) {
            Card turn = deckService.drawCards(1, room).get(0);

            board.setTurn(turn);
            game.setBoard(board);
            setGameState(room, game, players);

        }else if (board.getRiver() == null
                    || board.getRiver().equals(Card.EMPTY)) {
            Card river = deckService.drawCards(1, room).get(0);

            board.setRiver(river);
            game.setBoard(board);
            setGameState(room, game, players);
        }
    }

    private void setGameState(Room room, Game game, List<Player> players){
        Result<Room> roomResult = new Result<>();
        game.setPlayers(players);
        Result<Game> gameResult = update(game);

        if (!gameResult.isSuccess()) {
            roomResult.addMessage("An error occurred when updating the game", ResultType.INVALID);
            return;
        }

        room.setGame(game);
        roomResult = roomService.update(room);
        if (!roomResult.isSuccess()){
            roomResult.addMessage("An error occurred when updating the room", ResultType.INVALID);
        }
    }
}