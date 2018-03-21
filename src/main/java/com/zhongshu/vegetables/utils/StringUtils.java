package com.zhongshu.vegetables.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 专门用于操作字符串的类
 * @author 李熠
 * @date 2014年9月24日
 * @version 0.1.0
 * @since 0.1.0
 */
public class StringUtils {

	private static char chars[] = {'0','1','2','3','4','5','6','7','8','9'};
	private static int char_length = chars.length;
	
	/**
	 * <p>
	 * 	isEmpty(null) true<br/>
	 * isEmpty("") true<br/>
	 * isEmpty("null") false<br/>
	 * isEmpty(" ") false<br/>
	 * </p>
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return null == str || str.length() == 0;
	}


	/**
	 * <p>
	 * 	isEmpty(null) true<br/>
	 * isEmpty("") true<br/>
	 * isEmpty("null") true<br/>
	 * isEmpty(" ") true<br/>
	 * </p>
	 * @param str
	 * @return
	 */
	public static boolean isStrNull(String str){
		if(str == null) return true;
		if(str.trim().length()==0) return true;
		if("null".equalsIgnoreCase(str.trim()))return true;
		return false;
	}

	public static boolean isNull(Object str){
		if(str==null){
			return true;
		}
		return isStrNull(str.toString());
	}

	public static boolean isNotNull(Object str){
		return !isNull(str);
	}



	/**
	 * <p>
	 * 	isEmpty(null) false<br/>
	 * isEmpty("") false<br/>
	 * isEmpty("null") true<br/>
	 * isEmpty(" ") true<br/>
	 * </p>
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	/**
	 * <p>
	 * 	isEmpty(null) true<br/>
	 * isEmpty("") true<br/>
	 * isEmpty("null") false<br/>
	 * isEmpty(" ") true<br/>
	 * </p>
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str){
		int strLen;
		if(null == str || (strLen = str.length()) == 0){
			return true;
		}
		for(int i = 0;i < strLen;i++){
			if(!Character.isWhitespace(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * <p>
	 * 	isEmpty(null) false<br/>
	 * isEmpty("") false<br/>
	 * isEmpty("null") true<br/>
	 * isEmpty(" ") false<br/>
	 * </p>
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str){
		return !isBlank(str);
	}
	
	/**
     * 判断字符是否为中文字符
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
    	Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);    
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS    
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS    
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A    
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION    
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION    
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {    
          return true;    
        }    
        return false;
    }
    
    /**
     * 将第一个字符转换成大写<br/>
     * 如：abc转换后：Abc,1ab转换后:1ab
     * @param str 传入的字符串
     * @return 转换后的字符串
     */
    public static String capitalize(String str){
    	int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }
    
    /**
     * 生成随机字符串
     * @param length 字符串长度
     * @return
     */
    public static String randomString(int length){
    	StringBuilder builder = new StringBuilder(length);
    	Random random = new Random();
    	for (int i = 0; i < length; i++) {
			builder.append(random.nextInt(char_length));
		}
    	return builder.toString();
    }
    
    public static String uuid(){
    	return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 根据url获得其顶级域名
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public static String getTopDomainWithoutSubdomain(String url)throws MalformedURLException {

		String host = new URL(url).getHost().toLowerCase();// 此处获取值转换为小写
		Pattern pattern = Pattern.compile("[^\\.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");
		Matcher matcher = pattern.matcher(host);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
    
    /**
     * 将list转化为String
     * @param list
     * @param sep
     * @return
     */
    public static String convertList2String(List<Long> list,char sep){
    	StringBuilder builder = new StringBuilder();
    	if(null != list && list.size() > 0){
    		boolean first = true;
    		for (Long item : list) {
				if(first == false){
					builder.append(sep);
				}else{
					first = false;
				}
				builder.append(item);
			}
    	}
    	return builder.toString();
    }
    
    /**
     * 将string转化为list
     * @param str
     * @param sep
     * @return
     */
    public static List<Long> convertString2List(String str,String sep){
    	List<Long> list = new ArrayList<Long>();
    	if(StringUtils.isNotBlank(str)){
    		String strs[] = str.split(sep);
    		for(String item : strs){
    			list.add(Long.parseLong(item));
    		}
    	}
    	return list;
    }
    
    /**
     * 是否在区间内(闭区间)
     * @param value
     * @param start
     * @param end
     * @return
     */
    public static final boolean between(int value,int start,int end){
    	return value >= start && value <= end;
    }
    
    public static boolean isStringInArray(String str, String[] array){
        for (String val:array){
            if(str.equals(val)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * 将大写字母转换成小写字母，
     * 解决空指针问题，且性能比String.toLowerCase()高
     * @param str
     * @return
     */
    public static String toLower(String str){
    	int len = 0;
    	if(null == str || (len = str.length()) == 0){
    		return str;
    	}
    	StringBuilder builder = new StringBuilder();
    	char chars[] = str.toCharArray();
    	for (int i = 0; i < len; i++) {
			char c = chars[i];
			builder.append(toLower(c));
		}
    	return builder.toString();
    }
    
    public static char toLower(char c){
    	return ((c >= 'A') && (c <= 'Z')) ? (char)(c+32) : c;
    }
    
    /**
     * 将小写字母转换成大写字母
     * 解决空指针问题，且性能比String.toUpperCase()高
     * @param str
     * @return
     */
    public static String toUpper(String str){
    	int len = 0;
    	if(null == str || (len = str.length()) == 0){
    		return str;
    	}
    	StringBuilder builder = new StringBuilder();
    	char chars[] = str.toCharArray();
    	for (int i = 0; i < len; i++) {
			char c = chars[i];
			builder.append(toUpper(c));
		}
    	return builder.toString();
    }
    
    public static char toUpper(char c){
    	return ((c >= 'a') && (c <= 'z')) ? (char)(c-32) : c;
    }
    
    public static void main(String[] args) {
    	String str = "XDgdsf35dfASd";
    	long start = System.currentTimeMillis();
    	for (int i = 0; i < 100000; i++) {
			str.toLowerCase();
		}
    	long end = System.currentTimeMillis();
    	System.out.println((end - start)+"ms");
    	start = System.currentTimeMillis();
    	for (int i = 0; i < 100000; i++) {
			toLower(str);
		}
    	end = System.currentTimeMillis();
    	System.out.println((end - start)+"ms");
	}

    
	private StringUtils(){
		throw new AssertionError();
	}
}
