package learn.poker.domain;

import learn.poker.models.Player;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {


    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public Player getPlayerById(int playerId){
        return repository.findById(playerId);
    }

    public Result<Player> add(Player player){
        Result<Player> result = validate(player);
        if(!result.isSuccess()){
            return result;
        }

        if(player.getPlayerId() != 0){
            result.addMessage("playerId should not be set for add", ResultType.INVALID);
        }

        player = repository.create(player);
        result.setPayload(player);
        return result;
    }

    public Result<Player> update(Player player){
        Result<Player> result = validate(player);
        if(!result.isSuccess()){
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
        }

        return result;
    }


}
