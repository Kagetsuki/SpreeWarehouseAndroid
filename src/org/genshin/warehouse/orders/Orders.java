package org.genshin.warehouse.orders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.genshin.warehouse.Warehouse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Orders {
	private Context ctx;
	private Order selected;
	private ArrayList<Order> list;
	private int count;

	// 2項目取り出すから
	private final static int DATACOUNT = 2;

	// コンストラクタ
	public Orders(Context ctx) {
		this.ctx = ctx;
		this.selected = null;
		this.list = new ArrayList<Order>();
		count = 0;
	}

	// ゲッター、セッター
	public Order getSelectedOrder() {
		return selected;
	}
	public void setSelectedOrder (Order order) {
		selected = order;
	}

	public ArrayList<Order> getList() {
		return this.list;
	}
	public void setList(ArrayList<Order> list) {
		this.list = list;
	}

	// listの中身を初期化
	public void clear() {
		this.list = new ArrayList<Order>();
		count = 0;
	}

	// jsonデータを取得してorderに格納
	public ArrayList<Order> processOrderContainer(JSONObject orderContainer, int limit, String flag) {
		ArrayList<Order> collection = new ArrayList<Order>();
		int first = 0;

		if (orderContainer == null)
			return null;

		//Pick apart JSON object
		try {
			// 新規order作成だった場合は新規作成したorderのみを表示する
			if (flag.equals("NEW_ORDER")) {
				this.count = orderContainer.getInt("count");
				int tmp = count / 25 + 1;
				orderContainer =
						Warehouse.Spree().connector.getJSONObject("api/orders.json?page=" + tmp);
			}

			JSONArray orders = orderContainer.getJSONArray("orders");

			// 取得したjsonの一番最後のデータが一番新しいデータのため調整
			if (flag.equals("NEW_ORDER"))
				first = orders.length() - 1;

			for (int i = first; i < orders.length(); i++) {
				JSONObject orderJSON = orders.getJSONObject(i).getJSONObject("order");
				Order order = new Order(orderJSON);

				// 必要なデータが更に奥にあるためorder.numberを使用して取得。一度にまとめてやるためArrayListを使用。
				ArrayList tmpData = new ArrayList();
				tmpData = putData(order.getNumber());
				order.setCount((Integer) tmpData.get(0));
				order.setName((String) tmpData.get(1));

				collection.add(order);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		list = collection;
		return collection;
	}

	// jsonデータを一時的に格納
	public ArrayList putData(String number) {
		ArrayList tmpList = new ArrayList(DATACOUNT);
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

		// jsonデータが空だった時にnullデータを格納　NullPointerException対策
		if (cnt == DATACOUNT)
			return tmpList;
		else {
			for (int i = cnt; i < DATACOUNT; i++)
				tmpList.add(null);
			return tmpList;
		}
	}

	// 最新の（limit）件数を取得…現在は１ページ表示
	public ArrayList<Order> getNewestOrders(int limit, String flag) {
		ArrayList<Order> collection = new ArrayList<Order>();

		try {
			JSONObject orderContainer = Warehouse.Spree().connector.getJSONObject("api/orders.json");
			collection = processOrderContainer(orderContainer, limit, flag);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return collection;
	}

	// テキスト検索 (未実装)
	public ArrayList<Order> textSearch(String query) {
		ArrayList<Order> collection = new ArrayList<Order>();
		String escapedQuery = query;

		// UTF-8にエンコード
		try {
			escapedQuery = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// WTF unsupported encoding? fine, just take it raw
			escapedQuery = query;
		}

		try {
			JSONObject orderContainer =
				Warehouse.Spree().connector.getJSONObject("api/orders/search.json?q[number_cont]=" + escapedQuery);
			collection = processOrderContainer(orderContainer, 10, "SEARCH");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return collection;
	}
}
