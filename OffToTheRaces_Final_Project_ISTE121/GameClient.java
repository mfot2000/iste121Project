import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
   * This is GameClient where the GUI set up specifically for the client user
   * and be able to use to chat and play with players
   * Final Project - Off To The Races! ISTE121-03
   * @authors Makiah H, Mykhaylo B, Nuzhat M, Maxy T
   * @version 3.0 3/22/2020 - 4/30/2020
*/
public class GameClient extends JFrame implements ActionListener{

   //Name of client
   String clientName;
   
   //Input/Output Writer and Reader data
   PrintWriter pw;
   BufferedReader br;
   
   //JPanel components
   JTextArea  jtaMessages;
   JTextArea jtaJoined;
   JTextField jtfTypeMessage;
   
   //JButtons
   JButton jbSend;
   JButton jbExit;
   JButton jbPlay;
   
   //JMENUBAR
   JMenuBar jmb;
   JMenu jmbMenu;
   JMenuItem jmiAbout;
   JMenuItem jmiInstructions;
   
   //Socket
   Socket client;
   
/**
   * ChatClient Constructor - creates instantiation of objects such as
   getting the clientName, new BufferReader and PrintWriter, as well as buildGUI
   and ThreadMessage start-up programs
   * @param String clientName, String serverName
   * throws Exception
*/
   public GameClient(String clientName, String serverName) throws Exception {
        
      super(clientName);//uses the name for the title in GUI
      this.clientName = clientName;
      //instantiates new Socket for this client
      client  = new Socket(serverName,16789);
      
      //new BufferReader
      br = new BufferedReader(
            new InputStreamReader(
               client.getInputStream()));
      
      //new PrintWriter
      pw = new PrintWriter(
            client.getOutputStream(),true);
      pw.println(clientName);  // send name to server
      buildGUI(); //method to create GUI application
     
     //makes thread to "listen" to other clients' messages
      new ThreadMessage().start();
   }
/**
   * buildGUI() mutator
   * creates a GUI program for one client
*/
   public void buildGUI() {
   
     // ATTRIBUTES
      jtaMessages = new JTextArea();
      jtaJoined = new JTextArea(30,12);
      jtfTypeMessage  = new JTextField(30);
      JScrollPane jsp = new JScrollPane(jtaMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      add(jsp,"Center");
      
      //JMenu
      jmb = new JMenuBar();
      jmbMenu = new JMenu("Menu");
      jmiAbout = new JMenuItem("About");
      jmiInstructions = new JMenuItem("How To Play");
      jmbMenu.add(jmiAbout);
      jmbMenu.add(jmiInstructions);
      jmb.add(jmbMenu);
      setJMenuBar(jmb);
      
      jmiAbout.addActionListener(this);
      jmiInstructions.addActionListener(this);
      
      //ArrayList<String> clientsList = new ArrayList<String>();
      
   // add message area to the frame and sets editing to false
      add(jtaMessages);
      jtaMessages.setEditable(false);
      
   // JButtons functionality
      jbSend = new JButton("Send");
      jbExit = new JButton("Exit");
      jbPlay = new JButton("Play");
   
      jbSend.addActionListener(this);
      jbExit.addActionListener(this);
      jbPlay.addActionListener(this);
      
   // 1st JPanel holds the joined chat area    
      JPanel panel1 = new JPanel();
      panel1.add(jtaJoined);
      jtaJoined.setEditable(false);
      add(panel1, BorderLayout.EAST);
      
   // 1st JPanel - South - adding all components to the panel      
      JPanel jpanel2 = new JPanel( new FlowLayout());
      jpanel2.add(jtfTypeMessage);
      jpanel2.add(jbPlay);
      
      //JButtons added to panel1
      jpanel2.add(jbSend);
      jpanel2.add(jbExit);
      jpanel2.add(jbPlay);
   
      add(jpanel2, BorderLayout.SOUTH);
       
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //setSize(600,600);
      setVisible(true);
      pack();
      
   } // end buildGUI()
/**
   * actionPerformed mutator
   * adds functions for both buttons Play, Send, and Exit
   * @param ActionEvent ae
*/
   public void actionPerformed(ActionEvent ae){
   
      if (ae.getSource() == jbExit) {
         pw.println("end");  //send 'end' to server so that server knows about the termination
         jtaJoined.append(clientName + " left the chat.\n");
         System.exit(0); //exits the ChatClient program
         
      }//end jbExit
      else if(ae.getSource() == jbPlay) {
         // send play to the server to start game
         pw.println("play"); 
         
         try {
            String playMsg = br.readLine();
            System.out.println(playMsg);
            
            String playerAmount = br.readLine();
            JOptionPane.showMessageDialog(this,"Problem with number of players.",playerAmount,JOptionPane.WARNING_MESSAGE); 
            
            String horseGuessError = br.readLine();
            System.out.print(horseGuessError); 
         
            String betError = br.readLine();
            System.out.print(betError);
            
            String gameResult = br.readLine();
            JOptionPane.showMessageDialog(this, gameResult);
            
         }catch(Exception e) {   e.printStackTrace(); }
      }//end jbPlay
      else if(ae.getSource() == jbSend){
         try{
            pw.println(jtfTypeMessage.getText());//takes the text from JTextField portion
            //brings the current user's sent message to their JTextArea
            jtaMessages.append(clientName +": " + jtfTypeMessage.getText() + "\n");
            jtfTypeMessage.setText("");//resets text to empty so user can type something else
         }catch(Exception e) { e.printStackTrace(); }
      }//end jbSend
      
      else if(ae.getActionCommand().equals("About")){
         JOptionPane.showMessageDialog(this,"Hello, Welcome to Off to The Races.\nOff To the Races was developed by Makiah H, Mykhaylo B,\nNuzhat M, Maxy T for ISTE 121. ");
      }//end About
      
      else if(ae.getActionCommand().equals("How To Play")){
         JOptionPane.showMessageDialog(this,"Play against up to 5 friends by betting on horses.\nYou can win double your bet if you choose correctly.");
      }//end How To Play
   } // end action event
   
/**
   * Inner class for ThreadMessages
   * allows message's to be read and appended to the JTextArea
*/
   class ThreadMessage extends Thread{
      
      public void run() {
         String line;
         try {
            while(true) { //if the line is read
               line = br.readLine();
               jtaMessages.append(line + "\n");//adds line to textarea
            } // end of while
         }catch(Exception ex) {ex.printStackTrace();}
      }//run()
   }//end of ThreadMessage class
   
   public static void main(String[]args) {
      //asks user to enter the name they identify as
      String name = JOptionPane.showInputDialog(null,"Enter your name :", "Username",
                JOptionPane.PLAIN_MESSAGE);
                
      String serverName = "localhost";//sets serverName to localhost
      
      try{
         new GameClient(name,serverName);//instantiates new object of ChatClient
      } catch(Exception e) {e.printStackTrace();}
   } //main method
}//class GameClient