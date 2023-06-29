# Development Plan

## Team Name: Code Counters

## User Stories

* Player User Story
  * As a player, I want to be able to create an account, deposit (fake) money, choose a Room, and play a game.

* Admin User Story
  * As admin, I want to be able to create new Rooms, update existing Room settings (such as stakes), and delete Rooms

## Back End

## Models
* Player
  * int playerId
  * String displayName
  * String username
  * String password
  * int accountBalance
  * Collection <GrantedAuthorities> roles
  * List<Card> holeCards
  * Position position
  * boolean isPlayersAction

* Room
  * int roomId
  * double stake
  * int[] seats

* Game
  * int gameId
  * Room room
  * int pot
  * Board board
  * List<Player> players
  * Player winner

* Board
  * int boardId
  * List<Card> flop
  * Card turn
  * Card river

* Position (enum)
  * SMALL_BLIND
  * BIG_BLIND

* Action (enum)
  * BET
  * CHECK
  * FOLD
  * RAISE

* Card (enum)

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
│   │           │       PlayerService.java
│   │           │       RoomService.java
│   │           │       GameService.java
│   │           │       ResultType.java
│   │           │       Result.java
│   │           │
│   │           ├───models
│   │           │       Player.java
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
  * `public boolean delete(int id)` -- @Override

* [ ] `RoomRepository.java`
  * extract interface from RoomJdbcTemplateRepository

* [ ] `PlayerJdbcTemplateRepository.java`
  * `private final JdbcTemplate jdbcTemplate`
  * `public PlayerJdbcTemplateRepository(JdbcTemplate jdbcTemplate)`
  * `public Player findById(int id)` -- @Override
  * `public Player create(Player player)` -- @Override
  * `public boolean update(Player player)` -- @Override
  * `public boolean delete(int id)` -- @Override
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
  * `public boolean delete(int id)` -- @Override
  * we might need methods to get the room, board and list of players from the Game

* [ ] `GameRepository.java`
  * extract interface from GameJdbcTemplateRepository

#### src.main.java.learn.poker.data.mappers
* [ ] `PlayerMapper.java` -- implements RowMapper<Player>
  * `private final List<String> roles`
  * `public PlayerMapper(List<String> roles)`
  * `public Player mapRow(ResultSet rs, int i) throws SQLException` -- @Override

* [ ] `RoomMapper.java` -- implements RowMapper<Room>
  * `public Room mapRow(ResultSet rs, int rowNum) throws SQLException` -- @Override

* [ ] `GameMapper.java` -- implements RowMapper<Game>
  * `public Game mapRow(ResultSet rs, int rowNum) throws SQLException` -- @Override

#### src.main.java.learn.poker.domain
* [ ] `PlayerService.java`
  * `private PlayerRepository repository`
  * `public Player(PlayerRepository)`
  * `public Player getPlayerById(int)`
  * `public Result<Player> add(Player)`
  * `public Result<Player> update(Player)`
  * `public boolean deleteById(int id)`
  * `private Result validate(Player)`

* [ ] `RoomService.java`
  * `private final RoomRepository repository`
  * `public RoomService(RoomRepository repository)`
  * `public List<Room> findAll()`
  * `public Room findById(int)`
  * `public Result<Room> add(Room)`
  * `public Result<Room> update(Room)`
  * `public boolean deleteById(int)`
  * `private Result<Room> validate(Room)`

* [ ] `GameService.java`
  * `private final GameRepository repository`
  * `public GameService(GameRepository repository)`
  * `public Game findById(int)`
  * `public Player getWinner(List<Player> players, Board board)`
  * `public Result<Game> add(Game)`
  * `public Result<Game> update(Game)`
  * `public boolean deleteById(int)`
  * `private Result<Game> validate(Game)`
  * add game init and loop methods here, along with any helper methods
  * we might need methods to get the room, board and list of players from the Game

* [ ] `ResultType.java`  -- Enum
  * SUCCESS,INVALID,NOT_FOUND

