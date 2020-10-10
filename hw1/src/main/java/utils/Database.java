package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import models.GameBoard;
import models.Player;


public class Database {
  
  

  /**
   *  Every time a new game.
   */
  public void databaseNewGame() {
    Connection connection = null;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
      System.out.println("Opened database successfully");

      Statement stmt = connection.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS tictactoe (" 
                     + " P1             VARCHAR(1)  NOT NULL, "
                     + " P2             VARCHAR(1) , "
                     + " GameStart      INT         NOT NULL, "
                     + " Turn           INT         NOT NULL, "
                     + " Win            INT         NOT NULL, "
                     + " Draw           INT         NOT NULL, "
                     + " Grid1          VARCHAR(1) , "
                     + " Grid2          VARCHAR(1) , "
                     + " Grid3          VARCHAR(1) , "
                     + " Grid4          VARCHAR(1) , "
                     + " Grid5          VARCHAR(1) , "
                     + " Grid6          VARCHAR(1) , "
                     + " Grid7          VARCHAR(1) , "
                     + " Grid8          VARCHAR(1) , "
                     + " Grid9          VARCHAR(1) ) ";
      stmt.executeUpdate(sql);
      stmt.close();
      connection.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }
    
  
  /**
   * Every time a new game.
   */
  
  public void clearDatabase() {
    Connection connection = null;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
      System.out.println("Opened database successfully");

      Statement stmt = connection.createStatement();
      String sql = "DELETE FROM tictactoe";
      stmt.executeUpdate(sql);
      stmt.close();
      connection.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }
  
  /**
   * Every time a new game.
   */
  
  public void gameboardToDatabase(GameBoard gb) {
    
    char[] types = new char[2];
    types[0] = 'X';
    types[1] = 'O';
    
    char p2Type = (gb.getP1().getType() == types[0]) ? types[1] : types[0];
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
    Connection connection = null;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
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
                 + p2Type
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
      stmt.executeUpdate(sql);
      stmt.close();
      connection.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }
  
  /**
   * return.
   */
  public void fromDataBase(GameBoard gameBoard) {
    
    GameBoard gb = gameBoard;
    Connection connection = null;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
      connection.setAutoCommit(false);
      System.out.println("Ready to read the database");

      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("select * from tictactoe " 
                   + "where rowid in(select max(rowid) from tictactoe);");
       
      while (rs.next()) {
        String p1 = rs.getString("P1");
        Player player1 = new Player(p1.charAt(0), 1);
        gb.setP1(player1);
        String p2 = rs.getString("P2");
        Player player2 = new Player(p2.charAt(0), 2);
        gb.setP2(player2);
        int gameStart = rs.getInt("GameStart");
        if (gameStart == 1) {
          gb.setGameStarted(true);
        } else {
          gb.setGameStarted(false);
        }
        int turn = rs.getInt("Turn");
        //turn = (turn == 1) ? 2 : 1;
        gb.setTurn(turn);
        int win = rs.getInt("Win");
        gb.setWinner(win);
        int draw = rs.getInt("Draw");
        if (draw == 1) {
          gb.setDraw(true);
        } else {
          gb.setDraw(false);
        }
        
        String grid1 = rs.getString("Grid1");
        gb.setBoardStateForTest(grid1.charAt(0), 0, 0);
        String grid2 = rs.getString("Grid2");
        gb.setBoardStateForTest(grid2.charAt(0), 0, 1);
        String grid3 = rs.getString("Grid3");
        gb.setBoardStateForTest(grid3.charAt(0), 0, 2);
        String grid4 = rs.getString("Grid4");
        gb.setBoardStateForTest(grid4.charAt(0), 1, 0);
        String grid5 = rs.getString("Grid5");
        gb.setBoardStateForTest(grid5.charAt(0), 1, 1);
        String grid6 = rs.getString("Grid6");
        gb.setBoardStateForTest(grid6.charAt(0), 1, 2);
        String grid7 = rs.getString("Grid7");
        gb.setBoardStateForTest(grid7.charAt(0), 2, 0);
        String grid8 = rs.getString("Grid8");
        gb.setBoardStateForTest(grid8.charAt(0), 2, 1);
        String grid9 = rs.getString("Grid9");
        gb.setBoardStateForTest(grid9.charAt(0), 2, 2);
        
        for (int i = 0; i < 3; ++i) {
          for (int j = 0; j < 3; ++j) {
            if (gb.getBoardState()[i][j] == '0') {
              gb.setBoardStateForTest('\u0000', i, j);
            }
          }
        }
      }
      rs.close();
      stmt.close();
      connection.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }
}
