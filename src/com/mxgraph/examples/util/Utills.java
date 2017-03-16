package com.mxgraph.examples.util;

/**
 * 对字符串操作的工具类
 * @author spark
 *
 */
public class Utills {

	/**
	 * 向后查找离当前位置最近的字符所在的位置
	 * @param src 长字符串
	 * @param current 查找的开始点，从该位置进行查找
	 * @param str 想要查找的短字符串
	 * @return 向后查到的字符串的起始位置
	 */
	public static int indexOfFromCurrent(String src, int current, String str){
		String sub = src.substring(current);
		return sub.indexOf(str) != -1?current+ sub.indexOf(str):-1;
	}
	
	/**
	 * 向前查找离当前位置最近的字符所在的位置
	 * @param src 长字符串
	 * @param current 查找的开始点，从该位置进行查找
	 * @param str 想要查找的短字符串
	 * @return 向前查到的字符串的起始位置
	 */
	public static int indexOfBeforeCurrent(String src, int current, char str){
		String sub = src.substring(0,current+1);
		for(int i = current ; i>0 ; i--){
			if(sub.charAt(i)==str){
				return i;
			}
		}
		return -1;
	}

}
