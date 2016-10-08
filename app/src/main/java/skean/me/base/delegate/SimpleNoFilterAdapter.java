package skean.me.base.delegate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import skean.me.base.delegate.ArrayAdapter;

public class SimpleNoFilterAdapter extends ArrayAdapter<String> {

    public SimpleNoFilterAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public View createView(String item, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = getInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
        ((TextView) convertView).setText(item);
        return convertView;
    }



    @Override
    protected boolean doFiltering(String item, String constraintStr) {
        return true;
    }
}
