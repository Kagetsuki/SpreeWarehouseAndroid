package org.genshin.warehouse.racks;

import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.genshin.warehouse.WarehouseActivity;
import org.genshin.warehouse.stocking.StockingMenuActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class RackDetailsActivity extends Activity {
	private TextView rootName;
	private TextView rackName;

	private Intent intent;
	private int mode;

	private void hookupInterface() {
		rootName = (TextView) findViewById(R.id.rack_root);
		rackName = (TextView) findViewById(R.id.rack_select);

		mode = Warehouse.ResultCodes.NORMAL.ordinal();

		intent = getIntent();
		String modeString = intent.getStringExtra("MODE");
		String name = intent.getStringExtra("GROUP_NAME");
		rootName.setText(name);
		name = intent.getStringExtra("CHILD_NAME");
		rackName.setText(name);

		// 別の場所からコンテナを選択する時
		if (modeString != null) {
		    if (modeString.equals("CONTAINER_SELECT")) {
		    	mode = Warehouse.ResultCodes.CONTAINER_SELECT.ordinal();
		    	containerSelect(name);
		    }
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rack_details);

        Warehouse.setContext(this);

		hookupInterface();
	}

	// 別の場所からコンテナを選択する時
	public void containerSelect(String selectName) {
		final String name = selectName;

		AlertDialog.Builder question = new AlertDialog.Builder(this);
		question.setTitle(getString(R.string.register_this_container));
		question.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(getApplicationContext(), StockingMenuActivity.class);
				intent.putExtra("CONTAINER_NAME", name);
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
