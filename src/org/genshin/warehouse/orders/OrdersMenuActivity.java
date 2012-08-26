package org.genshin.warehouse.orders;

import java.util.ArrayList;

import org.genshin.gsa.network.NetworkTask;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.orders.Order;
import org.genshin.warehouse.orders.OrderListAdapter;
import org.genshin.warehouse.orders.OrderListItem;
import org.genshin.warehouse.orders.OrdersMenuActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

public class OrdersMenuActivity extends Activity {
	
	private OrderListAdapter ordersAdapter;
	private ListView orderList;
	
	private Button searchButton;
	private Spinner orderSpinner;
	private ArrayAdapter<String> sadapter;
	
	private ImageButton backwardButton;
	private boolean updown = false;		// falseの時は▽、trueの時は△表示
	
	private void hookupInterface() {
		
		// 検索
		searchButton = (Button) findViewById(R.id.order_search);
		searchButton.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				new SearchOrdersRefresh(v.getContext(), "R0555").execute();
				clearImage();
				orderSpinner.setSelection(0);				
			}
		});
		
		orderList = (ListView) findViewById(R.id.order_menu_list);
		
		// Order spinner
		orderSpinner = (Spinner) findViewById(R.id.order_spinner);
		sadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	    sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // アイテムを追加します
	    sadapter.add(getString(R.string.no_select));
	    sadapter.add(getString(R.string.return_default));
	    sadapter.add(getString(R.string.order_date));
	    sadapter.add(getString(R.string.payment_state));
	    sadapter.add(getString(R.string.picking_state));
	    sadapter.add(getString(R.string.packing_state));
	    sadapter.add(getString(R.string.shipment_state));
	    orderSpinner.setPrompt(getString(R.string.sort));
	    orderSpinner.setAdapter(sadapter);
	    
	    orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                Spinner spinner = (Spinner) parent;
                switch(position) {
                	case 0:		// 未選択
                		break;
                	case 1:		// 初期状態に戻す
                		new NewOrdersRefresh(Warehouse.getContext(), 25, "NORMAL").execute();
                		clearImage();
                		break;
                	case 2:		// 注文日順
						sortDate();
						clearImage();
                		break;
	                case 3:		// 入金状態
	                	sortPayment();
	                	clearImage();
	                	break;
	                case 4:		// ピッキング状態
	                	sortPicking();
	                	clearImage();
	                	break;
	                case 5:		// 梱包状態
	                	sortPacking();
	                	clearImage();
	                	break;
	                case 6:		// 発送状態
	                	sortShipment();
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

        Intent intent = getIntent();
        String mode = intent.getStringExtra("MODE");

        if (mode != null)
        	new NewOrdersRefresh(Warehouse.getContext(), 25, "NEW_ORDER").execute();
        else
        	new NewOrdersRefresh(Warehouse.getContext(), 25, "NORMAL").execute();   
	}

	public static enum menuCodes { registerOrder };

	// メニュー追加
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
        // メニューアイテムを追加する
        menu.add(Menu.NONE, menuCodes.registerOrder.ordinal(), Menu.NONE, res.getString(R.string.new_order));
        return super.onCreateOptionsMenu(menu);
    }
	
	// メニュー実装
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == menuCodes.registerOrder.ordinal()) {
			AlertDialog.Builder question = new AlertDialog.Builder(this);
			question.setTitle(getString(R.string.create_new_order));
			question.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					Order order = new Order();
					new SaveOrder(getApplicationContext(), true, order).execute();
				}
			});
			question.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			question.show();
        	
			return true;
		}

        return false;
    }
	
	public static void showOrderDetails(Context ctx, Order order) {
		OrdersMenuActivity.setSelectedOrder(order);
		Intent orderDetailsIntent = new Intent(ctx, OrderDetailsActivity.class);
    	ctx.startActivity(orderDetailsIntent);
	}
	
	private void orderListClickHandler(AdapterView<?> parent, View view, int position) {
		OrdersMenuActivity.showOrderDetails(this, Warehouse.Orders().list.get(position));				
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
                	if (Warehouse.Orders().list.size() == 1)
                		showOrderDetails(this, Warehouse.Orders().list.get(0));
                    
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
		return Warehouse.Orders().selected();
	}

	public static void setSelectedOrder(Order selectedOrder) {
		if (selectedOrder == null) {
			selectedOrder = new Order();
		}
		
		Warehouse.Orders().select(selectedOrder);
	}
	
	private void refreshOrderMenu() {	
		OrderListItem[] orderListItems = new OrderListItem[Warehouse.Orders().list.size()];
		
		for (int i = 0; i < Warehouse.Orders().list.size(); i++) {
			Order p = Warehouse.Orders().list.get(i);
			orderListItems[i] = new OrderListItem(p.number, p.date, p.name, p.count, p.price, 
					p.division, p.paymentState, p.pickingState, p.packingState, p.shipmentState);
		}
		
		ordersAdapter = new OrderListAdapter(this, orderListItems);
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
		int count;
		String mode;
		
		public NewOrdersRefresh(Context ctx, int count, String mode) {
			super(ctx);
			this.count = count;
			this.mode = mode;
		}

		@Override
		protected void process() {
			Warehouse.Orders().getNewestOrders(count, mode);
		}		
	}
	
	class SearchOrdersRefresh extends OrdersListRefresh {
		String query;
		
		public SearchOrdersRefresh(Context ctx, String query) {
			super(ctx);
			this.query = query;
		}
		
		@Override
		protected void process() {
			Warehouse.Orders().textSearch(query);
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
		
		for (int i = Warehouse.Orders().list.size() - 1; i >= 0; i--) {
			sortedList.add(Warehouse.Orders().list.get(i));
		}
		
		Warehouse.Orders().list = sortedList;
		refreshOrderMenu();
	}
	
	// 注文日順
	public void sortDate() {
		Order temp;

		for (int i = 0; i < Warehouse.Orders().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Orders().list.size(); j++) {
				if (Warehouse.Orders().list.get(j).date.after(Warehouse.Orders().list.get(i).date)) {
					temp = Warehouse.Orders().list.get(i);
					Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
					Warehouse.Orders().list.set(j, temp);
				}
			}
		}

		refreshOrderMenu();
	}

	// 入金状態
	public void sortPayment() {
		Order temp;
		
		for (int i = 0; i < Warehouse.Orders().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Orders().list.size(); j++) {
				if (Warehouse.Orders().list.get(i).paymentState.compareTo(Warehouse.Orders().list.get(j).paymentState) > 0) {
					temp = Warehouse.Orders().list.get(i);
					Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
					Warehouse.Orders().list.set(j, temp);
				} else if (Warehouse.Orders().list.get(i).paymentState.compareTo(Warehouse.Orders().list.get(j).paymentState) == 0) {
					if (Warehouse.Orders().list.get(j).date.after(Warehouse.Orders().list.get(i).date)) {
						temp = Warehouse.Orders().list.get(i);
						Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
						Warehouse.Orders().list.set(j, temp);
					}
				}
			}
		}

		refreshOrderMenu();
	}

	// ピッキング状態
	public void sortPicking() {
		Order temp;
		
		for (int i = 0; i < Warehouse.Orders().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Orders().list.size(); j++) {
				if (Warehouse.Orders().list.get(i).pickingState.compareTo(Warehouse.Orders().list.get(j).pickingState) > 0) {
					temp = Warehouse.Orders().list.get(i);
					Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
					Warehouse.Orders().list.set(j, temp);
				} else if (Warehouse.Orders().list.get(i).pickingState.compareTo(Warehouse.Orders().list.get(j).pickingState) == 0) {
					if (Warehouse.Orders().list.get(j).date.after(Warehouse.Orders().list.get(i).date)) {
						temp = Warehouse.Orders().list.get(i);
						Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
						Warehouse.Orders().list.set(j, temp);
					}
				}
			}
		}

		refreshOrderMenu();
	}
	
	// 梱包状態
	public void sortPacking() {
		Order temp;
		
		for (int i = 0; i < Warehouse.Orders().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Orders().list.size(); j++) {
				if (Warehouse.Orders().list.get(i).packingState.compareTo(Warehouse.Orders().list.get(j).packingState) > 0) {
					temp = Warehouse.Orders().list.get(i);
					Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
					Warehouse.Orders().list.set(j, temp);
				} else if (Warehouse.Orders().list.get(i).packingState.compareTo(Warehouse.Orders().list.get(j).packingState) == 0) {
					if (Warehouse.Orders().list.get(j).date.after(Warehouse.Orders().list.get(i).date)) {
						temp = Warehouse.Orders().list.get(i);
						Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
						Warehouse.Orders().list.set(j, temp);
					}
				}
			}
		}

		refreshOrderMenu();
	}
	
	// 発送状態
	public void sortShipment() {
		Order temp;
		for (int i = 0; i < Warehouse.Orders().list.size() - 1; i++) {
			for (int j = i + 1; j < Warehouse.Orders().list.size(); j++) {
				if (Warehouse.Orders().list.get(i).shipmentState.compareTo(Warehouse.Orders().list.get(j).shipmentState) > 0) {
					temp = Warehouse.Orders().list.get(i);
					Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
					Warehouse.Orders().list.set(j, temp);
				} else if (Warehouse.Orders().list.get(i).shipmentState.equals(Warehouse.Orders().list.get(j).shipmentState)) {
					if (Warehouse.Orders().list.get(j).date.after(Warehouse.Orders().list.get(i).date)) {
						temp = Warehouse.Orders().list.get(i);
						Warehouse.Orders().list.set(i, Warehouse.Orders().list.get(j));
						Warehouse.Orders().list.set(j, temp);
					}
				}
			}
		}

		refreshOrderMenu();
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
