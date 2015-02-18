package common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * MD5加密
 * 获得MD5字符串
 * @author grs
 * @since 2012.5
 */
public class MD5Util {

	public static final String MD5 = "MD5";
	public static final String CODE = "%02x";
	private static MessageDigest digest;
	static {
		try {
			digest = MessageDigest.getInstance(MD5);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("没有此加密算法！");
		}
	}
	/**
	 * md5加密处理
	 * @param text
	 * 		进行md5加密的String对象
	 * @return
	 * 		md5加密后的String对象
	 */
	public synchronized static String MD5(String text) {
		byte[] bytes = digest.digest(text.getBytes());
		StringBuilder output = new StringBuilder(bytes.length);
		for (byte entry : bytes) {
			output.append(String.format(CODE, entry));
		}
		digest.reset();
		bytes = null;
		return output.toString();
	}
	
}
