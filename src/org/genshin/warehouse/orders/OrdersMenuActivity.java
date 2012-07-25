package org.genshin.warehouse.orders;

import java.util.ArrayList;

import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.orders.Order;
import org.genshin.warehouse.orders.OrderListAdapter;
import org.genshin.warehouse.orders.OrderListItem;
import org.genshin.warehouse.orders.Orders;
import org.genshin.warehouse.orders.OrdersMenuActivity;
import org.genshin.warehouse.orders.OrdersMenuActivity.menuCodes;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

public class OrdersMenuActivity extends Activity {
	private static Orders orders;
	private static Order selectedOrder;
	private ArrayList<Order> list;
	private SpreeConnector spree;
	
	private OrderListAdapter ordersAdapter;
	
	private ListView orderList;
	private MultiAutoCompleteTextView searchBar;
	private Button clearButton;
	private Button searchButton;
	
	private Spinner orderSpinner;
	private ArrayAdapter<String> sadapter;
	
	private ImageButton backwardButton;
	private boolean updown = false;		// falseの時は▽、trueの時は△表示
	
	private void hookupInterface() {
		orderList = (ListView) findViewById(R.id.order_menu_list);
        
        searchBar = (MultiAutoCompleteTextView) findViewById(R.id.order_menu_searchbox);
        
        clearButton = (Button) findViewById(R.id.order_menu_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				orders.clear();
				searchBar.setText("");
				refreshOrderMenu();
				clearImage();
				orderSpinner.setSelection(0);
			}
		});
		
		//Standard text search hookup
		searchButton = (Button) findViewById(R.id.order_menu_search_button);
		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new SearchOrdersRefresh(v.getContext(), searchBar.getText().toString()).execute();
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
	    sadapter.add("初期状態に戻す");
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
                	case 1:		// 初期状態に戻す
                		new NewOrdersRefresh(Warehouse.getContext(), 10).execute();
                		clearImage();
                		break;
                	case 2:		// 名前順
						//sortName();
						clearImage();
                		break;
	                case 3:		// 値段順
	                	//sortPrice();
	                	clearImage();
	                	break;
	                case 4:		// 在庫数順
	                	//sortCountOnHand();
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
	    backwardButton = (ImageButton)findViewById(R.id.order_menu_backward_button);
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
        setContentView(R.layout.orders);
        Warehouse.setContext(this);
        
        hookupInterface();
        
        spree = new SpreeConnector(this);
        if (orders == null) {
        	orders = new Orders(this, spree);
        	new NewOrdersRefresh(Warehouse.getContext(), 10).execute();
        }
        
        new OrdersListRefresh(Warehouse.getContext()).execute();
        
	}

	public static enum menuCodes { registerOrder };

	// メニュー追加
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
        // メニューアイテムを追加する
        menu.add(Menu.NONE, menuCodes.registerOrder.ordinal(), Menu.NONE, "新規注文");
        return super.onCreateOptionsMenu(menu);
    }
	
	// メニュー実装
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		/*
		if (id == menuCodes.registerOrder.ordinal()) {
			Intent intent = new Intent(this, OrderEditActivity.class);
			intent.putExtra("IS_NEW", true);
            startActivity(intent);
        	
			return true;
		}
		*/
        
        return false;
    }
	
	public static void showOrderDetails(Context ctx, Order order) {
		OrdersMenuActivity.setSelectedOrder(order);
		Intent orderDetailsIntent = new Intent(ctx, OrderDetailsActivity.class);
    	ctx.startActivity(orderDetailsIntent);
	}
	
	private void orderListClickHandler(AdapterView<?> parent, View view, int position) {
		OrdersMenuActivity.showOrderDetails(this, orders.list.get(position));				
	}


	/*
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ResultCodes.SCAN.ordinal()) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //TODO limit this to bar code types?
                if (format != "QR_CODE") {
                	//Assume barcode, and barcodes correlate to orders
                	//Toast.makeText(this, "[" + format + "]: " + contents + "\nSearching!", Toast.LENGTH_LONG).show();
                	orders.findByBarcode(contents);
                	//if we have one hit that's the order we want, so go to it
                	refreshOrderMenu();
                	if (orders.list.size() == 1)
                		showOrderDetails(this, orders.list.get(0));
                    
                	//Toast.makeText(this, "Results:" + orders.count, Toast.LENGTH_LONG).show();
                }
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
    */

	public static Order getSelectedOrder() {
		return selectedOrder;
	}

	public static void setSelectedOrder(Order selectedOrder) {
		if (selectedOrder == null) {
		}
		
		OrdersMenuActivity.selectedOrder = selectedOrder;
	}
	
	private void refreshOrderMenu() {	
		OrderListItem[] orderListItems = new OrderListItem[orders.list.size()];
		
		for (int i = 0; i < orders.list.size(); i++) {
			Order p = orders.list.get(i);
			orderListItems[i] = new OrderListItem(p.number, p.date, p.name, p.count, p.price, 
					p.division, p.paymentState, p.pickingState, p.packingState, p.shipmentState);
		}
		
		ordersAdapter = new OrderListAdapter(Warehouse.getContext(), orderListItems);
		orderList.setAdapter(ordersAdapter);
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	orderListClickHandler(parent, view, position);
            }
        });
	}
	
	class OrdersListRefresh extends NetworkTask {

		public OrdersListRefresh(Context ctx) {
			super(ctx);
		}
		
		@Override
		protected void complete() {
			orderList = (ListView) findViewById(R.id.order_menu_list);
			refreshOrderMenu();
		}	
	}
	
	class NewOrdersRefresh extends OrdersListRefresh {
		private int count;
		
		public NewOrdersRefresh(Context ctx, int count) {
			super(ctx);
			this.count = count;
		}

		@Override
		protected void process() {
			orders.getNewestOrders(count);
		}
		
	}
	
	class SearchOrdersRefresh extends OrdersListRefresh {
		Context ctx;
		String query;
		String escapedQuery;
		
		public SearchOrdersRefresh(Context ctx, String query) {
			super(ctx);
			this.ctx = ctx;
			this.query = query;
		}
		
		@Override
		protected void process() {
			orders.textSearch(query);
		}
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
		ArrayList<Order> sortedList = new ArrayList<Order>();
		
		for (int i = orders.list.size() - 1; i >= 0; i--) {
			sortedList.add(orders.list.get(i));
		}
		
		orders.list = sortedList;
		new OrdersListRefresh(Warehouse.getContext());
	}
	
	/*
	// 値段順
	public void sortPrice() {
		Order temp;

		for (int i = 0; i < orders.list.size() - 1; i++) {
			for (int j = i + 1; j < orders.list.size(); j++) {
				if (orders.list.get(i).price < orders.list.get(j).price) {
					temp = orders.list.get(i);
					orders.list.set(i, orders.list.get(j));
					orders.list.set(j, temp);
				} else if (orders.list.get(i).price == orders.list.get(j).price) {
					if (orders.list.get(i).id > orders.list.get(j).id) {
						temp = orders.list.get(i);
						orders.list.set(i, orders.list.get(j));
						orders.list.set(j, temp);
					}
				}
			}
		}

		refreshOrderMenu();
	}
	
	// 在庫数順
	public void sortCountOnHand() {
		Order temp;
		
		for (int i = 0; i < orders.list.size() - 1; i++) {
			for (int j = i + 1; j < orders.list.size(); j++) {
				if (orders.list.get(i).countOnHand < orders.list.get(j).countOnHand) {
					temp = orders.list.get(i);
					orders.list.set(i, orders.list.get(j));
					orders.list.set(j, temp);
				} else if (orders.list.get(i).countOnHand == orders.list.get(j).countOnHand) {
					if (orders.list.get(i).id > orders.list.get(j).id) {
						temp = orders.list.get(i);
						orders.list.set(i, orders.list.get(j));
						orders.list.set(j, temp);
					}
				}
			}
		}

		refreshOrderMenu();
	}
	
	// 名前順
	public void sortName(){
		Order temp;
		
		for (int i = 0; i < orders.list.size() - 1; i++) {
			for (int j = i + 1; j < orders.list.size(); j++) {
				if (orders.list.get(i).name.compareTo(orders.list.get(j).name) > 0) {
					temp = orders.list.get(i);
					orders.list.set(i, orders.list.get(j));
					orders.list.set(j, temp);
				} else if (orders.list.get(i).name.compareTo(orders.list.get(j).name) == 0) {
					if (orders.list.get(i).id > orders.list.get(j).id) {
						temp = orders.list.get(i);
						orders.list.set(i, orders.list.get(j));
						orders.list.set(j, temp);
					}
				}
			}
		}

		refreshOrderMenu();
	}
	*/
	
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
