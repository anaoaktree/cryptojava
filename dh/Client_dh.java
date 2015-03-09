package dh;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.io.*;
import java.math.BigInteger;
import javax.crypto.CipherInputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.*;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.security.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;


public class Client_dh {
    public static void main(String args[])
    {
        SupportedCiphers_ supportedCiphers = new SupportedCiphers_();
        supportedCiphers.add("rc4", "RC4");
        supportedCiphers.add("cbc","AES/CBC/NoPadding");
        supportedCiphers.add("cfb8","AES/CFB8/NoPadding");
        supportedCiphers.add("cfb","AES/CFB/NoPadding");

        supportedCiphers.add("cbc_pdd","AES/CBC/PKCS5Padding");
        supportedCiphers.add("cfb8_pdd","AES/CFB8/PKCS5Padding");

        Socket s;
        BufferedReader in = null;
        PrintWriter out;
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
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());
            Encrypt enc = new Encrypt();
            Cipher cipher = enc.encrypt(mode);
            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 
            
            System.out.println("Connected to server");
            System.out.println("Mode: "+mode);                
            
           

            //Receive DH Params from server!
            String server_msg = "";
            ArrayList<String> dh_params = new ArrayList<>();
            int count_params = 0;
            
            System.out.println(serverIn.readLine());
            while (( server_msg = serverIn.readLine()) != null) {
                dh_params.add(server_msg);
                count_params ++;
                if(count_params == 3){ break;}
            }

            System.out.println("xpto");
            BigInteger p = new BigInteger(dh_params.get(0).getBytes());
            BigInteger g = new BigInteger(dh_params.get(1).getBytes());
            int l = Integer.parseInt(dh_params.get(2));

            //generate KeyPair
            DHParameterSpec spec = new DHParameterSpec(p,g,l);
            KeyPairGenerator keypairgen=KeyPairGenerator.getInstance("DH");
            keypairgen.initialize(spec);
            KeyPair keyPair=keypairgen.generateKeyPair(); /*Public and Private Keys*/
            PublicKey pubkey=keyPair.getPublic();

            out.println(mode);
            out.println(pubkey.getEncoded());
            out.flush();

            System.out.println("Big Integer P: "+p);
            System.out.println("Generator g: "+g);
            System.out.println("exit");

            //Send Public key to server

            //keyPair.getPublic();
            while((test=System.in.read())!=-1)
            {
                cos.write((byte)test);
                cos.flush();
            }
            serverIn.close();
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
    public Cipher encrypt(String mode){
        try{
            System.out.println("enc "+mode);
            Cipher cipher = Cipher.getInstance(mode);
            //IV
            Path piv = Paths.get("iv");

            SecureRandom random = new SecureRandom();
            byte iv[] = new byte[16];
            random.nextBytes(iv);
            Files.write(piv,iv);

            //KEY
            Path kp=Paths.get("../Rc4/chave");
            byte[] keyfile= Files.readAllBytes(kp);
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


class SupportedCiphers_{
    private HashMap<String, String> ciphers;
    
    public SupportedCiphers_(){
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
