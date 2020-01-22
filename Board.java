package aa.project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.util.concurrent.locks.*;


// Board class to provide the panel for the application
// and will be the main thread drawing the shapes for display
public class Board extends JPanel implements Runnable {

    private Shape shape1;
    private Thread animator;
    private ArrayList<Shape> items = new ArrayList<Shape>();
    private boolean first = true;
    private boolean collision = false;
    private int numberOfObstacles = 40;
    private Lock lock = LockFactory.getLock("myLock");;
    
    //Constructor
    public Board() {
      setBackground(Color.WHITE);
      setDoubleBuffered(true);
      shape1 = new Shape((int)(Math.random() * (500)),0);
      for(int i = 1; i <= numberOfObstacles; i++){
        Shape s = new Shape((int)(Math.random() * (500)),i*5);
        items.add(s);
      }        
    }
    
    // Get list of shapes
    public ArrayList<Shape> getItems(){
      return items;
    }
    
    // Get Collision statues
    public boolean getCollisionStatus(){
      return collision;
    }
    
    // On click of fire button, a bullet shape would be added in the bottom middle of the screen
    public void addShape(){
      Shape bulletUp = new Shape(225,390);
      items.add(bulletUp);
      first = false;
    }
    
    // Start this thread
    public void addNotify() {
        super.addNotify();
        animator = new Thread(this);
        animator.start();
    }
    
    // Paint all the items in the list
    // lock to ensure that no new shapes can be added in while painting
    public void paint(Graphics g) {
        super.paint(g);       
        Graphics2D g2d = (Graphics2D)g;
        lock.lock();
        Shape fired = items.get(items.size()-1);
        for (int i = 0; i < items.size(); i++) {
          Shape s = items.get(i);  
          g2d.draw3DRect(s.x, s.y, 1, 1, true);
        }
        lock.unlock();
        // Draw a big rectangle indicating the shot fired has hit 1 of the target.
        if(collision){
          g2d.draw3DRect(fired.x-25, fired.y-25, 50, 50, true);
          lock.lock();
        }


        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    // Method will run to move the shapes (target) by adding or subtracting the x values
    // move shot by subtracting its y value
    // lock to ensure that during the process of adding and subtracting, no new shape can be added.
    public void run() {
      while(true){
        lock = LockFactory.getLock("myLock");
        try {
          if(!first){
            lock.lock();
            for (int i = 0; i < items.size()-1; i++) {
              items.get(i).move();     
              if(items.get(i).x == items.get(items.size()-1).x && items.get(i).y == items.get(items.size()-1).y){
                collision = true;
              }
            }
            items.get(items.size()-1).moveUp();
            if(items.get(items.size()-1).y < 0){
              items.remove(items.size()-1);
              first = true;
              
            }
            lock.unlock();
            repaint();
            
          } else{
            for(Shape s : items){
              s.move();
            }
            repaint();
            
          }
          Thread.sleep(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }


}