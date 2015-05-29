package stationtostation;


import java.util.*; /* Scanner; */
import java.util.function.Consumer;
import java.io.*;

import java.io.*; /* Buffered Reader; IOException; InputStreamReader */
import java.net.*; /* Socket; */

import java.util.*; /* Scanner; */
import java.math.*; /* BigInteger; */

import java.nio.file.*; /* Files; */

import java.nio.*; /* Charset */

import java.security.*; /* NoSuchAlgorithmException; InvalidKeyException; KeyPairGenerator; KeyPair; InvalidAlgorithmParameterException; AlgorithmParameters; AlgorithmParameterGenerator; Message Digest*/
import java.security.spec.*; /*InvalidParameterSpecException; InvalidKeySpecException*/

import javax.crypto.*; /* CipherInputStream; Cipher; CipherOutputStream; KeyGenerator; SecretKey */
import javax.crypto.spec.*;


public class Utilities {
/**
* Gets an encoded public key in byte[] and decodes it
*
*/
    public PublicKey decodeX509(byte[] keyBytes){
        try{
        KeyFactory kf = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytes);
        return kf.generatePublic(x509Spec);
    } 
    catch (Exception e) {System.out.println(e);return null;}

    }



}