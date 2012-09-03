package org.genshin.warehouse.racks;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ContainerTaxon {
	private int id;
	private String name;
	private String permalink;
	private String updatedAt; //TODO make this a date
	private String warehouse;
	private ContainerTaxon root;

	// コンストラクタ
	public ContainerTaxon() {
		this.id = 0;
		this.name = "";
		this.permalink = "";
		this.updatedAt = "";
		this.warehouse = "";
		this.root = null;
	}

	// コンストラクタ　JSONObjectを格納
	public ContainerTaxon(JSONObject json) {
		try {
			id = json.getInt("id"); //TODO this will fail from QR code scans.
		} catch (JSONException e) {
			// TODO QR Code scans will NOT have IDs.
			// Cross reference/search with permalink and if that fails trace the tree.
		}

		try {
			name = json.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			permalink = json.getString("permalink");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			updatedAt = json.getString("updated_at");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("container", "container added!");
	}

	// 各種ゲッター
	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getPermalink() {
		return this.permalink;
	}
}
