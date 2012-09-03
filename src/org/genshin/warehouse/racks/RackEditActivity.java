package org.genshin.warehouse.racks;

import org.genshin.warehouse.R;
import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.Warehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RackEditActivity extends Activity {
		private SpreeConnector spree;
		private Context context;

		private boolean isNew;

		private EditText rackName;
		private EditText rackCode;
		private EditText containerComponent;
		private EditText address;
		private EditText note;
		private Button button;

	private void hookupInterface() {
		rackName = (EditText) findViewById(R.id.rack_name);
		rackName.setText("");

		rackCode = (EditText) findViewById(R.id.rack_code);
		rackCode.setText("");

		containerComponent = (EditText) findViewById(R.id.container_component);
		containerComponent.setText("");

		address = (EditText) findViewById(R.id.rack_address);
		address.setText("");

		note = (EditText) findViewById(R.id.rack_note);
		note.setText("");

		button = (Button) findViewById(R.id.new_register);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		isNew = intent.getBooleanExtra("IS_NEW", false);
		Warehouse.setContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rack_edit);

        if (isNew) {

        }
        else {

        }
        hookupInterface();
	}
}
