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



public class Encryption {
    public byte[] iv;



	public byte[] get16IV() throws IOException{
		SecureRandom random = new SecureRandom();
        this.iv = new byte[16];
        random.nextBytes(this.iv);
        Files.write(Paths.get("iv"),this.iv);
        return this.iv;
	}

	private Cipher init(String ciphmode, int mode, byte[] ivb){
		try{
        if (iv==null){get16IV();}
        else{this.iv=ivb;}
        Cipher cipher = Cipher.getInstance(ciphmode);

		Path kp=Paths.get("chave");
        byte[] keyfile= Files.readAllBytes(kp);
        SecretKey key;
		if(ciphmode.startsWith("AES")){
                key = new SecretKeySpec(keyfile,"AES");
                cipher.init(mode, key, new IvParameterSpec(this.iv));
            }
        else{
            key = new SecretKeySpec(keyfile,"RC4");
            cipher.init(mode, key);
            }

           return cipher;


		}
	catch (Exception e){
		System.err.println("Something went wrong "+ e);
        return null;
	}
	}

	public Cipher encrypt(String mode){
		return init(mode, Cipher.ENCRYPT_MODE, null);

	}

	public Cipher decrypt(String mode,byte[] ivb){
		return init(mode, Cipher.DECRYPT_MODE,ivb);

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