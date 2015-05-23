package diffieHellman;
/**
 *
 * @author Ana Paula Carvalho and Fábio Fernandes
 */

import java.io.*; /* Buffered Reader; IOException; InputStreamReader */
import java.net.*; /* Socket; */

import java.util.*; /* Scanner; */
import java.math.*; /* BigInteger; */

import java.nio.file.*; /* Files; */
import java.nio.*; /* Charset */

import java.security.*; /* NoSuchAlgorithmException; InvalidKeyException; KeyPairGenerator; KeyPair; InvalidAlgorithmParameterException; AlgorithmParameters; AlgorithmParameterGenerator; */
import java.security.spec.*; /*InvalidParameterSpecException; InvalidKeySpecException*/

import javax.crypto.*; /* CipherInputStream; Cipher; CipherOutputStream; KeyGenerator; SecretKey */
import javax.crypto.spec.*; /* SecretKeySpec; IvParameterSpec;  DHParameterSpec*/
import javax.crypto.interfaces.*;

import diffieHellman.*;

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
	   int client_id = 1;

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
        Boolean firstTime=true;
    	String cipherMode="";
        try {
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));

            /**
            * Reads cipher mode from client
            **/
            cipherMode = reader.readLine();  
            System.out.println("["+id+"]: Client connected with cipher "+cipherMode);       
            
            Utilities ut=new Utilities();
            DiffieHellman dh= new DiffieHellman();

            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());
            
            /**
            * Gets public key from client
            **/
            System.out.println("Getting public key from client");

            byte[] pubClient = new byte[in.readInt()];
            in.readFully(pubClient);

            //decodes clients key
            PublicKey clientPubKey = ut.decodeX509(pubClient);

            //creates his own dh pair with the same parameters as client
            dh.genParamsSpec(((DHPublicKey)clientPubKey).getParams());

            //Encodes public key and sends it to client
            System.out.println("Sending public key to client");

            byte[] pubSelf = dh.getPublicKey().getEncoded();
            out.writeInt(pubSelf.length);
            out.write(pubSelf);

            /**
            * Proceeds with the key agreement: initializes with its private key
            *  adds the public key from the server and then generates the secret, which is returned.
            */
            System.out.println("Computing shared secret");

            byte[] sharedSecret=dh.keyAgreement(dh.getPrivateKey(),clientPubKey);


//-----------------------------------------------------------


            
             /*
         * Client encrypts, using DES in ECB mode
         */
            SecretKey secrKey = dh.sharedSecretKey(clientPubKey,"DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secrKey);
            System.out.println("Cipher initiated");


            CipherInputStream cis = new CipherInputStream(this.client.getInputStream(),cipher);
            int test;
            int inicio_mensagem = 1;
            System.out.println(cis.read());

            while((test=cis.read())!=-1){

               if(inicio_mensagem == 1){
                         System.out.print("["+id+"]: ");
                         inicio_mensagem = 0;
                    }
                    
                    System.out.print((char) test);

                    if((char) test == '\n'){
                        inicio_mensagem = 1;
                    } 
            }
            System.out.println("["+id+"]: "+"Client disconnected");
    	} catch (Exception e) {System.out.println(e);}
    }
}
