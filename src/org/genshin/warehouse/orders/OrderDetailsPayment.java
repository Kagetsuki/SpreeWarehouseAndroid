package org.genshin.warehouse.orders;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailsPayment {
	public int id;
	public Date date;
	public double amount;
	public String paymentMethod;
	public String paymentState;

	// コンストラクタ
	public OrderDetailsPayment() {
		this.id = 0;
		this.date = new Date();
		this.amount = 0;
		this.paymentMethod = "";
		this.paymentState = "";
	}

	// コンストラクタ
	public OrderDetailsPayment(int id, Date date, double amount, String method, String paymentState) {
		this.id = id;
		this.date = new Date();
		this.amount = amount;
		this.paymentMethod = method;
		this.paymentState = paymentState;
	}

	// コンストラクタ jsonObjectを格納
	public OrderDetailsPayment(JSONObject jsonObject) {
		this.date = null;

		try {
			this.id = jsonObject.getInt("id");
			this.amount = jsonObject.getDouble("amount");
			this.paymentState = jsonObject.getString("state");
			JSONObject str = jsonObject.getJSONObject("payment_method");
			this.paymentMethod = str.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	};
}
