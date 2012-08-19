package org.genshin.warehouse.orders;

import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.products.Product;
import org.genshin.warehouse.products.ProductsMenuActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditProductActivity extends Activity {
	private SpreeConnector spree;
	private Context context;
	
	boolean isNew;
	private Order order;
	
	private EditText selectProduct;
	private Button selectButton;
	private EditText addNumber;
	private Button addButton;
	
	private boolean select;
	private Product product;

	private void hookupInterface() {
		
		if (select)
			product = ProductsMenuActivity.getSelectedProduct();

		selectProduct = (EditText) findViewById(R.id.new_product_name);
		if (select)
			selectProduct.setText(product.name);
		else
			selectProduct.setText("");
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
				if (!selectProduct.getText().toString().equals("") &&
									!addNumber.getText().toString().equals("")) {
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