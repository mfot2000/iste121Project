import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
   * This is Races2 where the game for racing horses to get to finishing line
   * and the balances of each users
   * Final Project - Off To The Races! ISTE121-03
   * @author Makiah H, Mykhaylo B, Nuzhat M, Maxy T
   * @version 3.0 3/22/2020 - 4/30/2020
*/
class Races2 extends JFrame{
   //Attributes
   private final static int DEFWINNER = -1;     // default 
   private final static int WAIT_TIME= 1000;   // one second = 1,000 ms
   private final static int DEFAULT_RACERS = 5;     // default number of racers
   private final static String ICON_IMAGE="embars.gif";
   
   private int iconWidth;
   private int iconHeight;
   private static int winner = DEFWINNER;
   private static String winnerName = "X";
   private int finishLineX;
/**
   * Races2 Constructor
   * @param int racers (number of racers)
*/
   public Races2(int racers){
      super("Off to the Races");
      int   FRAMEFACTOR = 20;
      int   frameW;
      int   frameH;
   
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      Container cp = getContentPane();
      cp.setLayout(new GridLayout(racers,1));
      Racer  [] racer = new Racer[racers];
      Thread [] racerT= new Thread[racers];
   
      Icon xPic = new ImageIcon(ICON_IMAGE);
      iconWidth = xPic.getIconWidth();
      iconHeight = xPic.getIconHeight();
      frameW = iconWidth*FRAMEFACTOR;
      frameH = iconHeight * 2 * racers;
      setSize(frameW, frameH );
      finishLineX = iconWidth*18;
      
      //adds racers
      for(int i=0; i< racers; i++){
         racer[i] = new Racer(i);
         cp.add(racer[i]);
         racerT[i] = new Thread( racer[i] );
      }
      
      setVisible(true);
      setLocationRelativeTo(null);
      
      try{ Thread.sleep( WAIT_TIME ); } //waiting time
      catch(Exception e){System.out.println("Sleep error "+e);}
      
      System.out.println("Starting race...");
      // to be different, start in reverse order
      for(int i=racers-1;i>=0;i--){
         racerT[i].start();
      }
   }  // end class Races2
/**
   * getWinner() accessor
   * @return winner
*/
   public int getWinner() {
      return winner;
   }
 /** 
      * Racer creates the race lane (JPanel) and the ability to 
      * keep itself going (Runnable)
   */
   class Racer extends JPanel implements Runnable{
   
      private int racePos;
      private Icon aPic;
      private int myName;
      private int iconH;
      private int iconW;
      int rightSide; //finish line
   
/**
   * Racers Constructor - creates icon and given name
   * @param int name (thread name)
*/
      public Racer(int name){
         System.out.println("Created racer #"+name);
         aPic = new ImageIcon(ICON_IMAGE);
         iconH = aPic.getIconHeight();
         iconW = aPic.getIconWidth();
         rightSide = getWidth() - iconW * 2;
         racePos = 0;   // starting X position
         myName = name;
         setName( ""+ myName );
      }

      /** 
         * paint() mutator
         * paints the icon
      */
      public void paint(Graphics g){
         super.paint(g);
         aPic.paintIcon( this, g, racePos, 0);
         g.drawLine(finishLineX, 0, finishLineX, aPic.getIconHeight()*2 );
         if( winner == myName){
            g.drawString("Winner is #"+myName,10,10);
         }
      }//end paint
      /** 
         * run() method keeps the thread (racer) alive and moving.  run() executes the repaint() 
         * to have the JPanel be refreshed.
      */
      public void run(){
         int rightSide = getWidth() - aPic.getIconWidth()*2;
         System.out.println("Running "+myName+" Right side is "+rightSide);
         
         while( racePos < rightSide && winner == DEFWINNER){
            racePos += (int)(Math.random()*iconW);
         //            System.out.println("Run racer #"+myName+"  Race position = "+ racePos );
            this.repaint();
            try{  Thread.sleep((int)(Math.random()*500+1)); }
            catch(Exception e){
               System.out.println("Interrupted, Looks like #"+myName+" lost.");
            }
         } // end while()
      
         System.out.println("Position of "+myName+" is "+racePos);
         synchronized(winnerName)
         {
            if (winner == DEFWINNER ){
               winner = myName;
               this.repaint();
               System.out.println("Thread #"+myName+" won!");
            }
         } // end synchronized
      }  // end run()
   }  // end inner class Racer
} // end class Racers2