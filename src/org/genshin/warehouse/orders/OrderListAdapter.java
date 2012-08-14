package org.genshin.warehouse.orders;

import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderListAdapter extends ArrayAdapter<OrderListItem> {
	
	Context context;
	OrderListItem[] data;

	public OrderListAdapter(Context context, OrderListItem[] data) {
		super(context, R.layout.order_list_item, data);
		this.context = context;
        this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater;
		if (convertView == null) {
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.order_list_item, parent, false);
		}
		
	    TextView number = (TextView) convertView.findViewById(R.id.orders_list_item_number);
		number.setText(data[position].number);
		TextView date = (TextView) convertView.findViewById(R.id.orders_list_item_date);
		if (data[position].date != null)
			date.setText(Warehouse.getLocalDateString(data[position].date));
		else 
			date.setText("");
		TextView name = (TextView) convertView.findViewById(R.id.orders_list_item_name);
		name.setText(data[position].name);
		
		TextView count = (TextView) convertView.findViewById(R.id.orders_list_item_count);
		count.setText(data[position].count + context.getString(R.string.units_counter));
		TextView price = (TextView) convertView.findViewById(R.id.orders_list_item_price);
		price.setText(data[position].price + context.getString(R.string.currency_unit));
		TextView division = (TextView) convertView.findViewById(R.id.orders_list_item_division);
		division.setText(data[position].division);
		
		// 状態が　完了の時：1　一部完了の時：2　未の時：3　許可待ちの時：4　手渡しの時：5　不明の時：6 
		ImageView paymentState = 
					(ImageView) convertView.findViewById(R.id.payment_status_icon);
		if (data[position].paymentState.equals("1"))
			paymentState.setImageResource(android.R.drawable.presence_online);
		//else if (data[position].paymentState.equals("2"))
		//	paymentState.setImageResource(android.R.drawable.presence_away);
		else if (data[position].paymentState.equals("3"))
			paymentState.setImageResource(android.R.drawable.presence_busy);
		else
			paymentState.setImageResource(android.R.drawable.presence_invisible);
			
		ImageView pickingState = 
					(ImageView) convertView.findViewById(R.id.picking_status_icon);
		//if (data[position].pickingState.equals("1"))
		//	pickingSState.setImageResource(android.R.drawable.presence_online);
		//else if (data[position].pickingState.equals("2"))
		//	pickingSState.setImageResource(android.R.drawable.presence_away);
		//else if (data[position].pickingState.equals("3"))
		//	pickingSState.setImageResource(android.R.drawable.presence_busy);
		//else
			pickingState.setImageResource(android.R.drawable.presence_invisible);
				
		ImageView packingState = 
					(ImageView) convertView.findViewById(R.id.packing_status_icon);
		//if (data[position].packingState.equals("1"))
		//	packingState.setImageResource(android.R.drawable.presence_online);
		//else if (data[position].packingState.equals("2"))
		//	packingState.setImageResource(android.R.drawable.presence_away);
		//else if (data[position].packingState.equals("3"))
		//	packingState.setImageResource(android.R.drawable.presence_busy);
		//else
			packingState.setImageResource(android.R.drawable.presence_invisible);

		ImageView shipmentState = 
					(ImageView) convertView.findViewById(R.id.shipment_status_icon);
		//if (data[position].shipmentState.equals("1"))
		//	shipmentState.setImageResource(android.R.drawable.presence_online);
		//else if (data[position].shipmentState.equals("2"))
		//	shipmentState.setImageResource(android.R.drawable.presence_away);
		//else if (data[position].shipmentState.equals("3"))
		//	shipmentState.setImageResource(android.R.drawable.presence_busy);
		//else if (data[position].shipmentState.equals("4"))
		//	shipmentState.setImageResource(android.R.drawable.presence_offline);
		//else if (data[position].shipmentState.equals("5"))
		//	shipmentState.setImageResource(R.drawable.hand);
		//else
			shipmentState.setImageResource(android.R.drawable.presence_invisible);

		return convertView;
	}
}
