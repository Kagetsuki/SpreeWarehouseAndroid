package org.genshin.warehouse.racks;

import java.util.ArrayList;

import org.genshin.warehouse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RacksMenuAdapter extends BaseExpandableListAdapter {
	private Context ctx;
	private LayoutInflater inflater;
	private ArrayList<RacksListData> groupData;
	private ArrayList<ArrayList<RacksListData>> childData;

	// コンストラクタ
	public RacksMenuAdapter(Context ctx, ArrayList<RacksListData> groupData,
			ArrayList<ArrayList<RacksListData>> childData) {
		this.ctx = ctx;
		this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupData = groupData;
		this.childData = childData;
	}

	public RacksListData getChild(int groupPosition, int childPosition) {
		return childData.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition,
												boolean isLastChild, View convertView, ViewGroup parent) {
		try {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.expandable_childview, parent, false);

			TextView name = (TextView) convertView.findViewById(R.id.childview_title);
			name.setText(childData.get(groupPosition).get(childPosition).getName());

			TextView group = (TextView) convertView.findViewById(R.id.childview_group);
			group.setText(childData.get(groupPosition).get(childPosition).getGroup());

			ImageView icon = (ImageView) convertView.findViewById(R.id.childview_icon);
			// クリックした後に次を表示するか詳細を表示するかでアイコンが変化
			if (childData.get(groupPosition).get(childPosition).getIcon())
				icon.setImageResource(android.R.drawable.ic_input_add);
			else
				icon.setImageResource(android.R.drawable.ic_menu_info_details);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		if (childData.size() == 0)
			return 0;
		else
			return childData.get(groupPosition).size();
	}

	public RacksListData getGroup(int groupPosition) {
		return groupData.get(groupPosition);
	}

	public int getGroupCount() {
		return groupData.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		try {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.expandable_groupview, parent, false);

			TextView name = (TextView) convertView.findViewById(R.id.group_title);
			name.setText(groupData.get(groupPosition).getName());

			ImageView icon = (ImageView) convertView.findViewById(R.id.group_icon);
			// 子要素が空の場合はindicator非表示(白丸)
			if (getChild(groupPosition, 0).getId() == null)
				icon.setImageResource(R.drawable.white);
			else {
				if (isExpanded)
					icon.setImageResource(android.R.drawable.arrow_up_float);
				else
					icon.setImageResource(android.R.drawable.arrow_down_float);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
