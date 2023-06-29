package learn.poker.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.stream.Collectors;

public class Player implements UserDetails {
    private int playerId;
    private String displayName;
    private String username;
    private String password;
    private boolean enabled;
    private int accountBalance;
    private Collection <GrantedAuthority> authorities;
    private List<Card> holeCards;
    private Position position;
    private boolean isPlayersAction;

    public Player(int playerId, String displayName, String username, String password, boolean enabled, int accountBalance, List<String> roles, List<Card> holeCards, Position position, boolean isPlayersAction) {
        this.playerId = playerId;
        this.displayName = displayName;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountBalance = accountBalance;
        this.authorities = convertRolesToAuthorities(roles);
        this.holeCards = holeCards;
        this.position = position;
        this.isPlayersAction = isPlayersAction;
    }

    public Player(int playerId, String username, String password, boolean enabled, List<String> roles) {
        this.playerId = playerId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = convertRolesToAuthorities(roles);
    }

    public Player(int playerId, String username, String password, boolean enabled) {
        this.playerId = playerId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public Player(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Player(){}

    private static Collection<GrantedAuthority> convertRolesToAuthorities(List<String> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return new ArrayList<>(authorities);
    }

    public void setAuthorities(List<String> roles) {
        this.authorities = convertRolesToAuthorities(roles);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId == player.playerId && Objects.equals(username, player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, username);
    }
}
