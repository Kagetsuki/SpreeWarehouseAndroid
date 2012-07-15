package org.genshin.gsa.network;

import org.apache.http.HttpResponse;
import org.genshin.gsa.Dialogs;
import org.genshin.warehouse.Warehouse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionStatus extends NetworkTask{
	protected boolean connected;
	
	private boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) Warehouse.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		
		return false;		
	}
	
	public ConnectionStatus(Context ctx) {
		super(ctx);
		connected = false;
	}
	
	@Override
	protected HttpResponse doInBackground(String... params) {
		if (isConnected())
			Log.d("NETWORK", "CONNECTED");	
		return null;
	}
	
	@Override
	protected void onPostExecute(HttpResponse result) {
		
	}

}
