package de.ibh_impex.androidhelputils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class FileCrypter
{
	private static final int BUFFERSIZE = 1024;
	
	private static byte[] privateKey = {0x12,0x66,0x44,0x32,(byte) 0xf0,(byte) 0xc3,0x4f,(byte) 0xdd,
		0x1b,(byte) 0xa0, 0x32, (byte) 0x97, 0x72, (byte) 0xf0, 0x0c, 0x6c};

	public FileCrypter()
	{

	}

	public static File encrypt(File f) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IOException
	{
		FileInputStream fis = new FileInputStream(f);
		String filename = f.getPath();
		filename = filename.concat(".enc");
		FileOutputStream fos = new FileOutputStream(filename);

		SecretKeySpec spec = new SecretKeySpec(privateKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, spec);

		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		int b;
		byte[] d = new byte[BUFFERSIZE];
		while ((b = fis.read(d)) != -1)
			cos.write(d,0,b);

		cos.flush();
		cos.close();
		fis.close();

		return new File(filename);
	}
	
	public static File decrypt(File f) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IOException
	{
		FileInputStream fis = new FileInputStream(f);
		String filename = f.getPath();
		filename = filename.substring(0, filename.lastIndexOf("."));
		FileOutputStream fos = new FileOutputStream(filename);
		
		SecretKeySpec spec = new SecretKeySpec(privateKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, spec);
		
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		int b;
		byte[] d = new byte[BUFFERSIZE];
		while ((b = cis.read(d)) != -1)
			fos.write(d,0,b);
		
		fos.flush();
		fos.close();
		cis.close();
		return new File(filename);
	}
	
	public static byte[] decryptToByteArray(File f) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IOException 
	{
		FileInputStream fis = new FileInputStream(f);
		
		SecretKeySpec spec = new SecretKeySpec(privateKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, spec);
		
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		int b;
		byte[] d = new byte[BUFFERSIZE];
		ArrayList<Byte> byteList = new ArrayList<Byte>();
		while ((b = cis.read(d)) != -1)
		{
			for (int i=0;i<b;i++)
				byteList.add(d[i]);
		}
		
		cis.close();
		
		byte[] result = new byte[byteList.size()];
		for (int i=0;i<result.length;i++)
			result[i] = byteList.get(i);
		
		return result;
	}
	
	
}
