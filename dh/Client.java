package dh;

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


public class Client {
    
    public static void main(String args[]){
        /*Initialize Supported Ciphers*/
        SupportedCiphers supportedCiphers = new SupportedCiphers();
        supportedCiphers.initialize(supportedCiphers);

        /*
            Cipher mode Choosing
            Default mode: RC4
        */

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
            Socket socketClient = new Socket("localhost", 4567);
            System.out.println("Cipher Mode: "+mode);
            System.out.println("Sending cipher mode to server");

            Thread read = new Thread(new ClientReaderDHParams(socketClient, mode));
            read.start();
            read.join();
            //writer.close();
            System.out.println("END");
        }
        catch(Exception e)
        {
            System.out.println("*** Failed to connect to server ***");
        }
    }
}


class ClientReaderDHParams implements Runnable {
    
    private Socket server;
    private String mode;

    public ClientReaderDHParams (Socket server, String mode) {
        this.server = server;
        this.mode = mode;
    }

    public void run() {
        try {
            PrintWriter writer = new PrintWriter(server.getOutputStream());
            writer.println(mode);

            writer.flush();
            //writer.close();
            System.out.println("Start reading parameters");

            DataInputStream in = new DataInputStream(server.getInputStream());
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            
            // out.writeUTF(mode);

            byte[] keyBytesP  = new byte[in.readInt()];
            in.readFully(keyBytesP);
            BigInteger p = new BigInteger(keyBytesP);
            //System.out.println("Big P "+p);
            
            byte[] keyBytesG  = new byte[in.readInt()];
            in.readFully(keyBytesG);
            BigInteger g = new BigInteger(keyBytesG);
            //System.out.println("Big G "+g);

            int l = in.readInt();
            //System.out.println("Size l "+l);

            System.out.println("Receiving server's public key");

            byte[] keyBytesPK = new byte[in.readInt()];
            in.readFully(keyBytesPK);

            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytesPK);
            PublicKey serverPublicKey = kf.generatePublic(x509Spec);

           
            DHParameterSpec spec = new DHParameterSpec(p,g,l);
            KeyPairGenerator keyPairGen=KeyPairGenerator.getInstance("DH");
            keyPairGen.initialize(spec);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PublicKey publickey = keyPair.getPublic();

            System.out.println("Sending my public key");
            byte[] keyBytesCPK = publickey.getEncoded();
            out.writeInt(keyBytesCPK.length);
            out.write(keyBytesCPK);

            // Perform the KeyAgreement
            System.out.println("Performing the KeyAgreement");
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(keyPair.getPrivate());
            ka.doPhase(serverPublicKey,true);

            //RECEIVING IV
            System.out.println("Receiving IV");
            byte[] iv = new byte[8];
            in.readFully(iv);


            // 
            byte[] sessionKeyBytes = ka.generateSecret();

            // Create the session key
            //SecretKeyFactory skf = SecretKeyFactory.getInstance("TripleDES");
            //DESedeKeySpec tripleDesSpec = new DESedeKeySpec(sessionKeyBytes);
            //SecretKey sessionKey = skf.generateSecret(tripleDesSpec);

            // Create the CipherStream to be used
            System.out.println("Creating the CipherStream");
            //Cipher cipher = Cipher.getInstance("TripleDES/CFB8/NoPadding");

            //IvParameterSpec ivSpec = new IvParameterSpec(iv);
            //cipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivSpec);            
            Cipher cipher = Cipher.getInstance(mode);

            SecretKey sessionKey;
            if(mode.startsWith("AES")){
                sessionKey = new SecretKeySpec(sessionKeyBytes,"AES");    
                cipher.init(Cipher.ENCRYPT_MODE, sessionKey,new IvParameterSpec(iv));
            }
            else{
                sessionKey = new SecretKeySpec(sessionKeyBytes,"RC4");
                cipher.init(Cipher.ENCRYPT_MODE, sessionKey);
            }

            
            CipherOutputStream cipherOut = new CipherOutputStream(server.getOutputStream(), cipher);
            
            int test;
            while((test=System.in.read())!=-1){
                cipherOut.write((byte)test);
                cipherOut.flush();
            }
            writer.close();
            out.close();
            in.close();
            cipherOut.close();


        } catch (Exception e) {}
    }
}

class ClientWriter implements Runnable {
    private Socket server;
    private String cipher_mode;

    public ClientWriter (Socket server, String cipher_mode) {
        this.server = server;
        this.cipher_mode = cipher_mode;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(this.server.getOutputStream(), true);
            Scanner input = new Scanner(System.in);
            String msg = "";
            out.println("Ligacao efectuada com sucesso ao cliente!");
            out.println(this.cipher_mode);

            while (!msg.equals("exit")) {
                msg = input.next();
                if (!msg.equals("exit")) {
                    if (this.server.isClosed()) {
                        System.out.println("Nao pode enviar mensagens porque o servidor terminou a ligacao!");
                    }
                    else{
                        out.println(msg);
                    }
                }
                else {
                    out.println("Ligacao terminada pelo cliente!");
                }
            }
            out.close();
        } catch (IOException e) {}
    }
}

