* [ ] `Result.java`
  * `private final ArrayList<String> messages`
  * `private ResultType type`
  * `private T payload`
  * `public boolean isSuccess()`
  * get and set payload, get type, get and add message

#### src.main.java.learn.poker.models
* [ ] `Player.java` -- implements UserDetails
  * `int playerId`
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

* [ ] `Room.java`
  * `int roomId`
  * `double stake`
  * `int seats`

* [ ] `Game.java`
  * `int gameId`
  * `int pot`
  * `String winner`
  * `Room room`
  * `Board board`
  * `Player player`

* [ ] `Board.java`
  * `int boardId`
  * `List<Card> flop`
  * `Card turn`
  * `Card river`

* [ ] `Position.java` -- Enum
  * SMALL_BLIND, BIG_BLIND

* [ ] `Action.java` -- Enum
  * BET, CHECK, FOLD, RAISE

* [ ] `Card.java` -- Enum
  * 2C, 3C, 4C, 5C, 6C, 7C, 8C, 9C, 10C, JC, QC, KC, AC, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D, JD, QD, KD, AD, 2H, 3H, 4H, 5H, 6H, 7H, 8H, 9H, 10H, JH, QH, KH, AH, 2S, 3S, 4S, 5S, 6S, 7S, 8S, 9S, 10S, JS, QS, KS, AS

#### src.main.java.learn.poker.controller
* [ ] `PlayerController.java` -- @RestController, @RequestMapping("/api/player"), @CrossOrigin(origins ={"http://localhost:3000"})
  * `private final PlayerService service`
  * `public PlayerController(PlayerService service)`
  * `public ResponseEntity<?> getPlayerById(int)` -- @GetMapping("/{username}")
  * `public ResponseEntity<?> add(@RequestBody Player player)` -- @PostMapping
  * `public ResponseEntity<?> update(@RequestBody Player player)` -- @PutMapping("/{id}")
  * `public ResponseEntity<?> delete(@PathVariable int id)` -- @DeleteMapping("/{id}")

* [ ] `RoomController.java` -- @RestController, @RequestMapping("/api/player"), @CrossOrigin(origins ={"http://localhost:3000"})
  * `public List<Room> findAll()` -- @GetMapping
  * `public ResponseEntity<?> findById(int)` -- @GetMapping("/{id}")
  * `public ResponseEntity<?> add(@RequestBody Room room)` -- @PostMapping
  * `public ResponseEntity<?> update(@RequestBody Room room)` -- @PutMapping("/{id}")
  * `public ResponseEntity<?> delete(@PathVariable int id)` -- @DeleteMapping("/{id}")

* [ ] `AuthController.java` -- @RestController, @RequestMapping("/security"), @ConditionalOnWebApplication
  * `private final AuthenticationManager authenticationManager`
  * `private final JwtConverter jwtConverter`
  * `private final PlayerService PlayerService`
  * `public AuthController(AuthenticationManager authenticationManager, JwtConverter jwtConverter, PlayerService PlayerService)`
  * `public ResponseEntity<Object> authenticate(@RequestBody Credentials credentials)` -- @PostMapping("/authenticate")
  * `public ResponseEntity<Object> refreshToken(@AuthenticationPrincipal Player player)` -- @PostMapping("/refresh-token")
  * `public ResponseEntity<Object> create(@RequestBody Credentials credentials)` -- @PostMapping("/create-account")
  * `private HashMap<String, String> makePlayerTokenMap(Player player)`

* [ ] `GlobalExceptionHandler.java` -- @ControllerAdvice
  * `public ResponseEntity<?> handleException(DuplicateKeyException ex)` -- @ExceptionHandler
  * `public ResponseEntity<?> handleException(HttpMessageNotReadableException ex)` -- @ExceptionHandler
  * `public ResponseEntity<?> handleException(Exception ex)` -- @ExceptionHandler
  * `private ResponseEntity<?> reportException(String message) `

