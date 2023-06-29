# Development Plan

## Team Name: Sparkle Motion

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

* [x] `App.java` -- @SpringBootApplication
* [x] `AppConfig.java` -- @Configuration
* [x] `WebSocketConfig.java` -- @Configuration, @EnableWebSocketMessageBroker
* [x] `Credentials.java`
* [x] `JwtConverter.java`
* [x] `JwtRequestFilter.java` -- extends BasicAuthenticationFilter
* [x] `RoomJdbcTemplateRepository.java`
* [x] `RoomRepository.java`
* [x] `PlayerJdbcTemplateRepository.java`
* [x] `PlayerRepository.java`
* [x] `GameJdbcTemplateRepository.java`
* [x] `GameRepository.java`
* [x] `PlayerMapper.java` -- implements RowMapper<Player>
* [x] `RoomMapper.java` -- implements RowMapper<Room>
* [x] `GameMapper.java` -- implements RowMapper<Game>
* [x] `PlayerService.java`
* [x] `RoomService.java`
* [x] `ResultType.java`  -- Enum
* [x] `Result.java`
* [x] `Player.java` -- implements UserDetails
* [x] `Room.java`
* [x] `Game.java`
* [x] `Board.java`
* [x] `Position.java` -- Enum
* [x] `Action.java` -- Enum
* [x] `Card.java` -- Enum
* [x] `RoomController.java` -- @RestController, @RequestMapping("/api/player")
* [x] `GlobalExceptionHandler.java` -- @ControllerAdvice
* [x] `PlayerController.java` -- @RestController, @RequestMapping("/api/player"), @CrossOrigin(origins ={"http://localhost:3000"}), @ConditionalOnWebApplication
* [x] `RoomRepositoryTest.java`
* [x] `GameRepositoryTest.java`
* [x] `GameServiceTest.java`
* [x] `PlayerServiceTest.java`
* [x] `RoomServiceTest.java`

#### src.main.java.learn.poker.security
* [ ] `SecurityConfig.java` -- @Configuration, @ConditionalOnWebApplication
  * `private final JwtConverter jwtConverter`
  * `public SecurityConfig(JwtConverter jwtConverter)`
  * `public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception` -- @Bean
  * `public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception` -- @Bean

<!-- #### src.main.java.learn.poker.data
* [ ] `DataException.java` 
    * `public DataException(String, Throwable)` -->

#### src.main.java.learn.poker.domain
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

#### src.main.java.learn.poker.controller
* [ ] `GameController.java` -- @Controller
  * `public Game startGame(@DestinationVariable int roomId)` -- @MessageMapping("/start"), @SendTo("/topic/room/{id}")
  * `public Game bet(@DestinationVariable int roomId, @DestinationVariable int amount)` -- @MessageMapping("/bet/{amount}"), @SendTo("/topic/room/{id}")
  * `public Game raise(@DestinationVariable int roomId, @DestinationVariable int amount)` -- @MessageMapping("/raise/{amount}"), @SendTo("/topic/room/{id}")
  * `public Game check(@DestinationVariable int roomId)` -- @MessageMapping("/check"), @SendTo("/topic/room/{id}")
  * `public Game fold(@DestinationVariable int roomId)` -- @MessageMapping("/fold"), @SendTo("/topic/room/{id}")
  * `public Game endGame(@DestinationVariable int roomId)` -- @MessageMapping("/end"), @SendTo("/topic/room/{id}")

### test.java.learn.mastery.data
* [ ] `PlayerRepositoryTest.java`


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

    * [x] setup frontend repo with React App project, install stomp and sockjs dependencies
    * [x] create App.java and App.js
    * [x] create development branches and individual working branches for both repos
    * [x] create model classes (45 minutes)
    * [x] create Result, ResultType (15 minutes)
    * [x] create PlayerService, PlayerService tests (2.5 hours)
    * [x] create RoomService, RoomService tests (2.5 hours)
    * [x] Write SQL scripts to setup production and test databases (2 hours)
    * [x] DataException class (5 minutes)
    * [x] PlayerJdbcTemplateRepository, PlayerRepository, PlayerMapper (1.5 hours)
    * [x] RoomJdbcTemplateRepository, RoomRepository, RoomMapper (1.5 hours)
    * [x] GameJdbcTemplateRepository, GameRepository, GameMapper (1.5 hours)
    * [x] create WebsocketConfig (15 minutes)
    * [x] create GameService CRUD and validation (not the game logic yet), tests (2.5 hours)
    * [x] RoomController (1 hour)
    * [x] PlayerController (1 hour)
    * any leftover tasks
    * [x] create AppConfig, JwtConverter, JwtRequestFilter, SecurityConfig, AuthController, GlobalExceptionHandler (3 hours)

### Thursday
* Together (4 hours)
* Xiao
  * fix PlayerService update tests
  * player controller http file
  * Landing, NotFound, Errors (2.5 hours)
  * create Navbar, Lobby, RoomForm
* Tom & Aaron
  * debug playerrepository tests, roomcontroller Put issue
  * setup CI/CD pipeline using Github Actions and Azure (1 hour)
  * create GameController and GameService game loop (3 hour)

### Friday
* Together in the morning (3 hours)
  * Room.js
  * wire up websocket connection functionality (connect, disconnect, useEffect, start game button, leave button)
* Together after lunch (3 hours)
  * finish visual layout of room (html and css)
* Tom
  * LoginForm, RegistrationForm, AuthContext, AuthApi (2.5 hours)

### Saturday
* Together (as much as needed)
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