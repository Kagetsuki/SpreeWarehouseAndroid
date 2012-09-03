package org.genshin.warehouse.orders;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class OrderDetailsActivity extends TabActivity {
	private SpreeConnector spree;
	private Bundle extras;

	private TextView number;
	private TextView statement;
	private TextView mainTotal;
	private TextView shipmentState;
	private TextView paymentState;

	private TextView itemTotal;
	private TextView cost;
	private TextView lastTotal;
	private Button pickingButton;
	private Button addProductButton;
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

	private TextView paymentStill;
	private ListView paymentListView;
	private OrderDetailsPaymentAdapter paymentAdapter;
	private Button paymentNewButton;
	private Button paymentCompleteButton;
	private Button paymentVoidButton;

	private ListView shipmentListView;
	private OrderDetailsShipmentAdapter shipmentAdapter;
	private Button shipmentNewButton;

	private static OrderDetails orderDetails;

	// 各種ゲッター
	public static OrderDetails getOrderDetails() {
		OrderDetails select = orderDetails;
		return select;
	}

	public void getOrderInfo() {
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

		shipmentListView = (ListView) findViewById
				(R.id.order_details_shipment).findViewById(R.id.order_details_shipment_list);
	}

	private void hookupInterface() {
		pickingButton = (Button) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_picking_button);
		addProductButton = (Button) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_product_add);
		addProductButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EditProductActivity.class);
	            intent.putExtra("isNew", false);
	            startActivity(intent);
			}
		});
		canselButton = (Button) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_cansel_button);
		editButton = (Button) findViewById
				(R.id.order_details_main).findViewById(R.id.order_details_edit_button);
		accountEditButton = (Button) findViewById
				(R.id.order_details_account).findViewById(R.id.order_details_accountedit_button);
		accountEditButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EditAddressActivity.class);
				startActivity(intent);
			}
		});
		paymentNewButton = (Button) findViewById
				(R.id.order_details_payment).findViewById(R.id.order_details_payment_new_button);
		paymentCompleteButton = (Button) findViewById
				(R.id.order_details_payment).findViewById(R.id.order_details_payment_complete);
		// 支払い状態を完了にする
		paymentCompleteButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(Warehouse.getContext());
				dialog.setTitle(getString(R.string.attention_complete));
				dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// チェックボックスの状態を取得
						SparseBooleanArray checkedArray = paymentListView.getCheckedItemPositions();
						for (int i = 0; i < checkedArray.size(); i++) {
							// チェックされているもののみ変更
							if (checkedArray.get(i)) {
								OrderDetailsPayment list = new OrderDetailsPayment();
								list = (OrderDetailsPayment)paymentListView.getItemAtPosition(i);
								if (list.paymentState.equals("checkout")) {
									int id = list.id;
									new setPayment(getApplicationContext(), id, "COMPLETE").execute();
								}
							}
						}
					}
				});
				dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				dialog.show();
			}
		});
		paymentVoidButton = (Button) findViewById
				(R.id.order_details_payment).findViewById(R.id.order_details_payment_void);
		// 支払い状態を無効にする
		paymentVoidButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(Warehouse.getContext());
				dialog.setTitle(getString(R.string.attention_void));
				dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// チェックボックスの状態を取得する
						SparseBooleanArray checkedArray = paymentListView.getCheckedItemPositions();
						for (int i = 0; i < checkedArray.size(); i++) {
							// チェックされているもののみ変更
							if (checkedArray.get(i)) {
								OrderDetailsPayment list = new OrderDetailsPayment();
								list = (OrderDetailsPayment)paymentListView.getItemAtPosition(i);
								if (list.paymentState.equals("completed")) {
									int id = list.id;
									new setPayment(getApplicationContext(), id, "VOID").execute();
								}
							}
						}
					}
				});
				dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				dialog.show();
			}
		});
		shipmentNewButton = (Button) findViewById
				(R.id.order_details_shipment).findViewById(R.id.order_details_shipment_new_button);
	}

	// タブを作成
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
		new getOrderDetails(this, order.getNumber()).execute();
	}

	public static enum menuCodes { addProduct, editAddress, editPayment, editAdjustment, editShipment };

	// メニュー作成
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
        // メニューアイテムを追加
        menu.add(Menu.NONE, menuCodes.addProduct.ordinal(), Menu.NONE, res.getString(R.string.new_product_add));
        menu.add(Menu.NONE, menuCodes.editAddress.ordinal(), Menu.NONE, res.getString(R.string.edit_address));
        menu.add(Menu.NONE, menuCodes.editPayment.ordinal(), Menu.NONE, res.getString(R.string.edit_payment));
        menu.add(Menu.NONE, menuCodes.editAdjustment.ordinal(), Menu.NONE, res.getString(R.string.edit_adjustment));
        menu.add(Menu.NONE, menuCodes.editShipment.ordinal(), Menu.NONE, res.getString(R.string.edit_shipment));
        return super.onCreateOptionsMenu(menu);
    }

	// メニューの中身
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == menuCodes.addProduct.ordinal()) {
			Intent intent = new Intent(this, EditProductActivity.class);
            intent.putExtra("isNew", false);
            startActivity(intent);
			return true;
		} else if (id == menuCodes.editAddress.ordinal()) {
			Intent intent = new Intent(this, EditAddressActivity.class);
			startActivity(intent);
			return true;
			/*
		} else if (id == menuCodes.editPayment.ordinal()) {
			Intent intent = new Intent(this, EditPaymentActivity.class);
			startActivity(intent);
			return true;
		} else if (id == menuCodes.editAdjustment.ordinal()) {
			Intent intent = new Intent(this, EditAdjustmentActivity.class);
			startActivity(intent);
			return true;
		} else if (id == menuCodes.editShipment.ordinal()) {
			Intent intent = new Intent(this, EditShipmentActivity.class);
			startActivity(intent);
			return true;
			*/
		}

        return false;
    }

	/** ↓以降に使うかどうか不明*/
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

	// orderをJSONObjectから取り出す
	public void getOrderJson(String number) {
		try {
			JSONObject tmp = spree.connector.getJSONObject("api/orders/" + number + ".json");
			container = tmp.getJSONObject("order");
		} catch (JSONException e) {
			e.printStackTrace();
			container = null;
		}
	}

	// orderの詳細を取得
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
			orderDetails = new OrderDetails(Warehouse.getContext(), spree);
			new setOrderDetails(Warehouse.getContext(), container).execute();
		}
	}

	// orderの詳細を格納
	class setOrderDetails extends NetworkTask {
		private JSONObject container;

		public setOrderDetails(Context ctx, JSONObject container) {
			super(ctx);
			this.container = container;
		}

		@Override
		protected void process() {
			orderDetails.setLineItem(container);
			orderDetails.setOrderDetails(container);
			orderDetails.setPayment(container);
			orderDetails.setShipment(container);
		}

		@Override
		protected void complete() {
			listView = (ListView) findViewById
					(R.id.order_details_main).findViewById(R.id.order_details_menu_list);
			paymentStill = (TextView) findViewById
					(R.id.order_details_payment).findViewById(R.id.order_details_payment_still);
			paymentListView = (ListView) findViewById
					(R.id.order_details_payment).findViewById(R.id.order_details_payment_list);
			paymentListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			paymentListView.setItemsCanFocus(false);
			shipmentListView = (ListView) findViewById
					(R.id.order_details_shipment).findViewById(R.id.order_details_shipment_list);

			number.setText(orderDetails.getNumber());
			statement.setText(orderDetails.getStatement());
			mainTotal.setText(orderDetails.getMainTotal() + getString(R.string.currency_unit));
			paymentAddress.setText(orderDetails.getPaymentAddress());
			shipmentAddress.setText(orderDetails.getShipmentAddress());
			email.setText(orderDetails.getEmail());

			// 注文した商品
			OrderLineItem[] orderLineItem = new OrderLineItem[orderDetails.getItemList().size()];
			for (int i = 0; i < orderDetails.getItemList().size(); i++) {
				OrderLineItem p = orderDetails.getItemList().get(i);

				orderLineItem[i] = new OrderLineItem(p.getName(), p.getPrice(), p.getQuantity(), p.getTotal());
			}
			adapter = new OrderDetailsAdapter(Warehouse.getContext(), orderLineItem);
			listView.setAdapter(adapter);
			setListViewHeightBasedOnChildren(listView, 0);

			// 請求先住所
			OrderDetailsPayment[] orderDetailsPayment =
					new OrderDetailsPayment[orderDetails.getPaymentList().size()];
			for (int i = 0; i < orderDetails.getPaymentList().size(); i++) {
				OrderDetailsPayment p = orderDetails.getPaymentList().get(i);

				orderDetailsPayment[i] = new OrderDetailsPayment(p.id, p.date,
						p.amount, p.paymentMethod, p.paymentState);
			}
			paymentAdapter = new OrderDetailsPaymentAdapter(Warehouse.getContext(), orderDetailsPayment);
			paymentListView.setAdapter(paymentAdapter);
			setListViewHeightBasedOnChildren(paymentListView, 1);

			// 配送先住所
			OrderDetailsShipment[] orderDetailsShipment =
					new OrderDetailsShipment[orderDetails.getShipmentList().size()];
			for (int i = 0; i < orderDetails.getShipmentList().size(); i++) {
				OrderDetailsShipment p = orderDetails.getShipmentList().get(i);

				orderDetailsShipment[i] = new OrderDetailsShipment (p.getNumber(), p.getShippingMethod(),
										p.getTracking(), p.getCost(), p.getState(), p.getDate(), p.getAction());
			}
			shipmentAdapter = new OrderDetailsShipmentAdapter(Warehouse.getContext(), orderDetailsShipment);
			shipmentListView.setAdapter(shipmentAdapter);
			setListViewHeightBasedOnChildren(shipmentListView, 2);

			// 計
			itemTotal.setText("" + orderDetails.getItemTotal());
			cost.setText("" + orderDetails.getCost());
			lastTotal.setText("" + orderDetails.getLastTotal());

			// 未払い額計算
			Double calc = orderDetails.getMainTotal();
			for (int i = 0; i < orderDetails.getPaymentList().size(); i++) {
				if (orderDetails.getPaymentList().get(i).paymentState.equals("completed"))
					calc -= orderDetails.getPaymentList().get(i).amount;
			}
			paymentStill.setText(getString(R.string.amount_owed) + calc + getString(R.string.currency_unit));
		}
	}

	// payment書き換え
	class setPayment extends NetworkTask {
		private Context ctx;
		private int id;
		private String mode;

		public setPayment(Context ctx, int id, String mode) {
			super(ctx);
			this.ctx = ctx;
			this.id = id;
			this.mode = mode;
		}

		@Override
		protected void process() {
			spree = new SpreeConnector(ctx.getApplicationContext());
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			String number = orderDetails.getNumber();

			// modeによって変わる
			if (mode.equals("NEW"))
				pairs.add(new BasicNameValuePair("order[state]", "cart"));
			else if (mode.equals("VOID"))
				pairs.add(new BasicNameValuePair("payment[state]", "void"));
			else if (mode.equals("COMPLETE"))
				pairs.add(new BasicNameValuePair("payment[state]", "completed"));

			if (mode.equals("NEW"))
				spree.connector.postWithArgs("api/orders#create", pairs);
			else if (mode.equals("VOID"))
				spree.connector.putWithArgs("api/orders/" + number + "/payments/" + id, pairs);
			else if (mode.equals("COMPLETE"))
				spree.connector.putWithArgs("api/orders/" + number + "/payments/" + id, pairs);
		}

		@Override
		protected void complete() {
			if (mode.equals("NEW"))
	        	Toast.makeText(ctx.getApplicationContext(), ctx.getString(R.string.created), Toast.LENGTH_LONG).show();
	        else
	        	Toast.makeText(ctx.getApplicationContext(), ctx.getString(R.string.saved), Toast.LENGTH_LONG).show();
        	OrdersMenuActivity.setSelectedOrder(order);
    		Intent intent = new Intent(ctx, OrderDetailsActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	ctx.startActivity(intent);
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