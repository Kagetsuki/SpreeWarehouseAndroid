package org.genshin.warehouse.products;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.genshin.gsa.ScanSystem;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.orders.EditProductActivity;
import org.genshin.warehouse.products.ProductEditActivity;
import org.genshin.warehouse.stocking.StockingMenuActivity;
import org.genshin.warehouse.stocking.StockingRepetitiveScanner;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailsActivity extends Activity {
	private SpreeConnector spree;

	private Bundle extras;
	//private EditText id;
	private TextView name;
	private TextView sku;
	private TextView skuTitle;
	private TextView price;
	private TextView countOnHand;
	private TextView description;
	private TextView permalink;
	private TextView visualCode;
	private ImageSwitcher imageSwitcher;
	private ProductImageViewer imageViewer;
	//private Image image;

	private Product product;

	private String modeString;
	private String barcodeString;

	private void initViewElements() {
		//id = (TextView) findViewById(R.id.product_id);
		name = (TextView) findViewById(R.id.product_name);
		sku = (TextView) findViewById(R.id.product_sku);
		skuTitle = (TextView) findViewById(R.id.product_sku_title);
		price = (TextView) findViewById(R.id.product_price);
		countOnHand = (TextView) findViewById(R.id.product_count_on_hand);
		description = (TextView) findViewById(R.id.product_description);
		permalink = (TextView) findViewById(R.id.product_permalink);
		visualCode = (TextView) findViewById(R.id.product_visualCode);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.product_image_switcher);
	}

	private void hookupInterface() {
		imageViewer = new ProductImageViewer(this);
		imageSwitcher.setFactory(imageViewer);
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
        // イメージ画像が登録されていない場合デフォルト画像を使用
        if (product.getImages().size() == 0)
        	imageSwitcher.setImageResource(R.drawable.spree);
        else
        	imageSwitcher.setImageDrawable(product.getImages().get(0).getData());

        Intent intent = getIntent();
        modeString = intent.getStringExtra("MODE");
        barcodeString = intent.getStringExtra("BARCODE");
        if (modeString != null) {
        	// バーコード登録
	        if (modeString.equals("UPDATE_PRODUCT_BARCODE")) {
	        	AlertDialog.Builder question = new AlertDialog.Builder(this);
				question.setTitle(getString(R.string.register_this_product));
				question.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						new registrationNewData(getApplicationContext(), barcodeString).execute();
					}
				});
				question.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
				question.show();
			// 商品追加
	        } else if (modeString.equals("ADD_PRODUCT")) {
	        	AlertDialog.Builder question = new AlertDialog.Builder(this);
				question.setTitle(getString(R.string.add_this_product));
				question.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(getApplicationContext(), EditProductActivity.class);
						intent.putExtra("SELECT", true);
						startActivity(intent);
					}
				});
				question.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
				question.show();
			// 商品入荷
	        } else if (modeString.equals("STOCK_PRODUCT")) {
	        	AlertDialog.Builder question = new AlertDialog.Builder(this);
				question.setTitle(getString(R.string.stock_this_product));
				question.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Warehouse.setSelectProduct(product);
						Intent intent = new Intent(getApplicationContext(), StockingMenuActivity.class);
						startActivity(intent);
					}
				});
				question.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
				question.show();
	        }
        }
	}

	// ゲッター
	private void getProductInfo() {
		product = ProductsMenuActivity.getSelectedProduct();
	}

	private void setViewFields() {
		name.setText(product.getName());

		if (product.getSku() == "") {
			sku.setVisibility(View.GONE);
			skuTitle.setVisibility(View.GONE);
		} else
			sku.setText(product.getSku());

		price.setText(product.getPrice() + getString(R.string.currency_unit));
		countOnHand.setText(product.getCountOnHand() + getString(R.string.units_counter));
		description.setText(product.getDescription());
		permalink.setText(product.getPermalink());
		visualCode.setText(product.getVisualCode());
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        spree = new SpreeConnector(this);

		getProductInfo();
		initViewElements();
		setViewFields();
		hookupInterface();
	}

	public static enum menuCodes { stock, destock, registerVisualCode, addProductImage, editProductDetails };

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
        // メニューアイテムを追加します
        menu.add(Menu.NONE, menuCodes.stock.ordinal(), Menu.NONE, res.getString(R.string.stock_in));
        menu.add(Menu.NONE, menuCodes.destock.ordinal(), Menu.NONE, res.getString(R.string.destock));
        if (visualCode.getText() == null || visualCode.getText().equals("") || visualCode.getText().equals("null"))
        	menu.add(Menu.NONE, menuCodes.registerVisualCode.ordinal(), Menu.NONE, res.getString(R.string.register_barcode));
        menu.add(Menu.NONE, menuCodes.addProductImage.ordinal(), Menu.NONE, res.getString(R.string.add_product_image));
        menu.add(Menu.NONE, menuCodes.editProductDetails.ordinal(), Menu.NONE, res.getString(R.string.edit_product_details));
        return super.onCreateOptionsMenu(menu);
    }

	// メニュー実装
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

		//Java can't do this!? WTF!
        /*switch (item.getItemId()) {
        	default:
        		return super.onOptionsItemSelected(item);
        	case registerVisualCode:

        		return true;
        }*/
		int id = item.getItemId();

		if (id == menuCodes.stock.ordinal()) {
			Warehouse.setSelectProduct(product);
			Intent intent = new Intent(this, StockingMenuActivity.class);
			intent.putExtra("MODE", "STOCK_PRODUCT");
			startActivity(intent);
		} else if (id == menuCodes.registerVisualCode.ordinal()) {
			ScanSystem.initiateScan(this);
			return true;
		} else if (id == menuCodes.editProductDetails.ordinal()) {
			Intent intent = new Intent(this, ProductEditActivity.class);
			startActivity(intent);
		}

        return false;
    }

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ResultCodes.SCAN.ordinal()) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //TODO limit this to bar code types?
                if (ScanSystem.isProductCode(format)) {
                	new Result(getApplicationContext(), contents).execute();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

	// onActivityResult
	class Result extends NetworkTask {
		String contents;

		public Result(Context ctx, String contents) {
			super(ctx);
			this.contents = contents;
		}

		@Override
		protected void process() {
			spree.connector.genericPut("api/products/" + product.getPermalink() + "?product[visual_code]=" + contents);
		}

		@Override
		protected void complete() {
			Toast.makeText(getApplicationContext(), R.string.register_barcode, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	// バーコードを商品に登録
	class registrationNewData extends NetworkTask {
		ArrayList<NameValuePair> pairs;
		String code;
		int num;
		String mode = "";

		public registrationNewData(Context ctx, String code) {
			super(ctx);
			this.mode = "UPDATE_PRODUCT_BARCODE";
			this.code = code;
			this.pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("product[visual_code]", code));
		}

		/*
		public registrationNewData(Context ctx, int num) {
			super(ctx);
			this.mode = "STOCK_PRODUCT";
			this.num = num;
			this.pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("product[count_on_hand]", String.valueOf(num)));
		}
		*/

		@Override
		protected void process() {
			int id = product.getId();
			spree.connector.putWithArgs("api/products/" + id + ".json", pairs);
		}

		@Override
		protected void complete() {
			modeString = null;
			//if (mode.equals("UPDATE_PRUDUCT_BARCODE"))
				Toast.makeText(getApplicationContext(), R.string.register_barcode, Toast.LENGTH_LONG).show();
			//else if (mode.equals("STOCK_PRODUCT"))
			//	Toast.makeText(getApplicationContext(), R.string.stock_in, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getApplicationContext(), StockingMenuActivity.class);
			startActivity(intent);
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