* [ ] `GameController.java` -- @Controller
  * `public Game startGame(@DestinationVariable int roomId)` -- @MessageMapping("/start"), @SendTo("/topic/room/{id}")
  * `public Game bet(@DestinationVariable int roomId, @DestinationVariable int amount)` -- @MessageMapping("/bet/{amount}"), @SendTo("/topic/room/{id}")
  * `public Game raise(@DestinationVariable int roomId, @DestinationVariable int amount)` -- @MessageMapping("/raise/{amount}"), @SendTo("/topic/room/{id}")
  * `public Game check(@DestinationVariable int roomId)` -- @MessageMapping("/check"), @SendTo("/topic/room/{id}")
  * `public Game fold(@DestinationVariable int roomId)` -- @MessageMapping("/fold"), @SendTo("/topic/room/{id}")
  * `public Game endGame(@DestinationVariable int roomId)` -- @MessageMapping("/end"), @SendTo("/topic/room/{id}")

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


GAME LOGIC
* init game
  * initialize the game, room, players (i.e. set initial state) -- Database call
  * return game state to all subscibers -- Push to UI
  * trigger game loop
* game loop
  * get deck of cards, deal cards to players -- API call, push to UI
  * do small blinds action
  * do big blinds action
  * if action is ending (meaning the player who is last flat calls, or checks) then deal next street (flop, turn, river)
  * if action is fold, restart game loop

## Front End
```
/
├───public
├───src
│   │   App.js
│   |   index.js
|   |   index.css
|   |
│   ├───components  
|   |   |   Navbar.js
|   |   |   Landing.js
|   |   |   Lobby.js
|   |   |   Room.js
|   |   |   RoomList.js
|   |   |   RoomForm.js
|   |   |   NotFound.js
|   |   |   LoginForm.js
|   |   |   RegistrationForm.js
|   |   |   Errors.js
|   |    
│   ├───contexts
|   |   |   AuthContext.js
|   |   
│   ├───services
|   |   |   AuthApi.js

```

### App.js
* `const EMPTY_USER`
* `const WAIT_TIME`
* [ ] `App()`
    <!-- Variables and methods -->
  * `const [user, setUser]`
  * `const refreshUser = useCallback(() => {}, [])`
  * `useEffect(() => { refreshUser(); }, [refreshUser])`
  * `const auth = {}`
  * `const maybeRedirect = (component, role) => {}`
    <!-- JSX Auth and Routes -->
  * `<AuthContext.Provider value={auth}>`
  * `<Route path='/' element={<Landing />} />`
  * `<Route path="/login" element={<LoginForm />} />`
  * `<Route path="/register" element={<RegistrationForm />} />`
  * `<Route path='/lobby' element={<Lobby />} />`
  * `<Route path='/room/add' element={<Room />} />`
  * `<Route path='/room/edit/:id' element={<Room />} />`
  * `<Route path='/room' element={<Room />} />`
  * `<Route path='*' element={<NotFound />} />`

### index.js
* TBD

### Index.css
* TBD

### components/Navbar.js
* TBD

### components/Landing.js
* TBD

### components/Lobby.js
* `const EMPTY_ROOM`
* [ ] `Lobby()`
  * `const [room, setRoom]`
  * `const [rooms, setRooms]`
  * `const url`
  * `useEffect(()=> {fetch(url) => {}}, [])` -- http
  * `const handleDeleteRoom = (roomId) => {}` -- admin only
    <!-- JSX -->
  * return Navbar component followed by grid displaying rooms
  * hover over should expose Join button for players
  * hover over should expose Edit and Delete buttons for Admin
  * Edit should navigate to RoomForm

