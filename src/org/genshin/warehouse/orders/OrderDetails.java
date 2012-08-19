package org.genshin.warehouse.orders;

import java.util.ArrayList;

import org.genshin.spree.SpreeConnector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class OrderDetails {
	
	public String number;
	public String statement;
	public Double mainTotal;
	//public String shipmentState;
	//public String paymentState;

	public double itemTotal;
	public double cost;
	public double lastTotal;
	
	public String paymentAddress;
	public String shipmentAddress;
	public String email;
	
	Context ctx;
	SpreeConnector spree;
	public int count;
	
	ArrayList<OrderLineItem> list;
	ArrayList<OrderDetailsPayment> paymentList;
	ArrayList<OrderDetailsShipment> shipmentList;
	
	// 請求先
	public String firstname;
	public String lastname;
	public String address1;
	public String address2;
	public String city;
	public String state;
	public String zipcode;
	public String country;
	public String phone;
	// 配送先
	public String shipFirstname;
	public String shipLastname;
	public String shipAddress1;
	public String shipAddress2;
	public String shipCity;
	public String shipState;
	public String shipZipcode;
	public String shipCountry;
	public String shipPhone;
	
	// 編集用
	public OrderDetails() {
		this.number = "";
		this.email = "";
		this.firstname = "";
		this.lastname = "";
		this.address1 = "";
		this.address2 = "";
		this.city = "";
		this.state = "";
		this.zipcode = "";
		this.country = "";
		this.phone = "";
		this.shipFirstname = "";
		this.shipLastname = "";
		this.shipAddress1 = "";
		this.shipAddress2 = "";
		this.shipCity = "";
		this.shipState = "";
		this.shipZipcode = "";
		this.shipCountry = "";
		this.shipPhone = "";
	}
	
	public OrderDetails(Context ctx, SpreeConnector spree) {
		this.ctx = ctx;
		this.list = new ArrayList<OrderLineItem>();
		this.spree = spree;
		
		this.email = "";
		this.firstname = "";
		this.lastname = "";
		this.address1 = "";
		this.address2 = "";
		this.city = "";
		this.state = "";
		this.zipcode = "";
		this.country = "";
		this.phone = "";
		this.shipFirstname = "";
		this.shipLastname = "";
		this.shipAddress1 = "";
		this.shipAddress2 = "";
		this.shipCity = "";
		this.shipState = "";
		this.shipZipcode = "";
		this.shipCountry = "";
		this.shipPhone = "";
	}

	public void putOrderDetails(JSONObject container) {
		
		JSONObject str = container;

		try {
			this.number = str.getString("number");
			this.statement = str.getString("state");
			this.mainTotal = str.getDouble("total");
			this.email = str.getString("email");
			
			this.itemTotal = str.getDouble("item_total");
			this.cost = str.getDouble("adjustment_total");
			this.lastTotal = str.getDouble("total");

			str = str.getJSONObject("bill_address");
			StringBuilder sb = new StringBuilder();
			
			this.firstname = str.getString("firstname");
			this.lastname = str.getString("lastname");
			this.address1 = str.getString("address1");
			this.address2 = str.getString("address2");
			this.city = str.getString("city");
			this.state = str.getString("state_name");
			this.zipcode = str.getString("zipcode");
			this.phone = str.getString("phone");
			
			// 請求先住所にまとめて表示
			sb.append(this.firstname);
			sb.append(" ");
			sb.append(this.lastname);
			sb.append(" (");
			sb.append(this.phone);
			sb.append(")\n");
			sb.append(this.address1);
			sb.append(", ");
			sb.append(this.address2);
			sb.append(", ");
			sb.append(this.city);
			sb.append(", ");
			sb.append(this.state);
			sb.append(", ");
			sb.append(this.zipcode);
			sb.append(", ");
			
			str = str.getJSONObject("country");
			this.country = str.getString("name");
			sb.append(this.country);			

			this.paymentAddress = new String(sb);
			
			str = container;
			str = str.getJSONObject("ship_address");
			sb = new StringBuilder();
			
			this.shipFirstname = str.getString("firstname");
			this.shipLastname = str.getString("lastname");
			this.shipAddress1 = str.getString("address1");
			this.shipAddress2 = str.getString("address2");
			this.shipCity = str.getString("city");
			this.shipState = str.getString("state_name");
			this.shipZipcode = str.getString("zipcode");
			this.shipPhone = str.getString("phone");
			
			// 配送先住所にまとめて表示
			sb.append(this.shipFirstname);
			sb.append(" ");
			sb.append(this.shipLastname);
			sb.append(" (");
			sb.append(this.shipPhone);
			sb.append(")\n");
			sb.append(this.shipAddress1);
			sb.append(", ");
			sb.append(this.shipAddress2);
			sb.append(", ");
			sb.append(this.shipCity);
			sb.append(", ");
			sb.append(this.shipState);
			sb.append(", ");
			sb.append(this.shipZipcode);
			sb.append(", ");
			
			str = str.getJSONObject("country");
			this.shipCountry = str.getString("name");
			sb.append(this.shipCountry);
			
			this.shipmentAddress = new String(sb);
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	// 注文詳細取得
	public void processOLIContainer(JSONObject container) {
		ArrayList<OrderLineItem> collection = new ArrayList<OrderLineItem>();

		try {
			JSONArray items = container.getJSONArray("line_items");
			
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i).getJSONObject("line_item");
				OrderLineItem lineItem = new OrderLineItem(item);	
				collection.add(lineItem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		list = collection;
	}
	
	// 支払い方法取得
	public void getPayment(JSONObject container) {
		ArrayList<OrderDetailsPayment> collection = new ArrayList<OrderDetailsPayment>();

		try {
			JSONArray items = container.getJSONArray("payments");
			
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i).getJSONObject("payment");
				OrderDetailsPayment lineItem = new OrderDetailsPayment(item);	
				collection.add(lineItem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		paymentList = collection;
	}
	
	// 配送取得
	public void getShipment(JSONObject container) {
		ArrayList<OrderDetailsShipment> collection = new ArrayList<OrderDetailsShipment>();

		try {
			JSONArray items = container.getJSONArray("shipments");
			
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i).getJSONObject("shipment");
				OrderDetailsShipment lineItem = new OrderDetailsShipment(item);	
				collection.add(lineItem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		shipmentList = collection;
	}
}
