package secure_channel;

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

public class Encrypt{

    public Cipher encrypt(String mode,byte [] keyfile, byte[] iv){
        try{
            Cipher cipher = Cipher.getInstance(mode);
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