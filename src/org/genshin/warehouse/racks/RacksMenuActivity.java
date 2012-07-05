package org.genshin.warehouse.racks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.genshin.spree.SpreeConnector;
import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class RacksMenuActivity extends Activity {
	private TextView racksRoot;
	private ExpandableListView racksRootList;

    private ExpandableListAdapter adapter;
    
    private Intent intent;
    private TextView selectRack;
    
	private void hookupInterface() {
		
		racksRootList = (ExpandableListView) findViewById(R.id.racks_root_list);
		selectRack = (TextView) findViewById(R.id.racks_select);
		
		intent = getIntent();
		String select = intent.getStringExtra("SELECT");
		if (select != null)
			selectRack.setText(select);
		else
			selectRack.setText("ROOT");
		String selectId = intent.getStringExtra("ID");

        WarehouseDivisions warehouses = Warehouse.Warehouses();
		
        ArrayList<HashMap<String, String>> warehouseRoots = new ArrayList<HashMap<String,String>>();
        ArrayList<ArrayList<HashMap<String, Object>>> containerTaxonomyNodes = new ArrayList<ArrayList<HashMap<String, Object>>>();
 
        if (selectId == null) {
			for (int i = 0; i < warehouses.count; i++) {
				HashMap<String, String> warehouseDivisionMap = new HashMap<String, String>();
				warehouseDivisionMap.put("warehouse", warehouses.divisions.get(i).name);
	
				ArrayList<HashMap<String, Object>> taxonomyNodeList = new ArrayList<HashMap<String, Object>>();
				for (int j = 0; j < warehouses.divisions.get(i).containers.size(); j++) {
					HashMap<String, Object> taxonomyNode = new HashMap<String, Object>();
					taxonomyNode.put("warehouse", warehouses.divisions.get(i).name);
					taxonomyNode.put("taxonomyName", warehouses.divisions.get(i).containers.get(j).name);
					taxonomyNode.put("id", "" + warehouses.divisions.get(i).containers.get(j).id);
					taxonomyNodeList.add(taxonomyNode);
				}
				containerTaxonomyNodes.add(taxonomyNodeList);
				warehouseRoots.add(warehouseDivisionMap);
			}
        }
        else {
        	if (!selectId.equals("0")) {
	        	ContainerTaxonomy selectContainer = new ContainerTaxonomy(selectId);
	
	        	for (int i = 0; i < selectContainer.list.size(); i++) {
					HashMap<String, String> warehouseDivisionMap = new HashMap<String, String>();
					warehouseDivisionMap.put("warehouse", selectContainer.list.get(i).name);
					
					ArrayList<HashMap<String, Object>> taxonomyNodeList = new ArrayList<HashMap<String, Object>>();
					/*
					for (int j = 0; j < warehouses.divisions.get(i).containers.size(); j++) {
						HashMap<String, Object> taxonomyNode = new HashMap<String, Object>();
						taxonomyNode.put("warehouse", warehouses.divisions.get(i).name);
						taxonomyNode.put("taxonomyName", warehouses.divisions.get(i).containers.get(j).name);
						taxonomyNode.put("id", "" + warehouses.divisions.get(i).containers.get(j).id);
						taxonomyNodeList.add(taxonomyNode);
					}
					*/
					HashMap<String, Object> taxonomyNode = new HashMap<String, Object>();
					taxonomyNode.put("warehouse", selectContainer.list.get(i).name);
					taxonomyNode.put("taxonomyName", "詳細を表示");
					taxonomyNodeList.add(taxonomyNode);
					containerTaxonomyNodes.add(taxonomyNodeList);
					warehouseRoots.add(warehouseDivisionMap);
				}
        	}
        }

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                warehouseRoots,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {"warehouse"},
                new int[] { android.R.id.text1 },
                containerTaxonomyNodes,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"taxonomyName", "warehouse"},
                new int[] { android.R.id.text1, android.R.id.text2 });
 
        racksRootList.setAdapter(adapter); 
        
        racksRootList.setOnChildClickListener(new OnChildClickListener() {
			
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO 自動生成されたメソッド・スタブ
				ExpandableListAdapter ada = parent.getExpandableListAdapter();
				Map<String, Object> map = (Map<String, Object>)ada.getChild(groupPosition, childPosition);
				String text = (String) map.get("taxonomyName");
				String selectID = (String) map.get("id");
				
				if (text.equals("詳細を表示")) {
					intent = new Intent(getApplicationContext(), RacksMenuActivity.class);
					text = (String) map.get("warehouse");
					intent.putExtra("SELECT", text);
					intent.putExtra("ID", "0");
					startActivity(intent);
				}
				else {
					intent = new Intent(getApplicationContext(), RacksMenuActivity.class);
					intent.putExtra("SELECT", text);
					intent.putExtra("ID", selectID);
					startActivity(intent);
				}
				return false;
			}
		});
        
        if (adapter.getGroupCount() == 0) {
        	LinearLayout layout = new LinearLayout(this);
        	setContentView(layout);
        	TextView textView = new TextView(this);
        	textView.setText("子が何もない場合は詳細を表示…");
        	textView.setLayoutParams(new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.WRAP_CONTENT,
        			LinearLayout.LayoutParams.WRAP_CONTENT));
        	layout.addView(textView);
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.racks);
		
        Warehouse.setContext(this);
        Warehouse.Warehouses();
        
		hookupInterface();
	}

}
