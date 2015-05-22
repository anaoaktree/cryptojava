//package diffieHellman;

import java.util.*; /* Scanner; */
import java.util.function.Consumer;
import java.io.*;


public class Utilities {
	 public static void main(String args[])
    {
    	Consumer<Object> print = str -> System.out.println(str);
    	print.accept("Hello");
    	//print = (str1)-> System.out.println(str1);
        //print.println("Wrong cipher identifier!");

    }

    public PublicKey decodeX509(byte[] keyBytes){
        try{
        KeyFactory kf = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytes);
        return kf.generatePublic(x509Spec);
    } catch (Exception e) {System.out.println(e);return null;}

    }



}