package org.genshin.warehouse.orders;

import org.genshin.warehouse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OrderDetailsPaymentAdapter extends ArrayAdapter<OrderDetailsPayment>{
	private Context ctx;
	private LayoutInflater inflater;
	private OrderDetailsPayment[] data;

	// コンストラクタ
	public OrderDetailsPaymentAdapter(Context ctx, OrderDetailsPayment[] data) {
		super(ctx, R.layout.order_details_payment_list, data);
		this.ctx = ctx;
		this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.order_details_payment_list, parent, false);

			final int p = position;
			final ListView list = (ListView) parent;

			CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.order_details_payment_checkbox);
			// 明示的に初期化。この処理をしないと実際のListの表示とズレが発生する。
			checkBox.setOnCheckedChangeListener(null);
			checkBox.setChecked(true);
			// チェックの状態を保存、表示
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					list.setItemChecked(p, isChecked);
				}
			});
			checkBox.setChecked(list.isItemChecked(position));

			TextView date = (TextView) convertView.findViewById(R.id.order_details_payment_date);
			date.setText("yyyy/MM/dd");
			TextView amount = (TextView) convertView.findViewById(R.id.order_details_payment_amount);
			amount.setText("" + data[position].amount + ctx.getString(R.string.currency_unit));
			TextView method = (TextView) convertView.findViewById(R.id.order_details_payment_method);
			method.setText("" + data[position].paymentMethod);

			ImageView state = (ImageView) convertView.findViewById(R.id.order_details_payment_state);

			if (data[position].paymentState.equals("completed"))
				state.setImageResource(android.R.drawable.presence_online);
			else if (data[position].paymentState.equals("void"))
				state.setImageResource(android.R.drawable.presence_offline);
			else if (data[position].paymentState.equals("checkout"))
				state.setImageResource(android.R.drawable.presence_busy);
			else
				state.setImageResource(android.R.drawable.presence_invisible);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
}
