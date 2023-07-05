package learn.poker.domain;

import learn.poker.data.GameRepository;
import learn.poker.data.RoomRepository;
import learn.poker.models.*;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GameService {

    private final GameRepository repository;
    private final RoomService roomService;
    private final DeckService deckService;
    private final PlayerService playerService;
    private final WinnerService winnerService;
    private final RoomRepository roomRepository;

    public GameService(GameRepository repository, RoomService roomService, DeckService deckService, PlayerService playerService, WinnerService winnerService, RoomRepository roomRepository) {
        this.repository = repository;
        this.roomService = roomService;
        this.deckService = deckService;
        this.playerService = playerService;
        this.winnerService = winnerService;
        this.roomRepository = roomRepository;
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

        if(game.getPlayers() == null || game.getPlayers().size() < 1) {
            result.addMessage("Cannot update a game with no players", ResultType.INVALID);
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

    public Result<Room> addPlayer(Room room) {
        Result<Room> result = new Result<>();

        if(room.getGame() == null || room.getGame().getPlayers().isEmpty()){
            result.addMessage("Cannot add empty players to game", ResultType.INVALID);
            return result;
        }

        List<Player> players = room.getGame().getPlayers();

        Game game = findById(room.getGame().getGameId());

        game.setPlayers(players);

        Result<Game> gameResult = update(game);
        if (!gameResult.isSuccess()){
            result.addMessage("Was unable to update the game after adding new players", ResultType.INVALID);
            return result;
        }

        Game updatedGame = findById(game.getGameId());
        room.setGame(updatedGame);

        Result roomUpdateResult = roomService.update(room);
        if (!roomUpdateResult.isSuccess()){
            result.addMessage("Was unable to update room after updating game");
        }
        result.setPayload(room);
        return result;
    }

    public Result<Room> endGame(Room room, Player winner) {
        /**
         * when a player leaves the room in the middle of a game
         * the game goes back to the following state
         * the player that leaves essentially folds
         * the remaining player is declared the winner and gets the pot
         * but it is not the same as the resetState() function
         * since we do not have two players
         *
         * maybe just call handleAction and set action to fold
         * but the problem is, if it is a problem is the fact that
         * there is no longer two players but only one left in the game
         * would it lead to an index out of bounds...
         *
         * or maybe just call deleteById
         *
         * delete the playerId
         *
         * go back to this state:
         * {
         *   "roomId": 4,
         *   "stake": 2,
         *   "seats": 2,
         *   "game": null
         * }
         */

        //if room_count is less than 2
        //just end the game -- 

        Result<Room> roomResult = new Result<>();

        Game game = room.getGame();

        //called winner but really Player remainingPlayer
        winner.setAccountBalance(winner.getAccountBalance() + game.getPot());

        game.setPot(0);
        game.setWinner(null);
        game.setLastAction(Action.NONE);
        game.setBoard(null);
        game.setBetAmount(0);

        roomResult.setPayload(room);

//        handleAction(room, Action.FOLD);
//        deleteById(game.getGameId());

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

        Deck deck = deckService.drawCards(4);
        List<PokerApiCard> playerCards = deck.getCards();

        List<Card> cards = playerCards.stream().map(pokerApiCard -> {
            String code = pokerApiCard.getCode();
            return Card.getCardFromAbbreviation(code);
        }).toList();

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
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        Player currentPlayer = game.getPlayers().stream().filter(Player::isPlayersAction).findFirst().orElse(null);
        Player opponent = game.getPlayers().stream().filter(player -> !player.isPlayersAction()).findFirst().orElse(null);
        double bet = game.getBetAmount();

        if (action.equals(Action.FOLD)) {
            String winner = null;
            for (Player player : game.getPlayers()){
                if (!player.isPlayersAction()) {
                    winner = player.getUsername();
                    resetState(room, player);
                }
            }
            game = room.getGame();
            game.setWinner(winner);
            setGameState(room, game, List.of(player1, player2));
            roomResult.setPayload(room);
            return roomResult;
        }

        boolean isTerminalCall = (lastAction.equals(Action.RAISE) || lastAction.equals(Action.BET)) && action.equals(Action.CALL);

        if (game.getBoard().getRiver() != null
                && isTerminalCall
                || lastAction.equals(Action.CHECK) && action.equals(Action.CHECK)) {
            String winner = winnerService.determineWinner(room);
            Player winningPlayer = game.getPlayers().stream().filter(p -> p.getUsername() == winner).findFirst().orElse(null);
            Player losingPlayer = game.getPlayers().stream().filter(p -> p.getUsername() != winner).findFirst().orElse(null);
            if (winningPlayer != null && losingPlayer != null) {
                winningPlayer.setAccountBalance(winningPlayer.getAccountBalance() + game.getPot());
                setGameState(room, game, List.of(winningPlayer, losingPlayer));
                roomResult.setPayload(room);
                return roomResult;
            }

        }

        // raise (also includes special case of small blind opening, which is the one time calling does not end round)
        if ((lastAction.equals(Action.NONE) && currentPlayer.getPosition().equals(Position.SMALLBLIND) && (action.equals(Action.CALL) || action.equals(Action.RAISE)))
                || ((lastAction.equals(Action.RAISE) || lastAction.equals(Action.BET) || lastAction.equals(Action.CHECK)) && action.equals(Action.RAISE))) {
            currentPlayer.setAccountBalance(currentPlayer.getAccountBalance() - bet);
            game.setPot(game.getPot() + bet);
            currentPlayer.setPlayersAction(false);
            opponent.setPlayersAction(true);
            setGameState(room, game, List.of(currentPlayer, opponent));
            roomResult.setPayload(room);
            return roomResult;
        }

        // big blind check behind (terminal action)
        if (lastAction.equals(Action.CALL) && currentPlayer.getPosition().equals(Position.BIGBLIND) && action.equals(Action.CHECK)) {
            currentPlayer.setPlayersAction(false);
            opponent.setPlayersAction(true);
            dealNext(room, game, List.of(currentPlayer, opponent));
            roomResult.setPayload(room);
            return roomResult;
        }

        // call behind (terminal action)
        if (isTerminalCall) {
            currentPlayer.setAccountBalance(currentPlayer.getAccountBalance() - bet);
            game.setPot(game.getPot() + bet);
            currentPlayer.setPlayersAction(false);
            opponent.setPlayersAction(true);
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

        deckService.shuffle();

        setGameState(room, game, List.of(player1, player2));
    }

    private void dealNext(Room room, Game game, List<Player> players){
        Board board = game.getBoard();

        if (board == null || board.getFlop().isEmpty()) {
            Deck apiCards = deckService.drawCards(3);
            List<PokerApiCard> playerCards = apiCards.getCards();

            List<Card> flop = playerCards.stream().map(pokerApiCard -> {
                String code = pokerApiCard.getCode();
                return Card.getCardFromAbbreviation(code);
            }).toList();

            board.setFlop(flop);
            game.setBoard(board);
            setGameState(room, game, players);

        }else if (!board.getFlop().isEmpty() && board.getTurn() == null) {
            Deck apiCard = deckService.drawCards(1);
            Card turn = Card.getCardFromAbbreviation(apiCard.getCards().get(0).getCode());

            board.setTurn(turn);
            game.setBoard(board);
            setGameState(room, game, players);

        }else if (board.getTurn() != null && board.getRiver() == null) {
            Deck apiCard = deckService.drawCards(1);
            Card river = Card.getCardFromAbbreviation(apiCard.getCards().get(0).getCode());

            board.setRiver(river);
            game.setBoard(board);
            setGameState(room, game, players);
        }
    }

    private Result<Room> setGameState(Room room, Game game, List<Player> players){
        Result<Room> roomResult = new Result<>();
        game.setPlayers(players);
        Result<Game> gameResult = update(game);

        if (!gameResult.isSuccess()) {
            roomResult.addMessage("An error occurred when updating the game", ResultType.INVALID);
            return roomResult;
        }

        room.setGame(game);
        roomResult = roomService.update(room);
        if (!roomResult.isSuccess()){
            roomResult.addMessage("An error occured when updating the room", ResultType.INVALID);
            return roomResult;
        }
        return roomResult;
    }
}