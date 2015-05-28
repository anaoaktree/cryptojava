package mac;

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

import java.security.*; /* NoSuchAlgorithmException; InvalidKeyException; KeyPairGenerator; KeyPair; InvalidAlgorithmParameterException; AlgorithmParameters; AlgorithmParameterGenerator; Message Digest*/
import java.security.spec.*; /*InvalidParameterSpecException; InvalidKeySpecException*/

import javax.crypto.*; /* CipherInputStream; Cipher; CipherOutputStream; KeyGenerator; SecretKey */
import javax.crypto.spec.*; /* SecretKeySpec; IvParameterSpec;  DHParameterSpec*/
import javax.crypto.interfaces.*;
//import mac.*;
import com.sun.crypto.provider.SunJCE;

public class Client {

    public static void main(String args[])
    {
        Encryption encryption = new Encryption();
        SupportedCiphers sup = new SupportedCiphers();
        
        /*Initialize Supported Ciphers*/
        Socket s;
        String mode = "";
        if (args.length > 0){
            mode = sup.getCipher(args[0]);
            if(mode.length() < 1){
                System.out.println("Wrong cipher identifier!");
                System.out.println(sup.getSupportedCiphers());
                mode = "RC4";
            }
        }
        else{
            mode = "RC4";
        }

        try{
            s = new Socket("localhost", 4567);
            PrintWriter writer = new PrintWriter(s.getOutputStream());
            
            /**
            * Sends cipher mode to server
            */
            writer.println(mode);
            writer.flush();

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            
            /**
            * Computes DH key Agreement and generates DH parameters.
            * Also, it creates a key pair based on the parameters
            **/ 
            Utilities ut=new Utilities();
            System.out.println("Computing DH parameters...");

            DiffieHellman dh= new DiffieHellman();
            dh.genParamsFull("manual"); //for auto, we needed a central authority to share it also with server
            
            //Gets the public key generated earlier
            byte[] pubSelf = dh.getPublicKey().getEncoded();

            //Sends public key to Server
            System.out.println("Sending public key to server");

            out.writeInt(pubSelf.length);
            out.write(pubSelf);
            
            /**
            * Gets public key from server
            **/
            System.out.println("Getting public key from server");

            byte[] pubServer = new byte[in.readInt()];
            in.readFully(pubServer);

            /**
            * Proceeds with the key agreement: initializes with its private key
            *  adds the public key from the server and then generates the secret, which is returned.
            */
            System.out.println("Computing shared secret");
            PublicKey serverPubKey= ut.decodeX509(pubServer);
            byte[] sharedSecret=dh.keyAgreement(dh.getPrivateKey(),serverPubKey);

//--------------------------------------------------------------------
            //Return the shared secret as the secret key, specifying the algorithm.
         
            byte[] secrKey= Arrays.copyOfRange(sharedSecret, 0, 16);
            byte[] macKey = Arrays.copyOfRange(sharedSecret, 16, sharedSecret.length);

         

            //------------------MAC
            Mac mac = encryption.digest(macKey,mode);


           //------------------MAC */

            Cipher cipher = encryption.encrypt(mode,secrKey);


            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 
            CipherInputStream cis = new CipherInputStream(s.getInputStream(), cipher);


            System.out.println("Connected to server");
            //System.out.println("Mode: "+mode);                
            System.out.println("You can start typing now!\n");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String test;
            while((test=br.readLine())!=null){
                cos.write(test.length());
                cos.write(test.getBytes());
                

                byte[] msgDigest = mac.doFinal(test.getBytes());
                cos.write(msgDigest.length);
                cos.write(msgDigest);

            }
            in.close();
            out.close();
            s.close();             
        }catch(Exception e){
            System.out.println("*** Failed to connect to server ***");
        }

        
    }
}


