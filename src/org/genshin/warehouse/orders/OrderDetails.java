package org.genshin.warehouse.orders;

import java.util.ArrayList;

import org.genshin.spree.SpreeConnector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class OrderDetails {
	private String number;
	private String statement;
	private Double mainTotal;
	private String shipmentState;
	private String paymentState;

	private double itemTotal;
	private double cost;
	private double lastTotal;

	private String paymentAddress;
	private String shipmentAddress;
	private String email;

	private Context ctx;
	private SpreeConnector spree;
	private int count;

	private ArrayList<OrderLineItem> itemList;
	private ArrayList<OrderDetailsPayment> paymentList;
	private ArrayList<OrderDetailsShipment> shipmentList;

	// 請求先
	private String firstname;
	private String lastname;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	private String phone;
	// 配送先
	private String shipFirstname;
	private String shipLastname;
	private String shipAddress1;
	private String shipAddress2;
	private String shipCity;
	private String shipState;
	private String shipZipcode;
	private String shipCountry;
	private String shipPhone;

	// コンストラクタ　編集用につき一部
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

	// コンストラクタ
	public OrderDetails(Context ctx, SpreeConnector spree) {
		this.ctx = ctx;
		this.itemList = new ArrayList<OrderLineItem>();
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

	// JSONObjectを格納
	public void setOrderDetails(JSONObject container) {

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
			e.printStackTrace();
		}
	}

	// 各種ゲッター
	public String getNumber() {
		return this.number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	public String getStatement() {
		return this.statement;
	}

	public Double getMainTotal() {
		return this.mainTotal;
	}

	public String getShipmentState() {
		return this.shipmentState;
	}

	public String getPaymentState() {
		return this.paymentState;
	}

	public double getItemTotal() {
		return this.itemTotal;
	}

	public double getCost() {
		return this.cost;
	}

	public double getLastTotal() {
		return this.lastTotal;
	}

	public String getPaymentAddress() {
		return this.paymentAddress;
	}

	public String getShipmentAddress() {
		return this.shipmentAddress;
	}

	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public int getCount() {
		return this.count;
	}

	public ArrayList<OrderLineItem> getItemList() {
		return this.itemList;
	}

	public ArrayList<OrderDetailsPayment> getPaymentList() {
		return this.paymentList;
	}

	public ArrayList<OrderDetailsShipment> getShipmentList() {
		return this.shipmentList;
	}

	public String getFirstname() {
		return this.firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getAddress1() {
		return this.address1;
	}
	public void setAddress1(String address) {
		this.address1 = address;
	}

	public String getAddress2() {
		return this.address2;
	}
	public void setAddress2(String address) {
		this.address2 = address;
	}

	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return this.state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return this.zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCountry() {
		return this.country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhone() {
		return this.phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShipFirstname() {
		return this.shipFirstname;
	}
	public void setShipFirstname(String firstname) {
		this.shipFirstname = firstname;
	}

	public String getShipLastname() {
		return this.shipLastname;
	}
	public void setShipLastname(String lastname) {
		this.shipLastname = lastname;
	}

	public String getShipAddress1() {
		return this.shipAddress1;
	}
	public void setShipAddress1(String address) {
		this.shipAddress1 = address;
	}

	public String getShipAddress2() {
		return this.shipAddress2;
	}
	public void setShipAddress2(String address) {
		this.shipAddress2 = address;
	}

	public String getShipCity() {
		return this.shipCity;
	}
	public void setShipCity(String city) {
		this.shipCity = city;
	}

	public String getShipState() {
		return this.shipState;
	}
	public void setShipState(String state) {
		this.shipState = state;
	}

	public String getShipZipcode() {
		return this.shipZipcode;
	}
	public void setShipZipcode(String zipcode) {
		this.shipZipcode = zipcode;
	}

	public String getShipCountry() {
		return this.shipCountry;
	}
	public void setShipCountry(String country) {
		this.shipCountry = country;
	}

	public String getShipPhone() {
		return this.shipPhone;
	}
	public void setShipPhone(String phone) {
		this.shipPhone = phone;
	}

	// 注文詳細格納
	public void setLineItem(JSONObject container) {
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

		itemList = collection;
	}

	// 支払い方法格納
	public void setPayment(JSONObject container) {
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

	// 配送格納
	public void setShipment(JSONObject container) {
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
