package org.genshin.warehouse.settings;

import org.genshin.gsa.SimpleMenuList;
import org.genshin.warehouse.R;
import org.genshin.warehouse.R.id;
import org.genshin.warehouse.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class WarehouseSettingsActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehouse_settings);

              
		ListView menuList = (ListView) findViewById(R.id.settings_category_list);
        
        SimpleMenuList menu = new SimpleMenuList(this);
        	menu.add(R.string.profiles, ProfileSettings.class);
        	/*menu.add(R.string.defaults, ProfileSettings.class);
        	menu.add(R.string.camera, ProfileSettings.class);*/
        
        

		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, (String[] = menuItems);
		menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                String item = (String) listView.getItemAtPosition(position);
                Toast.makeText(WarehouseSettingsActivity.this, item, Toast.LENGTH_LONG).show();
            }
        });*/

	}

}
