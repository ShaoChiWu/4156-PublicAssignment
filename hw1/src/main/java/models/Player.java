package models;

public class Player {

  private char type;

  private int id;
  
  /*
   * This is Player class.
   * id is 1 or 2
   * type is 'X' or 'O'
   */
  
  // default constructor
  public Player() {
    type = 'O';
    id = 1;
  }
  
  //common constructor
  public Player(char type, int id) {
    this.type = type;
    this.id = id;
  }
  
  public void setPlayer(char type, int id) {
    this.type = type;
    this.id = id;
  }
  
  public void setType(char type) {
    this.type = type;
  }
  
  public char getType() {
    return type;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public int getId() {
    return id;
  }
 
}