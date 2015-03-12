/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        SupportedCiphers supportedCiphers = new SupportedCiphers();
        supportedCiphers.add("rc4", "RC4");
        supportedCiphers.add("cbc","AES/CBC/NoPadding");
        supportedCiphers.add("cfb8","AES/CFB8/NoPadding");
        supportedCiphers.add("cfb","AES/CFB/NoPadding");
        supportedCiphers.add("cbc_pdd","AES/CBC/PKCS5Padding");
        supportedCiphers.add("cfb8_pdd","AES/CFB8/PKCS5Padding");

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
            **/
            Encrypt enc = new Encrypt();
            Cipher cipher = enc.encrypt(mode,sessionKeyBytes);


            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 

            System.out.println("Connected to server");
            System.out.println("Mode: "+mode);                
            
             
            while((test=System.in.read())!=-1)
            {
                cos.write((byte)test);
                cos.flush();
            }
            in.close();
            out.close();
            s.close();
                       
        }
        catch(Exception e)
        {
            System.out.println("*** Failed to connect to server ***");
        }
    }
}
   
class Encrypt{
    public Cipher encrypt(String mode,byte [] keyfile){
        try{
            System.out.println("enc "+mode);
            Cipher cipher = Cipher.getInstance(mode);
            //IV
            byte[] iv = new byte[16];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);

            
            SecretKey key;
            if(mode.startsWith("AES")){
                key = new SecretKeySpec(keyfile,"AES");    
                cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(iv));
            }
            else{
                key = new SecretKeySpec(keyfile,"RC4");
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            return cipher;
        }
        catch (Exception e) {System.out.println(e);}
        return null;
    }
}


class SupportedCiphers{
    private HashMap<String, String> ciphers;
    
    public SupportedCiphers(){
        this.ciphers = new HashMap<String, String>();
    }

    public void add(String cod, String cipher){
        this.ciphers.put(cod, cipher);
    }

    public String getCipher(String cod){
        if (this.ciphers.containsKey(cod)){
            return this.ciphers.get(cod);
        }
        return "";
    }
    public String getSupportedCiphers(){
        String res = "";
        for(String s : this.ciphers.keySet()){
            res = res + "Identifier "+s+"  Cipher: "+this.ciphers.get(s)+"\n";
        }
        return res;
    }
}
