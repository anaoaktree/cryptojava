package mac;
/**
 *
 * @author Ana Paula Carvalho and Fábio Fernandes
 */

import java.math.BigInteger;
import java.lang.String;
import java.util.Random;

import java.security.*; /* NoSuchAlgorithmException; InvalidKeyException; KeyPairGenerator; KeyPair; InvalidAlgorithmParameterException; AlgorithmParameters; AlgorithmParameterGenerator; */
import java.security.spec.*; /*InvalidParameterSpecException; InvalidKeySpecException*/

import javax.crypto.*; /* CipherInputStream; Cipher; CipherOutputStream; KeyGenerator; SecretKey */
import javax.crypto.spec.*;




public class DiffieHellman {
 
    	private String p="99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583";
    	private String g="44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675";
    	private BigInteger bigp = new BigInteger(p);
    	private BigInteger bigg = new BigInteger(g);
        private int intl= 0;
        private KeyPair keyPair;
        private KeyAgreement ka;
        PublicKey otherParty;

    public DiffieHellman(){
        this.bigp=bigp;
        this.bigg=bigg;
        this.intl=0;
        this.keyPair=null;

    }
    public DiffieHellman(BigInteger bigpman, BigInteger biggman, int intlman){
        this.bigp=bigpman;
        this.bigg=biggman;
        this.intl=intlman;
        this.keyPair=null;

    }

    public BigInteger getP(){ return this.bigp;}
    public BigInteger getG(){ return this.bigp;}
    public int getL(){ return this.intl;}

    public PublicKey getPublicKey(){ return  this.keyPair.getPublic();}
    public PrivateKey getPrivateKey(){ return  this.keyPair.getPrivate();}

    /*
    * Performs the KeyAgreement
    */

    public byte[] keyAgreement(PrivateKey privk, PublicKey pubk){
        try{
            ka.init(privk);
            ka.doPhase(pubk,true);
            return ka.generateSecret();
        } catch (Exception e) {System.out.println(e);return null;}

    }

    public byte[] sharedSecretKey(PublicKey pubk){
        try{
            ka.doPhase(pubk,true);
            return ka.generateSecret();
        } catch (Exception e) {System.out.println(e);return null;}

    }

    
    public void genParamsFull(String mode){
        try{
        //Parameter Generator P,G,l
            AlgorithmParameterGenerator pgen = AlgorithmParameterGenerator.getInstance("DiffieHellman"); 
            pgen.init(1024);
            DHParameterSpec dhspec;
            if (mode=="auto"){
                AlgorithmParameters params= pgen.generateParameters();
                dhspec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
            }
            //For manual params defined in this.bigp or this.bigg
            else{
                dhspec = new DHParameterSpec(this.bigp,this.bigg);
            }
            genParamsSpec(dhspec);
        }
        catch (Exception e) {System.out.println(e);}
    }

    /**
    * Generates parameters from a parameter specification
    *
    */
    public void genParamsSpec(DHParameterSpec dhspec){
        try{
            ka= KeyAgreement.getInstance("DH");
            KeyPairGenerator keypairGen = KeyPairGenerator.getInstance("DH");
            keypairGen.initialize(dhspec);
            this.keyPair= keypairGen.generateKeyPair();

            //resets the params in case it is auto
            this.bigp = dhspec.getP();
            this.bigg = dhspec.getG();
            this.intl = dhspec.getL();
        }
        catch (Exception e) {System.out.println(e);}
    }
}