package models;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;
  
  
  /**
   * GameBoard class of the model. 
   * boardState is a char 2D array recording 'X' and 'O'
   * winner, isDraw, gameStarted are boolean reflecting game status
   * turn is would be 1 or 2
   */
  
  public GameBoard() {
    gameStarted = false;
    turn = 1;
    this.boardState = new char[3][3];
    winner = 0;
    isDraw = false;
    p1 = null;
    p2 = null;    
  }
  
  /**
   * GameBoard class of the model. Copy constructor.
   */
  public GameBoard(GameBoard gb) {
    gameStarted = gb.gameStarted;
    turn = gb.turn;
    boardState = new char[3][3];
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 3; ++j) {
        boardState[i][j] = gb.boardState[i][j];
      }
    }
    winner = gb.winner;
    isDraw = gb.isDraw;
    p1 = gb.p1;
    p2 = gb.p2;
  }
  
  public void setP1(Player p1) {
    this.p1 = p1;
  }
  
  public Player getP1() {
    return p1;
  }
  
  public void setP2(Player p2) {
    this.p2 = p2;
  }
  
  public Player getP2() {
    return p2;
  }
  
  public void setWinner(int winner) {
    this.winner = winner;
  }

  public void setDraw(boolean draw) {
    this.isDraw = draw;
  }
  
  public void setGameStarted(boolean gameStarted) {
    this.gameStarted = gameStarted;
  }
  
  public boolean getGameStarted() {
    return gameStarted;
  }
  
  /**
   * Set 'X' or 'O' to the 3x3 board.
   * Check if the game ends 
   * Switch turn
   */
  public void setBoardState(Move move) {
    Message message = new Message();
    boardState[move.getMoveX()][move.getMoveY()] = move.getplayer().getType();
    if (message.checkWin(this, move)) {
      winner = this.turn;
      return;
    }
    if (message.checkDraw(this)) {
      isDraw = true;
      return;
    }
    turn = (move.getplayer().getId() == 1) ? 2 : 1;
    
  }
  
  /**
   * For testing
   * Set 'X' or 'O' to the 3x3 board.
   * Check if the game ends 
   * Switch turn
   */
  public void setBoardStateForTest(Move move) {
    boardState[move.getMoveX()][move.getMoveY()] = move.getplayer().getType();

  }
  
  /**
   * For testing
   * Set 'X' or 'O' to the 3x3 board.
   * Check if the game ends 
   * Switch turn
   */
  public void setBoardStateForTest(char type, int moveX, int moveY) {
    boardState[moveX][moveY] = type;

  }
  
  /**
   * Get the 3x3 board.
   */
  
  public char[][] getBoardState() {
    char[][] boardStateCopy = new char[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        boardStateCopy[i][j] = boardState[i][j];
      }
    }
    return boardStateCopy;
  }
  
  /** 
   * Call this when newGame.
   * Clear the board, ready to serve a new game.
   */
  
  public void setNewGame() {
    gameStarted = false;
    turn = 1;
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 3; ++j) {
        boardState[i][j] = 0;
      }
    }
    winner = 0;
    isDraw = false;
    p1 = null;
    p2 = null;
  }
  
  
  public int getTurn() {
    return turn;
  }
  
  
  public void setTurn(int turn) {
    this.turn = turn;
  }
  
  /**
   * Get game game result.
   */
  public int getResult() {
    if (isDraw) {
      return 12;
    } else {
      return winner;
    }
  }
  
}


