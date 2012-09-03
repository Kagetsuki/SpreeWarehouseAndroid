package org.genshin.warehouse.racks;

import java.util.ArrayList;

import org.genshin.warehouse.Warehouse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WarehouseDivisions {
	private int count;
	private ArrayList<WarehouseDivision> divisions;

	// コンストラクタ
	public WarehouseDivisions() {
		divisions = new ArrayList<WarehouseDivision>();
		//getWarehouses();
	}

	// JSONObjectを取り出す
	public void getWarehouses() {
		JSONObject warehousesJSONContainer = Warehouse.Spree().connector.getJSONObject("/api/warehouses.json");

		try {
			this.count = warehousesJSONContainer.getInt("count");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONArray warehousesJSON = new JSONArray();
		try {
			warehousesJSON = warehousesJSONContainer.getJSONArray("warehouses");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			warehousesJSON = new JSONArray();
		}

		for (int i = 0; i < warehousesJSON.length(); i++) {
			JSONObject warehouseJSON = null;
			try {
				warehouseJSON = warehousesJSON.getJSONObject(i);
			} catch (JSONException e) {
				warehouseJSON = null;
				e.printStackTrace();
			}

			if (warehouseJSON != null) {
				// take off the wrapper
				JSONObject warehouseJSONData = null;
				try {
					warehouseJSONData = warehouseJSON.getJSONObject("warehouse");
				} catch (JSONException e) {
					warehouseJSONData = null;
				}

				if (warehouseJSONData != null) {
					Log.d("WarehouseDivisions", "Adding Warehouse");
					divisions.add(new WarehouseDivision(warehouseJSONData));
				}
			}
		}
	}

	// 各種ゲッター
	public int getCount() {
		return this.count;
	}

	public ArrayList<WarehouseDivision> getDivisions() {
		return this.divisions;
	}
}
