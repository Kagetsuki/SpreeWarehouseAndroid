package org.genshin.warehouse.orders;

import java.util.ArrayList;

import org.genshin.warehouse.Warehouse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Orders {
	
	private Context ctx;
	private Order selected;
	ArrayList<Order> list;
	public int count;
	
	ArrayList tmpList;
	
	// 2項目取り出すから
	public final static int DATACOUNT = 2;

	public Orders(Context ctx) {
		this.ctx = ctx;
		this.selected = null;
		this.list = new ArrayList<Order>();
		count = 0;
	}
	
	public void select (Order order) {
		selected = order;
	}
	
	public Order selected() {
		return selected;
	}
	
	public void clear() {
		this.list = new ArrayList<Order>();
		count = 0;
	}
	
	// JSONデータ取得
	public ArrayList<Order> processOrderContainer(JSONObject orderContainer,
															int limit, String flag) {
		ArrayList<Order> collection = new ArrayList<Order>();
		int first = 0;
		
		if (orderContainer == null)
			return null;

		//Pick apart JSON object
		try {
			if (flag.equals("NEW_ORDER")) {
				this.count = orderContainer.getInt("count");
				int tmp = count / 25 + 1;
				orderContainer = 
						Warehouse.Spree().connector.getJSONObject("api/orders.json?page=" + tmp);
			}	
			JSONArray orders = orderContainer.getJSONArray("orders");
			if (flag.equals("NEW_ORDER"))
				first = orders.length() - 1;
			for (int i = first; i < orders.length(); i++) {
				JSONObject orderJSON = orders.getJSONObject(i).getJSONObject("order");
				Order order = new Order(orderJSON);
				
				tmpList = new ArrayList();
				tmpList = putData(order.number);
				
				order.count = (Integer) tmpList.get(0);
				order.name = (String) tmpList.get(1);
				
				collection.add(order);				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		list = collection;
		return collection;
	}

	// 最新の（limit）件数を取得…現在は１ページ表示
	public ArrayList<Order> getNewestOrders(int limit, String flag) {
		ArrayList<Order> collection = new ArrayList<Order>();
		JSONObject orderContainer = 
				Warehouse.Spree().connector.getJSONObject("api/orders.json");
		collection = processOrderContainer(orderContainer, limit, flag);

		return collection;
	}
	
	public ArrayList putData(String number) {
		tmpList = new ArrayList(DATACOUNT);
		String tmp = "";
		int num = 0;
		int cnt = 0;
		
		// orders.json にないデータを　orders/number.json から取り出す
		JSONObject container = 
				Warehouse.Spree().connector.getJSONObject("api/orders/" + number + ".json");

		try {
			JSONObject orderStr = container.getJSONObject("order");

			// 個数
			JSONArray items = orderStr.getJSONArray("line_items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject itemJSON = items.getJSONObject(i).getJSONObject("line_item");
				tmp = itemJSON.getString("quantity");
				num += Integer.parseInt(tmp);
			}
			tmpList.add(num);
			cnt++;
			
			// 名前
			JSONObject tmpStr = orderStr.getJSONObject("bill_address");
			StringBuilder sb = new StringBuilder(tmpStr.getString("firstname"));
			sb.append(" ");
			sb.append(tmpStr.getString("lastname"));	
			tmp = new String(sb);
			tmpList.add(tmp);
			cnt++;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// jsonデータが空だった時にnullデータを挿入　NullPointerException対策
		if (cnt == DATACOUNT)
			return tmpList;
		else {
			for (int i = cnt; i < DATACOUNT; i++) {
				tmpList.add(null);
			}
			return tmpList;
		}
	}
}
