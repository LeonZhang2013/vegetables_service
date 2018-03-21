package com.zhongshu.vegetables.encrypt;

import java.security.MessageDigest;

/**
 * <p>信息摘要加密算法</p>
 * <p>
 * 	信息摘要加密算法意为信息摘要算法，是一种不可逆的加密算法，
 * 主要包括了MD5、SHA1加密算法
 * 通过调用{@link #encrypt(String,String)}来实现加密
 * </p>
 * @author 李熠
 * @date 2014-7-8
 * @company 成都市映潮科技有限公司
 * @version 0.1.3
 * @since 0.1.3
 */
public class MessageDigestUtils {
	
	/**
	 * 对password进行加密
	 * @param password 要加密的明文
	 * @param algorithm 加密算法名字，有MD5,SHA1
	 * @return 加密后的密文
	 * @throws Exception
	 */
	public static String encrypt(String password,String algorithm) throws Exception{
		MessageDigest md = MessageDigest.getInstance(algorithm);
		byte[] b = md.digest(password.getBytes());
		return ByteUtils.byte2HexString(b);
	}
}
