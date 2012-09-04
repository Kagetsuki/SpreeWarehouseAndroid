package org.genshin.warehouse.stocking;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.genshin.gsa.RepetitiveScanner;
import org.genshin.gsa.ScanSystem;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.products.Product;
import org.genshin.warehouse.products.ProductSearcher;
import org.genshin.warehouse.products.ProductsMenuActivity;
import org.genshin.warehouse.racks.ContainerTaxon;
import org.genshin.warehouse.racks.ContainerTaxonomies;
import org.genshin.warehouse.racks.RacksMenuActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StockingMenuActivity extends Activity {
	private EditText supplierText;
	private EditText orderNumberText;
	private ImageButton stockingScanButton;
	private ContainerTaxonomies containerTaxonomies;

	// 画面下の方
	// コンテナ選択
	private TextView targetContainer;
	private Button selectContainer;
	// 商品選択
	private ImageView productImage;
	private TextView targetProduct;
	private Button selectProduct;
	// 数
	private EditText stockCount;
	private Button stockButton;

	private void hookupInterface() {
		supplierText = (EditText) findViewById(R.id.supplier_text);
		orderNumberText = (EditText) findViewById(R.id.order_number_text);

		stockingScanButton = (ImageButton) findViewById(R.id.scan_stocking_button);
		stockingScanButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StockingRepetitiveScanner.class);
                //intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, ResultCodes.SCAN.ordinal());
            }
		});

		// コンテナ選択
		targetContainer = (TextView) findViewById(R.id.target_container);
		// コンテナが選択済みかどうか
		if(Warehouse.getSelectContainer().getFullPath() != null &&
													!Warehouse.getSelectContainer().getFullPath().equals(""))
			targetContainer.setText(Warehouse.getSelectContainer().getFullPath());
		else
			targetContainer.setText("");
		selectContainer =(Button) findViewById(R.id.select_container_button);
		selectContainer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// コンテナを選択しに行く
				Intent intent = new Intent(getApplicationContext(), RacksMenuActivity.class);
				intent.putExtra("MODE", "CONTAINER_SELECT");
				startActivity(intent);
			}
		});

		// 商品選択
		productImage = (ImageView) findViewById(R.id.target_image);
		targetProduct = (TextView) findViewById(R.id.target_product);
		// 商品選択済みかどうか
		if (Warehouse.getSelectProduct().getName() != null &&
															!Warehouse.getSelectProduct().getName().equals("")) {
			// サムネイルが登録されているか。登録されていなかったらデフォルト画像表示
			if (Warehouse.getSelectProduct().getThumbnail() != null)
				productImage.setImageDrawable(Warehouse.getSelectProduct().getThumbnail().getData());
			else
				productImage.setImageResource(R.drawable.spree);
			targetProduct.setText(Warehouse.getSelectProduct().getName());
		} else {
			// 未選択の場合は空欄
			productImage.setImageDrawable(null);
			targetProduct.setText("");
		}
		selectProduct =(Button) findViewById(R.id.select_product_button);
		selectProduct.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 商品を選択しに行く
				Intent intent = new Intent(getApplicationContext(), ProductsMenuActivity.class);
				intent.putExtra("MODE", "STOCK_PRODUCT");
				startActivity(intent);
			}
		});

		// 入荷数、ボタン
		stockCount = (EditText) findViewById(R.id.stock_count);
		stockButton = (Button) findViewById(R.id.stock_button);
		stockButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// コンテナ、商品名、入荷数の空欄チェック
				if (targetProduct.getText().equals("") || targetContainer.getText().equals("") ||
																	stockCount.getText().toString().equals(""))
					Toast.makeText
						(getApplicationContext(), getString(R.string.fill_blank), Toast.LENGTH_LONG).show();
				else {
					new StockProduct(Warehouse.getContext(), stockCount.getText().toString()).execute();
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stocking);

        Warehouse.setContext(this);
        containerTaxonomies = new ContainerTaxonomies();

        hookupInterface();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ResultCodes.SCAN.ordinal()) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                //if it's a Barcode it's a product
                if (ScanSystem.isProductCode(format)) {
                	Warehouse.setContext(this);
                	new ProductSearcher(Warehouse.getContext(), format, contents, "SELECT").execute();
                } else {
					//QR code
					Toast.makeText(this, "processing QR", Toast.LENGTH_LONG).show();
				}
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            }
        }
	}

	// 入荷処理
	class StockProduct extends NetworkTask {
		private Context ctx;
		private String count;
		private Product product;
		private ContainerTaxon container;

		public StockProduct(Context ctx, String count) {
			super(ctx);
			this.ctx = ctx;
			this.count = count;
			this.product = Warehouse.getSelectProduct();
			this.container = Warehouse.getSelectContainer();
		}

		@Override
		protected void process() {
			SpreeConnector spree = new SpreeConnector(ctx.getApplicationContext());
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			int index = product.getPrimaryVarientIndex();
			int variantId = product.getVariants().get(index).getId();

			pairs.add(new BasicNameValuePair("stock_record[quantity]", count));
			pairs.add
				(new BasicNameValuePair("stock_record[variant_id]", String.valueOf(variantId)));
			pairs.add(new BasicNameValuePair("stock_record[direction]", "in"));
			pairs.add(new BasicNameValuePair("stock_record[permalink]", container.getPermalink()));

			spree.connector.postWithArgs("api/stock.json", pairs);
		}

		@Override
		protected void complete() {
			// 選択したコンテナと商品をクリアする
			Warehouse.setSelectContainer(new ContainerTaxon());
			Warehouse.setSelectProduct(new Product());

			Intent intent = new Intent(getApplicationContext(), StockingMenuActivity.class);
			startActivity(intent);
			Toast.makeText
				(getApplicationContext(), getString(R.string.stock_done), Toast.LENGTH_LONG).show();
		}

	}

	// 長押しで最初の画面へ
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	    	startActivity(new Intent(this, WarehouseActivity.class));
	        return true;
	    }
	    return super.onKeyLongPress(keyCode, event);
	}
}