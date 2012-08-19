package org.genshin.warehouse.orders;

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
	SpreeConnector spree;
	Context ctx;
	boolean isNew = false;
	Order order;
	OrderDetails orderDetails;
	boolean check = false;

	public SaveOrder(Context ctx, boolean isNew, Order order) {
		super(ctx);
		this.ctx = ctx;
		this.isNew = isNew;
		this.order = order;
	}
	
	public SaveOrder(Context ctx, boolean isNew, Order order,
						OrderDetails orderDetails, boolean check) {
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
		
		if (isNew)
			pairs.add(new BasicNameValuePair("order[state]", "cart"));
		
		pairs.add(new BasicNameValuePair("order[email]", orderDetails.email));

		/*
		pairs.add(new BasicNameValuePair
				("order[bill_address[firstname]]", orderDetails.firstname));
		pairs.add(new BasicNameValuePair
				("order[bill_address[lastname]]", orderDetails.lastname));
		pairs.add(new BasicNameValuePair
				("order[bill_address[address1]]", orderDetails.address1));
		pairs.add(new BasicNameValuePair
				("order[bill_address[address2]]", orderDetails.address2));
		pairs.add(new BasicNameValuePair
				("order[bill_address[city]]", orderDetails.city));
		pairs.add(new BasicNameValuePair
				("order[bill_address[state_name]]", orderDetails.state));
		pairs.add(new BasicNameValuePair
				("order[bill_address[zip_code]]", orderDetails.zipcode));
		pairs.add(new BasicNameValuePair
				("order[bill_address[country[name]]]", orderDetails.country));
		pairs.add(new BasicNameValuePair
				("order[bill_address[phone]]", orderDetails.phone));

		if (check) {
			pairs.add(new BasicNameValuePair
					("order[ship_address[firstname]]", orderDetails.firstname));
			pairs.add(new BasicNameValuePair
					("order[ship_address[lastname]]", orderDetails.lastname));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address1]]", orderDetails.address1));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address2]]", orderDetails.address2));
			pairs.add(new BasicNameValuePair
					("order[ship_address[city]]", orderDetails.city));
			pairs.add(new BasicNameValuePair
					("order[ship_address[state_name]]", orderDetails.state));
			pairs.add(new BasicNameValuePair
					("order[ship_address[zip_code]]", orderDetails.zipcode));
			pairs.add(new BasicNameValuePair
					("order[ship_address[country[name]]]", orderDetails.country));
			pairs.add(new BasicNameValuePair
					("order[ship_address[phone]]", orderDetails.phone));	
		} else {
			pairs.add(new BasicNameValuePair
					("order[ship_address[firstname]]", orderDetails.shipFirstname));
			pairs.add(new BasicNameValuePair
					("order[ship_address[lastname]]", orderDetails.shipLastname));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address1]]", orderDetails.shipAddress1));
			pairs.add(new BasicNameValuePair
					("order[ship_address[address2]]", orderDetails.shipAddress2));
			pairs.add(new BasicNameValuePair
					("order[ship_address[city]]", orderDetails.shipCity));
			pairs.add(new BasicNameValuePair
					("order[ship_address[state_name]]", orderDetails.shipState));
			pairs.add(new BasicNameValuePair
					("order[ship_address[zip_code]]", orderDetails.shipZipcode));
			pairs.add(new BasicNameValuePair
					("order[ship_address[country[name]]]", orderDetails.shipCountry));
			pairs.add(new BasicNameValuePair
					("order[ship_address[phone]]", orderDetails.shipPhone));
		}*/

		if (isNew) {
			spree.connector.postWithArgs("api/orders#create", pairs);
		} else
			spree.connector.putWithArgs("api/orders/" + orderDetails.number + ".json", pairs);
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