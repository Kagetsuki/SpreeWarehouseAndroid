package org.genshin.warehouse.orders;

import org.genshin.warehouse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderDetailsShipmentAdapter extends ArrayAdapter<OrderDetailsShipment>{
	private Context ctx;
	private LayoutInflater inflater;
	private OrderDetailsShipment[] data;

	// コンストラクタ
	public OrderDetailsShipmentAdapter(Context ctx, OrderDetailsShipment[] data) {
		super(ctx, R.layout.order_details_shipment_list, data);
		this.ctx = ctx;
		this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.order_details_shipment_list, parent, false);

			TextView number = (TextView) convertView.findViewById(R.id.order_details_shipment_number);
			number.setText(data[position].getNumber());
			TextView method = (TextView) convertView.findViewById(R.id.order_details_shipment_method);
			method.setText(data[position].getShippingMethod());
			TextView cost = (TextView) convertView.findViewById(R.id.order_details_shipment_cost);
			cost.setText(data[position].getCost() + ctx.getString(R.string.currency_unit));
			TextView tracking =  (TextView) convertView.findViewById(R.id.order_details_shipment_tracking);
			tracking.setText(data[position].getTracking());
			TextView state = (TextView) convertView.findViewById(R.id.order_details_shipment_state);
			state.setText("" + data[position].getState());

			TextView date = (TextView) convertView.findViewById(R.id.order_details_shipment_date);
			date.setText("yyyy/MM/dd");
			TextView action = (TextView) convertView.findViewById(R.id.order_details_shipment_action);
			action.setText("");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
}
