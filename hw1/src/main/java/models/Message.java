package models;

public class Message {

  private boolean moveValidity;

  private int code;

  private String message;

  
  /** 
   * This is message class.
   * moveValidity is boolean, checks if move is valid
   * code and message are for error handling
   */
  
  public Message() {
    this.moveValidity = false;
    this.code = 0;
    this.message = "I am a message!";
  }  
  
  /** 
   * This function checks MoveValidity.
   */
  
  public boolean checkMoveValidity(GameBoard gameBoard, Move move) {
    if (gameBoard.getBoardState()[move.getMoveX()][move.getMoveY()] == 0) {
      return true;
    } else {
      moveValidity = false;
      code = -100;
      message = "Not Valid!";
      return false;
    }
  }
  
  /** 
   * This function checks if Draw.
   */
  
  public boolean checkDraw(GameBoard gameBoard) {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (gameBoard.getBoardState()[i][j] == 0) {
          code = 11;
          message = "Draw!";
          return false;
        }
      }
    }
    return true;
  }
  
  /** 
   * This function checks if someone wins.
   * There are eight possible patterns to win this game  
   */ 
  
  public boolean checkWin(GameBoard gameBoard, Move move) {
    char type = move.getplayer().getType();
    if (gameBoard.getBoardState()[0][0] == type 
        && gameBoard.getBoardState()[0][1] == type 
        && gameBoard.getBoardState()[0][2] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    }
    if (gameBoard.getBoardState()[1][0] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[1][2] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    }

    if (gameBoard.getBoardState()[2][0] == type 
        && gameBoard.getBoardState()[2][1] == type 
        && gameBoard.getBoardState()[2][2] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    }
    
    if (gameBoard.getBoardState()[0][0] == type 
        && gameBoard.getBoardState()[1][0] == type 
        && gameBoard.getBoardState()[2][0] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    }
  
    if (gameBoard.getBoardState()[0][1] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[2][1] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    
    }
    if (gameBoard.getBoardState()[0][2] == type 
        && gameBoard.getBoardState()[1][2] == type 
        && gameBoard.getBoardState()[2][2] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    }
    if (gameBoard.getBoardState()[0][0] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[2][2] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    }
    if (gameBoard.getBoardState()[2][0] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[0][2] == type) {
      code = 1000;
      message = "End Game, some wins.";
      return true;
    }
    return false;
    
  }

}
