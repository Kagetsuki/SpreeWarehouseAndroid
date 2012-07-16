package org.genshin.spree;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.warehouse.Warehouse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionStatus extends SpreeConnector {
	protected boolean connected;
	protected String status;
	
	private void isConnected() {
		ConnectivityManager cm = (ConnectivityManager) Warehouse.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			connected = true;
			testAuth();
			return;
		}
		
		connected = false;
	}
	
	public ConnectionStatus(Context ctx) {
		super(ctx);
		connected = false;
	}
	
	private void testAuth() {
		int statusCode = 0;
		
		HttpGet getter = getGetter("");
		
		try {
			HttpResponse response;
			response = getHttpClient().execute(getter);
			StatusLine statusLine = response.getStatusLine();
			statusCode = statusLine.getStatusCode();
			response.getStatusLine();
			if (statusCode == 200) {
				status = "OK";
				return;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			status = "ERROR";
			return;
		} catch (IOException e) {
			e.printStackTrace();
			status = "ERROR";
			return;
		}
		
		status = "NOTCONNECTED";
	}
	
	@Override
	protected HttpResponse doInBackground(String... params) {
		isConnected();
		process();
		return null;
	}
}
