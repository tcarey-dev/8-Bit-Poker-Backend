package learn.poker.data;

import learn.poker.models.Game;

public interface GameRepository {
    Game findById(int gameId);

    Game create(Game game);

    boolean update(Game game);

    boolean delete(int gameId);
}
