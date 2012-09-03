package org.genshin.warehouse.products;

import org.genshin.warehouse.R;
import org.genshin.warehouse.products.ProductListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductListAdapter extends ArrayAdapter<ProductListItem> {
	Context ctx;
	LayoutInflater inflater;
	ProductListItem[] data;

	// コンストラクタ
	public ProductListAdapter(Context ctx, ProductListItem[] data) {
		super(ctx, R.layout.product_list_item, data);
		this.ctx = ctx;
		this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.product_list_item, parent, false);

			ImageView thumb = (ImageView)convertView.findViewById(R.id.products_list_item_image);

			// サムネイルが登録されていない場合はデフォルト画像を使用
			if (data[position].getThumb() != null)
				thumb.setImageDrawable(data[position].getThumb());
			else
				thumb.setImageResource(R.drawable.spree);
			TextView name = (TextView) convertView.findViewById(R.id.products_list_item_name);
			name.setText(data[position].getName());
			TextView sku = (TextView) convertView.findViewById(R.id.products_list_item_sku);
			sku.setText(data[position].getSku());
			TextView count = (TextView) convertView.findViewById(R.id.products_list_item_count);
			count.setText(data[position].getCount() + ctx.getString(R.string.units_counter));
			TextView price = (TextView) convertView.findViewById(R.id.products_list_item_price);
			price.setText(data[position].getPrice() + ctx.getString(R.string.currency_unit));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
}
