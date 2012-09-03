package org.genshin.warehouse.orders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SaveOrder extends NetworkTask {
	private SpreeConnector spree;
	private Context ctx;
	private boolean isNew = false;
	private Order order;
	private OrderDetails orderDetails;
	private boolean check = false;

	// コンストラクタ
	public SaveOrder(Context ctx, boolean isNew, Order order) {
		super(ctx);
		this.ctx = ctx;
		this.isNew = isNew;
		this.order = order;
	}

	// コンストラクタ
	public SaveOrder(Context ctx, boolean isNew, Order order, OrderDetails orderDetails, boolean check) {
		super(ctx);
		this.ctx = ctx;
		this.isNew = isNew;
		this.order = order;
		this.orderDetails = orderDetails;
		this.check = check;
	}

	@Override
	protected void process() {
		spree = new SpreeConnector(ctx.getApplicationContext());
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();

		// 新規作成の場合
		if (isNew)
			pairs.add(new BasicNameValuePair("order[state]", "cart"));

		pairs.add(new BasicNameValuePair("order[email]", orderDetails.getEmail()));

		/*
		// 請求先住所
		pairs.add(new BasicNameValuePair
				("order[bill_address[firstname]]", orderDetails.getFirstname()));
		pairs.add(new BasicNameValuePair
				("order[bill_address[lastname]]", orderDetails.getLastname()));
		pairs.add(new BasicNameValuePair
				("order[bill_address[address1]]", orderDetails.getAddress1()));
		pairs.add(new BasicNameValuePair
				("order[bill_address[address2]]", orderDetails.getAddress2()));
		pairs.add(new BasicNameValuePair
				("order[bill_address[city]]", orderDetails.getCity()));
		pairs.add(new BasicNameValuePair
				("order[bill_address[state_name]]", orderDetails.getState()));
		pairs.add(new BasicNameValuePair
				("order[bill_address[zip_code]]", orderDetails.getZipcode()));
		pairs.add(new BasicNameValuePair
				("order[bill_address[country[name]]]", orderDetails.country));
		pairs.add(new BasicNameValuePair
				("order[bill_address[phone]]", orderDetails.getPhone()));

		// 請求先住所と配送先住所は　真：同じ　偽：異なる
		if (check) {
			pairs.add(new BasicNameValuePair
					("order[ship_address[firstname]]", orderDetails.getFirstname()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[lastname]]", orderDetails.getLastname()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address1]]", orderDetails.getAddress1()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address2]]", orderDetails.getAddress2()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[city]]", orderDetails.getCity()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[state_name]]", orderDetails.getState()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[zip_code]]", orderDetails.getZipcode()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[country[name]]]", orderDetails.getCountry()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[phone]]", orderDetails.getPhone()));
		} else {
			pairs.add(new BasicNameValuePair
					("order[ship_address[firstname]]", orderDetails.getShipFirstname()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[lastname]]", orderDetails.getShipLastname()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address1]]", orderDetails.getShipAddress1()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address2]]", orderDetails.getShipAddress2()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[city]]", orderDetails.getShipCity()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[state_name]]", orderDetails.getShipState()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[zip_code]]", orderDetails.getShipZipcode()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[country[name]]]", orderDetails.getShipCountry()));
			pairs.add(new BasicNameValuePair
					("order[ship_address[phone]]", orderDetails.getShipPhone()));
		}*/

		if (isNew) {
			spree.connector.postWithArgs("api/orders#create", pairs);
		} else {
			String number = orderDetails.getNumber();
			spree.connector.putWithArgs("api/orders/" + number + ".json", pairs);
		}
	}

	@Override
	protected void complete() {
        if (isNew) {
        	Toast.makeText(ctx.getApplicationContext(), ctx.getString(R.string.created), Toast.LENGTH_LONG).show();
        	isNew = false;
	        Intent intent = new Intent(ctx.getApplicationContext(), OrdersMenuActivity.class);
	        intent.putExtra("MODE", "NEW_ORDER");
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        ctx.startActivity(intent);
        } else {
        	Toast.makeText(ctx.getApplicationContext(), ctx.getString(R.string.saved), Toast.LENGTH_LONG).show();
        	OrdersMenuActivity.setSelectedOrder(order);
    		Intent intent = new Intent(ctx, OrderDetailsActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	ctx.startActivity(intent);
        }
	}
}