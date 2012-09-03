package org.genshin.warehouse.orders;

import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.products.Product;
import org.genshin.warehouse.products.ProductsMenuActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditProductActivity extends Activity {
	private boolean isNew;
	private Order order;
	private TextView orderNumber;

	private EditText selectProduct;
	private Button selectButton;
	private EditText addNumber;
	private Button addButton;

	private boolean select;
	private Product product;

	private void hookupInterface() {
		// 追加する商品を一覧から選択してきた後かどうか
		if (select)
			product = ProductsMenuActivity.getSelectedProduct();

		// 追加する商品
		orderNumber = (TextView) findViewById(R.id.order_details_number);
		orderNumber.setText(order.getNumber());
		selectProduct = (EditText) findViewById(R.id.new_product_name);
		if (select)
			selectProduct.setText(product.getName());
		else
			selectProduct.setText("");

		// 商品一覧から選択してくる
		selectButton = (Button) findViewById(R.id.select_button);
		selectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ProductsMenuActivity.class);
				intent.putExtra("MODE", "ADD_PRODUCT");
				startActivity(intent);
			}
		});

		addNumber = (EditText) findViewById(R.id.new_product_num);
		addNumber.setText("");

		addButton = (Button) findViewById(R.id.add_button);
		addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 空欄がない場合に追加
				if (!selectProduct.getText().toString().equals("") && !addNumber.getText().toString().equals("")) {
					new SaveOrder(getApplicationContext(), false, order).execute();}
				else {
					AlertDialog.Builder dialog = new AlertDialog.Builder(Warehouse.getContext());
					dialog.setTitle(R.string.fill_blank);
					dialog.setPositiveButton("OK", null);
					dialog.show();
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		isNew = intent.getBooleanExtra("IS_NEW", false);
		select = intent.getBooleanExtra("SELECT", false);
		Warehouse.setContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_edit_product);

        // 新規orderなら新しくorder作成、そうでないならばどのorderに追加するか
        if (isNew) {
        	order = new Order();
        	new SaveOrder(getApplicationContext(), true, order).execute();
        }
        else {
        	order = OrdersMenuActivity.getSelectedOrder();
        }

        hookupInterface();
	}
}