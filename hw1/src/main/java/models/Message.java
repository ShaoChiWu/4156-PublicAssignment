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
    this.message = "I am a message!";
    this.moveValidity = false;
    this.code = 0;
  }  
  
  /** 
   * This function checks MoveValidity.
   */
  
  public boolean checkMoveValidity(GameBoard gameBoard, Move move) {
    if (gameBoard.getBoardState()[move.getMoveX()][move.getMoveY()] == 0) {
      this.moveValidity = true;
      this.message = "Valid!";
      this.code = 1;
    } else {
      this.moveValidity = false;
      this.message = "Not Valid!";
      this.code = -1;
    }
    return moveValidity;
  }
  
  /** 
   * This function checks game GameStarted.
   */
  
  public boolean checkJoin(GameBoard gameBoard) {
    if (gameBoard.getGameStarted()) {
      this.moveValidity = true;
      this.message = "P2 join, P1 ready to move!";
      this.code = 2;
    } else {
      this.moveValidity = false;
      this.message = "Wait for P2!";
      this.code = -2;
    }
    return moveValidity;
  }
  
  /** 
   * This function checks turn.
   */
  
  public boolean checkTurn(GameBoard gameBoard, int turn) {
    if (gameBoard.getTurn() == turn) {
      this.moveValidity = true;
      this.message = "Your Turn!";
      this.code = +3;
    } else {
      this.moveValidity = false;
      this.message = "Not Your Turn!";
      this.code = -3;
    }
    return moveValidity;
  }
  
  /** 
   * This function checks if Draw.
   */
  
  public boolean checkDraw(GameBoard gameBoard) {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (gameBoard.getBoardState()[i][j] == 0) {
          this.code = -12;
          return false;
        }
      }
    }
    this.message = "Draw!";
    gameBoard.setGameStarted(false);
    this.code = 12;
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
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    } else if (gameBoard.getBoardState()[1][0] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[1][2] == type) {
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    } else if (gameBoard.getBoardState()[2][0] == type 
        && gameBoard.getBoardState()[2][1] == type 
        && gameBoard.getBoardState()[2][2] == type) {
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    } else if (gameBoard.getBoardState()[0][0] == type 
        && gameBoard.getBoardState()[1][0] == type 
        && gameBoard.getBoardState()[2][0] == type) {
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    } else if (gameBoard.getBoardState()[0][1] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[2][1] == type) {
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    
    } else if (gameBoard.getBoardState()[0][2] == type 
        && gameBoard.getBoardState()[1][2] == type 
        && gameBoard.getBoardState()[2][2] == type) {
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    } else if (gameBoard.getBoardState()[0][0] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[2][2] == type) {
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    } else if (gameBoard.getBoardState()[2][0] == type 
        && gameBoard.getBoardState()[1][1] == type 
        && gameBoard.getBoardState()[0][2] == type) {
      this.message = "End Game, some wins.";
      gameBoard.setGameStarted(false);
      this.code = 100;
      return true;
    } else {
      this.code = -100;
      return false;
    }
    
  }
  
  public String getMessage() {
    System.out.println(code);
    return this.message; 
  }
  

}
