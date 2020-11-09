import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
/**
   * This is GameServer where the server keeps track of user's actions and messages
   * and the balances of each users
   * Final Project - Off To The Races! ISTE121-03
   * @author Makiah H, Mykhaylo B, Nuzhat M, Maxy T
   * @version 3.0 3/22/2020 - 4/30/2020
*/
public class GameServer{
   
   //instantiation of vector arraylists suitable for threads
   ArrayList<String> joined = new ArrayList();
   Vector<String> users = new Vector<String>();
   Vector<ThreadServer> clients = new Vector<ThreadServer>();
  
/**
   * beginServer() mutator
   * runs the server by accepting any client joining the specific server socket
   and also adds user to the clients vector ArrayList
*/
   public void beginServer() throws Exception  {
   
      ServerSocket server = new ServerSocket(16789);
      System.out.println("getLocalHost: "+InetAddress.getLocalHost() );              
      System.out.println("getByName:    "+InetAddress.getByName("localhost") ); 
      System.out.println("Server Started...");
      
      while(true) {//runs continuously
         try {
            Socket client = server.accept();//every time user joins chat
            ThreadServer c = new ThreadServer(client);
            clients.add(c);//user is added to Vector list
         //usersList();
         }catch(Exception e) {
            e.printStackTrace();
         }
      
      } // end while
   }
   public static void main (String[]args) throws Exception {
      //starts up the ChatServer
      new GameServer().beginServer();
   }//main

/**
   * broadcast() mutator
   * gets the user's message broadcasted to every client's chat
   * @param String user, String message
*/
   public void broadcast(String user, String message){
       //sends message to all connected users
      for(ThreadServer c : clients) //all users that are in ThreadServer class
         //checks if clients' username is not equal to mentioned user
         if(!c.getUserName().equals(user)){
            //user that sent message will be broadcasted
            c.sendMessage(user,message);
         }
   }//broadcast

/**
   * ThreadServer() class
   * reads and writes clients' messages that are sent to the ChatServer
   * extends Thread class
*/
   class ThreadServer extends Thread{
   
      //attributes
      String name = "";
      BufferedReader br;
      PrintWriter pw;
      
   /**
   * ThreadServer() constructor
   * takes in the client's socket and allow to send messages to others
   * @param Socket client
   */   
      public ThreadServer(Socket  client) throws Exception {
         //instantiation of bufferreader and printwriter
         br = new BufferedReader(
               new InputStreamReader(
                  client.getInputStream()));
         pw = new PrintWriter(client.getOutputStream(),true);
         
         //reads user's name
         name  = br.readLine();
         //start-up message only for the client once they join,
         //not broadcast to every client
         pw.println("Welcome " + name + ", you joined the chat.");
         pw.println("Type 'end' to terminate the program");
         users.add(name);//name added to the users vector arraylist
         joined.add(name);
         
         for(String joiner : joined) {
            pw.println(joiner + " joined the chat");
         }
         start();//begins the program of reading and writing lines
      }//ThreadServer
   /**   
   * sendMessage() mutator
   * sends user's message using printwriter
   * @param String clientName, String msg
   */  
      public void sendMessage(String clientName, String msg){
         pw.println( clientName + " " + msg);
      }
   /**   
   * getUserName() accessor - calls for name of client
   * @return name;
   */ 		
      public String getUserName(){  
         return name; 
      }
   /**
   * run() mutator
   * begins reading each line and if the line the client types
   starts with word 'end' the server will know they are being terminated.
   */ 
      public void run(){
         String line;
       
         try{
            broadcast(name, " joined the chat");
            while(true){
               line = br.readLine();
            
               if (line.equals("end")) {
                  //pw.println(name + " left the chat.");
                  broadcast(name , " left the chat."); // broadcasts the left chat message to all clients
                  //removed user from both vector arraylists
                  clients.remove(this);
                  users.remove(name);
                  joined.remove(name);
                  break;
               }else if(line.equals("play")) {
                  int a = JOptionPane.showConfirmDialog(null, "Ready to play?");
   
                  if(a== JOptionPane.YES_OPTION) { //if YES choice is picked
                     int players = 0; //starts at zero
                     //players are users
                     for(String player: users) {
                        players++;
                        if(players < 2) {
                           pw.println("Not enough players");
                           //System.exit(0); need a statement that allows to close and go back to chat
                        } else if(players > 5) {
                           pw.println("Game is full. Try to join later");
                           System.exit(0); // kick people out to join later once game concludes give thanks for playing msg to allow others to join and play they should still be able to chat!
                        
                        } // end if
                     } // end for each
                     
                     // ASK WHICH HORSE THEY WANT TO BET ON
                     int horseGuess = Integer.parseInt(JOptionPane.showInputDialog(null,"Which horse do you want to bet on?"));
                     while (horseGuess > players) { // while the guess is greater than the number of players
                        pw.println("Invalid guess. Must guess between 0 and " + players);
                        horseGuess = Integer.parseInt(JOptionPane.showInputDialog(null,"Which horse do you want to bet on?"));
                     
                     } 
                     pw.println(horseGuess); 
                     
                     int total = 100; //beginning balance when game starts
                     int bet = Integer.parseInt(JOptionPane.showInputDialog(null,"How much do you want to bet?"));
                     while(bet < 1 || bet > total) { //while the bet amount is less than one or greater than their total
                        pw.println("Invalid bet. You must bet between $1 and $" + total);
                        bet = Integer.parseInt(JOptionPane.showInputDialog(null,"How much do you want to bet?"));
                     
                     }
                     //instantiation of Races2 class
                     Races2 race = new Races2(players); // should set number of racers to number of users who join with the conditions of the for each and if above
                     
                     int winner = race.getWinner();
                     int newTotal = 0;
                  
                     if(winner == horseGuess) {
                        int win = bet + bet; //bets added
                        newTotal = total + win;
                        pw.println("You won " + win + "Total: " + newTotal + ". You bet " + bet);                     

                     } else if(winner != horseGuess) { //if guess is wrong, loses bet
                        int loss = total - bet;
                        newTotal = total;
                        pw.println("You lost. Total: " + loss + ". You bet " + bet);                     
                     }
                  }// end if yes option
               }//end if
               //continues to send message to every client
               broadcast(name,line);
            }//while
         }catch(Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
         }
      } //run()
   }//inner class
}//GameServer