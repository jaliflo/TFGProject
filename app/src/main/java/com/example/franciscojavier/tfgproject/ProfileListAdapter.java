package com.example.franciscojavier.tfgproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileListAdapter extends BaseAdapter{

    static class ViewHolder{
        TextView tasteText;
        CheckBox selected;
    }

    private static final String TAG = "CustomAdapter";
    private static int convertViewCounter = 0;

    private ArrayList<ProfileListData> data;
    private LayoutInflater inflater = null;

    public ProfileListAdapter(Context c, String[] data){
        ArrayList<ProfileListData> aux = new ArrayList<>();
        for(int i=0;i<data.length;i++){
            aux.add(new ProfileListData(data[i], false));
        }
        this.data = aux;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return 1;
    }

    @Override
    public int getItemViewType(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_profile_list, null);

            convertViewCounter++;

            holder = new ViewHolder();
            holder.tasteText = (TextView) convertView.findViewById(R.id.tastes_list_text);
            holder.selected = (CheckBox) convertView.findViewById(R.id.selectTast);
            holder.selected.setOnClickListener(checkListener);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProfileListData d = (ProfileListData) getItem(position);
        holder.selected.setTag(d);
        holder.tasteText.setText(data.get(position).getTasteText());
        holder.selected.setChecked(data.get(position).isSelected());

        return convertView;
    }

    public void setCheck(int position){
        ProfileListData d = data.get(position);
        d.setSelected(!d.isSelected());
        notifyDataSetChanged();
    }

    public List<String> getSelectedElements(){
        List<String> elements = new ArrayList<>();

        for(ProfileListData data: this.data){
            if(data.isSelected()) {
                elements.add(data.getTasteText());
            }
        }

        return elements;
    }

    private View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ProfileListData d = (ProfileListData) v.getTag();
            d.setSelected(!d.isSelected());
        }
    };
}
