package mac;

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



public class Encryption {
    public byte[] iv;

    public Mac digest(byte[] mackey, String mode) throws NoSuchAlgorithmException, InvalidKeyException{
        try{MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(mackey);
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(digest.digest(), mode));
        return mac;
        }
        catch(Exception e){System.out.println("Error on digest "+ e); return null;}

    }



	public byte[] get16IV() throws IOException{
		SecureRandom random = new SecureRandom();
        iv = new byte[16];
        random.nextBytes(iv);
        Files.write(Paths.get("iv"),iv);
        return iv;
	}

	private Cipher init(String ciphmode, int mode, byte[] skey){
		try{
        byte[] iv;
        if (mode==Cipher.ENCRYPT_MODE){iv=get16IV();}
        else {iv = Files.readAllBytes(Paths.get("iv"));}

        byte[] keyfile;
        if (skey==null){keyfile= Files.readAllBytes(Paths.get("chave"));}
        else{keyfile=skey;}


        Cipher cipher = Cipher.getInstance(ciphmode);
        
        SecretKey key;
		if(ciphmode.startsWith("AES")){
                key = new SecretKeySpec(keyfile,"AES");
                cipher.init(mode, key, new IvParameterSpec(iv));
            }
        else{
            key = new SecretKeySpec(keyfile,"RC4");
            cipher.init(mode, key);
            }

           return cipher;


		}
	catch (Exception e){
		System.err.println("Error initiating cipher of length "+skey.length+ e);
        return null;
	}
	}

	public Cipher encrypt(String mode){
		return init(mode, Cipher.ENCRYPT_MODE, null);

	}

    public Cipher encrypt(String mode, byte[] key){
        return init(mode, Cipher.ENCRYPT_MODE, key);

    }

	public Cipher decrypt(String mode){
		return init(mode, Cipher.DECRYPT_MODE, null);

	}
   
    public Cipher decrypt(String mode, byte[] key){
        return init(mode, Cipher.DECRYPT_MODE,key);

    }


}

class SupportedCiphers{
    private HashMap<String, String> ciphers;
    
    public SupportedCiphers(){
        this.ciphers = new HashMap<String, String>();
        this.ciphers.put("rc4", "RC4");
        this.ciphers.put("cbc","AES/CBC/NoPadding");
        this.ciphers.put("cfb8","AES/CFB8/NoPadding");
        this.ciphers.put("cfb","AES/CFB/NoPadding");
        this.ciphers.put("cbc_pdd","AES/CBC/PKCS5Padding");
        this.ciphers.put("cfb8_pdd","AES/CFB8/PKCS5Padding");
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