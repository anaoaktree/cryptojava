import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.*;
import java.util.Arrays;



public class Arc4{
	private static byte[] S = new byte[256];

	public static int posMod(int val, int modval){
		return ((val % modval)+modval)%modval;
	}

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
			j=posMod((j+S[i]+key[posMod(i,len)]),256);
			byte tmp=S[j];
			S[j]=S[i];
			S[i]=tmp;
		}
	}
	/*
	* FALTA CORRIGIR OS MODULOS
	*/
	public static byte[] arc4(byte[] input) throws Exception{
		int i=0;
		int j=0;
		byte k;
		final byte[] K = new byte[input.length];
		for(int c = 0;c<input.length;c++){
			i=posMod(i+1,256);
			j=posMod(j+S[i],256);
			byte tmp=S[j];
			S[j]=S[i];
			S[i]=tmp;
			k=S[posMod(S[i]+S[j],256)];
			K[c]= (byte)(input[c]^k);
		}
		return K;
	}

	public static void main(String[] args) {
		try{
		System.out.println("initiating key...");

		init(Files.readAllBytes(Paths.get("./chave")));

		System.out.println("arc4 working...");

		byte[] decifer=arc4(Files.readAllBytes(Paths.get("./texto_limpo")));
        String dec = new String(decifer,"UTF-8");
        byte[] newdec= dec.getBytes("UTF-8");
        //System.out.println(Arrays.equals(decifer, newdec));
        Files.write(Paths.get("./criptograma_arc4"), newdec);


		byte[] arc4back =arc4(Files.readAllBytes(Paths.get("./criptograma_arc4")));
		String back = new String(arc4back,"UTF-8");

        byte[] newback= dec.getBytes("UTF-8");
        Files.write(Paths.get("./decifrado_arc4"), arc4back);
		System.out.println("input " + newback );

		System.out.println("Done.");





	}
	catch (Exception e){
		System.err.println("Someting went wrong. "+ e);
	}
		
	}
   
}