package learn.poker.models;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class Player implements UserDetails {
    private int playerId;
    private String displayName;
    private final String username;
    private final String password;
    private int accountBalance;
    private Collection <GrantedAuthority> roles;
    private List<Card> holeCards;
    private Position position;
    private boolean isPlayersAction;

    public Player(String displayName, int accountBalance, List<Card> holeCards, boolean isPlayersAction, int playerId, String username, String password, Collection<GrantedAuthority> roles, Position position) {
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

    public Player(int playerId, String username, String password, Collection<GrantedAuthority> roles) {
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

    public Collection<GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<GrantedAuthority> roles) {
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

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
