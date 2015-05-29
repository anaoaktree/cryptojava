package stationtostation;

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
import javax.crypto.interfaces.*;
import com.sun.crypto.provider.SunJCE;

import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;


import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;



public class Client {

     public static PublicKey decodeX509(byte[] keyBytes){
        try{
        KeyFactory kf = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(keyBytes);
        return kf.generatePublic(x509Spec);
    } 
    catch (Exception err) {System.out.println(err);return null;}

    }


    public static void main(String args[])
    {
              

        Socket s;
        String mode = "";
        if (args.length > 0){
            if(mode.length() < 1){
                System.out.println("Wrong cipher identifier!");
                //System.out.println(supportedCiphers.getSupportedCiphers());
                mode = "RC4";
            }
        }
        else{
            mode = "RC4";
        }

        int test;
        try{
            s = new Socket("localhost", 4567);
            PrintWriter writer = new PrintWriter(s.getOutputStream());
            
            /**
            * Sends cipher mode to server
            */
            writer.println(mode);
            writer.flush();

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            //ObjectInputStream obin = new ObjectInputStream(s.getInputStream());
            //ObjectOutputStream obout = new ObjectOutputStream(s.getOutputStream());
            /**
            * Computes DH key Agreement and generates DH parameters.
            * Also, it creates a key pair based on the parameters
            **/ 
            System.out.println("Computing DH parameters...");

            DiffieHellman dh= new DiffieHellman();
            dh.genParamsFull("manual"); //for auto, we needed a central authority to share it also with server
            
            //Gets the public key generated earlier
            byte[] pubSelf = dh.getPublicKey().getEncoded();

            //Sends public key to Server
            System.out.println("Sending public key to server");

            out.writeInt(pubSelf.length);
            out.write(pubSelf);
            
            /**
            * Gets public key from server
            **/
            System.out.println("Getting public key from server");

            byte[] pubServer = new byte[in.readInt()];
            in.readFully(pubServer);

            /**
            * Proceeds with the key agreement: initializes with its private key
            *  adds the public key from the server and then generates the secret, which is returned.
            */
            System.out.println("Computing shared secret");
            PublicKey serverPubKey= decodeX509(pubServer);
            PrivateKey myPrivateKey = dh.getPrivateKey();
            byte[] sharedSecret=dh.keyAgreement(myPrivateKey,serverPubKey);

//--------------------------------------------------------------------
            //Return the shared secret as the secret key, specifying the algorithm.
         
             /*
         * Client encrypts, using DES in ECB mode
         */
           
        SecretKey secrKey = dh.sharedSecretKey(serverPubKey,"DES");

        

    //--------STS----------------
        //Generates RSA key pair
        KeyPairGenerator rsaKeyPairGen = KeyPairGenerator.getInstance("RSA");
        rsaKeyPairGen.initialize(1024);
        KeyPair rsaKeyPair = rsaKeyPairGen.generateKeyPair();
        RSAPrivateCrtKey rsapriv = (RSAPrivateCrtKey)rsaKeyPair.getPrivate();
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsapriv.getModulus(), rsapriv.getPublicExponent());
        KeyFactory kfact = KeyFactory.getInstance("RSA");
        PublicKey rsaPubKey = kfact.generatePublic(publicKeySpec);
        byte[] rsaPub=rsaPubKey.getEncoded();

        //Sends pubkey and modulus to server

        out.writeInt(rsapriv.getModulus().toByteArray().length);
        out.write(rsapriv.getModulus().toByteArray());

        out.writeInt(rsaPub.length);
        out.write(rsaPub);

        //receives from server
         byte[] rsaPubServer = new byte[in.readInt()];
         in.readFully(rsaPubServer);
            

         // Gerar número aleatório, calcular o expoente e 
		// enviá-lo ao servidor

        StationtoStation sts = new StationtoStation();
        SealedObject ciphsign = sts.sign(rsaKeyPair.getPrivate(),rsaPub,rsaPubServer,secrKey);

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(ciphsign);
        byte[] ciphSign = b.toByteArray();

        //byte[] ciphSign = ((Object)ciphsign).toByteArray();
        out.writeInt(ciphSign.length);
        out.write(ciphSign);


        //recebe do servidor
        byte[] sigServer = new byte[in.readInt()];
        in.readFully(sigServer);

        Boolean verif = sts.verify(sigServer,rsaPubKey,rsaPubServer);
        System.out.println("verified: " + verif);





        //enviar ao outro o sealed object
        //iniciar uma cifra para decifrar a chave
        // verificar a assinatura

        //------------------STS

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secrKey);

            OutputStream os = s.getOutputStream();
            CipherOutputStream cos = new CipherOutputStream(os,cipher); 

            System.out.println("Connected to server");
            //System.out.println("Mode: "+mode);                
            System.out.println("You can start typing now!\n");
                
            while((test=System.in.read())!=-1){
                cos.write((byte)test);
                cos.flush();
            }
            in.close();
            out.close();
            s.close();             
        }catch(Exception e){
            System.out.println("*** Failed to connect to server ***");
        }

        
    }
}


