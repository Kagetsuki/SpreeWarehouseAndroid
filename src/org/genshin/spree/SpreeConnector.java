package org.genshin.spree;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.profiles.Profile;
import org.genshin.warehouse.profiles.Profiles;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.util.Log;

public class SpreeConnector extends NetworkTask{
	Context ctx;
	public RESTConnector connector;
	private Profile profile;

	public SpreeConnector(Context ctx) {
		super(ctx);
		//set profile
		this.profile = Warehouse.Profiles().selected;
		
		connector = new RESTConnector(ctx);
		connector.setup(profile.server, profile.port, profile.apiKey);
	}

	// Set up the Getter with the API token and proper URL
	protected HttpGet getGetter(String targetURL) {
		HttpGet getter = new HttpGet(baseURL() + targetURL);	
		getter.addHeader("X-Spree-Token", apiKey);
		
		return getter;
	}
	
	public ArrayList<?> getList(String path, Class<?> containerClass) {
		ArrayList<String> collection = new ArrayList<String>();
		
		return collection;
	}

}
