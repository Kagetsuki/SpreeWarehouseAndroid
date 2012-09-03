package org.genshin.warehouse.products;

import java.util.ArrayList;

import org.genshin.gsa.ScanSystem;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.products.ProductDetailsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsMenuActivity extends Activity {

	private ProductListAdapter productsAdapter;

	private static int mode;
	private static Intent intent;

	private ListView productList;
	private TextView statusText;
	private MultiAutoCompleteTextView searchBar;
	private Button searchButton;

	private Button clearButton;

	private ImageButton scanButton;
	private Spinner orderSpinner;
	private ArrayAdapter<String> sadapter;

	private ImageButton backwardButton;
	private boolean updown = false;		// falseの時は▽、trueの時は△表示

	private void hookupInterface() {
		productList = (ListView) findViewById(R.id.product_menu_list);
        statusText = (TextView) findViewById(R.id.status_text);
        searchBar = (MultiAutoCompleteTextView) findViewById(R.id.product_menu_searchbox);

        //Visual Code Scan hookup
		scanButton = (ImageButton) findViewById(R.id.products_menu_scan_button);
		scanButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		clearImage();
        		orderSpinner.setSelection(0);
        		Toast.makeText(v.getContext(), getString(R.string.scan), Toast.LENGTH_LONG).show();
                ScanSystem.initiateScan(v.getContext());
            }
		});

		//Standard text search hookup
		searchButton = (Button) findViewById(R.id.products_menu_search_button);
		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new SearchProductsRefresh(v.getContext(), searchBar.getText().toString()).execute();
				clearImage();
				orderSpinner.setSelection(0);
			}
		});

		//Clear button
		clearButton = (Button) findViewById(R.id.products_menu_clear_button);
		clearButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchBar.setText("");
				Warehouse.Products().clear();
				refreshProductMenu();
				clearImage();
				orderSpinner.setSelection(0);
			}
		});

		//Order spinner
		orderSpinner = (Spinner) findViewById(R.id.order_spinner);
		sadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	    sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // アイテムを追加します
	    sadapter.add(getString(R.string.none));
	    sadapter.add(getString(R.string.name));
	    sadapter.add(getString(R.string.price));
	    sadapter.add(getString(R.string.stock));
	    orderSpinner.setPrompt(getString(R.string.sort));
	    orderSpinner.setAdapter(sadapter);

	    orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                Spinner spinner = (Spinner) parent;
                //String item = (String) spinner.getSelectedItem();
                switch(position) {
                	case 0:		// なし
                		break;
                	case 1:		// 名前順
						sortName();
						clearImage();
                		break;
	                case 2:		// 値段順
	                	sortPrice();
	                	clearImage();
	                	break;
	                case 3:		// 在庫数順
	                	sortCountOnHand();
	                	clearImage();
	                	break;
	                default :
	                	break;
                }
            }

			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });

	    // backwardButton 並びを逆に
	    backwardButton = (ImageButton)findViewById(R.id.product_menu_backward_button);
	    backwardButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!updown) {
					backwardButton.setImageResource(android.R.drawable.arrow_up_float);
					updown = true;
				} else {
					backwardButton.setImageResource(android.R.drawable.arrow_down_float);
					updown = false;
				}
				switchOrder();
			}
		});
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products);
        Warehouse.setContext(this);

        hookupInterface();

		intent = getIntent();

		mode = Warehouse.ResultCodes.NORMAL.ordinal();
		String modeString = intent.getStringExtra("MODE");
		if (modeString != null) {
			if (modeString.equals("PRODUCT_SELECT")) {
				mode = Warehouse.ResultCodes.PRODUCT_SELECT.ordinal();
				refreshProductMenu();
				Toast.makeText(this, getString(R.string.select_a_product), Toast.LENGTH_LONG).show();
			} else if (modeString.equals("PRODUCT_LIST")) {
				refreshProductMenu();
			} else if (modeString.equals("UPDATE_PRODUCT_BARCODE")) {
				mode = Warehouse.ResultCodes.UPDATE_PRODUCT_BARCODE.ordinal();
				Toast.makeText(this, getString(R.string.select_a_product), Toast.LENGTH_LONG).show();
			} else if (modeString.equals("ADD_PRODUCT")) {
				mode = Warehouse.ResultCodes.ADD_PRODUCT.ordinal();
				Toast.makeText(this, getString(R.string.select_a_product), Toast.LENGTH_LONG).show();
			}
		} else
			new NewProductsRefresh(this, 10).execute();
	}

	class ProductsListRefresh extends NetworkTask {
		public ProductsListRefresh(Context ctx) {
			super(ctx);
		}

		@Override
		protected void complete() {
			ListView productList = (ListView) findViewById(R.id.product_menu_list);
			refreshProductMenu();
		}
	}

	class NewProductsRefresh extends ProductsListRefresh {
		private int count;

		public NewProductsRefresh(Context ctx, int count) {
			super(ctx);
			this.count = count;
		}

		@Override
		protected void process() {
			Warehouse.Products().getNewestProducts(count);
		}
	}

	class SearchProductsRefresh extends ProductsListRefresh {
		String query;

		public SearchProductsRefresh(Context ctx, String query) {
			super(ctx);
			this.query = query;
		}

		@Override
		protected void process() {
			Warehouse.Products().textSearch(query);
		}
	}

	public static enum menuCodes { registerProduct };

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
        // メニューアイテムを追加する
        menu.add(Menu.NONE, menuCodes.registerProduct.ordinal(), Menu.NONE, res.getString(R.string.register_product));
        return super.onCreateOptionsMenu(menu);
    }

	// メニュー実装
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == menuCodes.registerProduct.ordinal()) {
			Intent intent = new Intent(this, ProductEditActivity.class);
			intent.putExtra("IS_NEW", true);
            startActivity(intent);

			return true;
		}

        return false;
    }

	// リスト再表示
	private void refreshProductMenu() {
		ProductListItem[] productListItems = new ProductListItem[Warehouse.Products().getList().size()];

		for (int i = 0; i < Warehouse.Products().getList().size(); i++) {
			Product p = Warehouse.Products().getList().get(i);
			Drawable thumb = null;
			if (p.getThumbnail() != null)
				thumb = p.getThumbnail().data;

			productListItems[i] = new ProductListItem(thumb, p.getName(),
									p.getSku(), p.getCountOnHand(), p.getPermalink(), p.getPrice(), p.getId());
		}

		statusText.setText(Warehouse.Products().getList().size() + this.getString(R.string.products_counter) );

		productsAdapter = new ProductListAdapter(this, productListItems);
		productList.setAdapter(productsAdapter);
		productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				productListClickHandler(parent, view, position);
			}
		});

	}

	// 商品詳細ページへ
	public static void showProductDetails(Context ctx, Product product, int selectMode) {
		ProductsMenuActivity.setSelectedProduct(product);
		Intent productDetailsIntent = new Intent(ctx, ProductDetailsActivity.class);
		if (mode == Warehouse.ResultCodes.UPDATE_PRODUCT_BARCODE.ordinal()) {
			String modeString = intent.getStringExtra("MODE");
			String barcodeString = intent.getStringExtra("BARCODE");
			productDetailsIntent.putExtra("MODE", modeString);
			productDetailsIntent.putExtra("BARCODE", barcodeString);
		//} else if (selectMode == Warehouse.ResultCodes.STOCK_PRODUCT.ordinal()) {
		//	productDetailsIntent.putExtra("MODE", "STOCK_PRODUCT");
		} else if (mode == Warehouse.ResultCodes.ADD_PRODUCT.ordinal()) {
			String modeString = intent.getStringExtra("MODE");
			productDetailsIntent.putExtra("MODE", modeString);
		}
    	ctx.startActivity(productDetailsIntent);
	}

	// どの商品を選択したか
	public static void selectProductActivity(Context ctx, String format, String contents) {
		Intent selectOneProduct = new Intent(ctx, ProductsMenuActivity.class);
		selectOneProduct.putExtra("MODE", "PRODUCT_SELECT");
		selectOneProduct.putExtra("FORMAT", format);
		selectOneProduct.putExtra("CONTENTS", contents);
		((Activity)ctx).startActivityForResult(selectOneProduct, Warehouse.ResultCodes.PRODUCT_SELECT.ordinal());
	}

	//
	public static void listProductsActivity(Context ctx) {
		Intent listProducts = new Intent(ctx, ProductsMenuActivity.class);
		listProducts.putExtra("MODE", "PRODUCT_LIST");
		((Activity)ctx).startActivityForResult(listProducts, Warehouse.ResultCodes.PRODUCT_LIST.ordinal());
	}

	// リストアイテムをクリックした時
	private void productListClickHandler(AdapterView<?> parent, View view, int position) {
		if (mode == Warehouse.ResultCodes.PRODUCT_SELECT.ordinal()) {
			Warehouse.Products().setSelect(Warehouse.Products().getList().get(position));
			setResult(ResultCodes.PRODUCT_SELECT.ordinal());
			finish();
		} else {
			ProductsMenuActivity.showProductDetails(this, Warehouse.Products().getList().get(position), mode);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Warehouse.ResultCodes.SCAN.ordinal()) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //TODO limit this to bar code types?
                if (format != "QR_CODE") {
                	//Assume barcode, and barcodes correlate to products
                	//Toast.makeText(this, "[" + format + "]: " + contents + "\nSearching!", Toast.LENGTH_LONG).show();
                	Warehouse.setContext(this);
                	new ProductSearcher(Warehouse.getContext(), format, contents).execute();

                	//Toast.makeText(this, "Results:" + products.count, Toast.LENGTH_LONG).show();
                }
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_LONG).show();
            }
        }
    }

	// ゲッター、セッター
	public static Product getSelectedProduct() {
		return Warehouse.Products().getSelected();
	}
	public static void setSelectedProduct(Product selectedProduct) {
		if (selectedProduct == null)
			selectedProduct = new Product(0, "", "", 0, 0, "", "");

		Warehouse.Products().setSelect(selectedProduct);
	}


	/////////////////////////////////////////////////////////////////////////////////////
	//
	// 各種ソート
	//
	/////////////////////////////////////////////////////////////////////////////////////

	// ▽ボタンを初期に
	public void clearImage() {
		if (updown) {
			backwardButton.setImageResource(android.R.drawable.arrow_down_float);
			updown = false;
		}
	}

	// 並びを逆にする
	public void switchOrder() {
		ArrayList<Product> sortedList = new ArrayList<Product>();

		for (int i = Warehouse.Products().getList().size() - 1; i >= 0; i--) {
			sortedList.add(Warehouse.Products().getList().get(i));
		}

		Warehouse.Products().setList(sortedList);
		refreshProductMenu();
	}

	// 値段順
	public void sortPrice() {
		Product temp;

		for (int i = 0; i < Warehouse.Products().getList().size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Products().getList().size(); j++) {
				if (Warehouse.Products().getList().get(i).getPrice() <
															Warehouse.Products().getList().get(j).getPrice()) {
					temp = Warehouse.Products().getList().get(i);
					Warehouse.Products().getList().set(i, Warehouse.Products().getList().get(j));
					Warehouse.Products().getList().set(j, temp);
				} else if (Warehouse.Products().getList().get(i).getPrice() ==
															Warehouse.Products().getList().get(j).getPrice()) {
					if (Warehouse.Products().getList().get(i).getId() >
															Warehouse.Products().getList().get(j).getId()) {
						temp = Warehouse.Products().getList().get(i);
						Warehouse.Products().getList().set(i, Warehouse.Products().getList().get(j));
						Warehouse.Products().getList().set(j, temp);
					}
				}
			}
		}

		refreshProductMenu();
	}

	// 在庫数順
	public void sortCountOnHand() {
		Product temp;

		for (int i = 0; i < Warehouse.Products().getList().size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Products().getList().size(); j++) {
				if (Warehouse.Products().getList().get(i).getCountOnHand() <
														Warehouse.Products().getList().get(j).getCountOnHand()) {
					temp = Warehouse.Products().getList().get(i);
					Warehouse.Products().getList().set(i, Warehouse.Products().getList().get(j));
					Warehouse.Products().getList().set(j, temp);
				} else if (Warehouse.Products().getList().get(i).getCountOnHand() ==
														Warehouse.Products().getList().get(j).getCountOnHand()) {
					if (Warehouse.Products().getList().get(i).getId() >
														Warehouse.Products().getList().get(j).getId()) {
						temp = Warehouse.Products().getList().get(i);
						Warehouse.Products().getList().set(i, Warehouse.Products().getList().get(j));
						Warehouse.Products().getList().set(j, temp);
					}
				}
			}
		}

		refreshProductMenu();
	}

	// 名前順
	public void sortName(){
		Product temp;

		for (int i = 0; i < Warehouse.Products().getList().size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Products().getList().size(); j++) {
				if (Warehouse.Products().getList().get(i).getName().compareTo
														(Warehouse.Products().getList().get(j).getName()) > 0) {
					temp = Warehouse.Products().getList().get(i);
					Warehouse.Products().getList().set(i, Warehouse.Products().getList().get(j));
					Warehouse.Products().getList().set(j, temp);
				} else if (Warehouse.Products().getList().get(i).getName().compareTo
														(Warehouse.Products().getList().get(j).getName()) == 0) {
					if (Warehouse.Products().getList().get(i).getId() >
														Warehouse.Products().getList().get(j).getId()) {
						temp = Warehouse.Products().getList().get(i);
						Warehouse.Products().getList().set(i, Warehouse.Products().getList().get(j));
						Warehouse.Products().getList().set(j, temp);
					}
				}
			}
		}

		refreshProductMenu();
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
