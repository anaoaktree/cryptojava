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
import java.util.*;

import java.math.BigInteger;

import javax.crypto.CipherInputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyPairGenerator;
import java.security.*;



import java.security.NoSuchAlgorithmException;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.KeyAgreement;
import javax.crypto.*;
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

    //private Key_Agreement_DH keyagree;
    
    public ReadMessage(Socket client,int id) throws NoSuchAlgorithmException {
       this.client = client;
       this.id=id;
       //this.keyagree = new Key_Agreement_DH();

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
        int count=1;
        String cipherMode="";
        AlgorithmParameterGenerator pgen = AlgorithmParameterGenerator.getInstance("DiffieHellman"); 
        pgen.init(1024);
        //Gera os parametros P e G
        AlgorithmParameters params= pgen.generateParameters();
        DHParameterSpec dhspec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
        BigInteger bigp = dhspec.getP();
        BigInteger bigg = dhspec.getG();
        int intl = dhspec.getL();
  
        KeyPairGenerator keypair = KeyPairGenerator.getInstance("DH");
        keypair.initialize(dhspec);
        KeyPair kp= keypair.generateKeyPair();
        PublicKey publickey = kp.getPublic();

        System.out.println("sending parameters");


        PrintWriter outclient = new PrintWriter(this.client.getOutputStream());

        outclient.println(new String(bigp.toByteArray()));
        outclient.println(new String(bigg.toByteArray()));
        outclient.println(Integer.toString(intl));
        outclient.println(publickey.getEncoded());

        BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        String msg;
        ArrayList<String> inparams = new ArrayList<>();
         while((msg = in.readLine()) != null){
            System.out.println("receiving parameters");

                inparams.add((String) msg);
                count++;
                if (count ==1) break;  
        } 
        System.out.println(inparams.get(0));
        /*
        PrivateKey privkey= kp.getPrivate();
        PublicKey pubkeyclient = inparams.get(1);
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(privkey);
        ka.doPhase(pubkeyclient, true);
        byte[] secret = ka.generateSecret();
        */


        InputStream is = this.client.getInputStream();
        cipherMode = inparams.get(0);
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
