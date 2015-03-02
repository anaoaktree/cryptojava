import java.math.BigInteger;
import java.lang.String;
import java.security.AlgorithmParameterGenerator;
import javax.crypto.KeyAgreement;
import java.security.KeyPairGenerator;
import java.util.Random;




public class Key_Agreement_DH {
 
    	String p="098765r4e3567890";
    	String g="098765r4e3567890";
    	BigInteger bigp = new BigInteger(p);
    	BigInteger gen = new BigInteger(g);


    public BigInteger genSecret(){
    	BigInteger r;
		do {
   			 r = new BigInteger(1024, new Random());
			} while (r.compareTo(bigp) >= 0);
		return r;

    }

    public BigInteger genGroupElem(BigInteger secr){
    	return gen.modPow(secr,bigp);
    }
}