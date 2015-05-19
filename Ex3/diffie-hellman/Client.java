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

import java.security.*; /* NoSuchAlgorithmException; InvalidKeyException; KeyPairGenerator; KeyPair; InvalidAlgorithmParameterException; AlgorithmParameters; AlgorithmParameterGenerator; Message Digest*/
import java.security.spec.*; /*InvalidParameterSpecException; InvalidKeySpecException*/

import javax.crypto.*; /* CipherInputStream; Cipher; CipherOutputStream; KeyGenerator; SecretKey */
import javax.crypto.spec.*; /* SecretKeySpec; IvParameterSpec;  DHParameterSpec*/



public class Client {

    public static void main(String args[])
    {
        
        /*Initialize Supported Ciphers*/
        SupportedCiphers supportedCiphers = new SupportedCiphers();
        supportedCiphers.initialize(supportedCiphers);

        Socket s;
        String mode = "";
        if (args.length > 0){
            mode = supportedCiphers.getCipher(args[0]);
            if(mode.length() < 1){
                System.out.println("Wrong cipher identifier!");
                System.out.println(supportedCiphers.getSupportedCiphers());
                mode = "RC4";
            }
        }
        else{
            mode = "RC4";
        }

        int test;
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
            * Computes DH key Agreement and sends public key to client
            **/ 
            Key_Agreement_DH dh_agreement= new Key_Agreement_DH();
            dh_agreement.genParams("manual");
            byte[] pubSelf = dh_agreement.getPublicKey().getEncoded();
            out.writeInt(pubSelf.length);
            out.write(pubSelf);

            /**
            * Gets public key from server
            **/
            byte[] pubServer = new byte[in.readInt()];
            in.readFully(pubServer);



            /***
            *Reads and generates RSA key pair
            **/
            byte[] modulo = new byte[in.readInt()];
            in.readFully(modulo);
            BigInteger bigmod = new BigInteger(modulo);

            byte[] pubexp = new byte[in.readInt()];
            in.readFully(pubexp);
            BigInteger bigexp = new BigInteger(pubexp);

            byte[] pubserver = new byte[in.readInt()];
            in.readFully(pubserver);

            RSAPrivateKeySpec rsaPrivateKey = new RSAPrivateKeySpec(bigmod,bigexp);
            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(bigmod,bigexp);
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(rsaPubKey);


            out.writeInt(pubKey.getEncoded().length);
            out.write(pubKey.getEncoded());



            /**
            *
            *ka.generateSecret() generates the shared secret between two parties
            */
            byte[] sessionKeyBytes = dh_agreement.keyAgreement(pubServer);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sessionKeyBytes=sha.digest(sessionKeyBytes);
            sessionKeyBytes=Arrays.copyOf(sessionKeyBytes,16);

            /**
            * Creates a new cipher from key keyfile
            * Getting IV from Server
            **/
            byte[] iv = new byte[16];
            in.readFully(iv);

            Encrypt enc = new Encrypt();
            Cipher cipher = enc.encrypt(mode,sessionKeyBytes,iv);

            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 

            System.out.println("Connected to server");
            System.out.println("Mode: "+mode);                
            System.out.println("You can start typing now!\n");
                
            while((test=System.in.read())!=-1){
                cos.write((byte)test);
                cos.flush();
            }
            in.close();
            out.close();
            s.close();             
        }catch(Exception e){
            System.out.println("*** Failed to connect to server ***");
        }
    }
}


