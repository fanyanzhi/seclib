package cn.seclib.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Common {
	private static final Logger logger = Logger.getLogger(cn.seclib.util.Common.class);
	
	public static String mergeTime(String str1, String str2) throws Exception {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = sdf1.parse(str1);
		Date d2 = sdf1.parse(str2);

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日(EEEE) HH:mm", Locale.CHINA);
		String s1 = sdf2.format(d1);
		String s2 = sdf2.format(d2);
		String[] sp = s2.split(" ");

		return s1 + "-" + sp[1];
	}

	public static String mergeTime(Date d1, Date d2) throws Exception {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日(EEEE) HH:mm", Locale.CHINA);
		String s1 = sdf2.format(d1);
		String s2 = sdf2.format(d2);
		String[] sp = s2.split(" ");

		return s1 + "-" + sp[1];
	}

	public static String GetDateTime() {
		return GetDateTime("yyyy-MM-dd HH:mm:ss");
	}

	public static String GetDate() {
		return GetDateTime("yyyy-MM-dd");
	}

	public static String GetDateTime(String Formatter) {
		SimpleDateFormat format = new SimpleDateFormat(Formatter);
		String strDate = format.format(new Date());
		return strDate;
	}

	public static String GetDateTime(long SpanTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = format.format(new Date().getTime() + SpanTime);
		return strDate;
	}

	public static String ConvertToDateTime(String DateTime) {
		return ConvertToDateTime(DateTime, "yyyy-MM-dd HH:mm:ss");
	}

	public static String ConvertToDateTime(String DateTime, long SpanTime) {
		return ConvertToDateTime(DateTime, "yyyy-MM-dd HH:mm:ss", SpanTime);
	}

	public static String ConvertToDateTime(String DateTime, String Formatter) {
		return ConvertToDateTime(DateTime, Formatter, 0);
	}

	public static String ConvertToDateTime(String DateTime, String Formatter, long SpanTime) {
		SimpleDateFormat format = new SimpleDateFormat(Formatter);
		String strDate = null;
		try {
			strDate = format.format(format.parse(DateTime).getTime() + SpanTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			format = null;
		}
		return strDate;
	}

	public static Date ConvertToDate(String DateTime, String Formatter) {
		if (IsNullOrEmpty(DateTime)) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(Formatter);
		Date date;
		try {
			date = format.parse(DateTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return date;
	}

	public static long GetTimeDiff(String StartTime, String EndTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long diff = 0;
		try {
			Date d1 = format.parse(StartTime);
			Date d2 = format.parse(EndTime);
			diff = d2.getTime() - d1.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diff;
	}

	private static Object[] GetEncryptKey(String PassWord) {
		String strMD5 = EnCodeMD5(PassWord);
		byte[] arrKey = new byte[8];
		byte[] arrIV = new byte[8];
		for (int i = 0; i < 8; i++) {
			arrKey[i] = (byte) strMD5.charAt(i * 4);
			arrIV[i] = (byte) strMD5.charAt(i * 4 + 2);
		}
		try {
			DESKeySpec keySpec = new DESKeySpec(arrKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			return new Object[] { keyFactory.generateSecret(keySpec), new IvParameterSpec(arrIV) };
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] EncryptData(byte[] Data, String PassWord) {
		byte[] arrData = null;
		Cipher cipher;
		Object[] arrKey = GetEncryptKey(PassWord);
		if (arrKey == null) {
			return null;
		}
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, (Key) arrKey[0], (IvParameterSpec) arrKey[1]);
			arrData = cipher.doFinal(Data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return arrData;
	}

	public static String EncryptData(String Data, String PassWord) {
		byte[] arrData = null;
		try {
			arrData = Data.getBytes("utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		byte[] arrDataRet = EncryptData(arrData, PassWord);
		BASE64Encoder base64 = new BASE64Encoder();
		String strData = base64.encode(arrDataRet).replace("\r", "").replace("\n", "");
		base64 = null;
		return strData;
	}

	public static byte[] DecryptData(byte[] Data, String PassWord) {
		byte[] arrData = null;
		Cipher cipher;
		Object[] arrKey = GetEncryptKey(PassWord);
		if (arrKey == null) {
			return null;
		}
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, (Key) arrKey[0], (IvParameterSpec) arrKey[1]);
			arrData = cipher.doFinal(Data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return arrData;
	}

	public static String DecryptData(String Data, String PassWord) {
		if (IsNullOrEmpty(Data)) {
			return null;
		}
		byte[] arrData = null;
		BASE64Decoder base64 = new BASE64Decoder();
		try {
			arrData = base64.decodeBuffer(Data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		arrData = DecryptData(arrData, PassWord);
		base64 = null;
		if (arrData == null) {
			return null;
		}
		return new String(arrData);
	}

	public static String EnCodeMD5(String Data) {
		byte[] arrData = null;
		try {
			arrData = Data.getBytes("utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return EnCodeMD5(arrData);
	}

	public static String EnCodeMD5(byte[] Data) {

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(Data);
			Data = md5.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder sbData = new StringBuilder(32);
		String strTemp = "";
		for (int i = 0; i < Data.length; i++) {
			strTemp = Integer.toHexString(Data[i] & 0XFF);
			if (strTemp.length() == 1) {
				sbData.append("0").append(strTemp);
			} else {
				sbData.append(strTemp);
			}
		}
		return sbData.toString();
	}

	public static boolean IsNullOrEmpty(String arg) {
		return arg == null || arg.isEmpty();
	}

	public static Object CovertToObject(Object value, Class<?> type) {
		if (value.getClass().equals(type)) {
			return value;
		}
		if (type.getName().equals("java.lang.String") || type.getName().equals("String")) {
			return String.valueOf(value);
		}
		if (type.getName().equals("java.lang.Integer") || type.getName().equals("int")) {
			return Integer.parseInt(String.valueOf(value));
		}
		if (type.getName().equals("java.lang.Long") || type.getName().equals("long")) {
			return Long.parseLong(String.valueOf(value));
		}

		if (type.getName().equals("java.lang.Short")) {
			return Short.parseShort(String.valueOf(value));
		}
		if (type.getName().equals("java.lang.Double")) {
			return Double.parseDouble(String.valueOf(value));
		}
		if (type.getName().equals("java.lang.Boolean") || type.getName().equals("boolean")) {
			if (value == null || value.toString().length() == 0) {
				return false;
			}
			if (value.toString().equalsIgnoreCase("false") || value.toString().equalsIgnoreCase("true")) {
				return (Boolean) value;
			} else {
				if (Integer.parseInt(String.valueOf(value)) > 0) {
					return true;
				} else {
					return false;
				}
			}
		}
		if (type.getName().equals("java.util.Date")) {
			return (java.util.Date) value;
		}
		return value;
	}

	/**
	 * @param arg0
	 *            - original string
	 * @param arg1
	 *            - starts string or ends string
	 */
	public static String Trim(String arg0, String arg1) {
		int iLen = arg1.length();
		while (arg0.startsWith(arg1)) {
			arg0 = arg0.substring(iLen, arg0.length());
		}
		while (arg0.endsWith(arg1)) {
			arg0 = arg0.substring(0, arg0.length() - iLen);
		}

		return arg0;
	}

	public static String getBase64Password(String strVal, String PassWord) {
		int n, nKeyLen;
		byte[] pbSrc = strVal.getBytes();
		byte[] pbKey = PassWord.getBytes();
		n = pbSrc.length;
		nKeyLen = pbKey.length;
		byte[] pDest = new byte[n];
		for (int i = 0; i < n; i++) {
			int j = i % nKeyLen;
			pDest[i] = (byte) (pbSrc[i] ^ pbKey[j]);
		}
		// String sResult = Base64.encode(new String(pDest));
		String sResult = base64Encode(new String(pDest));
		return sResult;
	}

	public static String getPasswordFromBase64(String strVal, String PassWord) {
		int n, nKeyLen;
		byte[] pbSrc = base64Decode(strVal).getBytes();
		byte[] pbKey = PassWord.getBytes();
		n = pbSrc.length;
		nKeyLen = pbKey.length;
		byte[] pDest = new byte[n];
		for (int i = 0; i < n; i++) {
			int j = i % nKeyLen;
			pDest[i] = (byte) (pbSrc[i] ^ pbKey[j]);
		}
		return new String(pDest);
	}

	public static String SHA1(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String toHexString(byte[] keyData) {
		if (keyData == null) {
			return null;
		}
		int expectedStringLen = keyData.length * 2;
		StringBuilder sb = new StringBuilder(expectedStringLen);
		for (int i = 0; i < keyData.length; i++) {
			String hexStr = Integer.toString(keyData[i] & 0x00FF, 16);
			if (hexStr.length() == 1) {
				hexStr = "0" + hexStr;
			}
			sb.append(hexStr);
		}
		return sb.toString();
	}

	public static JSONObject getSearchData(String Url, String token) throws IOException {
		URL searchUrl = new URL(Url);
		HttpURLConnection http = (HttpURLConnection) searchUrl.openConnection();
		http.addRequestProperty("Authorization", "Bearer " + token);
		// http.addRequestProperty("User-Agent", "your agent");
		http.setRequestMethod("GET");

		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		http.setUseCaches(false);
		http.setDoInput(true);
		if (http.getResponseCode() != 200) {
			return null;
		}
		InputStream input = http.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		StringBuilder sbData = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sbData.append(line);
		}
		return JSONObject.fromObject(sbData.toString());
	}

	public static String getXmlSearchData(String Url, String token) throws IOException {

		URL searchUrl = new URL(Url);
		HttpURLConnection http = (HttpURLConnection) searchUrl.openConnection();
		http.addRequestProperty("Authorization", "Bearer " + token);
		// http.addRequestProperty("User-Agent", "your agent");
		http.setRequestMethod("GET");

		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		http.setUseCaches(false);
		http.setDoInput(true);
		if (http.getResponseCode() != 200) {
			return null;
		}
		InputStream input = http.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		StringBuilder sbData = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sbData.append(line);
		}
		// XmlReader xmlReader = XmlReader.Read(sbData.toString().getBytes());
		return sbData.toString();
	}

	public static byte[] base64DecodeStr(String s) {
		if (IsNullOrEmpty(s)) {
			return null;
		}
		byte[] arrData = null;
		BASE64Decoder base64 = new BASE64Decoder();
		try {
			arrData = base64.decodeBuffer(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arrData;
	}

	public static String base64Decode(String s) {
		if (IsNullOrEmpty(s)) {
			return null;
		}
		byte[] arrData = null;
		String strData = null;
		BASE64Decoder base64 = new BASE64Decoder();
		try {
			arrData = base64.decodeBuffer(s);
			strData = new String(arrData, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strData;
	}

	public static String base64Encode(String s) {
		if (IsNullOrEmpty(s)) {
			return null;
		}
		String strData = null;
		BASE64Encoder base64 = new BASE64Encoder();
		try {
			strData = base64.encode(s.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strData;
	}

	public static String getCRC32(byte[] arg) {
		CRC32 crc = new CRC32();
		ByteArrayInputStream input = new ByteArrayInputStream(arg);
		CheckedInputStream checkedInput = new CheckedInputStream(input, crc);
		try {
			while (checkedInput.read() != -1) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				checkedInput.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Long.toHexString(crc.getValue());
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^[1-9]+[0-9]*$");
		return pattern.matcher(str).matches();
	}

	public static String getLocalAddr() {
		Enumeration<NetworkInterface> netInterfaces = null;
		InetAddress address = null;
		String strAddr = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					address = ips.nextElement();
					if (address instanceof Inet4Address) {
						strAddr = address.getHostAddress();
						if (!"127.0.0.1".equals(strAddr)) {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strAddr;
	}

	public static long getMilliSeconds(String DateTime) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	public static String getDateTime(long MilliSeconds) {
		Date date = new Date(MilliSeconds);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 把IP地址转化为int
	 * 
	 * @param ipAddr
	 * @return int
	 */
	public static int ipToBytesByReg(String ipAddr) {
		byte[] ret = new byte[4];
		try {
			String[] ipArr = ipAddr.split("\\.");
			ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
			return bytesToInt(ret);
		} catch (Exception e) {
			throw new IllegalArgumentException(ipAddr + " is invalid IP");
		}
	}

	private static int bytesToInt(byte[] bytes) {
		int addr = bytes[3] & 0xFF;
		addr |= ((bytes[2] << 8) & 0xFF00);
		addr |= ((bytes[1] << 16) & 0xFF0000);
		addr |= ((bytes[0] << 24) & 0xFF000000);
		return addr;
	}

	public static void requestUrl(String Url) {
		if (IsNullOrEmpty(Url)) {
			return;
		}
		URL url;
		HttpURLConnection http = null;
		try {
			url = new URL(Url);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			if (http.getResponseCode() != 200) {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			http.disconnect();
		}

	}

	public static String getClientIP(HttpServletRequest request) {
		if (request == null) {
			return "";
		}
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String transform(String content) {
		if (Common.IsNullOrEmpty(content)) {
			return "";
		}
		content = content.replaceAll("&", "&amp;");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(" ", "&nbsp;");
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll("\n", "<br>");
		return content;
	}

	/**
	 * 检测邮箱地址是否合法
	 * 
	 * @param email
	 * @return true合法 false不合法
	 */
	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		// Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static long ipToLong(String strIp) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	// 将十进制整数形式转换成127.0.0.1形式的ip地址
	public static String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	public static String sendPost(String url, String id) {
		String str = "";
		HttpClient hc = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(id.toString(), "utf-8"));
			HttpResponse hr = null;
			hr = hc.execute(httpPost);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he);
//				System.out.println(str + "长度是" + str.length());
			}
			httpPost.abort();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hc.getConnectionManager().shutdown();
		}
		return str;
	}

	public static String sendGet(String url, Map<String, String> headers) {
		String str = "";
		HttpClient client = new DefaultHttpClient();
		try {
			HttpGet httpGet = new HttpGet(url);
			if (headers != null && headers.size() > 0) {
				Set<String> keys = headers.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					httpGet.addHeader(key, headers.get(key));
					httpGet.setHeader(key, headers.get(key));
				}
			}

			HttpResponse hr = client.execute(httpGet);
			int code = hr.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity he = hr.getEntity();
				str = EntityUtils.toString(he, "utf-8");
			}
			httpGet.abort();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return str;// .replace("", "").replace("", "");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> JSONObject getJsonStr(Map<String, T> hashMap) {
		JSONObject obj = new JSONObject();
		Iterator<?> iter = hashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			obj.put((String) entry.getKey(), (T) entry.getValue());
		}
		return obj;

	}

	private static byte[] encryptAES(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] decryptAES(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static String encodeAES(String content, String password) {
		byte[] encryptResult = encryptAES(content, password);
		return parseByte2HexStr(encryptResult);
	}

	public static String decodeAES(String content, String password) {
		byte[] decryptFrom = parseHexStr2Byte(content);
		byte[] decryptResult = decryptAES(decryptFrom, password);
		return new String(decryptResult);
	}

	/*
	 * public static String DESEncrypt(String data, String password) { byte[]
	 * arrdata = null; try { arrdata = data.getBytes("utf-8"); } catch
	 * (Exception e) { //Logger.WriteException(e); return null; } byte[]
	 * arrDataRet = DESEncrypt(arrdata, password); String strData =
	 * Base64.getEncoder().encodeToString(arrDataRet); return strData; }
	 */

	private static byte[] DESEncrypt(byte[] datasource, String password) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * public static String DESDecrypt(String data, String password) { byte[]
	 * arrdata = null; try { arrdata = Base64.getDecoder().decode(data); byte[]
	 * arrDataRet = DESDecrypt(arrdata, password); String strData = new
	 * String(arrDataRet); return strData; } catch (Exception e) {
	 * //Logger.WriteException(e); return null; } }
	 */

	public static byte[] DESDecrypt(byte[] src, String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回短时间格式 yyyy-MM-dd
	 */

	public static Date getDateFromString(String strDateTime) {
		Date date = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 hh:mm", Locale.CHINA); // 产生一个指定国家指定长度的日期格式，长度不同，显示的日期完整性也不同
			date = df.parse(strDateTime);
		} catch (Exception e) {
			System.out.println(e);
		}
		return date;
	}

	public static String getAddressByIP(String ip) {
		if (ip.equals("127.0.0.1") || ip.equals("localhost")) {
			return "北京";
		}
		try {
			String value = "";
			URL url = new URL("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(20 * 1000);
			connection.setReadTimeout(20 * 1000);
			connection.setRequestProperty("Content-Type", "application/text");

			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String sLine = "";

			InputStream l_urlStream = connection.getInputStream();
			// 传说中的三层包装！
			BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
			while ((sLine = l_reader.readLine()) != null) {
				value += sLine + "\r\n";
			}
			l_urlStream.close();
			l_reader.close();

			JSONObject j = JSONObject.fromObject(value);
			if (!"-1".equals(j.getString("ret"))) {
				String province = j.getString("province");

				return province;
			} else if (j.getString("ip").startsWith("192.168")) {
				return "北京";
			} else {
				return "北京";
			}

		} catch (Exception e) {
			return "北京";
		}
	}

	/**
	 * 通过ip返回省市
	 * 
	 * @param ip
	 * @return
	 */
	public static String[] getTwoAddressByIP(String ip) {
		String[] res = new String[2];

		if (ip.equals("127.0.0.1") || ip.equals("localhost")) {
			res[0] = "北京";
			res[1] = "北京";
			return res;
		}
		try {
			String value = "";
			URL url = new URL("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(20 * 1000);
			connection.setReadTimeout(20 * 1000);
			connection.setRequestProperty("Content-Type", "application/text");
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String sLine = "";
			InputStream l_urlStream = connection.getInputStream();
			// 传说中的三层包装！
			BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
			while ((sLine = l_reader.readLine()) != null) {
				value += sLine + "\r\n";
			}
			l_urlStream.close();
			l_reader.close();

			JSONObject j = JSONObject.fromObject(value);
			if (!"-1".equals(j.getString("ret"))) {
				String province = j.getString("province");
				String city = j.getString("city");
				res[0] = province;
				res[1] = city;
				return res;
			} else if (j.getString("ip").startsWith("192.168")) {
				res[0] = "北京";
				res[1] = "北京";
				return res;
			} else {
				res[0] = "北京";
				res[1] = "北京";
				return res;
			}

		} catch (Exception e) {
			res[0] = "北京";
			res[1] = "北京";
			return res;
		}
	}

	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static void appendMethod(String fileName, String content) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName + "_" + getCurDate() + ".txt", true);
			writer.write(getCurTime() + content + "\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static String getCurDate() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr = sdf.format(d);
		return dateNowStr;
	}

	private static String getCurTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNowStr = sdf.format(d);
		return dateNowStr;

	}

	/**
	 * httpclient的sendpost
	 * 
	 * @return
	 */
	public static String sendPostForm(String url, com.alibaba.fastjson.JSONObject jsonObj) {
		logger.info("【sendpost的第一步   】 " + jsonObj.toString());
		org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
		PostMethod method = new PostMethod(url);
		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		if(null != jsonObj) {
			for (String key : jsonObj.keySet()) {
                paramList.add(new NameValuePair(key, jsonObj.getString(key)));
            }
		}
		NameValuePair[] array = paramList.toArray(new NameValuePair[paramList.size()]);
		logger.info("【sendpost的第二步   】 " + Arrays.toString(array));
		method.setRequestBody(array);
		String SubmitResult = "";
		try {
			client.executeMethod(method);
			SubmitResult = method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			method.releaseConnection();  
		}
		return SubmitResult;
	}
	
	public static int getIntervalDays(Date fDate, Date oDate) {

		if (null == fDate || null == oDate) {
			return -1;
		}
		long intervalMilli = oDate.getTime() - fDate.getTime();

		return (int) (intervalMilli / (24 * 60 * 60 * 1000));
	}
	
	public static Date getNowDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(0);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}
}
