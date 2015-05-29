package stationtostation;


import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*; /* SecretKeySpec; IvParameterSpec;  DHParameterSpec*/
import javax.crypto.interfaces.*;
import javax.crypto.interfaces.DHPrivateKey;
import com.sun.crypto.provider.SunJCE;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.ProviderException;
import javax.crypto.*;
import javax.crypto.spec.DHParameterSpec;
import sun.security.util.*;

import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;


import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;



public class StationtoStation{
    private Signature sig;
    private KeyPair keypair;
    public PublicKey pubKey;

    public StationtoStation(){
        try{this.sig=Signature.getInstance("SHA1withRSA");}
        catch (Exception e) {System.out.println(e);}
    }

    public StationtoStation(String alg){
        try{this.sig=Signature.getInstance(alg);}
        catch (Exception e) {System.out.println(e);}
    }

    public KeyPair genRSAKeyPair() throws Exception{
        KeyPairGenerator rsaKeyPairGen = KeyPairGenerator.getInstance("RSA");
        rsaKeyPairGen.initialize(1024);
        keypair = rsaKeyPairGen.generateKeyPair();
        return keypair;
    }

    public KeyFactory genRSAKeyFact() throws Exception{
        RSAPrivateCrtKey rsapriv = (RSAPrivateCrtKey)keypair.getPrivate();
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsapriv.getModulus(), rsapriv.getPublicExponent());
        return KeyFactory.getInstance("RSA");

    }

    /**
    *A party (A) signs with this function and sends it to another party (B) to verify
    ***/
    public byte[] sign(PrivateKey priv, byte[] mypub, byte[] otherpub, Cipher initcipher){
        try{
            sig.initSign(priv);
            // O par X,Y é assinado. Talvez sem calcular gx e gy explicitamente?
            sig.update(mypub);
            sig.update(otherpub);
            return initcipher.doFinal(sig.sign());

    	}catch (Exception err) {System.out.println("Error on sig sign: " + err);}
    	return null;

    }

 /**
    *A party (B) verifies the object he got from another party (A) and see if it matches
    ***/
    public Boolean verify(byte[] sign, PublicKey pub, byte[] otherpub){
        try{
            //byte[] assinaturaXY2 = (byte[]) sigXY2.getObject(dec);
            //Validacao da assinatura usando a chave publica do outro interveniente
            sig.initVerify(pub);
            sig.update(pub.getEncoded());
            sig.update(otherpub);
            Boolean verif=sig.verify(sign);
            System.out.println("DIG VERIF: " + verif);
            return verif;

        }catch (Exception e) {System.out.println("Error on sig verify: "+e);}
        return null;

    }
}