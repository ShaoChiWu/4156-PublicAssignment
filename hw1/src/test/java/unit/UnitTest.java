package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.PlayGame;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class) 
public class UnitTest {
  
  /**
   * Runs only once before the testing starts.
   */
  @BeforeAll
  public static void init() {
    // Start Server
    PlayGame.main(null);
    System.out.println("Before All");
  }
  
  /**
   * This is a test case to evaluate the newgame endpoint.
   */
  @Test
  public void newGameTest() {
      
    // Create HTTP request and get response
    HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();
        
    // Check assert statement (New Game has started)
    assertEquals(restStatus, 200);
    System.out.println("Test New Game");
  }
    
  /**
   * This is a test case to evaluate the startgame endpoint.
   */
  @Test
  public void startGameTest() {
    newGameTest();
    // Create a POST request to startgame endpoint and get the body
    HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    String responseBody = response.getBody();
        
    // --------------------------- JSONObject Parsing ----------------------------------
        
    System.out.println("Start Game Response: " + responseBody);
        
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);

    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(false, jsonObject.get("gameStarted"));
        
    // ---------------------------- GSON Parsing -------------------------
        
    // GSON use to parse data to object
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    Player player1 = gameBoard.getP1();
        
    // Check if player type is correct
    assertEquals('X', player1.getType());
    System.out.println("Test Start Game");
    
  }
  
  /** 
   * This is a test case to evaluate the joingame endpoint.
   * A player cannot make a move until both players have joined the game.
   */
  @Test
  public void beforeJoinGameTest() {
    newGameTest();
    startGameTest();
    // I start here: Before Join Game
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    String responseBody = response.getBody();    
    System.out.println("Before Join Game Response: " + responseBody);       
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    Gson gson = new Gson();
    Message message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Wait for P2!", message.getMessage());
    System.out.println("Invalid Test: player 1 move before join");
 
  }
  
  /**
   * This is a test case to evaluate the joingame endpoint.
   */
  @Test
  public void joinGameTest() {
    newGameTest();
    startGameTest();
    beforeJoinGameTest();
    // I start here: Join Game
    HttpResponse<String> response = Unirest.get("http://localhost:8080/joingame").asString();
    response = Unirest.post("http://localhost:8080/getgameboard").asString();
    String responseBody = response.getBody();
    System.out.println("Join Game Response: " + responseBody);
    JSONObject jsonObject = new JSONObject(responseBody);
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    assertEquals(1, gameBoard.getP1().getId());
    assertEquals(2, gameBoard.getP2().getId());
  
  }
  
  /**
   * This is a test case to evaluate the move endpoint.
   */
  @Test
  public void moveTest() {
    newGameTest();
    startGameTest();
    beforeJoinGameTest();
    joinGameTest();
    // Player 2 move 1, before player 1 move 1, invalid
    // After game has started Player 1 always makes the first move.
    // Create a POST request to move endpoint and get the message from body
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();    
    System.out.println("Player 2 move 1, before player 1 move 1 Response: " + responseBody);       
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    Gson gson = new Gson();
    Message message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: player 2 move before player 1");
    
    // Player 1 move 1, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 1");
    
    // Try player 1 move second time before player 2 moves, invalid 
    // A player cannot make two moves in their turn.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: Test move player 1 move 2");
    
    // Player 2 move 1, try an invalid move 
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Valid!", message.getMessage());
    System.out.println("Test invalid: player 2 move 1");
    
    // Player 2 move 1
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 1");
    
    // Try player 2 move second time before player 1 moves, invalid 
    // A player cannot make two moves in their turn.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: Test move player 2 move again");
    
    // Player 1 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 2");
    
    // Player 2 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 2");
    
    // Player 1 move 3, win
    // A player should be able to win a game.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message  = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 3");
    
    // Player 1 is winner, check
    // A player should be able to win a game.
    response = Unirest.post("http://localhost:8080/getgameboard").asString();
    responseBody = response.getBody();
    System.out.println("Win Game Response: " + responseBody);
    jsonObject = new JSONObject(responseBody);
    gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    assertEquals(1, gameBoard.getResult());
    
  }
  
  /**
   * This is a test case to evaluate the move endpoint.
   */
  @Test
  public void anotherMoveTest() {
    newGameTest();
    startGameTest();
    beforeJoinGameTest();
    joinGameTest();
    // Player 2 move 1, before player 1 move 1, invalid
    // After game has started Player 1 always makes the first move.
    // Create a POST request to move endpoint and get the message from body
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();    
    System.out.println("Player 2 move 1, before player 1 move 1 Response: " + responseBody);       
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    Gson gson = new Gson();
    Message message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: player 2 move befroe player 1");
    
    // Player 1 move 1, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 1");
    
    // Try player 1 move second time before player 2 moves, invalid 
    // A player cannot make two moves in their turn.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: Test move player 1 move again");
    
    // Player 2 move 1, try an invalid move 
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Valid!", message.getMessage());
    System.out.println("Test invalid: player 2 move 1");
    
    // Player 2 move 1
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 1");
    
    // Player 1 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 2");
    
    // Player 2 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 2");
    
    // Player 1 move 3, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 3");
    
    // Player 2 move 3, try an invalid move 
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 3");
    
    // Player 1 move 4, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 4");
    
    // Player 2 move 4, try an invalid move 
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 4");
    
    
    
    // Player 1 move 5, draw
    // A game should be a draw if all the positions are exhausted and no one has won.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message  = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 5");
    
    // Player 1 is winner, check
    // A player should be able to win a game.
    response = Unirest.post("http://localhost:8080/getgameboard").asString();
    responseBody = response.getBody();
    System.out.println("Draw Game Response: " + responseBody);
    jsonObject = new JSONObject(responseBody);
    gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    assertEquals(12, gameBoard.getResult());
    
  }
  
  /**
   * This is a test case to evaluate the newgame endpoint.
   */
  @Test
  public void newGameTest1() {
    
    // Create HTTP request and get response
    HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();
        
    // Check assert statement (New Game has started)
    assertEquals(restStatus, 200);
    System.out.println("Test New Game");
  }
  
  /**
   * This is a test case to evaluate the startgame endpoint.
   */
  @Test
  public void startGameTest1() {
    newGameTest1();
    // Create a POST request to startgame endpoint and get the body
    HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    String responseBody = response.getBody();
        
    // --------------------------- JSONObject Parsing ----------------------------------
        
    System.out.println("Start Game Response: " + responseBody);
        
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);

    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(false, jsonObject.get("gameStarted"));
        
    // ---------------------------- GSON Parsing -------------------------
        
    // GSON use to parse data to object
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    Player player1 = gameBoard.getP1();
        
    // Check if player type is correct
    assertEquals('O', player1.getType());
    System.out.println("Test Start Game");
    
  }
  
  /** 
   * This is a test case to evaluate the joingame endpoint.
   * A player cannot make a move until both players have joined the game.
   */
  @Test
  public void beforeJoinGameTest1() {
    newGameTest1();
    startGameTest1();
    // I start here: Before Join Game
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    String responseBody = response.getBody();    
    System.out.println("Before Join Game Response: " + responseBody);       
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    Gson gson = new Gson();
    Message message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Wait for P2!", message.getMessage());
    System.out.println("Invalid Test: player 1 move before join");
    System.out.println("HERE");
  }
  
  /**
   * This is a test case to evaluate the joingame endpoint.
   */
  @Test
  public void joinGameTest1() {
    System.out.println("HERE");
    newGameTest1();
    startGameTest1();
    beforeJoinGameTest1();
    // I start here: Join Game
    HttpResponse<String> response = Unirest.get("http://localhost:8080/joingame").asString();
    response = Unirest.post("http://localhost:8080/getgameboard").asString();
    String responseBody = response.getBody();
    System.out.println("Join Game Response: " + responseBody);
    JSONObject jsonObject = new JSONObject(responseBody);
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    assertEquals(1, gameBoard.getP1().getId());
    assertEquals(2, gameBoard.getP2().getId());
    System.out.println("DONE");
  }
  
  /**
   * This is a test case to evaluate the move endpoint.
   */
  @Test
  public void moveTest1() {
    newGameTest1();
    startGameTest1();
    beforeJoinGameTest1();
    joinGameTest1();
    // Player 2 move 1, before player 1 move 1, invalid
    // After game has started Player 1 always makes the first move.
    // Create a POST request to move endpoint and get the message from body
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();    
    System.out.println("Player 2 move 1, before player 1 move 1 Response: " + responseBody);       
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    Gson gson = new Gson();
    Message message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: player 2 move before player 1");
    
    // Player 1 move 1, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 1");
    
    // Try player 1 move second time before player 2 moves, invalid 
    // A player cannot make two moves in their turn.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: Test player 1 move again");
    
    // Player 2 move 1, try an invalid move 
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Valid!", message.getMessage());
    System.out.println("Test invalid: move player 2 move 1");
    
    // Player 2 move 1
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 1");
    
    // Player 1 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 2");
    
    // Player 2 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 2");
    
    // Player 1 move 3, win
    // A player should be able to win a game.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message  = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 3");
    
    // Player 1 is winner, check
    // A player should be able to win a game.
    response = Unirest.post("http://localhost:8080/getgameboard").asString();
    responseBody = response.getBody();
    System.out.println("Win Game Response: " + responseBody);
    jsonObject = new JSONObject(responseBody);
    gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    assertEquals(1, gameBoard.getResult());
    
  }
  
  /**
   * This is a test case to evaluate the move endpoint.
   */
  @Test
  public void anotherMoveTest1() {
    newGameTest1();
    startGameTest1();
    beforeJoinGameTest1();
    joinGameTest1();
    // Player 2 move 1, before player 1 move 1, invalid
    // After game has started Player 1 always makes the first move.
    // Create a POST request to move endpoint and get the message from body
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();    
    System.out.println("Player 2 move 1, before player 1 move 1 Response: " + responseBody);       
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    Gson gson = new Gson();
    Message message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: player 2 move before player 1");
    
    // Player 1 move 1, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 1");
    
    // Try player 1 move second time before player 2 moves, invalid 
    // A player cannot make two moves in their turn.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: Test move player 1 move again");
    
    // Player 2 move 1, try an invalid move 
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Valid!", message.getMessage());
    System.out.println("Test invalid: move player 2 move 1");
    
    // Player 2 move 1
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 1");
    
    
    // Try player 2 move second time before player 1 moves, invalid 
    // A player cannot make two moves in their turn.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Not Your Turn!", message.getMessage());
    System.out.println("Test invalid: Test move player 2 move again");
    
    // Player 1 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 2");
    
    // Player 2 move 2
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 2");
    
    // Player 1 move 3, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 3");
    
    // Player 2 move 3
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 3");
    
    // Player 1 move 4, valid
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 4");
    
    // Player 2 move 4
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 2 move 4");
    
    
    
    // Player 1 move 5, draw
    // A game should be a draw if all the positions are exhausted and no one has won.
    // Create a POST request to move endpoint and get the message from body
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
    responseBody = response.getBody();    
    System.out.println("Move Response: " + responseBody);       
    // Parse the response to JSON object
    jsonObject = new JSONObject(responseBody);
    // GSON use to parse data to object
    gson = new Gson();
    message  = gson.fromJson(jsonObject.toString(), Message.class);
    // Check if system message is correct
    assertEquals("Valid!", message.getMessage());
    System.out.println("Test: player 1 move 5");
    
    // Draw, check
    // A player should be able to win a game.
    response = Unirest.post("http://localhost:8080/getgameboard").asString();
    responseBody = response.getBody();
    System.out.println("Draw Game Response: " + responseBody);
    jsonObject = new JSONObject(responseBody);
    gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    assertEquals(12, gameBoard.getResult());
    
  }
  
  // END OF Endpoint tests
  
  GameBoard gameBoard = new GameBoard();
  Message message = new Message();
  Move move = new Move(0, 0);
  Player p1 = new Player('X', 1);
  Player p2 = new Player('O', 2);
  
  @Test
  public void testCheckWin1() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 0);
    Move move2 = new Move(p2, 1, 1);
    Move move3 = new Move(p2, 2, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin2() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 2);
    Move move2 = new Move(p2, 1, 1);
    Move move3 = new Move(p2, 2, 0);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin3() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 2);
    Move move2 = new Move(p2, 1, 2);
    Move move3 = new Move(p2, 2, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin4() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 1);
    Move move2 = new Move(p2, 1, 1);
    Move move3 = new Move(p2, 2, 1);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin5() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 0);
    Move move2 = new Move(p2, 1, 0);
    Move move3 = new Move(p2, 2, 0);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin6() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 0);
    Move move2 = new Move(p2, 0, 1);
    Move move3 = new Move(p2, 0, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin7() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 1, 0);
    Move move2 = new Move(p2, 1, 1);
    Move move3 = new Move(p2, 1, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin8() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 2, 0);
    Move move2 = new Move(p2, 2, 1);
    Move move3 = new Move(p2, 2, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(true, win);
  }
  
  @Test
  public void testCheckWin9() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 0);
    Move move2 = new Move(p2, 0, 1);
    Move move3 = new Move(p2, 1, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(false, win);
  }
  
  @Test
  public void testCheckWin10() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 1, 0);
    Move move2 = new Move(p2, 1, 1);
    Move move3 = new Move(p2, 2, 1);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(false, win);
  }
  
  @Test
  public void testCheckWin11() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 2, 0);
    Move move2 = new Move(p2, 2, 1);
    Move move3 = new Move(p2, 1, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(false, win);
  }
  
  @Test
  public void testCheckWin12() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 2);
    Move move2 = new Move(p2, 1, 2);
    Move move3 = new Move(p2, 2, 1);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(false, win);
  }
  
  @Test
  public void testCheckWin13() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 0, 0);
    Move move2 = new Move(p2, 1, 1);
    Move move3 = new Move(p2, 0, 1);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(false, win);
  }
  
  @Test
  public void testCheckWin14() {
    gameBoard.setP1(p2);
    Move move1 = new Move(p2, 2, 0);
    Move move2 = new Move(p2, 0, 0);
    Move move3 = new Move(p2, 0, 2);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    boolean win  = message.checkWin(gameBoard, move3);
    assertEquals(false, win);
  }
  
  @Test
  public void testCheckDraw() {
    gameBoard.setP1(p1);
    gameBoard.setP2(p2);
    Move move1 = new Move(p1, 0, 2); 
    Move move2 = new Move(p2, 0, 0);
    Move move3 = new Move(p1, 0, 1);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    Move move4 = new Move(p2, 1, 2);
    Move move5 = new Move(p1, 1, 0);
    Move move6 = new Move(p2, 1, 1);
    gameBoard.setBoardStateForTest(move4);
    gameBoard.setBoardStateForTest(move5);
    gameBoard.setBoardStateForTest(move6);
    Move move7 = new Move(p1, 2, 2);
    Move move8 = new Move(p2, 2, 0);
    Move move9 = new Move(p1, 2, 1);
    gameBoard.setBoardStateForTest(move7);
    gameBoard.setBoardStateForTest(move8);
    gameBoard.setBoardStateForTest(move9);
    boolean draw  = message.checkDraw(gameBoard);
    assertEquals(true, draw);
  }
  
  @Test
  public void testCheckDraw1() {
    gameBoard.setP1(p1);
    gameBoard.setP2(p2);
    Move move1 = new Move(p1, 0, 0); 
    Move move2 = new Move(p2, 0, 2);
    Move move3 = new Move(p1, 0, 1);
    gameBoard.setBoardStateForTest(move1);
    gameBoard.setBoardStateForTest(move2);
    gameBoard.setBoardStateForTest(move3);
    Move move4 = new Move(p2, 1, 2);
    Move move5 = new Move(p1, 1, 0);
    Move move6 = new Move(p2, 1, 1);
    gameBoard.setBoardStateForTest(move4);
    gameBoard.setBoardStateForTest(move5);
    gameBoard.setBoardStateForTest(move6);
    Move move7 = new Move(p1, 2, 2);
    Move move8 = new Move(p2, 2, 0);
    gameBoard.setBoardStateForTest(move7);
    gameBoard.setBoardStateForTest(move8);
    boolean draw  = message.checkDraw(gameBoard);
    assertEquals(false, draw);
  }
  
  @Test
  public void testCheckTurn() {
    gameBoard.setTurn(1);
    message.checkTurn(gameBoard, 1);
    assertEquals("Your Turn!", message.getMessage());
  }
  
  @Test
  public void testCheckTurn1() {
    gameBoard.setTurn(2);
    message.checkTurn(gameBoard, 1);
    assertEquals("Not Your Turn!", message.getMessage());
  }
  
  @Test
  public void testSetType() {
    p1.setType('O');
    assertEquals('O', p1.getType());
  }
  
  @Test
  public void testCheckJoin() {
    gameBoard.setGameStarted(true);
    message.checkJoin(gameBoard);
    assertEquals("P2 join, P1 ready to move!", message.getMessage());
  }  
  
  @Test
  public void testcheckMoveValidity() {
    gameBoard.setP1(p1);
    gameBoard.setP1(p2);
    Move move1 = new Move(p1, 1, 1);
    gameBoard.setBoardStateForTest(move1);
    Move move2 = new Move(p2, 1, 2);
    message.checkMoveValidity(gameBoard, move2);
    assertEquals("Valid!", message.getMessage());
  }  
  
  @Test
  public void testcheckMoveValidity1() {
    gameBoard.setP1(p1);
    gameBoard.setP1(p2);
    Move move1 = new Move(p1, 1, 1);
    gameBoard.setBoardStateForTest(move1);
    Move move2 = new Move(p2, 1, 1);
    message.checkMoveValidity(gameBoard, move2);
    assertEquals("Not Valid!", message.getMessage());
  } 

  
  @Test
  public void testSetId() {
    p1.setId(1);
    assertEquals(1, p1.getId());
  }
  
  /**
   * This will run every time after a test has finished.
   */
  @AfterEach
  public void finishGame() {
    System.out.println("After Each");
  }
    
  /**
   * This method runs only once after all the test cases have been executed.
   */
  @AfterAll
  public static void close() {
    // Stop Server
    PlayGame.stop();
    System.out.println("After All");
  }
  
}
