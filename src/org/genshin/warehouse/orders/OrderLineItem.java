package org.genshin.warehouse.orders;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderLineItem {
	private String name;
	private double price;
	private int quantity;
	private double total;

	// コンストラクタ
	public OrderLineItem() {
		this.name = "";
		this.price = 0;
		this.quantity = 0;
		this.total = 0;
	}

	// コンストラクタ
	public OrderLineItem(String name, double price, int quantity, double total) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.total = total;
	}

	// コンストラクタ JSONObjectを格納
	public OrderLineItem(JSONObject itemJSON) {
		try {
			JSONObject str = itemJSON.getJSONObject("variant");
			this.name = str.getString("name");
			this.price = itemJSON.getDouble("price");
			this.quantity = itemJSON.getInt("quantity");
			this.total = price * quantity;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 各種ゲッター
	public String getName() {
		return this.name;
	}

	public double getPrice() {
		return this.price;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public double getTotal() {
		return this.total;
	}
}
