import java.nio.file.Files;
import java.nio.file.*;
import java.nio.charset.Charset;

import java.io.*;
import java.util.List;
import java.util.stream.*;
import java.lang.StringBuilder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.*;





public class Rc4 {
    /**
    * Generates a key
    **/
	public static void genkey(String kpath) throws IOException{
		try{
		System.out.println("** Generating key for RC4...");
		Path kp = Paths.get(kpath);
		KeyGenerator kg = KeyGenerator.getInstance("RC4");
        //key size has to be 128!
		kg.init(128);
		SecretKey key = kg.generateKey();
		System.out.println("** Writing key to "+kpath+"...");
		Files.write(kp,key.getEncoded());
		System.out.println("** DONE! ");

	}
	catch (Exception e){
		System.err.println("Someting went wrong. "+ e);
	}

	}

	public static void crypt(int mode,String keypath, String inpath, String outpath ) throws IOException, NoSuchAlgorithmException, InvalidKeyException{
		try{
		Path ip=Paths.get(inpath);
		Path kp=Paths.get(keypath);
		Path op=Paths.get(outpath);
		System.out.println("** Reading input and key files...");


		byte[] infile= Files.readAllBytes(ip);
		byte[] keyfile= Files.readAllBytes(kp);
		SecretKey key = new SecretKeySpec(keyfile,"RC4");

		System.out.println("** Initiating algorithm RC4...");


		Cipher e = Cipher.getInstance("RC4");
		e.init(mode,key);

		if (mode == Cipher.DECRYPT_MODE)
			System.out.println("** Decrypting to "+outpath+"...");
		else
			System.out.println("** Encrypting to "+outpath+"...");

		Files.write(op,e.doFinal(infile));
		System.out.println("** DONE! ");

		}
	catch (Exception e){
		System.err.println("Something went wrong "+ e);
	}
}
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException,InvalidKeyException{
    	int i = 0,j;
    	String arg="";
    	Boolean wflag=false;

    	if (args.length==0){wflag=true;}
    	else arg=args[i++];
    	if(arg.equals("-help")|| arg.equals("-h")){wflag=true;}
   		else if(arg.equals("-genkey")){
   			if(i+1==args.length){
    		genkey("./"+args[i++]);
    			}
    		else{
    			wflag=true;
    			System.err.println("Something went wrong. Too much or too little files");
    			}
    		}
    		else if(arg.equals("-enc")){
    			if((i+3)==args.length){
    				crypt(Cipher.ENCRYPT_MODE,"./"+args[i++],"./"+args[i++],"./"+args[i++]);
    			}
    			else{
    				wflag=true;
    				System.err.println("Something went wrong. Too much or too little files");
    			}

    		}
    		else if(arg.equals("-dec")){
    			if((i+3)==args.length){
    				crypt(Cipher.DECRYPT_MODE,"./"+args[i++],"./"+args[i++],"./"+args[i++]);
    			}
    			else{
    				wflag=true;
    				System.err.println("Something went wrong. Too much or too little files");
    			}

    		}
    		else{
    			wflag=true;
    		}
    	
    if(wflag){
    	System.err.println("** Usage:\njava Rc4 [-genkey <keyfile>] [-enc <keyfile> <infile> <outfile>] [-dec <keyfile> <infile> <outfile>]");
	
    }

    }
}