### components/Room.js
* `const EMPTY_GAME`
* [ ] `Room()`
  * `const [connected, setConnected]` -- to track websocket connection
  * `const [game, setGame]` -- track game state
  * `const navigate = useNavigate()`
  * `const connect = useCallback(() => {})` -- websocket connection, wrapped in callback so we can use in useEffect
  * `const disconnect = () => {}`
  * `const useEffect = () => {}, [connect]` -- establish websocket connection
  * `const handleChange = (event) => {}` -- to capture slider value
  * `const handleSubmit = (event) => {}` -- will contain a switch statement to call handler functions for bet/raise, check, and fold buttons
  * `const handleStartGame = () => {}` -- websocket request to initialize game, and start game loop
  * `const handleBet = () => {}` -- websocket request for betting/raising, takes value from a slider
  * `const handleCheck = () => {}` -- websocket request for checking
  * `const handleFold = () => {}` -- websocket request for folding
  * `const handleLeave = () => {}` -- websocket request to end game loop, then calls disconnect, and redirects to Lobby
    <!-- JSX -->
  * Leave button, triggers handleLeave
  * Start Game button underneath Leave button, disappears after game starts, triggers handleStartGame
  * form buttons for betting/raising/checking/folding with html range slider to capture bet/raise amount
    * slider should have a default value of the minimum bet (stored in room object, may need to pass from parent as a prop)
  * render board, player icons and stacks, player cards, and pot based on game state

### components/NotFound.js
* TBD

### components/LoginForm.js
* TBD

### components/RegistrationForm.js
* TBD

### components/Errors.js
* TBD

### contexts/AuthContext.js
* TBD

### services/AuthApi.js
* TBD

## Task Delegation with Time Estimates
### Tuesday
* Together (1 hour)
  * [x] setup backend repo with Maven project, spring boot mvc, websockets, and junit dependencies
  * [x] setup frontend repo with React App project, install stomp and sockjs dependencies
  * [x] create App.java and App.js
  * [x] create development branches and individual working branches for both repos
* Xiao
  * [x] create model classes (45 minutes)
  * [x] create Result, ResultType (15 minutes)
  * create PlayerService, PlayerService tests (2.5 hours)
  * [x] create RoomService, RoomService tests (2.5 hours)
  * HW
    * any leftover tests
* Aaron
  * [x] Write SQL scripts to setup production and test databases (2 hours)
  * DataException class (5 minutes)
  * PlayerJdbcTemplateRepository, PlayerRepository, PlayerMapper (1.5 hours)
  * RoomJdbcTemplateRepository, RoomRepository, RoomMapper (1.5 hours)
  * GameJdbcTemplateRepository, GameRepository, GameMapper (1.5 hours)
  * HW
    * Data layer tests
* Tom
  * [x] create WebsocketConfig (15 minutes)
  * create GameController (3 hour)
  * [x] create AppConfig, JwtConverter, JwtRequestFilter, SecurityConfig, AuthController, GlobalExceptionHandler (3 hours)
  * HW
    * complete any unfinished tasks, help others with tests

### Wednesday
* Xiao
  * finish RoomJdbcTemplateRepository
  * RoomController (1 hour)
  * any leftover tasks
* Aaron
  * [] GameService
  * any leftover tasks
* Tom:
  * finish GameJdbcTemplateRepository, PlayerJdbcTemplateRepository
  * finish PlayerService, PlayerService tests (2.5 hours)
  * PlayerController (1 hour)
  * setup CI/CD pipeline using Github Actions and Azure (1 hour)

### Thursday
* Together  (3 hours)
  * add game logic to Game Service
* Tom
  * finish GameController
  * create GameService CRUD and validation (not the game logic yet), tests (2.5 hours)
  * LoginForm, RegistrationForm, AuthContext, AuthApi (2.5 hours)

### Friday
* Together (4 hours)
  * create Navbar, Lobby, RoomForm
* Xiao & Aaron
  * Landing, NotFound, Errors (2.5 hours)
* Tom (3 hours)
  * wire up websocket connection functionality (connect, disconnect, useEffect, start game button, leave button)

### Saturday
* Together (as much as needed)
  * finish visual layout of room (html and css)
  * add basic game flow functionality (bet, raise, check, fold)

### Sunday
* Together (as much as needed)
  * add complete game flow functionality (player turns, updating balances, displaying winner, game rounds)

### Monday
* Bug hunt and fix all day

### Tuesday
* hopefully take the day off, mostly

### Wednesday
* Spend morning finishing bug fixes
* Prepare powerpoint in afternoon

### Thursday
* practice run of presentations

### Friday
* presentations