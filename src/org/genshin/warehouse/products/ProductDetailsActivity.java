package org.genshin.warehouse.products;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.genshin.gsa.ScanSystem;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.spree.SpreeImageData;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.orders.EditProductActivity;
import org.genshin.warehouse.products.ProductEditActivity;
import org.genshin.warehouse.stocking.StockingMenuActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

	// サムネイル用
	private ArrayList<Drawable> images;
	private LinearLayout gallery;
	private int THUMBNAIL_SIZE = 48;
	// 大きな画像用
	private Drawable bigImage;

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
		gallery = (LinearLayout) findViewById(R.id.product_images);
	}

	private void hookupInterface() {
		imageViewer = new ProductImageViewer(this);
		imageSwitcher.setFactory(imageViewer);
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
		// イメージ画像が登録されていない場合デフォルト画像を使用
		if (product.getImages().size() == 0)
			imageSwitcher.setImageResource(R.drawable.spree);
		else {
			// イメージ画像を並べて表示
			images = new ArrayList<Drawable>();
			new setThumnail(Warehouse.getContext()).execute();
		}

		// モード判別
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
			// 注文詳細に商品を追加
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

	// ゲッター どの商品の詳細を表示するか
	private void getProductInfo() {
		product = ProductsMenuActivity.getSelectedProduct();
	}

	private void setViewFields() {
		name.setText(product.getName());

		// skuが登録してなかったらTextView非表示
		if (product.getSku() == "") {
			sku.setVisibility(View.GONE);
			skuTitle.setVisibility(View.GONE);
		} else
			sku.setText(product.getSku());

		// 各種データセット
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
		Warehouse.setContext(this);

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
		// メニューアイテムを追加
		menu.add(Menu.NONE, menuCodes.stock.ordinal(), Menu.NONE, res.getString(R.string.stock_in));
		menu.add(Menu.NONE, menuCodes.destock.ordinal(), Menu.NONE, res.getString(R.string.destock));
		// すでにバーコード登録されている場合はバーコード登録メニューは非表示
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

		// 入荷
		if (id == menuCodes.stock.ordinal()) {
			Warehouse.setSelectProduct(product);
			Intent intent = new Intent(this, StockingMenuActivity.class);
			intent.putExtra("MODE", "STOCK_PRODUCT");
			startActivity(intent);
		// 在庫管理
		} else if (id == menuCodes.destock.ordinal()) {

		// バーコード登録
		} else if (id == menuCodes.registerVisualCode.ordinal()) {
			ScanSystem.initiateScan(this);
			return true;
		// 商品画像登録
		} else if (id == menuCodes.addProductImage.ordinal()) {

		// 商品編集
		} else if (id == menuCodes.editProductDetails.ordinal()) {
			Intent intent = new Intent(this, ProductEditActivity.class);
			startActivity(intent);
		}

		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// SCANモードだった場合
		if (requestCode == ResultCodes.SCAN.ordinal()) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				//TODO limit this to bar code types?
				if (ScanSystem.isProductCode(format))
					new Result(getApplicationContext(), contents).execute();
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

		@Override
		protected void process() {
			int id = product.getId();
			spree.connector.putWithArgs("api/products/" + id + ".json", pairs);
		}

		@Override
		protected void complete() {
			// モード初期化
			modeString = null;
			Toast.makeText(getApplicationContext(), R.string.register_barcode, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getApplicationContext(), StockingMenuActivity.class);
			startActivity(intent);
		}
	}

	// 画像をViewにセット
	private View insertImage(Drawable image, String mode){
		Bitmap bm = null;
		int width = 0;
		int height = 0;
		// サムネイルか大きい画像か
		if (mode.equals("SMALL")) {
			// 画像をリサイズ。THUMBNAIL_SIZEは設定サイズ
			bm = resizeBitmap(image, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
		} else {
			// 端末の解像度を取得
			WindowManager wm = (WindowManager)Warehouse.getContext().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);

			// とりあえず画面の0.8倍くらいに…
			width = (int)(metrics.xdpi * 0.8);
			height = (int)(metrics.ydpi * 0.8);
			bm = resizeBitmap(image, width, height);
		}

		// 枠を少し大きめに作って
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(bm.getWidth() + 10 , bm.getHeight() + 10));
		layout.setGravity(Gravity.CENTER);

		// 画像をセット
		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(bm.getWidth(), bm.getHeight()));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}

	// 画像をリサイズ
	public Bitmap resizeBitmap(Drawable image, int reqWidth, int reqHeight) {
		float resizeScaleH = 1;
		float resizeScaleW = 1;
		Matrix matrix = new Matrix();

		// 実際の画像サイズと、設定サイズを元に縮尺値を設定
		// 元の画像が設定サイズより大きい場合は縮小、小さい場合はそのまま
		if (image.getIntrinsicHeight() > reqHeight || image.getIntrinsicWidth() > reqWidth) {
			// 縦横の長さが違う場合、比率を合わせるためどちらか片方に合わせる
			if (image.getIntrinsicWidth() < image.getIntrinsicHeight()) {
				resizeScaleH = (float)reqHeight / (float)image.getIntrinsicHeight();
				resizeScaleW = resizeScaleH;
			} else {
				resizeScaleW = (float)reqWidth / (float)image.getIntrinsicWidth();
				resizeScaleH = resizeScaleW;
			}
		}
		matrix.postScale(resizeScaleW, resizeScaleH);

		Bitmap resizeBitmap = Bitmap.createBitmap(((BitmapDrawable)image).getBitmap(),
									0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight(), matrix, true);

		return resizeBitmap;
	}

	// 各画像（Drawable）を取得
	private void setImageData(String mode, int id) {
		// サムネイル画像か大きな画像かを判別
		if (mode.equals("SMALL")) {
			for (int i = 0; i < product.getImages().size(); i++) {
				InputStream is = Warehouse.Spree().connector.getStream(product.getSmallImagePath().get(i));

				if (is != null) {
					Drawable imageData = Drawable.createFromStream(is, product.getImages().get(i).getName());
					images.add(imageData);
				}
			}
		} else {
			InputStream is = Warehouse.Spree().connector.getStream(product.getImagePath().get(id));
			if (is != null) {
				bigImage = Drawable.createFromStream(is, product.getImages().get(id).getName());
			}
		}

	}

	// 各画像（Drawable）を取得
	class setThumnail extends NetworkTask {
		public setThumnail(Context ctx) {
			super(ctx);
		}

		@Override
		protected void process() {
			// サムネイルの場合はID不要なので
			setImageData("SMALL", 0);
		}

		@Override
		protected void complete() {
			for (int i = 0; i < product.getImages().size(); i++) {
				View view = insertImage(images.get(i), "SMALL");
				// viewにIDをセットしてどの画像がクリックされたかを判別する
				view.setId(i);
				gallery.addView(view);
				view.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						int id = v.getId();
						// 大きい画像を取得、表示
						new setImage(Warehouse.getContext(), id).execute();
					}
				});
			}
		}
	}

	// 大きな画像を取得
	class setImage extends NetworkTask {
		private int id;

		public setImage(Context ctx, int id) {
			super(ctx);
			this.id = id;
		}

		@Override
		protected void process() {
			setImageData("BIG", id);
		}

		@Override
		protected void complete() {
			// ダイアログで表示
			AlertDialog.Builder dialog = new AlertDialog.Builder(Warehouse.getContext());
			dialog.setView(insertImage(bigImage, "BIG"));
			dialog.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					bigImage = null;
				}
			});
			dialog.show();
		}
	}

	// 長押しで最初の画面へ
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(this, WarehouseActivity.class));
			return true;
		}

		return super.onKeyLongPress(keyCode, event);
	}
}