package org.genshin.warehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.genshin.gsa.ScanSystem;
import org.genshin.gsa.ThumbListAdapter;
import org.genshin.gsa.ThumbListItem;
import org.genshin.gsa.network.NetworkTask;
import org.genshin.spree.ConnectionStatus;
import org.genshin.warehouse.Warehouse.ResultCodes;
import org.genshin.warehouse.orders.OrdersMenuActivity;
import org.genshin.warehouse.packing.PackingMenuActivity;
import org.genshin.warehouse.picking.PickingMenuActivity;
import org.genshin.warehouse.products.ProductSearcher;
import org.genshin.warehouse.products.ProductsMenuActivity;
import org.genshin.warehouse.profiles.Profiles;
import org.genshin.warehouse.racks.RacksMenuActivity;
import org.genshin.warehouse.settings.WarehouseSettingsActivity;
import org.genshin.warehouse.shipping.ShippingMenuActivity;
import org.genshin.warehouse.stocking.StockingMenuActivity;

public class WarehouseActivity extends Activity {
	Warehouse warehouse;

	//Interface objects
	private Button scanButton;
	private ListView menuList;

	private Spinner profileSpinner;
	private Profiles profiles;

	ThumbListItem[] menuListItems;

	private void createMainMenu() {
		if (profiles.list.size() <= 0) {
			Profiles.noRegisteredProfiles();
			return;
		}

		//Create main menu list items
		menuListItems  = new ThumbListItem[] {
				new ThumbListItem(R.drawable.products, getString(R.string.products), "", ProductsMenuActivity.class),
				new ThumbListItem(R.drawable.orders, getString(R.string.orders), "", OrdersMenuActivity.class),
				new ThumbListItem(R.drawable.stocking, getString(R.string.stocking), "", StockingMenuActivity.class),
				new ThumbListItem(R.drawable.racks, getString(R.string.racks), "", RacksMenuActivity.class),
				new ThumbListItem(R.drawable.picking, getString(R.string.picking), "", PickingMenuActivity.class),
				new ThumbListItem(R.drawable.packing, getString(R.string.packing), "", PackingMenuActivity.class),
				new ThumbListItem(R.drawable.shipping, getString(R.string.shipping), "", ShippingMenuActivity.class)
			};

		//Menu List
        menuList = (ListView) findViewById(R.id.main_menu_actions_list);
        ThumbListAdapter menuAdapter = new ThumbListAdapter(this, menuListItems);
		menuList.setAdapter(menuAdapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	menuListClickHandler(parent, view, position);
            }
        });
	}

	private void loadProfiles() {
		//Load profiles from the local DB
		profiles = new Profiles(this);

		//set up spinner and select default
		profileSpinner = profiles.attachToSpinner(profileSpinner);
		profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                profiles.selectProfile(position);
                checkConnection();
            }

			public void onNothingSelected(AdapterView<?> arg0) {
                //checkConnection();
			}
		});
	}

	private void hookupInterface() {
		//Scan Button
		scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		ScanSystem.initiateScan(v.getContext());
            }
		});

      //Profile Spinner
      profileSpinner = (Spinner) findViewById(R.id.warehouse_profile_spinner);
      //Profile Spinner contents loaded and spinner refreshsed in loadProfiles
      loadProfiles();

      createMainMenu();


	}

	private class ConnectionStatusIndicator extends ConnectionStatus {
		private ImageView connectionStatusIcon;
		public ConnectionStatusIndicator(Context ctx) {
			super(ctx);
			connectionStatusIcon = (ImageView) findViewById(R.id.connection_status_icon);

			connectionStatusIcon.setImageResource(android.R.drawable.ic_media_pause);
		}

		@Override
		protected void complete() {
			if (connected) {
				connectionStatusIcon.setImageResource(R.drawable.blue_tile);
				if (status.contentEquals("OK")) {
					connectionStatusIcon.setImageResource(R.drawable.green_tile);
				} else if (status.contentEquals("ERROR")){
					connectionStatusIcon.setImageResource(R.drawable.orange_tile);
				} else if (status.contentEquals("NOTCONNECTED")) {
					connectionStatusIcon.setImageResource(R.drawable.red_tile);
				}
			} else
				connectionStatusIcon.setImageResource(R.drawable.red_tile);
		}

	}

	private void checkConnection() {
		//Dialogs.showConnecting(this);
		new ConnectionStatusIndicator(this).execute();
		//Dialogs.dismiss();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        warehouse = new Warehouse(this);

        hookupInterface();
        NetworkTask.Setup(warehouse.Profiles().selected.server,
        		warehouse.Profiles().selected.port,
        		warehouse.Profiles().selected.apiKey,
        		warehouse.Profiles().selected.useHTTPS);
        checkConnection();
    }

    public void settingsClickHandler(View view) {
		Intent settingsIntent = new Intent(this, WarehouseSettingsActivity.class);
    	startActivityForResult(settingsIntent, ResultCodes.SETTINGS.ordinal());
	}

    private void menuListClickHandler(AdapterView<?> parent, View view, int position) {
        Intent menuItemIntent = new Intent(parent.getContext(), menuListItems[position].cls);
    	startActivity(menuItemIntent);
    }

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Warehouse.setContext(this);

        if (requestCode == ResultCodes.SCAN.ordinal()) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Handle successful scan
				if (ScanSystem.isQRCode(format)) {
					//TODO if it's a QR code check if it's JSON

					//TODO if it's JSON parse it by the header

				} else if (ScanSystem.isProductCode(format)) {
					// if it's a Barcode it's a product

					new ProductSearcher(this, format, contents).execute();

                	/*ArrayList<Product> foundProducts = Warehouse.Products().findByBarcode(contents);
                	//one result means forward to that product
                	if (foundProducts.size() == 1) {
                		ProductsMenuActivity.showProductDetails(this, foundProducts.get(0));
                	} else if (foundProducts.size() == 0) {
                		//New product?
                		Warehouse.Products().unregisteredBarcode(contents);
                	} else if (foundProducts.size() > 1) {
						ProductsMenuActivity.selectProductActivity(this, format, contents);
                	}*/
                } else {
                	// not a hadled code type
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(WarehouseActivity.this, getString(R.string.scan_cancelled), Toast.LENGTH_LONG).show();
            }
		} else if (resultCode == ResultCodes.PRODUCT_SELECT.ordinal()) {
			ProductsMenuActivity.showProductDetails(this, Warehouse.Products().getSelected(), resultCode);
        } else if (resultCode == ResultCodes.SETTINGS.ordinal()) {
        	loadProfiles();
        	checkConnection();
        }
    }
}
