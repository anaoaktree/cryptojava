package dh;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ssi_aula1_aula2;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.lang.String;

import javax.crypto.CipherInputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyPairGenerator;
import java.security.KeyPair;



import java.security.NoSuchAlgorithmException;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import java.io.InputStream;


/**
 *
 * @author User
 */
public class Server_dh {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
       ServerSocket ss = new ServerSocket(4567);
       int client_id = 0;

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

    private Key_Agreement_DH keyagree;
    
    public ReadMessage(Socket client,int id) throws NoSuchAlgorithmException {
       this.client = client;
       this.id=id;
       this.keyagree = new Key_Agreement_DH();

    }
    public Cipher decrypt(String ciphmode){
        try{
        Cipher e = Cipher.getInstance(ciphmode);
        byte[] keyfile= Files.readAllBytes(Paths.get("../rc4/chave"));
        byte[] iv= null;
        SecretKey key;
        if(ciphmode.startsWith("AES")){
            key = new SecretKeySpec(keyfile,"AES");
        iv= Files.readAllBytes(Paths.get("./iv"));
        e.init(Cipher.DECRYPT_MODE,key,new IvParameterSpec(iv));
        }
        else{
            key = new SecretKeySpec(keyfile,"RC4");
            e.init(Cipher.DECRYPT_MODE,key);

        }
        return e;
    }
    catch (Exception e) {System.out.println(e);}
        return null;
    }
    public void run() {
    try {
        Boolean firstTime=true;
        String cipherMode="";
        AlgorithmParameterGenerator pgen = AlgorithmParameterGenerator.getInstance("DiffieHellman"); 
        pgen.init(1024);
        //Gera os parametros P e G
        AlgorithmParameters params= pgen.generateParameters();
        DHParameterSpec dhspec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
        KeyPairGenerator keypair = KeyPairGenerator.getInstance("DH");
        keypair.initialize(dhspec);
        KeyPair kp= keypair.generateKeyPair();
        PrintWriter outclient = new PrintWriter(this.client.getOutputStream());
        outclient.println(dhspec);

        if (firstTime){
        BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        String msg;
            while ((msg = in.readLine()) != null){
                cipherMode=(String) msg;
                firstTime=false;
                break;    
           } 
        }
        InputStream is = this.client.getInputStream();
        System.out.println("["+id+"]: Client connected with cipher "+cipherMode);
        Cipher ciph=decrypt(cipherMode);
        CipherInputStream cis = new CipherInputStream(is,ciph);
        int test;
        while((test=cis.read())!=-1){
            System.out.print("["+id+"]:"); 
            System.out.println((char) test);
        }
        System.out.println("["+id+"]: "+"Client disconnected");
    } catch (Exception e) {System.out.println(e);}
    }
}
