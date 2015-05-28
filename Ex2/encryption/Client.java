/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

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
        Encryption encryption = new Encryption();
        SupportedCiphers sup = new encryption.SupportedCiphers();
        Socket s;
       // BufferedReader in;
        //PrintWriter out;
        String mode = "";
        if (args.length > 0){
            mode = sup.getCipher(args[0]);
            if(mode.length() < 1){
                System.out.println("Wrong cipher identifier!");
                System.out.println(sup.getSupportedCiphers());
                mode = "RC4";
            }
        }
        else{
            mode = "RC4";
        }

        int test;
        try{
            s = new Socket("localhost", 4567);
           // in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //out = new PrintWriter(s.getOutputStream());
            Cipher cipher = encryption.encrypt(mode);
            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            

            System.out.println("Connected to server");
            System.out.println("Mode: "+ mode);                
            //while(!(str.equals("exit")))
            //out.println(mode);
            //out.flush();
            byte[] ciphmode= mode.getBytes();
            out.writeInt(ciphmode.length);
            out.write(ciphmode);
             
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
   
