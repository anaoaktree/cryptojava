package secure_channel;

/**
 *
 * @author Ana Paula Carvalho and Fábio Fernandes
 */

import java.security.*;
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
    public SealedObject sign(Cipher enc, PrivateKey priv, byte[] gx, byte[] gy){
        try{
            sig.initSign(priv);
            sig.update(gx);
            sig.update(gy);
            byte[] assinaturaXY = sig.sign();
            SealedObject sigXY = new SealedObject(assinaturaXY, enc); 
            return sigXY;

    	}catch (Exception e) {System.out.println(e);}
    	return null;

    }

 /**
    *A party (B) verifies the object he got from another party (A) and see if it matches
    ***/
    public Boolean verify(Cipher dec, SealedObject sigXY2, PublicKey pub, byte[] gx, byte[] gy){
        try{
            byte[] assinaturaXY2 = (byte[]) sigXY2.getObject(dec);
            sig.initVerify(pub);
            sig.update(gx);
            sig.update(gy);
            if( sig.verify(assinaturaXY2) ) {
                    return true;
                }
            else {
                return false;
            } 


        }catch (Exception e) {System.out.println(e);}
        return null;

    }
}