package introx509;

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

public class Decrypt{

    public Cipher decrypt(String ciphmode, byte[] keyfile, byte[] iv){
        try{
            Cipher e = Cipher.getInstance(ciphmode);
        	SecretKey key;
        	if(ciphmode.startsWith("AES")){
        	key = new SecretKeySpec(keyfile,"AES");
        	e.init(Cipher.DECRYPT_MODE,key,new IvParameterSpec(iv));
        	}
        	else{
        		key = new SecretKeySpec(keyfile,"RC4");
        		e.init(Cipher.DECRYPT_MODE,key);
        	}
        	return e;
    	}catch (Exception e) {System.out.println(e);}
    	return null;
    }
}