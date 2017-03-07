package com.mingyu.ices.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http访问工具
 * 
 * @author yuhao
 * @date 20130820
 */
public class HttpUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	private static final int TIME_OUT = 15;

	/**
	 * 通过HTTP GET 发送参数
	 * 
	 * @param httpUrl
	 * @param parameter
	 */
	public static String sendGet(String httpUrl, Map<String, String> parameter) {
		if (parameter == null || httpUrl == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, String>> iterator = parameter.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			if (sb.length() > 0) {
				sb.append('&');
			}
			Entry<String, String> entry = iterator.next();
			String key = entry.getKey();
			String value;
			try {
				value = URLEncoder.encode(entry.getValue(),
						HttpConst.DEFAULT_HTTP_ENCODING);
			} catch (UnsupportedEncodingException e) {
				logger.error("[URLEncoder.encode({},{});]", entry.getValue(),
						HttpConst.DEFAULT_HTTP_ENCODING);
				logger.error("error={}", e);
				value = "";
			}
			sb.append(key).append('=').append(value);
		}
		String urlStr = null;
		if (httpUrl.lastIndexOf('?') != -1) {
			urlStr = httpUrl + '&' + sb.toString();
		} else {
			urlStr = httpUrl + '?' + sb.toString();
		}

		logger.debug("request info={}", urlStr);
		HttpURLConnection httpCon = null;
		String responseBody = null;
		try {
			URL url = new URL(urlStr);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod(HttpConst.HTTP_GET);
			httpCon.setConnectTimeout(TIME_OUT * 1000);
			httpCon.setReadTimeout(TIME_OUT * 1000);
			// 开始读取返回的内容
			InputStream in = httpCon.getInputStream();
			byte[] readByte = new byte[1024];
			// 读取返回的内容
			int readCount = in.read(readByte, 0, 1024);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (readCount != -1) {
				baos.write(readByte, 0, readCount);
				readCount = in.read(readByte, 0, 1024);
			}
			responseBody = new String(baos.toByteArray(),
					HttpConst.DEFAULT_HTTP_ENCODING);
			baos.close();
		} catch (Exception e) {
			logger.error("Http Send POST Failed! url=" + httpUrl, e);
		} finally {
			if (httpCon != null)
				httpCon.disconnect();
		}
		logger.debug("response info={}", responseBody);
		return responseBody;
	}
	
	/**
	 * get方法访问url
	 * 
	 * @param url
	 * @return
	 */
	public static String getMethod(String url) {
		String response = null;
		if (StringUtils.isNotEmpty(url)) {
			// 构造HttpClient的实例
			HttpClient httpClient = new HttpClient();
			// 创建GET方法的实例
			GetMethod getMethod = new GetMethod(url);
			// 使用系统提供的默认的恢复策略
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			try {
				// 执行getMethod
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode == HttpStatus.SC_OK) {
					// 读取内容
					byte[] responseBody = getMethod.getResponseBody();
					// 处理内容
					response = new String(responseBody);
				} else {
					System.err.println("Method failed: "
							+ getMethod.getStatusLine());
				}

			} catch (HttpException e) {
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				System.out.println("Please check your provided http address!");
				e.printStackTrace();
			} catch (IOException e) {
				// 发生网络异常
				e.printStackTrace();
			} finally {
				// 释放连接
				getMethod.releaseConnection();
			}
		}
		System.out.println("response:" + response);
		return response;
	}

	/**
	 * 使用HTTP POST 发送文本
	 * 
	 * @param httpUrl
	 *            发送的地址
	 * @param postBody
	 *            发送的内容
	 * @return 返回HTTP SERVER的处理结果,如果返回null,发送失败
	 */
	public static String sentPost(String httpUrl, String postBody) {
		return sentPost(httpUrl, postBody, HttpConst.DEFAULT_HTTP_ENCODING,
				null);
	}

	/**
	 * 使用HTTP POST 发送文本
	 * 
	 * @param httpUrl
	 *            发送的地址
	 * @param postBody
	 *            发送的内容
	 * @return 返回HTTP SERVER的处理结果,如果返回null,发送失败
	 */
	public static String sentPost(String httpUrl, String postBody,
			String encoding) {
		return sentPost(httpUrl, postBody, encoding, null);
	}

	/**
	 * 使用HTTP POST 发送文本
	 * 
	 * @param httpUrl
	 *            目的地址
	 * @param postBody
	 *            post的包体
	 * @param headerMap
	 *            增加的Http头信息
	 * @return
	 */
	public static String sentPost(String httpUrl, String postBody,
			Map<String, String> headerMap) {
		return sentPost(httpUrl, postBody, HttpConst.DEFAULT_HTTP_ENCODING,
				headerMap);
	}

	/**
	 * 使用HTTP POST 发送文本
	 * 
	 * @param httpUrl
	 *            发送的地址
	 * @param postBody
	 *            发送的内容
	 * @param encoding
	 *            发送的内容的编码
	 * @param headerMap
	 *            增加的Http头信息
	 * @return 返回HTTP SERVER的处理结果,如果返回null,发送失败
	 */
	public static String sentPost(String httpUrl, String postBody,
			String encoding, Map<String, String> headerMap) {
		HttpURLConnection httpCon = null;
		String responseBody = null;
		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		try {
			httpCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		if (httpCon == null) {
			logger.error("Http Send POST Failed! url={},httpCon == null",
					httpUrl);
			return null;
		}
		httpCon.setDoOutput(true);
		httpCon.setConnectTimeout(TIME_OUT * 1000);
		httpCon.setReadTimeout(TIME_OUT * 1000);
		httpCon.setDoOutput(true);
		httpCon.setUseCaches(false);
		httpCon.setRequestProperty("Content-Type", "application/json");
		try {
			httpCon.setRequestMethod(HttpConst.HTTP_POST);
		} catch (ProtocolException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		if (headerMap != null) {
			Iterator<Entry<String, String>> iterator = headerMap.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				httpCon.addRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		OutputStream output;
		try {
			output = httpCon.getOutputStream();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		try {
			output.write(postBody.getBytes(encoding));
		} catch (UnsupportedEncodingException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		try {
			output.flush();
			output.close();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}

		if (logger.isDebugEnabled()) {
			int code = -1;
			String result = null;
			try {
				code = httpCon.getResponseCode();
				result = httpCon.getResponseMessage();
			} catch (IOException e) {
				logger.error("Http Send POST Failed! url={},error={}", httpUrl,
						e);
			}
			logger.debug("Http URL = {}", httpUrl);
			logger.debug("code={}", code);
			logger.debug("ResponseMsg={}", result);
		}

		// 开始读取返回的内容
		InputStream in;
		try {
			in = httpCon.getInputStream();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		/**
		 * 这个方法可以在读写操作前先得知数据流里有多少个字节可以读取。 需要注意的是，如果这个方法用在从本地文件读取数据时，一般不会遇到问题，
		 * 但如果是用于网络操作，就经常会遇到一些麻烦。
		 * 比如，Socket通讯时，对方明明发来了1000个字节，但是自己的程序调用available()方法却只得到900，或者100，甚至是0，
		 * 感觉有点莫名其妙，怎么也找不到原因。 其实，这是因为网络通讯往往是间断性的，一串字节往往分几批进行发送。
		 * 本地程序调用available()方法有时得到0，这可能是对方还没有响应，也可能是对方已经响应了，但是数据还没有送达本地。
		 * 对方发送了1000个字节给你，也许分成3批到达，这你就要调用3次available()方法才能将数据总数全部得到。
		 * 
		 * 经常出现size为0的情况，导致下面readCount为0使之死循环(while (readCount != -1)
		 * {xxxx})，出现死机问题
		 */
		int size = 0;
		try {
			size = in.available();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		if (size == 0) {
			size = 1024;
		}
		byte[] readByte = new byte[size];
		// 读取返回的内容
		int readCount = -1;
		try {
			readCount = in.read(readByte, 0, size);
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (readCount != -1) {
			baos.write(readByte, 0, readCount);
			try {
				readCount = in.read(readByte, 0, size);
			} catch (IOException e) {
				logger.error("Http Send POST Failed! url={},error={}", httpUrl,
						e);
				return null;
			}
		}
		try {
			responseBody = new String(baos.toByteArray(), encoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e);
			return null;
		} finally {
			if (httpCon != null) {
				httpCon.disconnect();
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					logger.error("Http disconnect 失败!!!! url={},error={}",
							httpUrl, e);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("返回信息={}", responseBody);
		}
		return responseBody;
	}

	/**
	 * 使用HTTP POST 发送文本
	 * 
	 * @param httpUrl
	 *            发送的地址 不为空
	 * @param postBody
	 *            发送的内容 不为空
	 * @param sign
	 *            发送的密钥签名 不为空
	 * @return 返回HTTP SERVER的处理结果,如果返回null,发送失败
	 */
	public static String sentPostBySign(String httpUrl, String postBody,
			String key) {
		return sentPostBySign(httpUrl, postBody,
				HttpConst.DEFAULT_HTTP_ENCODING, key);
	}

	/**
	 * 使用HTTP POST 发送文本
	 * 
	 * @param httpUrl
	 *            发送的地址 不为空
	 * @param postBody
	 *            发送的内容 不为空
	 * @param encoding
	 *            发送的内容的编码 不为空
	 * @param sign
	 *            发送的密钥签名 不为空
	 * @return 返回HTTP SERVER的处理结果,如果返回null,发送失败
	 */
	public static String sentPostBySign(String httpUrl, String postBody,
			String encoding, String key) {
		HttpURLConnection httpCon = null;
		String responseBody = null;
		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		try {
			httpCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		if (httpCon == null) {
			logger.error("Http Send POST Failed! url={},httpCon is null!!!",
					httpUrl);
			return null;
		}
		httpCon.setConnectTimeout(TIME_OUT * 1000);
		httpCon.setReadTimeout(TIME_OUT * 1000);
		httpCon.setDoOutput(true);
		httpCon.setUseCaches(false);
		// 请求签名
		String mkSign = EncryptUtil.MD5(key + postBody + key);
		if (StringUtils.isEmpty(mkSign)) {
			logger.error("String mkSign = MD5.md5Digest({});", key + postBody
					+ key);
			logger.error("{},MD5加密失败,导致签名失败!!!", postBody, key + postBody + key);
			return null;
		}
		httpCon.addRequestProperty(HttpConst.BODY_SIGN, mkSign);
		try {
			httpCon.setRequestMethod(HttpConst.HTTP_POST);
		} catch (ProtocolException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		OutputStream output = null;
		try {
			output = httpCon.getOutputStream();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		try {
			output.write(postBody.getBytes(encoding));
		} catch (UnsupportedEncodingException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		try {
			output.flush();
			output.close();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}

		if (logger.isDebugEnabled()) {
			int code = -1;
			String result = null;
			try {
				code = httpCon.getResponseCode();
				result = httpCon.getResponseMessage();
			} catch (IOException e) {
				logger.error("Http Send POST Failed! url={},error={}", httpUrl,
						e);
			}
			logger.debug("Http URL = {}", httpUrl);
			logger.debug("code={}", code);
			logger.debug("ResponseMsg={}", result);
		}

		// 开始读取返回的内容
		InputStream in = null;
		try {
			in = httpCon.getInputStream();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		/**
		 * 这个方法可以在读写操作前先得知数据流里有多少个字节可以读取。 需要注意的是，如果这个方法用在从本地文件读取数据时，一般不会遇到问题，
		 * 但如果是用于网络操作，就经常会遇到一些麻烦。
		 * 比如，Socket通讯时，对方明明发来了1000个字节，但是自己的程序调用available()方法却只得到900，或者100，甚至是0，
		 * 感觉有点莫名其妙，怎么也找不到原因。 其实，这是因为网络通讯往往是间断性的，一串字节往往分几批进行发送。
		 * 本地程序调用available()方法有时得到0，这可能是对方还没有响应，也可能是对方已经响应了，但是数据还没有送达本地。
		 * 对方发送了1000个字节给你，也许分成3批到达，这你就要调用3次available()方法才能将数据总数全部得到。
		 * 
		 * 经常出现size为0的情况，导致下面readCount为0使之死循环(while (readCount != -1)
		 * {xxxx})，出现死机问题
		 */
		int size = 0;
		try {
			size = in.available();
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		if (size == 0) {
			size = 1024;
		}
		byte[] readByte = new byte[size];
		// 读取返回的内容
		int readCount;
		try {
			readCount = in.read(readByte, 0, size);
		} catch (IOException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (readCount != -1) {
			baos.write(readByte, 0, readCount);
			try {
				readCount = in.read(readByte, 0, size);
			} catch (IOException e) {
				logger.error("Http Send POST Failed! url={},error={}", httpUrl,
						e);
				return null;
			}
		}
		try {
			responseBody = new String(baos.toByteArray(), encoding);
		} catch (UnsupportedEncodingException e1) {
			logger.error("Http Send POST Failed! url={},error={}", httpUrl, e1);
			return null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					logger.error("Http disconnect 失败!!!! url={},error={}",
							httpUrl, e);
				}
			}
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("返回信息={}", responseBody);
		}
		// 回调响应签名
		String rKey = httpCon.getHeaderField(HttpConst.BODY_SIGN);
		if (StringUtils.isEmpty(rKey)) {
			logger.error("String rKey = httpCon.getHeaderField(HttpConst.BODY_SIGN);");
			logger.error("{},获取网络签名为空,导致失败!!!", responseBody);
			return null;
		} else {
			String rmkSign = EncryptUtil.MD5(key + responseBody + key);
			if (StringUtils.isEmpty(rmkSign)) {
				logger.error("String rmkSign = MD5.md5Digest({});", key
						+ responseBody + key);
				logger.error("{},MD5({})失败,导致数据签名失败!!!", responseBody, key
						+ responseBody + key);
				return null;
			}
			if (rmkSign.trim().equals(rKey.trim())) {
				if (logger.isDebugEnabled()) {
					logger.debug("{},签名成功!!!", responseBody);
				}
			} else {
				logger.error("{}!!!", responseBody);
				logger.error("由于数据可能被撰写,导致数据签名失败!!!网络sign={},本地网络sign={}",
						rKey, rmkSign);
				return null;
			}
		}
		return responseBody;
	}

	/** 
	 * 获取访问用户的客户端IP（适用于公网与局域网）. 
	 */
	public static final String getIpAddr(final HttpServletRequest request)  
	        throws Exception {  
	    if (request == null) {  
	        throw (new Exception("getIpAddr method HttpServletRequest Object is null"));  
	    }  
	    String ipString = request.getHeader("x-forwarded-for");  
	    if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {  
	        ipString = request.getHeader("Proxy-Client-IP");  
	    }  
	    if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {  
	        ipString = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {  
	        ipString = request.getRemoteAddr();  
	    }  
	      
	    // 多个路由时，取第一个非unknown的ip  
	    final String[] arr = ipString.split(",");  
	    for (final String str : arr) {  
	        if (!"unknown".equalsIgnoreCase(str)) {  
	            ipString = str;  
	            break;  
	        }  
	    }  
	      
	    return ipString;  
	}
	
	/**
	 * nginx或apache 重定向了的取值
	* @author robinsen
	* @version 创建时间：2015年9月17日 下午12:23:56
	* @param request
	* @return
	 */
	public static String getRealIp(HttpServletRequest request) {
		String ip = null;
		if (request != null) {
			ip = request.getHeader("X-Real-IP");
		}
		return ip;
	}
	
	/**
	 * 取request中的body
	 * 
	 * @param request
	 * @return
	 */
	public static String getBodyString(HttpServletRequest request) {

		String inputLine;
		StringBuffer str = new StringBuffer();
		BufferedReader br = null;
		try {
			br = request.getReader();
			while ((inputLine = br.readLine()) != null) {
				str.append(inputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				br = null;
				e.printStackTrace();
			}
		}
		return str.toString();
	}
	
	public static void main(String[] args) {
		JSONObject jo = new JSONObject();
		jo.put("account","张三");
		jo.put("id","123456778");
		HttpUtils.sentPost("http://127.0.0.1:8080/userLogin",jo.toString());

	// bd0517968eca3989de83d00dd13cde5f
												// sb.append(version).append(merID).append(payMoney).append(orderId).append(returnUrl).append(cardInfo).append("").append("1").append(privateKey);
												// try {
		// String t =
		// URLEncoder.encode("RdGy/mh3hhdwVPBKfQblxQ+w3w0YEtU1j3k0gJkV1YpCaQgvZcReDw==","utf-8");
		// System.out.println(t);
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// //ok String
		// sign="notify_data=%3Cnotify%3E%3Cpartner%3E2088701364541757%3C%2Fpartner%3E%3Cdiscount%3E0.00%3C%2Fdiscount%3E%3Cpayment_type%3E1%3C%2Fpayment_type%3E%3Csubject%3E%E6%80%AA%E7%89%A9%E5%90%83%E7%B3%96%E6%9E%9C%3C%2Fsubject%3E%3Ctrade_no%3E2012010464821875%3C%2Ftrade_no%3E%3Cbuyer_email%3E13147099771%3C%2Fbuyer_email%3E%3Cgmt_create%3E2012-01-04+11%3A44%3A19%3C%2Fgmt_create%3E%3Cquantity%3E1%3C%2Fquantity%3E%3Cout_trade_no%3E03112010411151850018_100212000945%3C%2Fout_trade_no%3E%3Cseller_id%3E2088701364541757%3C%2Fseller_id%3E%3Ctrade_status%3ETRADE_FINISHED%3C%2Ftrade_status%3E%3Cis_total_fee_adjust%3EN%3C%2Fis_total_fee_adjust%3E%3Ctotal_fee%3E0.01%3C%2Ftotal_fee%3E%3Cgmt_payment%3E2012-01-04+11%3A44%3A19%3C%2Fgmt_payment%3E%3Cseller_email%3Eyyhpay%40appchina.com%3C%2Fseller_email%3E%3Cgmt_close%3E2012-01-04+11%3A44%3A19%3C%2Fgmt_close%3E%3Cprice%3E0.01%3C%2Fprice%3E%3Cbuyer_id%3E2088702562178750%3C%2Fbuyer_id%3E%3Cuse_coupon%3EN%3C%2Fuse_coupon%3E%3C%2Fnotify%3E&sign=VAQYdhQzOld6jZb0QrCg16GNLMuutgTAiVLCUWc5qd5ebl5C3zdnVe3KCtLiEcD0EYzLbVVp%2B%2FATFLinqUf5uzyBOUXuEVU6ceIjLXc2C6rHARaFRDmrFJe4CPCYSB%2FO2NPCqPEesMtCOIyJPv5trX23KTeYWBzJdxnhr%2BnEyUk%3D";
		// String sign =
		// "sign=ODK%2Fux5nuv1MnZp10%2FmbnWGfh4BegNEjQpxtwMNKCkuZ4q0bS89q6Eelz%2F1enqMF7pRW4Ls0CpSzONI%2FwX9jqoFT0Evpf2lYuwmbrSJS2%2F3wsVpdjEP1Ym%2BxKulhM0CRqMLl%2BA9Ij6M010Go7r9qzx08XH9Qatst0Lb%2FxCaRtnY%3D&sign_type=RSA&notify_data=%3Cnotify%3E%3Cseller_email%3Eyyhpay%40appchina.com%3C%2Fseller_email%3E%3Cpartner%3E2088701364541757%3C%2Fpartner%3E%3Cpayment_type%3E1%3C%2Fpayment_type%3E%3Cbuyer_email%3E13147099771%3C%2Fbuyer_email%3E%3Ctrade_no%3E2012010467763575%3C%2Ftrade_no%3E%3Cbuyer_id%3E2088702562178750%3C%2Fbuyer_id%3E%3Cquantity%3E1%3C%2Fquantity%3E%3Ctotal_fee%3E0.01%3C%2Ftotal_fee%3E%3Cuse_coupon%3EN%3C%2Fuse_coupon%3E%3Cis_total_fee_adjust%3EY%3C%2Fis_total_fee_adjust%3E%3Cprice%3E0.01%3C%2Fprice%3E%3Cgmt_create%3E2012-01-04+14%3A15%3A14%3C%2Fgmt_create%3E%3Cout_trade_no%3E00012022713281500001%3C%2Fout_trade_no%3E%3Cseller_id%3E2088701364541757%3C%2Fseller_id%3E%3Csubject%3E%E6%80%AA%E7%89%A9%E5%90%83%E7%B3%96%E6%9E%9C%3C%2Fsubject%3E%3Ctrade_status%3ETRADE_FINISHED%3C%2Ftrade_status%3E%3Cdiscount%3E0.00%3C%2Fdiscount%3E%3C%2Fnotify%3E";
		// sentPost("http://192.168.0.143:8080/alipay/payment/notifyCallBackUrl",
		// sign, "UTF-8");

		String sign = "privateField=00012022713281500001&payResult=0&cardMoney=11";
		for (int i = 0; i < 10000; i++) {
			String result = sentPost("http://192.168.0.82:7771/wen", sign,
					"UTF-8");
			System.out.println(i + "---" + result);
		}

	}
}
