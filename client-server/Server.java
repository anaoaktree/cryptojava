/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ssi_aula1_aula2;
import java.io.*;
import java.net.*;

/**
 *
 * @author User
 */
public class Server {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */

    public static void main(String[] args) throws IOException {
	   ServerSocket ss = new ServerSocket(4567);
	   int client_id = 0;

	   while (true) {
	      Socket cs = ss.accept();
	     Thread t1 = new Thread(new ReadMessage(cs,client_id));
	     t1.start();
	     client_id++;
	   }
    }
}



class ReadMessage implements Runnable {
    
    private Socket client;
    private int id;

    
    public ReadMessage(Socket client,int id) {
	   this.client = client;
	   this.id=id;
    }

    public void run() {
	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
	    String msg;
            while ((msg = in.readLine()) != null){
                System.out.println("["+id+"]: "+msg);
                        
                /*
            TRATAR MENSAGEM
            */
            
            }
          System.out.println("["+id+"]: "+"Client disconnected");
            
	   
	} catch (IOException e) {}
    }
    
}
