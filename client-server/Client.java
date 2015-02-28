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

public class Client {
    public static void main(String args[])
    {
        Socket s;
        BufferedReader in;
        PrintWriter out;
        String str = "", str2;
        Scanner stdin = new Scanner(System.in);
        //G
        String mode = "";
        if (args.length > 0)
            mode = args[0];
        else{
            mode = "RC4";
        }
        int test;
        //END G2
        try{

            Cipher cipher = Cipher.getInstance(mode);
            s = new Socket("localhost", 4567);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());
            //G2
            Path kp=Paths.get("../Rc4/chave");
            byte[] keyfile= Files.readAllBytes(kp);
            SecretKey key = new SecretKeySpec(keyfile,mode);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 
            //END G2

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
