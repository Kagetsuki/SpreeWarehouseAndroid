package org.genshin.warehouse.orders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.genshin.warehouse.products.Variant;
import org.json.JSONException;
import org.json.JSONObject;

public class Order {
	private String number;
	private Date date;
	private String name;
	private int count;
	private double price;
	private String division;
	private String paymentState;
	private String pickingState;
	private String packingState;
	private String shipmentState;

	/** ↓今後使用するか不明 */
	private int primaryVarientIndex;
	private ArrayList<Variant> variants;

	// コンストラクタ
	public Order() {
		this.number = "";
		this.date = new Date();
		this.name = "";
		this.count = 0;
		this.price = 0;
		this.division = "";

		// ソート用に各種ステータスを数字に変換
		// 状態が　完了の時：1　一部完了の時：2　未の時：3　許可待ちの時：4　手渡しの時：5　不明の時：6
		this.paymentState = "6";
		this.pickingState = "6";
		this.packingState = "6";
		this.shipmentState = "6";
	}

	// コンストラクタ　orderJSONを格納
	public Order(JSONObject orderJSON) {
		// orderJSONから取り出して格納
		try {
			this.number = orderJSON.getString("number");

			String strDate = orderJSON.getString("created_at");
			if (strDate != null && strDate != "null" && strDate != "") {
				Date date = null;
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					date = format.parse(strDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				this.date = date;
			}
			else
				this.date = null;

			this.price = orderJSON.getDouble("total");
			this.paymentState = orderJSON.getString("payment_state");
			this.division = "";

			// ソート用に各種ステータスを数字に変換
			// 状態が　完了の時：1　一部完了の時：2　未の時：3　許可待ちの時：4　手渡しの時：5　不明の時：6
			if (paymentState.equals("paid"))
				paymentState = "1";
			//else if (paymentState.equals("paid"))
			//	paymentState = "2";
			else if (paymentState.equals("balance_due"))
				paymentState = "3";
			else
				paymentState = "6";
			// nullPointerException対策に仮格納
			this.pickingState = "6";
			this.packingState = "6";
			this.shipmentState = "6";

		} catch (JSONException e) {
			e.printStackTrace();
		}

		/** ↓今後使用するか不明 */
		//obtainVariants(orderJSON);
	}

	// 各種ゲッター、セッター
	public String getNumber() {
		return this.number;
	}

 	public Date getDate() {
		return this.date;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return this.count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public double getPrice() {
		return this.price;
	}

	public String getDivision() {
		return this.division;
	}

	public String getPaymentState() {
		return this.paymentState;
	}

	public String getPickingState() {
		return this.pickingState;
	}

	public String getPackingState(){
		return this.packingState;
	}

	public String getShipmentState() {
		return this.shipmentState;
	}

	/** ↓今後使用するか不明 */
	public Variant variant() {
		if (variants.size() == 0) // no variants
			return new Variant(); // return dummy

		return variants.get(primaryVarientIndex);
	}
	public Variant variant(int idx) {
			return variants.get(idx);
		}

	/** ↓以下、今後使用するか不明。とりあえずジャマなのでコメントアウト */
	/*
	public void addVariant(int id, String name, int countOnHand, // basics
			String visualCode, String sku, double price, // extended identifying information
			double weight, double height, double width, double depth, //physical specifications
			Boolean isMaster, double costPrice,	String permalink) { // extended data information
		variants.add(new Variant(id, name, countOnHand, visualCode, sku, price, weight, height, width, depth, isMaster, costPrice, permalink));

		if (isMaster) {
			this.primaryVarientIndex = variants.size() - 1; // set as last added variant
		}
	}
	*/

	/*
	private void processVariantJSON(JSONObject v) {
		//pre-build object
		boolean isMaster = false;
		try {
			isMaster = v.getBoolean("is_master");
		} catch (JSONException e) {
			isMaster = false;
		}

		Date date = this.date;
		try {
			date = v.getInt("id");
		} catch (JSONException e) {
			//no unique ID, so set to product ID
			date = this.date;
		}

		String number = this.number;
		try {
			number = v.getString("number");
		} catch (JSONException e) {
			number = this.number;
		}

		String name = this.name;
		try {
			name = v.getString("user_id");
		} catch (JSONException e) {
			name = this.name;
		}

		double count = this.count;
		try {
			count = v.getInt("total");
		} catch (JSONException e) {
			count = this.count;
		}

		double price = this.price;
		try {
			price = v.getDouble("item_total");
		} catch (JSONException e) {
			price = this.price;
		}

		String paymentMethod = this.paymentMethod;
		try {
			paymentMethod = v.getString("payment_state");
		} catch (JSONException e) {
			paymentMethod = this.paymentMethod;
		}

		String paymentState = this.paymentState;
		try {
			paymentState = v.getString("payment_state");
		} catch (JSONException e) {
			paymentState = this.paymentState;
		}

		String shippingMethod = this.shippingMethod;
		try {
			shippingMethod = v.getString("shipment_state");
		} catch (JSONException e) {
			shippingMethod = this.shippingMethod;
		}

		//addVariant(date, number, state,
		//			paymentState, shipmentState, mail, count, price);

	}
	/*

	/*
	private void obtainVariants(JSONObject orderJSON) {
		JSONArray variantArray = new JSONArray();

		try {
			variantArray = orderJSON.getJSONArray("variants");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// get master first
		for (int i = 0; i < variantArray.length(); i++) {
			JSONObject v = new JSONObject();
			try {
				v = variantArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				// something broken? skip this one
				continue;
			}

			boolean isMaster = false;
			try {
				isMaster = v.getBoolean("is_master");
			} catch (JSONException e) {
				isMaster = false;
			}

			if (isMaster) {
				processVariantJSON(v);
				break;
			}
		}
	}
	*/
}
