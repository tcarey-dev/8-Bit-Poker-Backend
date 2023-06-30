package learn.poker.data.mappers;

import learn.poker.models.Board;
import learn.poker.models.Card;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class BoardMapper implements RowMapper<Board> {
    @Override
    public Board mapRow(ResultSet rs, int rowNum) throws SQLException {
        Board board = new Board();
        board.setBoardId(rs.getInt("board_id"));

        List<String> flopCards = Arrays.stream(rs.getString("flop").split(",")).toList();

        for(String card : flopCards) {
            if (card == null || card.isBlank()){
                return null;
            }
        }

        List<Card> flop = Arrays.stream(rs.getString("flop").split(","))
                .map(Card::getCardFromAbbreviation).toList();

        board.setFlop(flop);
        board.setTurn(Card.getCardFromAbbreviation(rs.getString("turn")));
        board.setRiver(Card.getCardFromAbbreviation(rs.getString("river")));
        return board;
    }
}
