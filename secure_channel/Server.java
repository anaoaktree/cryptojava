package secure_channel;

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

            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());
                
            /**
            * Computes DH key Agreement and sends public key to client
            **/ 
            Key_Agreement_DH dh_agreement= new Key_Agreement_DH();
            dh_agreement.genParams("manual");
            byte[] pubSelf = dh_agreement.getPublicKey().getEncoded();
            out.writeInt(pubSelf.length);
            out.write(pubSelf);


            /**
            * Gets public key from client
            **/
            byte[] pubClient = new byte[in.readInt()];
            in.readFully(pubClient);

            /**
            * Station to station dig signature
            */
            StationtoStation digsig= new StationtoStation();
            byte[] sig = digsig.sign(dh_agreement.getPrivateKey(), pubSelf, pubClient);
            out.writeInt(sig.length);
            out.write(sig);

    		/**
    		*
    		*ka.generateSecret() generates the shared secret between two parties
    		*/
    		byte[] sessionKeyBytes = dh_agreement.keyAgreement(pubClient);

    		/**
    		* Decipher mode with agreed key from diffie hellman
    		*
    		**/
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sessionKeyBytes=sha.digest(sessionKeyBytes);
            sessionKeyBytes=Arrays.copyOf(sessionKeyBytes,16);

            /**
            * Sends generated IV to client
            *
            **/
            byte[] iv = new byte[16];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);
            out.write(iv);


            Decrypt dec = new Decrypt();
            Cipher ciph=dec.decrypt(cipherMode, sessionKeyBytes, iv);
            CipherInputStream cis = new CipherInputStream(this.client.getInputStream(),ciph);
            int test;
            int inicio_mensagem = 1;

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
