package controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import java.io.IOException;
import java.lang.Object;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;


class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  /** Main method of the application.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {
    
    GameBoard gameBoard = new GameBoard();

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
   
    // Endpoint startgame
    app.post("/startgame", ctx -> {
      String type = ctx.body();
      char type1 = type.charAt(type.length() - 1); // Get 'X' or 'O' from "type=?"
      System.out.println(type1);
      Player p1 = new Player(type1, 1);
      gameBoard.setP1(p1);
      System.out.println(new Gson().toJson(gameBoard)); // Convert models to JSON objects
      ctx.result(new Gson().toJson(gameBoard));
      
    });
    
    // Endpoint joingame
    app.get("/joingame", ctx -> {
      ctx.redirect("/tictactoe.html?p=2");
      char type2 = gameBoard.getP1().getType() == 'X' ? 'O' : 'X'; // If p1 'X', p2 'O'; vise versa
      Player p2 = new Player(type2, 2);
      gameBoard.setP2(p2);
      gameBoard.setGameStarted(true);
      sendGameBoardToAllPlayers(new Gson().toJson(gameBoard));
      System.out.println(new Gson().toJson(gameBoard));
    });
    
    // Endpoint newgame
    app.get("/newgame", ctx -> {
      gameBoard.setNewGame(); // clear gameBoard, ready to start again
      ctx.redirect("tictactoe.html");
    });
    
    // Endpoint move
    app.post("/move/:playerId", ctx -> {
      
      Message message = new Message();
      
      // First check if the player get its turn
      if (gameBoard.getTurn() == Character.getNumericValue(ctx.pathParam("playerId").charAt(0))) {
        String move = ctx.body();
        int moveX = move.charAt(2) - '0'; // Get moveX from "x=0&y=0"
        int moveY = move.charAt(move.length() - 1) - '0'; // Get moveY from "x=0&y=0"
        Move playerMove = new Move(moveX, moveY);
        if (gameBoard.getTurn() == 1) { //PLayer 1 moves
          playerMove.setplayer(gameBoard.getP1());
        } else if (gameBoard.getTurn() == 2) { //PLayer 2 moves
          playerMove.setplayer(gameBoard.getP2());
        }
        if (message.checkMoveValidity(gameBoard, playerMove)) { // Check if move is valid
          gameBoard.setBoardState(playerMove);
          sendGameBoardToAllPlayers(new Gson().toJson(gameBoard)); 
        }
      }
    });

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }

  /** Send message to all players.
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        // Add logger here
      }
    }
  }

  public static void stop() {
    app.stop();
  }
}
