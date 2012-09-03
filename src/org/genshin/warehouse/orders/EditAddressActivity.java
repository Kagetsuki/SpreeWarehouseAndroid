package org.genshin.warehouse.orders;

import org.genshin.warehouse.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class EditAddressActivity extends Activity {
	private Order order;
	private OrderDetails orderDetails;
	private TextView orderNumber;

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
		// 編集するorderDetails取得
		orderDetails = OrderDetailsActivity.getOrderDetails();

		// 請求先住所
		orderNumber = (TextView) findViewById(R.id.order_details_number);
		orderNumber.setText(orderDetails.getNumber());
		email = (EditText) findViewById(R.id.edit_email);
		email.setText(orderDetails.getEmail());
		firstname = (EditText) findViewById(R.id.edit_first_name);
		firstname.setText(orderDetails.getFirstname());
		lastname = (EditText) findViewById(R.id.edit_last_name);
		lastname.setText(orderDetails.getLastname());
		address1 = (EditText) findViewById(R.id.edit_address_1);
		address1.setText(orderDetails.getAddress1());
		address2 = (EditText) findViewById(R.id.edit_address_2);
		address2.setText(orderDetails.getAddress2());
		city = (EditText) findViewById(R.id.edit_city);
		city.setText(orderDetails.getCity());
		state = (EditText) findViewById(R.id.edit_states);
		state.setText(orderDetails.getState());
		zipcode = (EditText) findViewById(R.id.edit_zip_code);
		zipcode.setText(orderDetails.getZipcode());
		country = (EditText) findViewById(R.id.edit_country);
		country.setText(orderDetails.getCountry());
		phone = (EditText) findViewById(R.id.edit_phone);
		phone.setText(orderDetails.getPhone());

		addButton1 = (Button) findViewById(R.id.add_button);
		addButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setData();
			}
		});

		checkBox = (CheckBox) findViewById(R.id.checkBox);
		checkBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox checkBox= (CheckBox) v;
				check = checkBox.isChecked();
			}
		});

		// 配送先住所
		shipFirstname = (EditText) findViewById(R.id.edit_first_name_ship);
		shipFirstname.setText(orderDetails.getShipFirstname());
		shipLastname = (EditText) findViewById(R.id.edit_last_name_ship);
		shipLastname.setText(orderDetails.getShipLastname());
		shipAddress1 = (EditText) findViewById(R.id.edit_address_1_ship);
		shipAddress1.setText(orderDetails.getShipAddress1());
		shipAddress2 = (EditText) findViewById(R.id.edit_address_2_ship);
		shipAddress2.setText(orderDetails.getShipAddress2());
		shipCity = (EditText) findViewById(R.id.edit_city_ship);
		shipCity.setText(orderDetails.getShipCity());
		shipState = (EditText) findViewById(R.id.edit_states_ship);
		shipState.setText(orderDetails.getShipState());
		shipZipcode = (EditText) findViewById(R.id.edit_zip_code_ship);
		shipZipcode.setText(orderDetails.getShipZipcode());
		shipCountry = (EditText) findViewById(R.id.edit_country_ship);
		shipCountry.setText(orderDetails.getShipCountry());
		shipPhone = (EditText) findViewById(R.id.edit_phone_ship);
		shipPhone.setText(orderDetails.getShipPhone());

		addButton2 = (Button) findViewById(R.id.add_button);
		addButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setData();
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_edit_address);

        // 編集するorderを取得
        order = OrdersMenuActivity.getSelectedOrder();

        hookupInterface();
	}

	// 各EditTextから文字を取得して保存する
	public void setData() {
		OrderDetails input = new OrderDetails();
		input.setNumber(orderDetails.getNumber());

		// 請求先住所
		if (email.getText().toString() != "")
			input.setEmail(email.getText().toString());
		if (firstname.getText().toString() != "")
			input.setFirstname(firstname.getText().toString());
		if (lastname.getText().toString() != "")
			input.setLastname(lastname.getText().toString());
		if (address1.getText().toString() != "")
			input.setAddress1(address1.getText().toString());
		if (address2.getText().toString() != "")
			input.setAddress2(address2.getText().toString());
		if (city.getText().toString() != "")
			input.setCity(city.getText().toString());
		if (state.getText().toString() != "")
			input.setState(state.getText().toString());
		if (zipcode.getText().toString() != "")
			input.setZipcode(zipcode.getText().toString());
		if (country.getText().toString() != "")
			input.setCountry(country.getText().toString());
		if (phone.getText().toString() != "")
			input.setPhone(phone.getText().toString());

		// 配送先住所に請求先住所を使用しない場合のみ取得
		if (!check) {
			if (shipFirstname.getText().toString() != "")
				input.setShipFirstname(shipFirstname.getText().toString());
			if (shipLastname.getText().toString() != "")
				input.setShipLastname(shipLastname.getText().toString());
			if (shipAddress1.getText().toString() != "")
				input.setShipAddress1(shipAddress1.getText().toString());
			if (shipAddress2.getText().toString() != "")
				input.setShipAddress2(shipAddress2.getText().toString());
			if (shipCity.getText().toString() != "")
				input.setShipCity(shipCity.getText().toString());
			if (shipState.getText().toString() != "")
				input.setShipState(shipState.getText().toString());
			if (shipZipcode.getText().toString() != "")
				input.setShipZipcode(shipZipcode.getText().toString());
			if (shipCountry.getText().toString() != "")
				input.setShipCountry(shipCountry.getText().toString());
			if (shipPhone.getText().toString() != "")
				input.setShipPhone(shipPhone.getText().toString());
		}

		new SaveOrder(getApplicationContext(), false, order, input, check).execute();
	}
}
