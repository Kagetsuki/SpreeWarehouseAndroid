/******************************************************************************
 * Warehouse Profiles manager
 *
 * Holds a list of all registered profiles, and manages the database 
 * interactions to obtain and manage that list.
 *
 * プロフィルのリストを管理し、DBとのやりとりを代理に行う。
 ******************************************************************************/

package org.genshin.warehouse.profiles;

import java.util.ArrayList;

import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.products.ProductEditActivity;
import org.genshin.warehouse.settings.WarehousePreferences;
import org.genshin.warehouse.settings.WarehouseSettingsActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Profiles {
	private ProfileDBHelper helper;
	
	//All profiles
	//全プロフィールのリスト
	public int selectedProfilePosition;
	public ArrayList<Profile> list;
	//Selected profile
	//選択されているプロフィール
	public Profile selected;
	
	private WarehousePreferences preferences;

	private Context ctx;
	public Profiles(Context ctx) {
		this.ctx = ctx;
		helper = new ProfileDBHelper(ctx);
		preferences = new WarehousePreferences(ctx);
		
		getAllProfiles();
		//getDefaultProfile();
		
	}

	//Create a new profile
	public Profile createProfile(String server, long port, String profileName, String apiKey, boolean useHTTPS, boolean allowUnsigned) {
		return helper.createProfile(server, port, profileName, apiKey, useHTTPS, allowUnsigned);
	}

	private int getProfileListPosition(Profile profile) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).id == profile.id)
				return i;

		return 0;
	}

	//Update a profile
	//The profile ID is held within the profile object
	public void updateProfile(Profile profile) {
		helper.updateProfile(profile);
	}

	//Gets a list of all available profiles and updates the local list
	public ArrayList<Profile> getAllProfiles() {
		list = helper.getAllProfiles();

		selected = getDefaultProfile();
		selectedProfilePosition = getProfileListPosition(selected);
		
		return list;
	}

	public Profile selectProfile(int position) {
		if (position < 0 || position >= list.size())
			return new Profile(); //OOB
		
		Profile sel = list.get(position);
		
		preferences.setDefaultProfileID(sel.id);
		
        selectedProfilePosition = position;
		selected = list.get(position);
		
		return sel;
    }
	
	//Delete the specified profile
	public void deleteProfile(Profile profile) {
		if (profile == null || profile.id == -1)
			return;

		helper.deleteProfile(profile);
	}

	//Deletes profile at the specified position in "list"
	public void deleteProfile(int position) {
		deleteProfile(list.get(position));
	}

	//Deletes the currently selected profile
	public void deleteSelectedProfile() {
        deleteProfile(selected);
	}

	public Spinner attachToSpinner(Spinner spinner) {
		AdapterView.OnItemSelectedListener selected = new AdapterView.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                selectProfile(position);
            }

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
		};
		
		spinner.setAdapter(getArrayAdapter());
    	spinner.setOnItemSelectedListener(selected);
    	spinner.setSelection(selectedProfilePosition);
  
    	return spinner;
	}

	public ArrayAdapter<String> getArrayAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (int i = 0; i < list.size(); i++) {
			adapter.add(list.get(i).name + ":" + list.get(i).server);
		}

		return adapter;
	}
	
	private Profile createDummyProfile() {
		Profile dummy = new Profile();
		dummy.id = -1;
		return dummy;
	}
	
	public Profile getDefaultProfile() {
		if (list.size() > 0) {
			//Get default ID from preferences
			long defID = preferences.getDefaultProfileID();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).id == defID) {
					selectProfile(i);
					return selected;
				}
			}
			
			selectProfile(0);
			return selected;
		}
		
		return createDummyProfile();
	}
	
	public static void noRegisteredProfiles() {
		Context ctx = Warehouse.getContext();
		AlertDialog.Builder question = new AlertDialog.Builder(ctx);

		question.setTitle(ctx.getString(R.string.no_profiles));
		question.setMessage(ctx.getString(R.string.please_register_profile));
		//question.setIcon(R.drawable.newproduct);
		question.setPositiveButton(ctx.getString(R.string.register), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(Warehouse.getContext(), ProfileSettingsActivity.class);
	            Warehouse.getContext().startActivity(intent);
			}
		});
		
		question.setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		question.show();	
	}
}
