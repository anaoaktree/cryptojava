
/**
 *
 * @author Ana Paula Carvalho and Fábio Fernandes
 */


import java.util.*; /* HashMap; */


public class SupportedCiphers{
    private HashMap<String, String> ciphers;
    
    public SupportedCiphers(){
        this.ciphers = new HashMap<String, String>();
    }

    public void add(String cod, String cipher){
        this.ciphers.put(cod, cipher);
    }

    public String getCipher(String cod){
        if (this.ciphers.containsKey(cod)){
            return this.ciphers.get(cod);
        }
        return "";
    }
    public String getSupportedCiphers(){
        String res = "";
        res += "\n************* Supported Ciphers *************\n";
        for(String s : this.ciphers.keySet()){
            res += "*   Id: "+s+" =>  Cipher: "+this.ciphers.get(s)+"\n";
        }
        res += "************* ----------------- *************\n";
        return res;
    }

    public void initialize( SupportedCiphers supportedCiphers){
        supportedCiphers.add("rc4", "RC4");
        supportedCiphers.add("cbc","AES/CBC/NoPadding");
        supportedCiphers.add("cfb8","AES/CFB8/NoPadding");
        supportedCiphers.add("cfb","AES/CFB/NoPadding");
        supportedCiphers.add("cbc_p","AES/CBC/PKCS5Padding");
        supportedCiphers.add("cfb8_p","AES/CFB8/PKCS5Padding");
    }
}