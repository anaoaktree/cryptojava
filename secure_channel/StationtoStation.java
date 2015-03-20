package secure_channel;

/**
 *
 * @author Ana Paula Carvalho and Fábio Fernandes
 */

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;


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
    public byte[] sign(PrivateKey priv, byte[] mypub, byte[] otherpub){
        try{
            //Inicializada a chave privada Usar RSAPrivateKey?
            sig.initSign(priv);
            // O par X,Y é assinado. Talvez sem calcular gx e gy explicitamente?
            sig.update(mypub);
            sig.update(otherpub);
            // Falta cifrar a sig
            byte[] sign = sig.sign();
            return sign;

    	}catch (Exception e) {System.out.println("Error on sig sign: "+e);}
    	return null;

    }

 /**
    *A party (B) verifies the object he got from another party (A) and see if it matches
    ***/
    public Boolean verify(byte[] sign, byte[] otherpub, byte[] mypub){
        try{
            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(otherpub);
            PublicKey pub=kf.generatePublic(x509Spec);
            //byte[] assinaturaXY2 = (byte[]) sigXY2.getObject(dec);
            //Validacao da assinatura usando a chave publica do outro interveniente
            sig.initVerify(pub);
            sig.update(mypub);
            sig.update(otherpub);
            return sig.verify(sign);

        }catch (Exception e) {System.out.println("Error on sig verify: "+e);}
        return null;

    }
}