package com.annotatedsql.util;

public class TextUtils {

	public static boolean isEmpty(String str){
		return str == null || str.length() == 0;
	} 
	
	public static String capitalize(String str){
		if(str == null || str.length() == 0)
			return str;
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}
	
	public static String var2class(String str){
		if(str == null || str.length() == 0)
			return str;
		String[] split = str.split("_");
		if(split.length > 1){
			StringBuilder builder = new StringBuilder();
			for(String part : split){
				builder.append(capitalize(part));
			}
			return builder.toString();
		}else{
			return str;
		}
	}
}
