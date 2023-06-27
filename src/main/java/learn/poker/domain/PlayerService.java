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

    public Result<Player> add(Player){
        Result<Player> result = validate(player);
        if(!result.isSuccess()){
            return result;
        }



    }

    private Result validate(Player player){
        Result result = new Result();

        return result;
    }


}
