package org.genshin.warehouse.racks;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.genshin.warehouse.R;
import org.genshin.warehouse.Warehouse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class RacksMenuActivity extends Activity {
	private ExpandableListView racksRootList;

    private RacksMenuAdapter adapter;
    
    private Intent intent;
    private TextView selectRack;
    
    private String mode;
    
	private WarehouseDivisions warehouses; // = Warehouse.Warehouses();
	
    private ArrayList<RacksListData> warehouseRoots;// = new ArrayList<RacksListData>();
    private ArrayList<ArrayList<RacksListData>> containerTaxonomyNodes;// = new ArrayList<ArrayList<RacksListData>>();
    
	private void hookupInterface() {
		
		racksRootList = (ExpandableListView) findViewById(R.id.racks_root_list);
		selectRack = (TextView) findViewById(R.id.racks_select);
		
		intent = getIntent();
		String selectName = intent.getStringExtra("SELECT_NAME");
		if (selectName != null)
			selectRack.setText(selectName);
		else
			selectRack.setText("/");
		String selectId = intent.getStringExtra("ID");
		
		String flag = intent.getStringExtra("MODE");
		if (flag != null)
			mode = flag;
		else
			mode = "NORMAL";

		putJsonData(selectId, mode);
        adapter = new RacksMenuAdapter(
        		this, warehouseRoots, containerTaxonomyNodes);     
        racksRootList.setAdapter(adapter);
        racksRootList.setGroupIndicator(null);

        racksRootList.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				RacksMenuAdapter adapter = (RacksMenuAdapter)parent.getExpandableListAdapter();
				String selectId = adapter.getChild(groupPosition, childPosition).id;
				String text = adapter.getChild(groupPosition, childPosition).name;	

				intent = new Intent(getApplicationContext(), RacksMenuActivity.class);
				intent.putExtra("SELECT_NAME", text);
				intent.putExtra("ID", selectId);
				startActivity(intent);

				return false;
			}
		});

        racksRootList.setOnGroupClickListener(new OnGroupClickListener() {		
			public boolean onGroupClick(ExpandableListView parent, View v,
													int groupPosition, long id) {
				if (adapter.getChild(groupPosition, 0).id == null) {
					// 子要素が空の場合の処理
					//String selectId = adapter.getGroup(groupPosition).id;
					String selectId = "0";
					String text = adapter.getGroup(groupPosition).name;
					intent = new Intent(getApplicationContext(), RacksMenuActivity.class);
					intent.putExtra("SELECT_NAME", text);
					intent.putExtra("ID", selectId);
					intent.putExtra("MODE", "DETAIL");
					startActivity(intent);
					return true;
				}
				else
					return false;
			}		
		});
        
        //if (adapter.getGroupCount() == 0) {
        // 	finishActivity(Warehouse.ResultCodes.CONTAINER_SELECT.ordinal());
        //}
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.racks);
		
        Warehouse.setContext(this);
        Warehouse.Warehouses();
        
		hookupInterface();
	}
	
	public void putJsonData(String selectId, String mode) {
		
		warehouses = Warehouse.Warehouses();
		
        warehouseRoots = new ArrayList<RacksListData>();
        containerTaxonomyNodes = new ArrayList<ArrayList<RacksListData>>();

        if (selectId == null) {
			for (int i = 0; i < warehouses.count; i++) {
				RacksListData warehouseDivisionMap = new RacksListData();
				warehouseDivisionMap.name = (warehouses.divisions.get(i).name);
				warehouseDivisionMap.id = ("" + warehouses.divisions.get(i).id);

				ArrayList<RacksListData> taxonomyNodeList = new ArrayList<RacksListData>();
				for (int j = 0; j < warehouses.divisions.get(i).containers.size(); j++) {
					RacksListData taxonomyNode = new RacksListData();
					taxonomyNode.group = (warehouses.divisions.get(i).name);
					taxonomyNode.name = (warehouses.divisions.get(i).containers.get(j).name);
					taxonomyNode.id = ("" + warehouses.divisions.get(i).containers.get(j).id);
					taxonomyNodeList.add(taxonomyNode);
				}
				containerTaxonomyNodes.add(taxonomyNodeList);
				warehouseRoots.add(warehouseDivisionMap);
			}
        }
        else if (mode.equals("NORMAL")){
	        ContainerTaxonomy selectContainer = new ContainerTaxonomy(selectId);
	
        	for (int i = 0; i < selectContainer.list.size(); i++) {
				RacksListData warehouseDivisionMap = new RacksListData();
				warehouseDivisionMap.name = (selectContainer.list.get(i).name);
				
				ArrayList<RacksListData> taxonomyNodeList = new ArrayList<RacksListData>();

				// 子要素がない場合はnull挿入・・・とりあえず今は参考データがないので全部null
				RacksListData taxonomyNode = new RacksListData();
				taxonomyNode.group = null;
				taxonomyNode.name = null;
				taxonomyNode.id = null;
				taxonomyNodeList.add(taxonomyNode);

				containerTaxonomyNodes.add(taxonomyNodeList);
				warehouseRoots.add(warehouseDivisionMap);
			}
        }
	}

}
