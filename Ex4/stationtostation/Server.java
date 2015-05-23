package stationtostation;
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

import com.sun.crypto.provider.SunJCE;


import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;


import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;


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
    public static PublicKey decodeX509(byte[] keyBytes){
        try{
        KeyFactory kf = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytes);
        return kf.generatePublic(x509Spec);
    } 
    catch (Exception e) {System.out.println(e);return null;}

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
            
            DiffieHellman dh= new DiffieHellman();

            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());
            //ObjectInputStream obin = new ObjectInputStream(client.getInputStream());
            //ObjectOutputStream obout = new ObjectOutputStream(client.getOutputStream());
            
            
            /**
            * Gets public key from client
            **/
            System.out.println("Getting public key from client");

            byte[] pubClient = new byte[in.readInt()];
            in.readFully(pubClient);

            //decodes clients key
            PublicKey clientPubKey = decodeX509(pubClient);

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
            PrivateKey myPrivateKey=dh.getPrivateKey();

            byte[] sharedSecret=dh.keyAgreement(myPrivateKey,clientPubKey);


//-----------------------------------------------------------


            
             /*
         * Client encrypts, using DES in ECB mode
         */
            SecretKey secrKey = dh.sharedSecretKey(clientPubKey,"DES");
            
            System.out.println("Cipher initiated");


            //--------Signature----------------
             //--------STS----------------
            //receives from client
         byte[] rsaMod = new byte[in.readInt()];
         in.readFully(rsaMod);
         //receives from server
         byte[] rsaPubClient = new byte[in.readInt()];
         in.readFully(rsaPubClient);
            
        //Generates RSA key pair
        KeyPairGenerator rsaKeyPairGen = KeyPairGenerator.getInstance("RSA");
        rsaKeyPairGen.initialize(1024);
        KeyPair rsaKeyPair = rsaKeyPairGen.generateKeyPair();
        RSAPrivateCrtKey rsapriv = (RSAPrivateCrtKey)rsaKeyPair.getPrivate();
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsapriv.getModulus(), rsapriv.getPublicExponent());
        KeyFactory kfact = KeyFactory.getInstance("RSA");
        PublicKey rsaPubKey = kfact.generatePublic(publicKeySpec);
        byte[] rsaPub = rsaPubKey.getEncoded();

        //Sends pubkey to client

        out.writeInt(rsaPub.length);
        out.write(rsaPub);




        StationtoStation sts = new StationtoStation();
        SealedObject ciphsign = sts.sign(rsaKeyPair.getPrivate(),rsaPub,rsaPubClient,secrKey);

        byte[] sigClient = new byte[in.readInt()];
        in.readFully(sigClient);
        //SealedObject sigClient= (SealedObject) obin.readObject();



        System.out.println("Got SigClient ");




        //enviar ao outro o sealed object
        //iniciar uma cifra para decifrar a chave
        // verificar a assinatura

        //--------Signature----------------

        //enviar ao outro o sealed object
        //iniciar uma cifra para decifrar a chave
        // verificar a assinatura


        //------------------Sig
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secrKey);


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
