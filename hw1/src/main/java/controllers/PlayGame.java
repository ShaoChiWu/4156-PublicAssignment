package controllers;


import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import utils.Database;

import org.eclipse.jetty.websocket.api.Session;


public class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  /** Main method of the application.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {
    
    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);
    
    Database db = new Database();
    
    // CDOE START HERE
    db.databaseNewGame();
    GameBoard gameBoard = new GameBoard();
    db.fromDataBase(gameBoard);
    
    // Endpoint newgame
    app.get("/newgame", ctx -> {
      gameBoard.setNewGame(); // clear gameBoard, ready to start again
      ctx.redirect("tictactoe.html");
      new Database().databaseNewGame(); 
      //Every time a new game starts, the database table(s) must be cleaned.
      new Database().clearDatabase();
    });
   
    // Endpoint startgame
    app.post("/startgame", ctx -> {
      String type = ctx.body();
      char type1 = type.charAt(type.length() - 1); // Get 'X' or 'O' from "type=?"
      System.out.println(type1);
      Player p1 = new Player(type1, 1);
      gameBoard.setP1(p1);
      gameBoard.setP2(null);
      System.out.println(new Gson().toJson(gameBoard)); // Convert models to JSON objects
      ctx.result(new Gson().toJson(gameBoard));
      new Database().gameboardToDatabase(gameBoard);
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
      
      // START HERE
      new Database().gameboardToDatabase(gameBoard);
      //ctx.result(new Gson().toJson(new Message().getMessage()));
    });
    
    // Endpoint move
    app.post("/move/:playerId", ctx -> {
      
      Message message = new Message();
      int turn = Character.getNumericValue(ctx.pathParam("playerId").charAt(0));
      if (message.checkTurn(gameBoard, turn) == false) {
        ctx.result(new Gson().toJson(message));
        return;
      }
      
      // First check if the player get its turn
      if (message.checkJoin(gameBoard)) {
        if (message.checkTurn(gameBoard, turn)) {
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
            ctx.result(new Gson().toJson(message));
            new Database().gameboardToDatabase(gameBoard);
            return;
          }
          ctx.result(new Gson().toJson(message));
          new Database().gameboardToDatabase(gameBoard);
          return;
        }
      } 
      ctx.result(new Gson().toJson(message));
      //new Database().gameboardToDatabase(gameBoard);
    });
    
    // Endpoint getgameboard
    app.post("/getgameboard", ctx -> {
      System.out.println(new Gson().toJson(gameBoard)); // Convert models to JSON objects
      ctx.result(new Gson().toJson(gameBoard));
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
