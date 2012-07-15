package org.genshin.gsa.network;

import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
import org.genshin.spree.RESTConnector.AnyCertSSLSocketFactory;
import org.genshin.warehouse.Warehouse;

import android.content.Context;
import android.os.AsyncTask;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;

public class NetworkTask extends AsyncTask<String, Void, HttpResponse>{
	boolean usePresets;
	private static String server;
	private static String apiKey;
	private static int port;
	private static boolean useHTTPS;
	
	Context ctx;
	
	public NetworkTask(Context ctx) {
		usePresets = true;
		this.ctx = ctx;
	}
	
	public static void Setup(String server, String apiKey, int port, boolean useHTTPS) {
		NetworkTask.server = server;
		NetworkTask.apiKey = apiKey;
		NetworkTask.port = port;
		NetworkTask.useHTTPS = useHTTPS;
	}
	
	private String protocolHeader() {
		String protocol = "http://";
		if (this.port ==  443) {
			protocol = "https://";
		}
		
		return protocol;
	}
	
	public String baseURL() {
		return protocolHeader() + server + ":" + port + "/";
	}

	private HttpClient getHttpClient() {
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
		
		return null;
	}

}
