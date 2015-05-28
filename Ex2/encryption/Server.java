/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.lang.String;


import javax.crypto.CipherInputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.io.InputStream;

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
        Encryption encryption = new Encryption();
       
	try {
		  DataOutputStream out = new DataOutputStream(client.getOutputStream());
         DataInputStream in = new DataInputStream(client.getInputStream());
            

	   // BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
	    String msg;
	    byte[] cipherMode = new byte[in.readInt()];
        in.readFully(cipherMode);

		String ciphMode= new String(cipherMode);


		InputStream is = this.client.getInputStream();
        System.out.println("["+id+"]: Client connected with cipher "+ciphMode);
		byte[] iv= Files.readAllBytes(Paths.get("./iv"));

        Cipher ciph=encryption.decrypt(ciphMode,iv);
        CipherInputStream cis = new CipherInputStream(is,ciph);
        int test;

        
        while((test=cis.read())!=-1){
            System.out.print("["+id+"]:"); 
        	System.out.println((char) test);
        }
        System.out.println("["+id+"]: "+"Client disconnected");
	} 
	catch (Exception e) {System.out.println(e);}
   }
}
