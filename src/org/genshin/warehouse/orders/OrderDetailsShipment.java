package org.genshin.warehouse.orders;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailsShipment {
	private String number;
	private String shippingMethod;
	private String tracking;
	private double cost;
	private String state;
	private Date date;
	private String action;

	// コンストラクタ
	public OrderDetailsShipment() {
		this.number = "";
		this.shippingMethod = "";
		this.tracking = "";
		this.cost = 0;
		this.state = "";
		this.date = new Date();
		this.action = "";
	}

	// コンストラクタ
	public OrderDetailsShipment(String number, String method,
										String tracking, double cost, String state, Date date, String action) {
		this.number = number;
		this.shippingMethod = method;
		this.tracking = tracking;
		this.cost = cost;
		this.state = state;
		this.date = new Date();
		this.action = "";
	}

	// コンストラクタ jsonObjectを格納
	public OrderDetailsShipment(JSONObject jsonObject) {
		this.date = null;
		this.action = null;

		try {
			this.number = jsonObject.getString("number");
			this.tracking = jsonObject.getString("tracking");
			this.cost = jsonObject.getDouble("cost");
			this.state = jsonObject.getString("state");
			JSONObject str = jsonObject.getJSONObject("shipping_method");
			this.shippingMethod = str.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	};

	// 各種ゲッター
	public String getNumber() {
		return this.number;
	}

	public String getShippingMethod() {
		return this.shippingMethod;
	}

	public String getTracking() {
		return this.tracking;
	}

	public double getCost() {
		return this.cost;
	}

	public String getState() {
		return this.state;
	}

	public Date getDate() {
		return this.date;
	}

	public String getAction() {
		return this.action;
	}
}
