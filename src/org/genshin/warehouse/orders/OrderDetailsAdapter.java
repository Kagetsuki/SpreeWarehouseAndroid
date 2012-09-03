package org.genshin.warehouse.orders;

import org.genshin.warehouse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderDetailsAdapter extends ArrayAdapter<OrderLineItem>{
	private Context ctx;
	private LayoutInflater inflater;
	private OrderLineItem[] data;

	// コンストラクタ
	public OrderDetailsAdapter(Context ctx, OrderLineItem[] data) {
		super(ctx, R.layout.order_details_list_item, data);
		this.ctx = ctx;
		this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.order_details_list_item, parent, false);

			TextView name = (TextView) convertView.findViewById(R.id.order_details_name);
			name.setText(data[position].getName());
			TextView price = (TextView) convertView.findViewById(R.id.order_details_price);
			price.setText("" + data[position].getPrice());
			TextView quantity = (TextView) convertView.findViewById(R.id.order_details_quantity);
			quantity.setText("" + data[position].getQuantity());
			TextView total = (TextView) convertView.findViewById(R.id.order_details_total);
			total.setText("" + data[position].getTotal());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
}
