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
import android.app.AlertDialog;
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
	    sadapter.add("未選択");
	    sadapter.add("初期値に戻す");
	    sadapter.add("名前順");
	    sadapter.add("値段順");
	    sadapter.add("在庫数順");
	    orderSpinner.setPrompt("ソート");
	    orderSpinner.setAdapter(sadapter);
	    
	    orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                Spinner spinner = (Spinner) parent;
                //String item = (String) spinner.getSelectedItem();
                switch(position) {
                	case 0:		// 未選択
                		break;
                	case 1:		// 初期値に戻す
                		new SearchProductsRefresh(view.getContext(), searchBar.getText().toString()).execute();
                		clearImage();
                		break;
                	case 2:		// 名前順
						sortName();
						clearImage();
                		break;
	                case 3:		// 値段順
	                	sortPrice();
	                	clearImage();
	                	break;
	                case 4:		// 在庫数順
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
				Toast.makeText(this, getString(R.string.select_a_product), Toast.LENGTH_LONG).show();
			} else if (modeString.equals("PRODUCT_LIST")) {
				
			} else if (modeString.equals("UPDATE_PRODUCT_BARCODE")) {
				mode = Warehouse.ResultCodes.UPDATE_PRODUCT_BARCODE.ordinal();
				Toast.makeText(this, getString(R.string.select_a_product), Toast.LENGTH_LONG).show();
			}
		} else if (Warehouse.Products().list.size() == 0)
			new NewProductsRefresh(this, 10).execute();
	}
	
	class ProductsListRefresh extends NetworkTask {

		public ProductsListRefresh(Context ctx) {
			super(ctx);
		}
		
		@Override
		protected void complete() {
			ListView productList = (ListView) findViewById(R.id.product_menu_list);
			//refreshProductMenu();
			ProductListItem[] productListItems = new ProductListItem[Warehouse.Products().list.size()];
			
			for (int i = 0; i < Warehouse.Products().list.size(); i++) {
				Product p = Warehouse.Products().list.get(i);
				Drawable thumb = null;
				if (p.thumbnail != null)
					thumb = p.thumbnail.data;
				
				productListItems[i] = new ProductListItem(thumb, p.name, p.sku, p.countOnHand, p.permalink, p.price, p.id);
			}
			
			statusText.setText(Warehouse.Products().count + Warehouse.getContext().getString(R.string.products_counter) );
			
			productsAdapter = new ProductListAdapter(Warehouse.getContext(), productListItems);
			productList.setAdapter(productsAdapter);
			productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					productListClickHandler(parent, view, position);
				}
			});
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
	
	private void refreshProductMenu() {		
		ProductListItem[] productListItems = new ProductListItem[Warehouse.Products().list.size()];
		
		for (int i = 0; i < Warehouse.Products().list.size(); i++) {
			Product p = Warehouse.Products().list.get(i);
			Drawable thumb = null;
			if (p.thumbnail != null)
				thumb = p.thumbnail.data;
			
			productListItems[i] = new ProductListItem(thumb, p.name, p.sku, p.countOnHand, p.permalink, p.price, p.id);
		}
		
		statusText.setText(Warehouse.Products().count + this.getString(R.string.products_counter) );
		
		productsAdapter = new ProductListAdapter(this, productListItems);
		productList.setAdapter(productsAdapter);
		productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				productListClickHandler(parent, view, position);
			}
		});
        
	}
	
	public static void showProductDetails(Context ctx, Product product) {
		ProductsMenuActivity.setSelectedProduct(product);
		Intent productDetailsIntent = new Intent(ctx, ProductDetailsActivity.class);
		if (mode == Warehouse.ResultCodes.UPDATE_PRODUCT_BARCODE.ordinal()) {
			String modeString = intent.getStringExtra("MODE");
			String barcodeString = intent.getStringExtra("BARCODE");
			productDetailsIntent.putExtra("MODE", modeString);
			productDetailsIntent.putExtra("BARCODE", barcodeString);
		}
    	ctx.startActivity(productDetailsIntent);
	}
	
	public static void selectProductActivity(Context ctx, String format, String contents) {
		Intent selectOneProduct = new Intent(ctx, ProductsMenuActivity.class);
		selectOneProduct.putExtra("MODE", "PRODUCT_SELECT");
		selectOneProduct.putExtra("FORMAT", format);
		selectOneProduct.putExtra("CONTENTS", contents);
		((Activity)ctx).startActivityForResult(selectOneProduct, Warehouse.ResultCodes.PRODUCT_SELECT.ordinal());
	}
	
	public static void listProductsActivity(Context ctx) {
		Intent listProducts = new Intent(ctx, ProductsMenuActivity.class);
		listProducts.putExtra("MODE", "PRODUCT_LIST");
		((Activity)ctx).startActivityForResult(listProducts, Warehouse.ResultCodes.PRODUCT_LIST.ordinal());
	}
	
	private void productListClickHandler(AdapterView<?> parent, View view, int position) {
		if (mode == Warehouse.ResultCodes.PRODUCT_SELECT.ordinal()) {
			Warehouse.Products().select(Warehouse.Products().list.get(position));
			setResult(ResultCodes.PRODUCT_SELECT.ordinal());
			finish();
		} else {
			ProductsMenuActivity.showProductDetails(this, Warehouse.Products().list.get(position));
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
                	Warehouse.Products().findByBarcode(contents);
                	//if we have one hit that's the product we want, so go to it
                	refreshProductMenu();
                	if (Warehouse.Products().list.size() == 1)
                		showProductDetails(this, Warehouse.Products().list.get(0));
                    
                	//Toast.makeText(this, "Results:" + products.count, Toast.LENGTH_LONG).show();
                }
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_LONG).show();
            }
        }
    }

	public static Product getSelectedProduct() {
		return Warehouse.Products().selected();
	}

	public static void setSelectedProduct(Product selectedProduct) {
		if (selectedProduct == null)
			selectedProduct = new Product(0, "", "", 0, 0, "", "");
		
		Warehouse.Products().select(selectedProduct);
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
		
		for (int i = Warehouse.Products().list.size() - 1; i >= 0; i--) {
			sortedList.add(Warehouse.Products().list.get(i));
		}
		
		Warehouse.Products().list = sortedList;
		refreshProductMenu();
	}
	
	// 値段順
	public void sortPrice() {
		Product temp;

		for (int i = 0; i < Warehouse.Products().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Products().list.size(); j++) {
				if (Warehouse.Products().list.get(i).price < Warehouse.Products().list.get(j).price) {
					temp = Warehouse.Products().list.get(i);
					Warehouse.Products().list.set(i, Warehouse.Products().list.get(j));
					Warehouse.Products().list.set(j, temp);
				} else if (Warehouse.Products().list.get(i).price == Warehouse.Products().list.get(j).price) {
					if (Warehouse.Products().list.get(i).id > Warehouse.Products().list.get(j).id) {
						temp = Warehouse.Products().list.get(i);
						Warehouse.Products().list.set(i, Warehouse.Products().list.get(j));
						Warehouse.Products().list.set(j, temp);
					}
				}
			}
		}

		refreshProductMenu();
	}
	
	// 在庫数順
	public void sortCountOnHand() {
		Product temp;
		
		for (int i = 0; i < Warehouse.Products().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Products().list.size(); j++) {
				if (Warehouse.Products().list.get(i).countOnHand < Warehouse.Products().list.get(j).countOnHand) {
					temp = Warehouse.Products().list.get(i);
					Warehouse.Products().list.set(i, Warehouse.Products().list.get(j));
					Warehouse.Products().list.set(j, temp);
				} else if (Warehouse.Products().list.get(i).countOnHand == Warehouse.Products().list.get(j).countOnHand) {
					if (Warehouse.Products().list.get(i).id > Warehouse.Products().list.get(j).id) {
						temp = Warehouse.Products().list.get(i);
						Warehouse.Products().list.set(i, Warehouse.Products().list.get(j));
						Warehouse.Products().list.set(j, temp);
					}
				}
			}
		}

		refreshProductMenu();
	}
	
	// 名前順
	public void sortName(){
		Product temp;
		
		for (int i = 0; i < Warehouse.Products().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Products().list.size(); j++) {
				if (Warehouse.Products().list.get(i).name.compareTo(Warehouse.Products().list.get(j).name) > 0) {
					temp = Warehouse.Products().list.get(i);
					Warehouse.Products().list.set(i, Warehouse.Products().list.get(j));
					Warehouse.Products().list.set(j, temp);
				} else if (Warehouse.Products().list.get(i).name.compareTo(Warehouse.Products().list.get(j).name) == 0) {
					if (Warehouse.Products().list.get(i).id > Warehouse.Products().list.get(j).id) {
						temp = Warehouse.Products().list.get(i);
						Warehouse.Products().list.set(i, Warehouse.Products().list.get(j));
						Warehouse.Products().list.set(j, temp);
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
