package learn.poker.data.mappers;


import learn.poker.models.Board;
import learn.poker.models.Card;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoleCardMapper implements RowMapper<List<Card>> {
    @Override
    public List<Card> mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Arrays.stream(rs.getString("hole_cards").split(",")).map(Card::getCardFromAbbreviation).toList();
    }
}
