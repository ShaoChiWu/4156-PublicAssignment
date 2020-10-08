package utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Queue;
import models.GameBoard;
import models.Player;


public class Database {
  
  private int code; 
  
  public Database() {
    code = 0;
  }
  
  private void testConnection() {
    try {
      Class.forName("org.sqlite.JDBC");
      Connection connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Opened database successfully");
    
  }
  

  
  /**
   *  Every time a new game.
   */
  public void databaseNewGame() {
    
    try {
      Class.forName("org.sqlite.JDBC");
      Connection connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
      System.out.println("Opened database successfully");

      Statement stmt = connection.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS tictactoe (" 
                     + " P1             VARCHAR(1)  NOT NULL, "
                     + " P2             VARCHAR(1)  NOT NULL, "
                     + " GameStart      VARCHAR(1)  NOT NULL, "
                     + " Turn           VARCHAR(1)  NOT NULL, "
                     + " Win            VARCHAR(1)  NOT NULL, "
                     + " Draw           VARCHAR(1)  NOT NULL, "
                     + " Grid1          VARCHAR(1)  NOT NULL, "
                     + " Grid2          VARCHAR(1)  NOT NULL, "
                     + " Grid3          VARCHAR(1)  NOT NULL, "
                     + " Grid4          VARCHAR(1)  NOT NULL, "
                     + " Grid5          VARCHAR(1)  NOT NULL, "
                     + " Grid6          VARCHAR(1)  NOT NULL, "
                     + " Grid7          VARCHAR(1)  NOT NULL, "
                     + " Grid8          VARCHAR(1)  NOT NULL, "
                     + " Grid9          VARCHAR(1)  NOT NULL) ";
      stmt.executeUpdate(sql);
      sql = "DELETE FROM tictactoe";
      stmt.executeUpdate(sql);
      stmt.close();
      connection.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Database: New Game");
  }
  
  /**
   * Every time a new game.
   */
  
  private void clearDatabase() {
    
    try {
      Class.forName("org.sqlite.JDBC");
      Connection connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
      System.out.println("Opened database successfully");

      Statement stmt = connection.createStatement();
      String sql = "DELETE FROM tictactoe";
      stmt.executeUpdate(sql);
      stmt.close();
      connection.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Database: New Game");
  }
  
  /**
   * Every time a new game.
   */
  
  public void gameboardToDatabase(GameBoard gb) {
    
    testConnection();
    
    int getGameStarted = gb.getGameStarted() ? 1 : 0;
    int draw;
    int win;
    if (gb.getResult() == 12) {
      draw = 1;
      win = 0;
    } else {
      draw = 0;
      win = gb.getResult();
    }
    try {
      Class.forName("org.sqlite.JDBC");
      Connection connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
      System.out.println("Opened database successfully");
      
      Statement stmt = connection.createStatement();
      String sql = "INSERT INTO tictactoe"
                 + "(P1, P2, GameStart, Turn, Win, Draw,"
                 + "Grid1, Grid2, Grid3, Grid4, Grid5, Grid6, Grid7, Grid8, Grid9) "
                 + "VALUES("
                 + "\""
                 + gb.getP1().getType()
                 + "\", "
                 + "\""
                 + gb.getP2().getType()
                 + "\", "
                 + getGameStarted + ", "
                 + gb.getTurn() + ", "
                 + win + ", "
                 + draw;
      for (int i = 0; i < 3; ++i) {
        for (int j = 0; j < 3; ++j) {
          sql += ", ";
          if (gb.getBoardState()[i][j] == 0) {
            sql += "\"0\"";
            continue;
          }
          sql +=  "\"";
          sql += gb.getBoardState()[i][j];
          sql +=  "\"";
        }
      }
      sql += ")";
      System.out.println(sql);
      stmt.executeUpdate(sql);
      System.out.println("Done");
      stmt.close();
      connection.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Database: New Game");
  }
  
}
