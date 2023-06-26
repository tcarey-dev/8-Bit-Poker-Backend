# Development Plan

## Team Name: Code Counters

## User Stories

* Player User Story
    * As a player, I want to be able to create an account, deposit (fake) money, choose a Room, and play a game.

* Admin User Story
    * As admin, I want to be able to create new Rooms, update existing Room settings (such as stakes), and delete Rooms

## Models
* Player
  * int id
  * String displayName
  * String username
  * String password
  * int accountBalance
  * Collection <GrantedAuthorities> roles
  * List<Card> holeCards
  * Position position
  * boolean isPlayersAction

* Room
  * int id
  * double stake
  * int[] seats

* Game
  * int id
  * Room room
  * int pot
  * Board board
  * List<Player> players
  * Player winner

* Board
  * int id
  * List<Card> flop
  * Card turn
  * Card river

* Position (enum)
  * SMALL_BLIND
  * BIG_BLIND

* Action(enum)
  * BET
  * CHECK
  * FOLD
  * RAISE

## Validation rules/business logic
* Room stake must be even and greater or equal to 2 cents (consider creating drop down for stakes)

## Package/Class Overview
```
src
├───main
│   ├───java
│   │   └───learn
│   │       └───poker
│   │           │   App.java
│   │           │   AppConfig.java
│   │           │   WebSocketConfig.java
│   │           │
│   │           ├───security
│   │           │       Player.java
│   │           │       PlayerService.java
│   │           │       Credentials.java
│   │           │       JwtConverter.java
│   │           │       JwtRequestFilter.java
│   │           │       SecurityConfig.java
│   │           │
│   │           ├───data
│   │           |   └─── mappers
|   |           |       |      PlayerMapper.java
|   |           |       |      RoomMapper.java
|   |           |       |      GameMapper.java
│   │           │       DataException.java
│   │           │       RoomJdbcTemplateRepository.java
│   │           │       RoomRepository.java
│   │           │       PlayerJdbcTemplateRepository.java
│   │           │       PlayerRepository.java
│   │           │       GameJdbcTemplateRepository.java
│   │           │       GameRepository.java
│   │           │
│   │           ├───domain
│   │           │       RoomService.java
│   │           │       GameService.java
│   │           │       ResultType.java
│   │           │       Result.java
│   │           │
│   │           ├───models
│   │           │       Room.java
│   │           │       Game.java
│   │           │       Board.java
│   │           │       Position.java
│   │           │       Action.java
│   │           │
│   │           └───controller
│   │                   PlayerController.java
│   │                   RoomController.java
│   │                   GameController.java
│   │                   AuthController.java
│   │                   GlobalExceptionHandler.java
│   │
│   └───resources
└───test
    └───java
        └───learn
            └───mastery
                ├───data
                │       UserRepositoryTest.java
                │       UserRepositoryDouble.java
                │       RoomRepositoryTest.java
                │       RoomRepositoryDouble.java
                │       GameRepositoryTest.java
                │       GameRepositoryDouble.java
                │
                └───domain
                        UserServiceTest.java
                        RoomServiceTest.java
                        GameServiceTest.java
```

## Class methods and fields

#### src.main.java.learn.poker
* [ ] `App.java` -- @SpringBootApplication
* [ ] `AppConfig.java` -- @Configuration
    * `public PasswordEncoder getPasswordEncoder()` -- @Bean
    * `public WebMvcConfigurer corsConfigurer()` -- @Bean
* [ ] `WebSocketConfig.java` -- @Configuration, @EnableWebSocketMessageBroker
    * `public void configureMessageBroker(MessageBrokerRegistry config)` -- @Override
    * `public void registerStompEndpoints(StompEndpointRegistry registry)` --@Override

#### src.main.java.learn.poker.security
* [ ] `Player.java` -- implements UserDetails
    * `int id`
    * `private String displayName`
    * `private final String username`
    * `private final String password`
    * `int accountBalance`
    * `Collection <GrantedAuthorities> roles`
    * `List<Card> holeCards`
    * `Position position`
    * `boolean isPlayersAction`
    * `public Player(int playerId, String username, String password, List<String> roles)`
    * constructor, getters and setters, as well as override methods to implement UserDetails contract
* [ ] `PlayerService.java` 
    * `private PlayerRepository repository`
    * `public Player(PlayerRepository)`
    * `public Player getPlayerById(int)`
    * `public Result add(Player)`
    * `public Result update(Player)`
    * `public Result delete(Player)`
    * `private Result validate(Player)`
