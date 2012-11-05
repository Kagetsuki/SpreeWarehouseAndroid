package org.genshin.spree;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.profiles.Profile;
import org.genshin.warehouse.profiles.Profiles;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class SpreeConnector extends NetworkTask{
	protected Context ctx;
	public RESTConnector connector;
	private Profile profile;

	public SpreeConnector(Context ctx, Profile profile) {
		super(ctx);
<<<<<<< HEAD
		//set profile
		this.profile = profile;
=======
		
		if (!checkProfile(profile))
			return;
>>>>>>> origin/master
		
		connector = new RESTConnector(ctx);
		connector.setup(profile.server, profile.port, profile.apiKey);
	}
	
	public SpreeConnector(Context ctx) {
		super(ctx);
		
		//set profile
		this.profile = Warehouse.Profile();
		if (!checkProfile(profile))
			return;
		
		connector = new RESTConnector(ctx);
		connector.setup(profile.server, profile.port, profile.apiKey);
	}
	
	//check to make sure the profile exists and is complete
	private Boolean checkProfile(Profile profile) {
		if (profile == null)
			return false;
		
		// check for dummy
		if (profile.id == -1)
			return false;
		/*if (profile.server.compareTo("") != 0 || profile.apiKey.compareTo("") != 0)
			return false;*/
		
		return true;
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
	
	public JSONObject getJSONObject(String targetURL) {
		JSONObject data = new JSONObject();
		
		HttpResponse response = this.getResponse(getGetter(targetURL));
		
		if (response == null)
			return null;

		HttpEntity entity = response.getEntity();
		String content;
		try {
			content = EntityUtils.toString(entity);
			data = new JSONObject(content);
		} catch (ParseException e) {
			//Toast.makeText(ctx, "ParseException: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			Log.v("org.genshin.warehouse (Session Filter)", "ParseException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			//Toast.makeText(ctx, "IOException: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			Log.v("org.genshin.warehouse (Session Filter)", "IOException: " + e.getLocalizedMessage());
		} catch (JSONException e) {
			//Toast.makeText(ctx, "JSON parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
			Log.v("org.genshin.warehouse (Session Filter)", "JSON parse error: " + e.getMessage());
		}

		return data;
	}
	
	// Process the Getter and handle response exceptions
	private HttpResponse getResponse(HttpGet getter) {
		if (getter == null)
			return null;
		
		try {
			HttpResponse response = getHttpClient().execute(getter);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				return response;
			} else {
				//Toast.makeText(ctx, getter.getURI() + "\nResponse not 200, Status: " + statusCode, Toast.LENGTH_LONG).show();
				Log.v("org.genshin.warehouse (Session Filter)", getter.getURI() + " Response not 200, Status: " + statusCode);
				Log.e("getHttpResponse", statusLine.toString());
			}
		} catch (ClientProtocolException e) {
			//Toast.makeText(ctx, "ClientProtocolException: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			Log.v("org.genshin.warehouse (Session Filter)", "ClientProtocolException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			//Toast.makeText(ctx, "IOException: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			Log.v("org.genshin.warehouse (Session Filter)", "IOException: " + e.getLocalizedMessage());
		}

		
		// Something went wrong!
		//Toast.makeText(ctx, "Server did not respond", Toast.LENGTH_LONG).show();
		return null;
	}
	
	public void testConnection() {
		
	}

}
