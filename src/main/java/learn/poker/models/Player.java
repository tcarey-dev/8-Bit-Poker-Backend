package learn.poker.models;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class Player {
    private int playerId;
    private String displayName;
    private final String username;
    private final String password;
    private int accountBalance;
    private Collection <GrantedAuthorities> roles;
    private List<Card> holeCards;
    private Position position;
    private boolean isPlayersAction;

    public Player(String displayName, int accountBalance, List<Card> holeCards, boolean isPlayersAction, int playerId, String username, String password, Collection<GrantedAuthorities> roles, Position position) {
        this.displayName = displayName;
        this.accountBalance = accountBalance;
        this.holeCards = holeCards;
        this.isPlayersAction = isPlayersAction;
        this.playerId = playerId;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.position = position;
    }

    public Player(int playerId, String username, String password, Collection<GrantedAuthorities> roles) {
        this.playerId = playerId;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Player(String username, String password){
        this.username = username;
        this.password = password;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Collection<GrantedAuthorities> getRoles() {
        return roles;
    }

    public void setRoles(Collection<GrantedAuthorities> roles) {
        this.roles = roles;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public void setHoleCards(List<Card> holeCards) {
        this.holeCards = holeCards;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isPlayersAction() {
        return isPlayersAction;
    }

    public void setPlayersAction(boolean playersAction) {
        isPlayersAction = playersAction;
    }
}
