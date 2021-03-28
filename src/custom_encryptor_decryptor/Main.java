package custom_encryptor_decryptor;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class Main {

	static Cipher cipher;
	static KeyPair keyPair;
	static PublicKey publicKey;
	static PrivateKey privateKey;
	
	public static void main(String[] args) {
		
		Console con = System.console();
		if(con == null) {
			try {
				String launcherName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
				Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start", "cmd", "/k", "java -jar " + launcherName});
				System.exit(0);
			} catch (IOException e) {}
		}
		
		
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(1024);
			keyPair = keyPairGen.generateKeyPair();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		Scanner sc = new Scanner(System.in);
		
		try {
			while (true) {
				println("");
				println("encrypt or decrypt");
				switch (sc.nextLine()) {
				case "encrypt":
					println("type in text to encrypt");
					saveEncrypted(encrypt(sc.nextLine()));
					saveKey(privateKey);
					printByte(readEncrypted());
					break;
				case "decrypt":
					println("decrypting file...");
					PrivateKey decryptKey = readPrivateKey();
					println(decrypt(decryptKey, readEncrypted()));
					break;
				}
			}
		} catch (FileNotFoundException fnfe) {
			println("files with proper names not found");
			fnfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.close();
	}
	
	public static byte[] encrypt(String str) throws Exception {
		byte[] input = str.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		cipher.update(input);
		byte[] encrypted = cipher.doFinal();
		
		return encrypted;
	}
	
	public static String decrypt(PrivateKey privateKey, byte[] encrypted) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		cipher.update(encrypted);
		String decrypted = new String(cipher.doFinal(), "UTF-8");

		return decrypted;
	}
	
	public static void saveEncrypted(byte[] encrypted) throws Exception {
		FileOutputStream encryptedWriter = new FileOutputStream("encrypted");
		encryptedWriter.write(encrypted);
		encryptedWriter.close();
	}
	
	public static void saveKey(PrivateKey privateKey) throws Exception {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		FileOutputStream keyWriter = new FileOutputStream("private.key");
		keyWriter.write(pkcs8EncodedKeySpec.getEncoded());
		keyWriter.close();
	}
	
	public static byte[] readEncrypted() throws Exception {
		File encryptedFile = new File("encrypted");
		FileInputStream fileReader = new FileInputStream("encrypted");
		byte[] encrypted = new byte[(int) encryptedFile.length()];
		fileReader.read(encrypted);
		fileReader.close();
		
		return encrypted;
	}
	
	public static PrivateKey readPrivateKey() throws Exception {
		File privateKeyFile = new File("private.key");
		FileInputStream keyReader = new FileInputStream("private.key");
		byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];
		keyReader.read(encodedPrivateKey);
		keyReader.close();
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		privateKey = keyFactory.generatePrivate(privateKeySpec);
		return privateKey;
	}
	
	public static void println(Object obj) {
		System.out.println(obj);
	}
	
	public static void printByte(byte[] toPrint) {
		try {
			System.out.println(new String(toPrint, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
