package stationtostation;

/**
 *
 * @author Ana Paula Carvalho and Fábio Fernandes
 */

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



public class StationtoStation{
    private Signature sig;
    public StationtoStation(){
        try{
        this.sig=Signature.getInstance("SHA1withRSA");
    }
    catch (Exception e) {System.out.println(e);}
    }

    /**
    *A party (A) signs with this function and sends it to another party (B) to verify
    ***/
    public SealedObject sign(PrivateKey priv, byte[] mypub, byte[] otherpub, SecretKey secrKey){
        try{
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secrKey);
            sig.initSign(priv);
            // O par X,Y é assinado. Talvez sem calcular gx e gy explicitamente?
            sig.update(mypub);
            sig.update(otherpub);
            new SealedObject(sig.sign(),cipher);

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