/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年4月5日上午10:33:49
 *
*/

package com.yuminsoft.cps.pic.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;

/**
 * HTTP请求 <br/>
 * Date: 2017年4月5日 上午10:33:49 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class HttpKit {

	private static String CHARSET = "UTF-8";

	/**
	 * https 域名校验
	 */
	private class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * https 证书管理
	 */
	private class TrustAnyTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}

	private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
	private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpKit().new TrustAnyHostnameVerifier();

	private static SSLSocketFactory initSSLSocketFactory() {
		try {
			TrustManager[] tm = { new HttpKit().new TrustAnyTrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLS"); // ("TLS",
																	// "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * POST 请求. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年4月5日 上午10:37:25
	 * @param url
	 * @param queryParas
	 * @return
	 */
	public String post(String url, Map<String, String> queryParas) {
		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), "POST", null);
			conn.connect();
			//OutputStream out = conn.getOutputStream();
			//out.write(data != null ? data.getBytes(CHARSET) : null);
			//out.flush();
			//out.close();
			return readResponseString(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * 文件上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年4月5日 上午9:23:09
	 */
	public String post(String url, Map<String, String> queryParas, File file) {

		String BOUNDARY = "---------------------------" + UUID.randomUUID().toString().replaceAll("-", ""); // boundary就是request头和上传文件内容的分隔符

		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), "POST", BOUNDARY);
			conn.connect();

			OutputStream out = conn.getOutputStream();

			String filename = file.getName();
			String contentType = Files.probeContentType(file.toPath());

			StringBuffer strBuf = new StringBuffer();
			strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
			strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
			strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
			out.write(strBuf.toString().getBytes());

			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);

			out.flush();
			out.close();

			return readResponseString(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	// 返回值解析
	private static String readResponseString(HttpURLConnection conn) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		try {
			inputStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LogKit.error(e.getMessage(), e);
				}
			}
		}
	}

	// 参数拼接
	private static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
		if (queryParas == null || queryParas.isEmpty())
			return url;

		StringBuilder sb = new StringBuilder(url);
		boolean isFirst;
		if (!url.contains("?")) {
			isFirst = true;
			sb.append("?");
		} else {
			isFirst = false;
		}

		for (Entry<String, String> entry : queryParas.entrySet()) {
			if (isFirst)
				isFirst = false;
			else
				sb.append("&");

			String key = entry.getKey();
			String value = entry.getValue();
			if (StrKit.notBlank(value))
				try {
					value = URLEncoder.encode(value, CHARSET);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			sb.append(key).append("=").append(value);
		}
		return sb.toString();
	}

	// 创建 HttpURLConnection
	private static HttpURLConnection getHttpConnection(String url, String method, String BOUNDARY) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		URL _url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
		if (conn instanceof HttpsURLConnection) {
			((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
		}

		conn.setRequestMethod(method);
		conn.setDoOutput(true);
		conn.setDoInput(true);

		conn.setConnectTimeout(19000);
		conn.setReadTimeout(19000);

		if (null != BOUNDARY)
			conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
		if (null != BOUNDARY)
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		return conn;
	}
}
