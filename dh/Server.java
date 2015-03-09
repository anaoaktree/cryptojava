package dh;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ssi_aula1_aula2;

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

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
       ServerSocket ss = new ServerSocket(4567);
       int client_id = 1;

        while (true) {
            Socket cs = ss.accept();
            Thread t1 = new Thread(new SendDHParams(cs,client_id));
            t1.start();
            try{
                //t1.join();
            }catch(Exception e){}
            client_id++;
       }
    }
}



class SendDHParams implements Runnable {
    
    private Socket client;
    private int id;
    
    public SendDHParams(Socket client,int id) throws NoSuchAlgorithmException {
        this.client = client;
        this.id=id;
    }
    
    public void run() {
        try {
            System.out.println("********** Client ["+this.id+"] has connected!! ");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            String ciphmode = reader.readLine();
            System.out.println("********** Client ["+this.id+"] wants to use cipher mode "+ciphmode);

            //Parameter Generator P,G,l
            AlgorithmParameterGenerator pgen = AlgorithmParameterGenerator.getInstance("DiffieHellman"); 
            pgen.init(1024);
            AlgorithmParameters params= pgen.generateParameters();

            DHParameterSpec dhspec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
            KeyPairGenerator keypairGen = KeyPairGenerator.getInstance("DH");
            keypairGen.initialize(dhspec);
            KeyPair keyPair= keypairGen.generateKeyPair();
            PublicKey publickey = keyPair.getPublic();

            BigInteger bigp = dhspec.getP();
            BigInteger bigg = dhspec.getG();
            int intl = dhspec.getL();
            
            System.out.println("********** Sending parameters to Client ["+this.id+"] ");

            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());
            //String ciphmode = in.readUTF();

            byte[] keyBytesP = bigp.toByteArray();
            out.writeInt(keyBytesP.length);
            out.write(keyBytesP);
            
            byte[] keyBytesG = bigg.toByteArray();
            out.writeInt(keyBytesG.length);
            out.write(keyBytesG);

            out.writeInt(intl);
            //System.out.println("Big P "+bigp);
            //System.out.println("Big G "+bigg);
            //System.out.println("Size l "+intl);
            
            System.out.println("********** parameters sent to Client ["+this.id+"] ");

            System.out.println("********** Sending server's public key to Client ["+this.id+"] ");
            byte[] keyBytesPK = publickey.getEncoded();
            out.writeInt(keyBytesPK.length);
            out.write(keyBytesPK);


            System.out.println("********** Receiving client's public key from Client ["+this.id+"] ");
            byte[] keyBytesCPK = new byte[in.readInt()];
            in.readFully(keyBytesCPK);

            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytesCPK);
            PublicKey clientPublicKey = kf.generatePublic(x509Spec);
            System.out.println("********** public key received from Client ["+this.id+"] ");


            // Perform the KeyAgreement
            System.out.println("********** Performing the KeyAgreement with Client ["+this.id+"] ");
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(keyPair.getPrivate());
            ka.doPhase(clientPublicKey,true);

            // Create and send the IVParameterSpec
            System.out.println("********** Sending IV to Client ["+this.id+"] ");
            byte[] iv = new byte[8];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);
            out.write(iv);

            //
            byte[] sessionKeyBytes = ka.generateSecret();

            // Create the session key
            System.out.println("********** Creating the Session Key with Client ["+this.id+"] ");
            //SecretKeyFactory skf = SecretKeyFactory.getInstance("TripleDES");
            //DESedeKeySpec tripleDesSpec = new DESedeKeySpec(sessionKeyBytes);
            //SecretKey sessionKey = skf.generateSecret(tripleDesSpec);

            // Create the CipherStream to be used
            System.out.println("********** Creating the CipherStream to Client ["+this.id+"]");

            //Cipher decrypter = Cipher.getInstance("TripleDES/CFB8/NoPadding");
            //IvParameterSpec spec = new IvParameterSpec(iv);
            //decrypter.init(Cipher.DECRYPT_MODE, sessionKey, spec);
            
            SecretKey sessionKey;
            Cipher decrypter = Cipher.getInstance(ciphmode);
            if(ciphmode.startsWith("AES")){
                sessionKey = new SecretKeySpec(sessionKeyBytes,"AES");
                decrypter.init(Cipher.DECRYPT_MODE,sessionKey,new IvParameterSpec(iv));
            }
            else{
                sessionKey = new SecretKeySpec(sessionKeyBytes,"RC4");
                decrypter.init(Cipher.DECRYPT_MODE,sessionKey);
            }

            CipherInputStream cipherIn = new CipherInputStream(client.getInputStream(), decrypter);

            int test;
            while((test=cipherIn.read())!=-1){
                if((char) test == '\n'){
                    System.out.print(" <-- ["+id+"]");
                } 
                System.out.print((char) test);
            }
            System.out.println("["+id+"]: "+"Client disconnected");

            cipherIn.close();
            //reader.close();
            writer.close();
            out.close();
            in.close();

        }catch (Exception e) {System.out.println(e);}

    }
}

























