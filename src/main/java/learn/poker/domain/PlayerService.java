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

        if(!repository.update(player))

        return result;
    }



    private Result validate(Player player){
        Result result = new Result();

        return result;
    }


}
