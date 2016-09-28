package de.ibh_impex.androidhelputils;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {

	private final Cipher cipher;
	private final SecretKeySpec key;
	private AlgorithmParameterSpec spec;
	
	private static final short[] keyArray = {0x5a,0x8a,0x80,0x9d,0x29,0x10,0xd9,0x5b,0x4f,0xf8,0x92,0x79,0x8f,0x1d,0x30,0x34};
	private static final String IBH_KEY = Utils.byteArrayToString(keyArray);
	private byte[] ivArray;
	
	public static final int KEY_ID = 0x49424837; 
	
	public AESCrypt() throws Exception	//With standard IBH_KEY
	{
		byte[] bArray = new byte[IBH_KEY.length()];
		for (int i=0;i<bArray.length;i++)
			bArray[i] = (byte) (IBH_KEY.charAt(i) & 0xff);
		
		key = new SecretKeySpec(bArray, "AES");
		cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
		spec = generateIV();
	}   

//	public AESCrypt(String password) throws Exception
//	{
//		// hash password with SHA-256 and crop the output to 128-bit for key
//		MessageDigest digest = MessageDigest.getInstance("MD5");
//		digest.update(password.getBytes("UTF-8"));
//		byte[] keyBytes = new byte[16];
//		System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
//
//		cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
//		key = new SecretKeySpec(keyBytes, "AES128");
//		spec = generateIV();
//	}       

	public AlgorithmParameterSpec generateIV()
	{
		byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		ivArray = iv;
		
		IvParameterSpec ivParameterSpec;
		ivParameterSpec = new IvParameterSpec(iv);

		return ivParameterSpec;
	}
	
	public byte[] getIVasByteArray()
	{
		return ivArray;
	}
	
	public void setIV(byte[] lastIv)	//16 bytes
	{
		ivArray = lastIv;
		IvParameterSpec ivParameterSpec;
		ivParameterSpec = new IvParameterSpec(lastIv);
		spec = ivParameterSpec;
	}

	public String encrypt(String plainText) throws Exception
	{
		//create byte Array out of plaintext
		byte[] bArray = new byte[plainText.length()];
		for (int i=0;i<bArray.length;i++)
			bArray[i] = (byte) (plainText.charAt(i) & 0xff);
		
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		byte[] encrypted = cipher.doFinal(bArray);
		
		short[] keyshort = new short[encrypted.length];
		for (int i=0;i<keyshort.length;i++)
		{
			keyshort[i] = (short) (encrypted[i] & 0xFF);
		}
		String keyString = Utils.byteArrayToString(keyshort);
		
		return keyString;
//		
	}

	public String decrypt(String cryptedText) throws Exception
	{
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		byte[] bytes = new byte[cryptedText.length()];
		for (int i=0;i<bytes.length;i++)
			bytes[i] = (byte) (cryptedText.charAt(i) & 0xff);
		
		byte[] decrypted = cipher.doFinal(bytes);
		short[] fArray = new short[decrypted.length];
		for (int i=0;i<fArray.length;i++)
			fArray[i] = (short) (decrypted[i] & 0xff);
		
		String decryptedText = Utils.byteArrayToString(fArray);

		return decryptedText;
	}
	
	public byte[] getKey()
	{
		return key.getEncoded();
	}
}
