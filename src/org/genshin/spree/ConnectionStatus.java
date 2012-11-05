package org.genshin.spree;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.profiles.Profile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class ConnectionStatus extends SpreeConnector {
	protected boolean connected;
	protected String status;
	Context ctx;
	
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
		this.ctx = ctx;
		connected = false;
	}
	
	public ConnectionStatus(Context ctx, Profile profile) {
		super(ctx, profile);
		this.ctx = ctx;
		connected = false;
	}
	
	private void testAuth() {
		int statusCode = 0;
		
		if (connector == null) {
			status = "NOTCONNECTED";
			return;
		}
		
		HttpGet getter = getGetter("");
		status = "NotConnected";
		
		try {
			HttpResponse response = getHttpClient().execute(getter);
			StatusLine statusLine = response.getStatusLine();
			statusCode = statusLine.getStatusCode();
			response.getStatusLine();
			if (statusCode == 200) {
				status = "OK";
			}
		} catch (ClientProtocolException e) {
			//e.printStackTrace();
			Log.d("ConnectionStatus", "ClientProtocolException");
			status = "Error";
		} catch (IOException e) {
			e.printStackTrace();
<<<<<<< HEAD
			//Log.d("ConnectionStatus", "IOException");
			status = "NotConnected";
		}		
=======
			status = "ERROR";
			return;
		}
		
		status = "NOTCONNECTED";
		connected = false;
>>>>>>> origin/master
	}
	
	@Override
	protected HttpResponse doInBackground(String... params) {
		isConnected();
		process();
		return null;
	}
}
