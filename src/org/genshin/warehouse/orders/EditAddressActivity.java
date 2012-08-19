package org.genshin.warehouse.orders;

import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class EditAddressActivity extends Activity {
	private SpreeConnector spree;

	private Order order;
	private OrderDetails orderDetails;
	
	private EditText email;
	private EditText firstname;
	private EditText lastname;
	private EditText address1;
	private EditText address2;
	private EditText city;
	private EditText state;
	private EditText zipcode;
	private EditText country;
	private EditText phone;
	
	private Button addButton1;
	private Button addButton2;
	private CheckBox checkBox;
	
	private EditText shipFirstname;
	private EditText shipLastname;
	private EditText shipAddress1;
	private EditText shipAddress2;
	private EditText shipCity;
	private EditText shipState;
	private EditText shipZipcode;
	private EditText shipCountry;
	private EditText shipPhone;
	
	private boolean check = false;

	private void hookupInterface() {
		
		orderDetails = OrderDetailsActivity.getOrderDetails();
		
		email = (EditText) findViewById(R.id.edit_email);
		email.setText(orderDetails.email);
		firstname = (EditText) findViewById(R.id.edit_first_name);
		firstname.setText(orderDetails.firstname);
		lastname = (EditText) findViewById(R.id.edit_last_name);
		lastname.setText(orderDetails.lastname);
		address1 = (EditText) findViewById(R.id.edit_address_1);
		address1.setText(orderDetails.address1);
		address2 = (EditText) findViewById(R.id.edit_address_2);
		address2.setText(orderDetails.address2);
		city = (EditText) findViewById(R.id.edit_city);
		city.setText(orderDetails.city);
		state = (EditText) findViewById(R.id.edit_states);
		state.setText(orderDetails.state);
		zipcode = (EditText) findViewById(R.id.edit_zip_code);
		zipcode.setText(orderDetails.zipcode);
		country = (EditText) findViewById(R.id.edit_country);
		country.setText(orderDetails.country);
		phone = (EditText) findViewById(R.id.edit_phone);
		phone.setText(orderDetails.phone);
		addButton1 = (Button) findViewById(R.id.add_button);
		addButton1.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				addData();
			}
		});
		
		checkBox = (CheckBox) findViewById(R.id.checkBox);
		checkBox.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				CheckBox checkBox= (CheckBox) v;
				check = checkBox.isChecked();	
			}
		});
		
		shipFirstname = (EditText) findViewById(R.id.edit_first_name_ship);
		shipFirstname.setText(orderDetails.shipFirstname);
		shipLastname = (EditText) findViewById(R.id.edit_last_name_ship);
		shipLastname.setText(orderDetails.shipLastname);
		shipAddress1 = (EditText) findViewById(R.id.edit_address_1_ship);
		shipAddress1.setText(orderDetails.shipAddress1);
		shipAddress2 = (EditText) findViewById(R.id.edit_address_2_ship);
		shipAddress2.setText(orderDetails.shipAddress2);
		shipCity = (EditText) findViewById(R.id.edit_city_ship);
		shipCity.setText(orderDetails.shipCity);
		shipState = (EditText) findViewById(R.id.edit_states_ship);
		shipState.setText(orderDetails.shipState);
		shipZipcode = (EditText) findViewById(R.id.edit_zip_code_ship);
		shipZipcode.setText(orderDetails.shipZipcode);
		shipCountry = (EditText) findViewById(R.id.edit_country_ship);
		shipCountry.setText(orderDetails.shipCountry);
		shipPhone = (EditText) findViewById(R.id.edit_phone_ship);
		shipPhone.setText(orderDetails.shipPhone);
		addButton2 = (Button) findViewById(R.id.add_button);
		addButton2.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				addData();	
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_edit_address);
 
        order = OrdersMenuActivity.getSelectedOrder();

        hookupInterface();
	}
	
	public void addData() {
		OrderDetails input = new OrderDetails();
		input.number = orderDetails.number;

		if (email.getText().toString() != "")
			input.email = email.getText().toString();
		if (firstname.getText().toString() != "")
			input.firstname = firstname.getText().toString();
		if (lastname.getText().toString() != "")
			input.lastname = lastname.getText().toString();
		if (address1.getText().toString() != "")
			input.address1 = address1.getText().toString();
		if (address2.getText().toString() != "")
			input.address2 = address2.getText().toString();
		if (city.getText().toString() != "")
			input.city = city.getText().toString();
		if (state.getText().toString() != "")
			input.state = state.getText().toString();
		if (zipcode.getText().toString() != "")
			input.zipcode = zipcode.getText().toString();
		if (country.getText().toString() != "")
			input.country = country.getText().toString();
		if (phone.getText().toString() != "")
			input.phone = phone.getText().toString();

		if (!check) {
			if (shipFirstname.getText().toString() != "")
				input.shipFirstname = shipFirstname.getText().toString();
			if (shipLastname.getText().toString() != "")
				input.shipLastname = shipLastname.getText().toString();
			if (shipAddress1.getText().toString() != "")
				input.shipAddress1 = shipAddress1.getText().toString();
			if (shipAddress2.getText().toString() != "")
				input.shipAddress2 = shipAddress2.getText().toString();
			if (shipCity.getText().toString() != "")
				input.shipCity = shipCity.getText().toString();
			if (shipState.getText().toString() != "")
				input.shipState = shipState.getText().toString();
			if (shipZipcode.getText().toString() != "")
				input.shipZipcode = shipZipcode.getText().toString();
			if (shipCountry.getText().toString() != "")
				input.shipCountry = shipCountry.getText().toString();
			if (shipPhone.getText().toString() != "")
				input.shipPhone = shipPhone.getText().toString();
		}
		new SaveOrder(getApplicationContext(), false, order, input, check).execute();
	}
}
