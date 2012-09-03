package org.genshin.warehouse.products;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.genshin.gsa.ScanSystem;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.products.Product;

public class ProductEditActivity extends Activity {
	private SpreeConnector spree;

	private boolean isNew;
	private Product product;

	private EditText nameEdit;
	private EditText skuEdit;
	private EditText priceEdit;
	private EditText permalinkEdit;
	private EditText barcodeEdit;
	private ImageButton barcodeScanButton;
	private EditText descriptionEdit;

	private ImageSwitcher imageSwitcher;
	private ProductImageViewer imageViewer;

	private Button saveButton;
	private Button deleteButton;
	private ToggleButton listedToggle;

	private void hookupInterface() {
		nameEdit = (EditText) findViewById(R.id.product_name_edit);
		nameEdit.setText(product.getName());

		skuEdit = (EditText) findViewById(R.id.product_sku_edit);
		skuEdit.setText(product.getSku());

		priceEdit = (EditText) findViewById(R.id.product_price_edit);
		priceEdit.setText("" + product.getPrice());

		permalinkEdit = (EditText) findViewById(R.id.product_permalink_edit);
		permalinkEdit.setText(product.getPermalink());

		barcodeEdit = (EditText) findViewById(R.id.barcode_text);
		barcodeEdit.setText(product.getVisualCode());
		barcodeScanButton = (ImageButton) findViewById(R.id.barcode_scan_button);
		barcodeScanButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		ScanSystem.initiateScan(v.getContext());
            }
		});

		descriptionEdit = (EditText) findViewById(R.id.product_description_edit);
		descriptionEdit.setText(product.getDescription());

		imageSwitcher = (ImageSwitcher) findViewById(R.id.product_image_switcher);
		imageViewer = new ProductImageViewer(this);
		imageSwitcher.setFactory(imageViewer);
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
        // イメージ画像が登録されていない場合デフォルト画像を使用
        if (product.getImages().size() == 0)
        	imageSwitcher.setImageResource(R.drawable.spree);
        else
        	imageSwitcher.setImageDrawable(product.getImages().get(0).data);

        saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new saveProduct(getApplicationContext()).execute();
			}
		});

		deleteButton = (Button) findViewById(R.id.delete_button);
		//TODO implement delete
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		isNew = intent.getBooleanExtra("IS_NEW", false);
		String barcode = intent.getStringExtra("BARCODE");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);

        // 新規商品の場合は作成、そうでない場合は情報取得
        if (isNew)
        	product = new Product();
        else
        	product = ProductsMenuActivity.getSelectedProduct();

        hookupInterface();
        //　他画面でバーコードを取得してきた場合、表示する
        if (barcode != "" && barcode != null)
        	barcodeEdit.setText(barcode);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ResultCodes.SCAN.ordinal()) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Handle successful scan
				/*if (ScanSystem.isQRCode(format)) {
					//TODO product details codes?
				} else */
                if (ScanSystem.isProductCode(format)) {
					// put barcode in product barcode details
                	this.barcodeEdit.setText(contents);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

	// 保存
	class saveProduct extends NetworkTask {

		public saveProduct(Context ctx) {
			super(ctx);
		}

		@Override
		protected void process() {
			spree = new SpreeConnector(getApplicationContext());
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (nameEdit.getText().toString() != "")
				pairs.add(new BasicNameValuePair("product[name]", nameEdit.getText().toString()));
			if (priceEdit.getText().toString() != "")
				pairs.add(new BasicNameValuePair("product[price]", priceEdit.getText().toString()));
			if (permalinkEdit.getText().toString() != "")
				pairs.add(new BasicNameValuePair("product[permalink]", permalinkEdit.getText().toString()));
			if (barcodeEdit.getText().toString() != "")
				pairs.add(new BasicNameValuePair("product[visual_code]", barcodeEdit.getText().toString()));
			if (descriptionEdit.getText().toString() != "")
				pairs.add(new BasicNameValuePair("product[description]", descriptionEdit.getText().toString()));

			if (isNew) {
				spree.connector.postWithArgs("api/products#create", pairs);
				isNew = false;
			} else {
				int id = product.getId();
				spree.connector.putWithArgs("api/products/" + id + ".json", pairs);
			}
		}

		@Override
		protected void complete() {
	        Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_LONG).show();
	        finish();
		}
	}
}
