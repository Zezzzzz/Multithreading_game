package aa.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


// Window class to provide the frame for the application
public class Window extends JFrame implements ActionListener{
  private static Board b;
  private static int clicks = 0;
  private static int timeTaken;
  private static ArrayList<Shape> arr;
  private static MyThread t;
  private static Lock lock = LockFactory.getLock("myLock");
  private static JButton fire, close;
  private static SoundThread soundThread;
  
  // Constructor
  public Window() {
    b = new Board();
    //t = new Thread();
    add(b);
    invalidate();
    validate();
    setTitle("AA Project");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(500, 500);
    setLocationRelativeTo(null);
    setVisible(true);
    setResizable(false);
    fire = new JButton("FIRE");
    close = new JButton("Close");
    fire.addActionListener(this);
    close.addActionListener(this);
    add(fire , BorderLayout.SOUTH);
    add(close , BorderLayout.NORTH);
    }
    
    // Main Method
    public static void main(String[] args) {
      new Window();
      soundThread = new SoundThread(true);
      soundThread.start();
      arr = b.getItems();
      Shape shape = arr.get(arr.size()-1);
      t = new MyThread(b, shape);
      t.start();
      
    }
    
    // Action listener for the 2 buttons
    public void actionPerformed(ActionEvent e){
      JButton button = (JButton) e.getSource();
      // When fire button is clicked, the total number of clicks is added
      // Ensure that shot fired is added to the list of items before painting from the Board thread
      if (button == fire) {
        if(b.getItems().size() <= 40){
          clicks++;
          lock.lock();
          b.addShape();
          lock.unlock();

        }
      } else{
        // When close button is clicked, it will check whether the game has ended
        // if it has ended, the summary
        if(lock.tryLock()){
          lock.unlock();
        }
        if(b.getCollisionStatus()){
          System.out.println();          
          System.out.println("Congratulations");
          System.out.println("Summary of round: ");
          System.out.println("Time Taken: " + t.getTimeTaken() + "s");
          System.out.println("Shots Fired: " + clicks);
        }
        System.exit(0);
      }
    }
}

// MyCallable 
// This thread will be mainly for counting the time taken for the user to finish 1 round
class MyThread extends Thread{
  private int timeTaken = 0;
  private Shape shape;
  private Lock lock = LockFactory.getLock("myLock");
  private Board board;
  private boolean collision;
  
  public MyThread(Board board, Shape shape){
    this.shape = shape;
    this.board = board;
  }
  
  public int getTimeTaken(){
      return timeTaken;    
  }
  
  public void closeTimeTaken(){
    this.timeTaken = 0;    
  }
  
  // The run method will count how many seconds the user take
  @Override
  public void run(){
    while(!collision){
      try{
        timeTaken++;
        lock.lock();
        collision = board.getCollisionStatus();
        lock.unlock();
        Thread.sleep(1000);       // may throw InterruptedException
      } catch(InterruptedException ex){
        ex.printStackTrace();
      }
    }
  }
}