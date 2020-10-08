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
  
  public char getType() {
    return type;
  }
  
  public void setType(char type) {
    this.type = type;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
 
}