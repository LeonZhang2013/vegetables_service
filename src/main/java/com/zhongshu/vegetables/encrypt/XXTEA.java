package com.zhongshu.vegetables.encrypt;

/**
 * XXTEA加密算法
 * @author 李熠
 * @date 2014-7-8
 * @company 成都市映潮科技有限公司
 * @version 0.1.3
 * @since 0.1.3
 */
public class XXTEA {
	
//	private static int delta = 0x9e3779b;
	private static int delta = 0x98e7a9b;
	
	 /**
     * Encrypt data with key.
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return ByteUtils.intArray2ByteArray(encrypt(ByteUtils.byteArray2IntArray(data, true), ByteUtils.byteArray2IntArray(key, false)), false);
    }
    /**
     * Decrypt data with key.
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return ByteUtils.intArray2ByteArray(decrypt(ByteUtils.byteArray2IntArray(data, false), ByteUtils.byteArray2IntArray(key, false)), true);
    }
    /**
     * Encrypt data with key.
     *
     * @param data
     * @param key
     * @return
     */
    public static int[] encrypt(int[] data, int[] key) {
        int n = data.length - 1;
        if (n < 1) {
            return data;
        }
        if (key.length < 4) {
            int[] k = new int[4];
            System.arraycopy(key, 0, k, 0, key.length);
            key = k;
        }
        int z = data[n], y = data[0], sum = 0, e;
        int p, q = 6 + 52 / (n + 1);
        while (q-- > 0) {
            sum = sum + delta;
            e = sum >>> 2 & 3;
            for (p = 0; p < n; p++) {
                y = data[p + 1];
                z = data[p] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (key[p & 3 ^ e] ^ z);
            }
            y = data[0];
            z = data[n] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (key[p & 3 ^ e] ^ z);
        }
        return data;
    }
    /**
     * Decrypt data with key.
     *
     * @param v
     * @param k
     * @return
     */
    public static int[] decrypt(int[] data, int[] key) {
        int n = data.length - 1;
        if (n < 1) {
            return data;
        }
        if (key.length < 4) {
            int[] k = new int[4];
            System.arraycopy(key, 0, k, 0, key.length);
            key = k;
        }
        int z = data[n], y = data[0], sum, e;
        int p, q = 6 + 52 / (n + 1);
        sum = q * delta;
        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = data[p - 1];
                y = data[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (key[p & 3 ^ e] ^ z);
            }
            z = data[n];
            y = data[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (key[p & 3 ^ e] ^ z);
            sum = sum - delta;
        }
        return data;
    }
    
    /**
     * 加密
     * @param password 明文
     * @param key 密钥
     * @return 密文
     */
   public static String encrypt(String password,String key){
	   byte[] data = encrypt(password.getBytes(), key.getBytes());
	   return ByteUtils.byte2HexString(data);
   }
   
   /**
    * 解密
    * @param password 密文
    * @param key 密钥
    * @return 明文
    * @throws Exception 如果传入的密文有误，则会抛出异常
    */
   public static String decrypt(String password,String key) throws Exception{
	   byte[] data = decrypt(ByteUtils.hexString2Byte(password), key.getBytes());
	   return new String(data);
   }
  
}
