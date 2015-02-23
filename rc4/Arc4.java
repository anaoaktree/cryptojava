import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.*;



public class Arc4{
	private static byte[] S = new byte[256];
	public static void init(byte [] key){
		/*
		*
		* Key scheduling algorithm
		*/
		int len=key.length;
		for(int i = 0;i<256;i++){
			S[i]=(byte)i;
		}
		int j=0;
		for(int i = 0;i<256;i++){
			j=(((j+S[i]+key[((i%len)+len)%len])%256)+256)%256;
			byte tmp=S[j];
			S[j]=S[i];
			S[i]=tmp;
		}
	}
	/*
	* FALTA CORRIGIR OS MODULOS
	*/
	public static byte[] arc4(byte[] input){
		int i=0;
		int j=0;
		byte k;
		final byte[] K = new byte[input.length];
		for(int c = 0;c<input.length;c++){
		i=(((i+1)%256)+256)%256;
		j=(((j+S[i])%256)+256)%256;
		byte tmp=S[j];
		S[j]=S[i];
		S[i]=tmp;
		k=S[(((S[i]+S[j])%256)+256)%256];
		K[c]= (byte)(input[c]^k);
		}
		return K;
	}

	public static void main(String[] args) {
		try{
			System.out.println("initiating key...");
		init(Files.readAllBytes(Paths.get("./key")));
			System.out.println("arc4 working...");

		byte[] decifer=arc4(Files.readAllBytes(Paths.get("./input")));
        Files.write(Paths.get("./arc4out"), decifer);
		byte[] arc4back =arc4(Files.readAllBytes(Paths.get("./arc4out")));
        Files.write(Paths.get("./arc4back"), arc4back);




	}
	catch (Exception e){
		System.err.println("Someting went wrong. "+ e);
	}
		
	}
   
}