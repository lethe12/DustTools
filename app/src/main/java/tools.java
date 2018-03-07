package com;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
/*
 * 工具合集
 * */
@SuppressLint({ "SimpleDateFormat", "DefaultLocale" }) public class tools {
		
	final static byte[] crc16_tab_h = { (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0,  
	        (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,  
	        (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,  
	        (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,  
	        (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40 };  
	
	final static byte[] crc16_tab_l = { (byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04, (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8, (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC, (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12,  
	        (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10, (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4, (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38, (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C, (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7,  
	        (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0, (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4, (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68, (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C, (byte) 0xB4, (byte) 0x74,  
	        (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0, (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54, (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98, (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D,  
	        (byte) 0x4C, (byte) 0x8C, (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40 };  
	
	/** 
	 * 计算CRC16校验 
	 *  
	 * @param data 
	 *            需要计算的数组 
	 * @return CRC16校验值 
	 */  
	public static int calcCrc16(byte[] data) {  
	    return calcCrc16(data, 0, data.length);  
	}  
	
	/** 
	 * 计算CRC16校验 
	 *  
	 * @param data 
	 *            需要计算的数组 
	 * @param offset 
	 *            起始位置 
	 * @param len 
	 *            长度 
	 * @return CRC16校验值 
	 */  
	public static int calcCrc16(byte[] data, int offset, int len) {  
	    return calcCrc16(data, offset, len, 0xffff);  
	}

	/**
	 *
	 * @param data
	 * @param offset
	 * @param len
	 */
	public static void addCrc16(byte[] data,int offset,int len){
		int crc;
		byte [] crcBuff=new byte[2];
		if(data.length >= (offset+len+2)){
			crc = calcCrc16(data,offset,len);
		}else {
			crc = calcCrc16(data,offset,(data.length-offset - 2));
		}
		crcBuff = int2byte(crc);
		data[offset+len] = crcBuff[0];
		data[offset+len+1] = crcBuff[1];
	}
	
	/** 
	 * 计算CRC16校验 
	 *  
	 * @param data 
	 *            需要计算的数组 
	 * @param offset 
	 *            起始位置 
	 * @param len 
	 *            长度 
	 * @param preval 
	 *            之前的校验值 
	 * @return CRC16校验值 
	 */  
	public static int calcCrc16(byte[] data, int offset, int len, int preval) {  
	    int ucCRCHi = (preval & 0xff00) >> 8;  
	    int ucCRCLo = preval & 0x00ff;  
	    int iIndex;  
	    for (int i = 0; i < len; ++i) {  
	        iIndex = (ucCRCLo ^ data[offset + i]) & 0x00ff;  
	        ucCRCLo = ucCRCHi ^ crc16_tab_h[iIndex];  
	        ucCRCHi = crc16_tab_l[iIndex];  
	    }  
	   // return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;  
	    return (ucCRCHi & 0x00ff) | ((ucCRCLo & 0x00ff) << 8)& 0xffff;
	} 

	public tools() {
		// TODO 自动生成的构造函数存根
	}
	
	/**
	 * 隐藏导航栏
	 */
	public static void hide_bar() {
		try {
			String ProcID = "79";
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				ProcID = "42"; // ICS
			// 需要root 权限
			Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "service call activity " + ProcID + " s16 com.android.systemui" }); // WAS
			proc.waitFor();
		} catch (Exception ex) {
			//Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 展现导航栏
	 */
	public static void show_bar() {
		try {
			Process proc = Runtime.getRuntime().exec(new String[] { "am", "startservice", "-n", "com.android.systemui/.SystemUIService" });
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
	
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
	
	public static String bytesToHexString(byte[] src, int size) {
		String ret = "";
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(src[i] & 0xFF);
			// Log.i(TAG, hex);
			if (hex.length() < 2) {
				hex = "0" + hex;
			}
			hex += "";
			ret += hex;
		}
		return ret.toUpperCase();
	}

	public static String bytesToHexStringWithHex(byte[] src, int size) {
		String ret = "";
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(src[i] & 0xFF);
			// Log.i(TAG, hex);
			if (hex.length() < 2) {
				hex = "0" + hex;
			}
			hex += "";
			ret += hex;
		}
		return ret.toUpperCase();
	}
	
	/*
	 * now 当前时间
	 * plan 计划下次测量时间
	 * interval 测量间隔
	 * */
	
	public static long calcNextTime(long now, long plan,long interval) {
		long res;
		res = plan;
		while(now > res){
			res = res + interval;
		}
		
		return res;
	}

	public static byte[] int2bytes(int i){
		byte[] temp = {0x00,0x00,0x00,0x00};
		temp[3] = (byte) (i&0x000000FF);
		temp[2] = (byte) ((i>>8)&0x000000FF);
		temp[1] = (byte) ((i>>16)&0x000000FF);
		temp[0] = (byte) ((i>>24)&0x000000FF);
		return temp;
	}
	
	/*int 转 成 HEX格式字符转  
	 * 输入 
	 * n 为需要转的 int型变量
	 * wei 为需要转的总得位数，不足位数的前面补0
	 * 输出
	 * 字符串
	 * */

	// int 转 HEX 字符串
	public static String int2hexstr(int n, int wei) {
		String str = Integer.toHexString(n);
		String strfhString = str;
		if (str.length() > wei) {
			strfhString = str.substring(str.length() - wei, str.length());
		}
		else {
			for (int i = 0; i < (wei - str.length()); i++) {
				strfhString = "0" + strfhString;
			}
		}
		return strfhString;
	}
	/*将字符串转化为时间戳
	 * */
	public static long string2timestamp(String string){
		long l=0;
		Date date;
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			date = sDateFormat.parse(string);
			l = date.getTime();
		}
		catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return l;
	}

	/*将字符串转化为时间戳
	 * */
	public static long tcpTimeString2timestamp(String string){
		long l=0;
		Date date;
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			date = sDateFormat.parse(string);
			l = date.getTime();
		}
		catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return l;
	}

	public static String timeStamp2TcpString(long l){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return  sDateFormat.format(new Date(l));
	}

	public static String timeStamp2TcpStringWithoutMs(long l){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return  sDateFormat.format(new Date(l));
	}

	/*将时间戳转化为字符串
	 * */
	public static String timestamp2stringWithSecond(long l){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String string = sDateFormat.format(new Date(l));
		return string;

	}
	
	/*将时间戳转化为字符串
	 * */
	public static String timestamp2string(long l){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String string = sDateFormat.format(new Date(l));
		return string;
				
	}
	/*获取当前时间的时间戳
	 * */
	public static long nowtime2timestamp(){
		java.util.Date currentdate = new java.util.Date();//当前时间
		long n = currentdate.getTime();
		return n;
	}


	
	public static String nowDate2string(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowtimeString = sDateFormat.format(new java.util.Date());
		return nowtimeString;
	}
	/*获取当前时间的字符串
	 * */
	public static String nowtime2string(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowtimeString = sDateFormat.format(new java.util.Date());
		return nowtimeString;
	}

	/*获取当前时间的字符串
	 * */
	public static String nowTime2FileString(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		String nowtimeString = sDateFormat.format(new java.util.Date());
		return nowtimeString;
	}
	/**
	 * 将计算结果 转成字符转串
	 * @param data
	 * @return
	 */
	/*public static String result2String(float data){
		DecimalFormat fnum = new DecimalFormat("##0.00"); //保留小数点后3位
		return fnum.format(data); 
	}*/
	/**
	 * 将float型转换成字符串，保留小数点后3位
	 * @param data
	 * @return
	 */
	public static String float2String3(float data){
		DecimalFormat fnum = new DecimalFormat("##0.000"); //保留小数点后3位
		return fnum.format(data); 
	}
	/**
	 * 将float型转换成字符串，保留小数点后4位
	 * @param data
	 * @return
	 */
	public static String float2String4(float data){
		DecimalFormat fnum = new DecimalFormat("##0.0000"); //保留小数点后3位
		return fnum.format(data); 
	}
	/**
	 * float转字符串，无小数部分
	 * @param data
	 * @return
	 */
	public static String float2String0(float data){
		DecimalFormat fnum = new DecimalFormat("##0"); //保留小数点后3位
		return fnum.format(data); 
	}
	/*
	 * 162. * 通过byte数组取得float 163. * 164. * @param bb 165. * @param index 166. * @return
	 * 167.
	 */
	public static float getFloat(byte[] b, int index) {
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index - 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index - 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index - 3] << 24);
		return Float.intBitsToFloat(l);
	}

	public static float getFloatWithLowByteFirst(byte[] b, int index) {
		int l;
		l = b[index - 3];
		l &= 0xff;
		l |= ((long) b[index - 2] << 8);
		l &= 0xffff;
		l |= ((long) b[index - 1] << 16);
		l &= 0xffffff;
		l |= ((long) b[index] << 24);
		return Float.intBitsToFloat(l);
	}
	
	public static byte[] float2byte (float data) {
		int res = Float.floatToIntBits(data);
		byte[] resbyte=new byte[4];
		resbyte[0] = (byte) (res&0x000000ff);
		resbyte[1] = (byte) ((res>>8)&0x000000ff);
		resbyte[2] = (byte) ((res>>16)&0x000000ff);
		resbyte[3] = (byte) ((res>>24)&0x000000ff);
		return resbyte;
	}

	public static byte[] float2byteBack (float data) {
		int res = Float.floatToIntBits(data);
		byte[] resbyte=new byte[4];
		resbyte[3] = (byte) (res&0x000000ff);
		resbyte[2] = (byte) ((res>>8)&0x000000ff);
		resbyte[1] = (byte) ((res>>16)&0x000000ff);
		resbyte[0] = (byte) ((res>>24)&0x000000ff);
		return resbyte;
	}
	
	public static boolean str2bool(String charSequence) {

		if (charSequence.equals("1")) {
			return true;
		} else if (charSequence.equals("0")) {
			return false;
		} else {
			return false;
		}

	}
	
	public static int byte2int(byte[] data,int offset){
		return ((data[offset] &0x00ff)<<8)| (data[offset+1]&0x00ff)&0xffff;
	}
	
	public static byte[] int2byte(int data){
		byte [] temp = {0x00,0x00};
		temp[1] = (byte) (data & 0x00ff);
		temp[0] = (byte) ((data>>8) & 0x00ff);
		return temp;
	}

}
