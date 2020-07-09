package me.skean.skeanframework.delegate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.skean.skeanframework.R;

public class SimpleNoFilterAdapter extends ArrayAdapter<Object> {

    public SimpleNoFilterAdapter(Context context, List<Object> list) {
        super(context, list);
    }

    @Override
    public View createView(Object item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = getInflater().inflate(R.layout.listitem_no_filter, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.text.setText(getContentFromObject(item));
        return convertView;
    }

    public String getContentFromObject(Object item) {
        return item.toString();
    }

    @Override
    protected boolean doFiltering(Object item, String constraintStr) {
        return true;
    }

    class ViewHolder {
        TextView text;

        public ViewHolder(View convertView) {
            text = (TextView) convertView.findViewById(android.R.id.text1);
        }
    }
}
