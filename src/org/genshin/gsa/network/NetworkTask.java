package org.genshin.gsa.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import android.content.Context;
import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<String, Void, HttpResponse>{
	protected boolean usePresets;
	protected static String server;
	protected static String apiKey;
	protected static int port;
	protected static boolean useHTTPS;
	
	Context ctx;
	
	public NetworkTask(Context ctx) {
		usePresets = true;
		this.ctx = ctx;
		prepare();
	}
	
	public static void Setup(String server, int port, String apiKey) {
		NetworkTask.server = server;
		NetworkTask.apiKey = apiKey;
		NetworkTask.port = port;
		NetworkTask.useHTTPS = isStandardHTTPSPort(port);
	}
	
	public static void Setup(String server, int port, String apiKey, boolean useHTTPS) {
		NetworkTask.server = server;
		NetworkTask.apiKey = apiKey;
		NetworkTask.port = port;
		NetworkTask.useHTTPS = useHTTPS;
	}
	
	public static boolean isStandardHTTPSPort(int port) {
		if (port ==  443)
			return true;
		else
			return false;
	}
	
	private String protocolHeader() {
		if (NetworkTask.useHTTPS)
			return "https://";
		else
			return "http://";
	}
	
	public String baseURL() {
		return protocolHeader() + server + ":" + port + "/";
	}

	protected HttpClient getHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);
	        
			SSLSocketFactory sf = new AnyCertSSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        
	        HttpParams params = new BasicHttpParams();
	        //HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
	        return new DefaultHttpClient();	
		}
	}
	
	public void DisablePresets() {
		usePresets = false;
	}
	
	@Override
	protected HttpResponse doInBackground(String... params) {
		process();
		return null;
	}
	
	@Override
	protected void onPostExecute(HttpResponse result) {
		complete();
	}
	
	public class AnyCertSSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance("TLS");

	    public AnyCertSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };

	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
	
	protected void prepare() {
	}
	
	protected void process() {
	}
	
	protected void complete() {
	}

}