* [ ] `Credentials.java` 
    * `private String username`
    * `private String password`
    * constructor, getters and setters
* [ ] `JwtConverter.java` 
    * `private Key key`
    * `private final String ISSUER`
    * `private final int EXPIRATION_MINUTES`
    * `private final int EXPIRATION_MILLIS`
    * `public String getTokenFromPlayer(Player user)`
    * `public Player getPlayerFromToken(String token)`
* [ ] `JwtRequestFilter.java` -- extends BasicAuthenticationFilter
    * `private final JwtConverter converter`
    * `public JwtRequestFilter(AuthenticationManager authenticationManager, JwtConverter converter)`
    * `protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException ` -- @Override
* [ ] `SecurityConfig.java` -- @Configuration, @ConditionalOnWebApplication
    * `private final JwtConverter jwtConverter`
    * `public SecurityConfig(JwtConverter jwtConverter)`
    * `public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception` -- @Bean
    * `public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception` -- @Bean

#### src.main.java.learn.poker.data

* [ ] `DataException.java` 
    * `public DataException(String, Throwable)`
* [ ] `RoomJdbcTemplateRepository.java` 
    * `private final JdbcTemplate jdbcTemplate`
    * `private final RowMapper<Room> rowMapper = new RoomMapper()`
    * `public RoomJdbcTemplateRepository(JdbcTemplate jdbcTemplate)`
    * `public List<Room> findAll()` -- @Override
    * `public Room findById()` -- @Override
    * `public Room create(Room)` -- @Override
    * `public boolean update(Room)` -- @Override
    * `public boolean delete(Room)` -- @Override
* [ ] `RoomRepository.java` 
    * extract interface from RoomJdbcTemplateRepository
* [ ] `PlayerJdbcTemplateRepository.java` 
    * `private final JdbcTemplate jdbcTemplate`
    * `public PlayerJdbcTemplateRepository(JdbcTemplate jdbcTemplate)`
    * `public Player findByUsername(String username)` -- @Override
    * `public Player create(Player player)` -- @Override
    * `public boolean update(Player player)` -- @Override
    * `public boolean delete(Player player)` -- @Override 
    * `private void updateRoles(Player player)`
    * `private List<String> getRolesByUsername(String username)`
* [ ] `PlayerRepository.java` 
    * extract interface from PlayerJdbcTemplateRepository
* [ ] `GameJdbcTemplateRepository.java` 
    * `private final JdbcTemplate jdbcTemplate`
    * `private final RowMapper<Game> rowMapper = new GameMapper()`
    * `public GameJdbcTemplateRepository(JdbcTemplate jdbcTemplate)`
    * `public Game findById(int gameId)` -- @Override
    * `public Game create(Game)` -- @Override
    * `public boolean update(Game)` -- @Override
    * `public boolean delete(Game)` -- @Override
    * we might need a helper function to get players from the game here, TBD
* [ ] `GameRepository.java` 
    * extract interface from GameJdbcTemplateRepository

#### src.main.java.learn.poker.data.mappers
* [ ] `PlayerMapper.java` 
* [ ] `RoomMapper.java` 
* [ ] `GameMapper.java` 

#### src.main.java.learn.poker.domain
* [ ] `RoomService.java` 
* [ ] `GameService.java` 
* [ ] `ResultType.java` 
* [ ] `Result.java` 

#### src.main.java.learn.poker.models
* [ ] `Room.java` 
* [ ] `Game.java` 
* [ ] `Board.java` 
* [ ] `Position.java` 
* [ ] `Action.java` 

#### src.main.java.learn.poker.controller
* [ ] `PlayerController.java` 
* [ ] `RoomController.java` 
* [ ] `GameController.java` 
* [ ] `AuthController.java` 
* [ ] `GlobalExceptionHandler.java` 

### test.java.learn.mastery.data
* [ ] `UserRepositoryTest.java` 
* [ ] `UserRepositoryDouble.java` 
* [ ] `RoomRepositoryTest.java` 
* [ ] `RoomRepositoryDouble.java` 
* [ ] `GameRepositoryTest.java` 
* [ ] `GameRepositoryDouble.java` 

### test.java.learn.mastery.domain
* [ ] `UserServiceTest.java` 
* [ ] `RoomServiceTest.java` 
* [ ] `GameServiceTest.java` 

