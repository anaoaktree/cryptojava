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
        BufferedReader in;
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
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());
            Encrypt enc = new Encrypt();
            Cipher cipher = enc.encrypt(mode);
            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 
            

            System.out.println("Connected to server");
            System.out.println("Mode: "+mode);                
            //while(!(str.equals("exit")))
            out.println(mode);
            out.flush();
             
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
                iv= Files.readAllBytes(Paths.get("./iv"));
            }
            else{
                key = new SecretKeySpec(keyfile,"RC4");
            }
            cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(iv));
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
