package aa.project;

import java.awt.Image;
import javax.swing.ImageIcon;

// Shape Class of target and bullets
public class Shape {
    public int x,y;
    private int limit = 500;
    private boolean leftRight = true;
    
    // Constructor
    public Shape(int initialX,int initialY){
      x = initialX;
      y = initialY;
    }

    // Move up by y - 2 pixels (Some target cannot be hit)
    // provides illusion that game is easy
    public void moveUp(){
        this.y -= 2;
    }
    
    // Move target either left or right by 1 pixel
    public void move(){

      if(x == 1){
        leftRight = true;
      } else if(x == limit){
        leftRight = false;
      }
      if(leftRight == true){
        x++; 
      } else{
        x--;
      }
    }


}