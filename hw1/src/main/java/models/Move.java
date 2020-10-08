package models;

public class Move {

  private Player player;
  
  private int moveX;
  
  private int moveY;
  
  /** 
   * This is Move class.
   * moveX and moveY represents the move
   * player is the one who moves
   */
  
  public Move(int moveX, int moveY) {
    this.player = new Player();
    this.moveX = moveX;
    this.moveY = moveY;
  }
  
  /** 
   * This is Move class.
   * moveX and moveY represents the move
   * player is the one who moves
   */
 
  public Move(Player player, int moveX, int moveY) {
    this.player = player;
    this.moveX = moveX;
    this.moveY = moveY;
  }
  
  
  public int getMoveX() {
    return moveX;
  }
  
  public int getMoveY() {
    return moveY;
  }
 
  public void setplayer(Player player) {
    this.player = player;
  }
  
  public Player getplayer() {
    return player;
  }
  
}
