package org.genshin.warehouse.orders;

import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class OrderDetailsActivity extends TabActivity {
	SpreeConnector spree;
	Bundle extras;

	private TextView number;
	private TextView statement;
	private TextView mainTotal;
	private TextView shipmentState;
	private TextView paymentState;
	
	private TextView itemTotal;
	private TextView cost;
	private TextView lastTotal;
	private Button pickingButton;	
	private Button canselButton;
	private Button editButton;
	
	private TextView paymentAddress;
	private TextView shipmentAddress;
	private TextView email;
	private Button accountEditButton;
	
	private Order order;
	private JSONObject container;
	
	private ListView listView;
	private OrderDetailsAdapter adapter;
	
	private ListView paymentListView;
	private OrderDetailsPaymentAdapter paymentAdapter;
	private Button paymentNewButton;
	
	private ListView shipmentListView;
	private OrderDetailsShipmentAdapter shipmentAdapter;
	private Button shipmentNewButton;

	private void getOrderInfo() {
		order = OrdersMenuActivity.getSelectedOrder();
	}
	
	private void initViewElements() {
		number = (TextView) findViewById(R.id.order_details_number);
		statement = (TextView) findViewById(R.id.order_details_main_statement);
		mainTotal = (TextView) findViewById(R.id.order_details_main_total);
		shipmentState = (TextView) findViewById(R.id.order_details_main_shipment);
		paymentState = (TextView) findViewById(R.id.order_details_main_payment);
		
		listView = (ListView) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_menu_list);

		itemTotal = (TextView) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_item_total);
		cost = (TextView) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_cost);
		lastTotal =(TextView) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_last_total);
		
		paymentAddress = (TextView) findViewById
				(R.id.order_details_account).findViewById(R.id.order_details_payment_address);
		shipmentAddress = (TextView) findViewById
				(R.id.order_details_account).findViewById(R.id.order_details_shipment_address);
		email = (TextView) findViewById
				(R.id.order_details_account).findViewById(R.id.order_details_email);
		
		paymentListView = (ListView) findViewById
				(R.id.order_details_payment).findViewById(R.id.order_details_payment_list);
		shipmentListView = (ListView) findViewById
				(R.id.order_details_shipment).findViewById(R.id.order_details_shipment_list);		
	}
	
	private void hookupInterface() {
		pickingButton = (Button) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_picking_button);
		canselButton = (Button) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_cansel_button);
		editButton = (Button) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_edit_button);
		accountEditButton = (Button) findViewById
				(R.id.order_details_account).findViewById(R.id.order_details_accountedit_button);
		paymentNewButton = (Button) findViewById
				(R.id.order_details_payment).findViewById(R.id.order_details_payment_new_button);
		shipmentNewButton = (Button) findViewById
				(R.id.order_details_shipment).findViewById(R.id.order_details_shipment_new_button);
	}
	
	private void createTab() {
		TabHost tabHost = getTabHost();
        
        TabSpec tab1 = tabHost.newTabSpec("tab1");
        tab1.setIndicator(getString(R.string.order_details));
        tab1.setContent(R.id.order_details_main);
        tabHost.addTab(tab1);
        
        TabSpec tab2 = tabHost.newTabSpec("tab2");
        tab2.setIndicator(getString(R.string.customer_info));
        tab2.setContent(R.id.order_details_account);
        tabHost.addTab(tab2);
        
        TabSpec tab3 = tabHost.newTabSpec("tab3");
        tab3.setIndicator(getString(R.string.adjustment));
        tab3.setContent(R.id.order_details_adjustment);
        tabHost.addTab(tab3);
        
        TabSpec tab4 = tabHost.newTabSpec("tab4");
        tab4.setIndicator(getString(R.string.payment_method));
        tab4.setContent(R.id.order_details_payment);
        tabHost.addTab(tab4);
        
        TabSpec tab5 = tabHost.newTabSpec("tab5");
        tab5.setIndicator(getString(R.string.shipping));
        tab5.setContent(R.id.order_details_shipment);
        tabHost.addTab(tab5);
        
        TabSpec tab6 = tabHost.newTabSpec("tab6");
        tab6.setIndicator(getString(R.string.returned_goods));
        tab6.setContent(R.id.order_details_return);
        tabHost.addTab(tab6);
        
        tabHost.setCurrentTab(0);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details); 
        Warehouse.setContext(this);
        
        spree = new SpreeConnector(this);

        createTab();
		getOrderInfo();
		initViewElements();
		hookupInterface();
		new getOrderDetails(this, order.number).execute();
	}
	
	//public static enum menuCodes { stock, destock, registerVisualCode, addOrderImage, editOrderDetails };
	
	/*
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
        // メニューアイテムを追加します
        menu.add(Menu.NONE, menuCodes.stock.ordinal(), Menu.NONE, res.getString(R.string.stock_in));
        menu.add(Menu.NONE, menuCodes.destock.ordinal(), Menu.NONE, res.getString(R.string.destock));
        menu.add(Menu.NONE, menuCodes.registerVisualCode.ordinal(), Menu.NONE, res.getString(R.string.register_barcode));
        menu.add(Menu.NONE, menuCodes.addOrderImage.ordinal(), Menu.NONE, res.getString(R.string.add_order_image));
        menu.add(Menu.NONE, menuCodes.editOrderDetails.ordinal(), Menu.NONE, res.getString(R.string.edit_order_details));
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	*/	  
		//Java can't do this!? WTF!
        /*switch (item.getItemId()) {
        	default:
        		return super.onOptionsItemSelected(item);
        	case registerVisualCode:
            
        		return true;
        }*//*
		int id = item.getItemId();

		if (id == menuCodes.registerVisualCode.ordinal()) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            //intent.putExtra("SCAN_MODE", "BARCODE_MODE");
            startActivityForResult(intent, ResultCodes.SCAN.ordinal());
        	
			return true;
		} else if (id == menuCodes.editOrderDetails.ordinal()) {
			Intent intent = new Intent(this, OrderEditActivity.class);
			startActivity(intent);
		}
        
        return false;
    }
	*/
	/*
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ResultCodes.SCAN.ordinal()) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //TODO limit this to bar code types?
                
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
    */
	
	public void getOrderJson(String number) {	
		try {
			JSONObject tmp = spree.connector.getJSONObject("api/orders/" + number + ".json");
			container = tmp.getJSONObject("order");
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			container = null;
		}
	}
	
	class getOrderDetails extends NetworkTask {
		private String number;

		public getOrderDetails(Context ctx, String number) {
			super(ctx);
			this.number = number;
		}	
		
		@Override
		protected void process() {
			getOrderJson(number);
		}
		
		@Override
		protected void complete() {
			new setOrderDetails(Warehouse.getContext(), container).execute();
		}
	}
	
	class setOrderDetails extends NetworkTask {
		private JSONObject container;
		OrderDetails orderDetails = new OrderDetails(Warehouse.getContext(), spree);

		public setOrderDetails(Context ctx, JSONObject container) {
			super(ctx);
			this.container = container;
		}	
		
		@Override
		protected void process() {
			orderDetails.processOLIContainer(container);
			orderDetails.putOrderDetails(container);
			orderDetails.getPayment(container);
			orderDetails.getShipment(container);
		}
		
		@Override
		protected void complete() {
			listView = (ListView) findViewById
					(R.id.order_details_main).findViewById(R.id.order_details_menu_list);
			paymentListView = (ListView) findViewById
					(R.id.order_details_payment).findViewById(R.id.order_details_payment_list);
			shipmentListView = (ListView) findViewById
					(R.id.order_details_shipment).findViewById(R.id.order_details_shipment_list);
			
			number.setText(order.number);
			statement.setText(orderDetails.statement);
			mainTotal.setText(orderDetails.mainTotal + getString(R.string.currency_unit));
			paymentAddress.setText(orderDetails.paymentAddress);
			shipmentAddress.setText(orderDetails.shipmentAddress);
			email.setText(orderDetails.email);

			// 注文した商品
			OrderLineItem[] orderLineItem = new OrderLineItem[orderDetails.list.size()];		
			for (int i = 0; i < orderDetails.list.size(); i++) {
				OrderLineItem p = orderDetails.list.get(i);
				
				orderLineItem[i] = new OrderLineItem(p.name, p.price, p.quantity, p.total);
			}
			adapter = new OrderDetailsAdapter(Warehouse.getContext(), orderLineItem);
			listView.setAdapter(adapter);
			setListViewHeightBasedOnChildren(listView, 0);
			
			// 請求先住所
			OrderDetailsPayment[] orderDetailsPayment = 
					new OrderDetailsPayment[orderDetails.paymentList.size()];		
			for (int i = 0; i < orderDetails.paymentList.size(); i++) {
				OrderDetailsPayment p = orderDetails.paymentList.get(i);
				
				orderDetailsPayment[i] = new OrderDetailsPayment(p.date, 
						p.amount, p.paymentMethod, p.paymentState, p.action);
			}
			paymentAdapter = new OrderDetailsPaymentAdapter(Warehouse.getContext(), orderDetailsPayment);
			paymentListView.setAdapter(paymentAdapter);
			setListViewHeightBasedOnChildren(paymentListView, 1);
			
			// 配送先住所
			OrderDetailsShipment[] orderDetailsShipment = 
					new OrderDetailsShipment[orderDetails.shipmentList.size()];		
			for (int i = 0; i < orderDetails.shipmentList.size(); i++) {
				OrderDetailsShipment p = orderDetails.shipmentList.get(i);
				
				orderDetailsShipment[i] = new OrderDetailsShipment
						(p.number, p.shippingMethod, p.tracking, p.cost, p.state, p.date, p.action);
			}
			shipmentAdapter = new OrderDetailsShipmentAdapter(Warehouse.getContext(), orderDetailsShipment);
			shipmentListView.setAdapter(shipmentAdapter);
			setListViewHeightBasedOnChildren(shipmentListView, 2);
			
			// 計
			itemTotal.setText("" + orderDetails.itemTotal);
			cost.setText("" + orderDetails.cost);
			lastTotal.setText("" + orderDetails.lastTotal);
		}
	}
	
	// ListViewの高さを動的に取得
	public void setListViewHeightBasedOnChildren(ListView listView, int type) {
		OrderDetailsAdapter adapter = null;
		OrderDetailsPaymentAdapter pAdapter = null;
		OrderDetailsShipmentAdapter sAdapter = null;

		// 設定するListViewからアダプタを取得する
		if (type == 0)
			adapter = (OrderDetailsAdapter) listView.getAdapter();
		else if (type == 1)
			pAdapter = (OrderDetailsPaymentAdapter) listView.getAdapter();
		else if (type == 2)
			sAdapter = (OrderDetailsShipmentAdapter) listView.getAdapter();

		int height = 0;
		int width = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);

		// アダプタのデータ分ループして、高さなどを設定
		if (type == 0) {
			for (int i = 0; i < adapter.getCount(); i++) {
				View view = adapter.getView(i, null, listView);
				view.measure(width, MeasureSpec.UNSPECIFIED);
				height += view.getMeasuredHeight();
			}
		} else if (type == 1) {
			for (int i = 0; i < pAdapter.getCount(); i++) {
				View view = pAdapter.getView(i, null, listView);
				view.measure(width, MeasureSpec.UNSPECIFIED);
				height += view.getMeasuredHeight();
			}
		} else if (type == 2) {
			for (int i = 0; i < sAdapter.getCount(); i++) {
				View view = sAdapter.getView(i, null, listView);
				view.measure(width, MeasureSpec.UNSPECIFIED);
				height += view.getMeasuredHeight();
			}
		}

		// 実際のListViewに反映する
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if (type == 0)
			params.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
		else if (type == 1)
			params.height = height + (listView.getDividerHeight() * (pAdapter.getCount() - 1));
		else if (type == 2)
			params.height = height + (listView.getDividerHeight() * (sAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
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