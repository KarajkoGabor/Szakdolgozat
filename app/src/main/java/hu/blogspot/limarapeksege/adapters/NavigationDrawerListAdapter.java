package hu.blogspot.limarapeksege.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;

public class NavigationDrawerListAdapter extends ArrayAdapter<DrawerListItem> {

    private Context context;
    private List<DrawerListItem> items;
    int layoutID;

    public NavigationDrawerListAdapter(Context context, List<DrawerListItem> items, int layoutID) {
        super(context, layoutID, items);
        this.context = context;
        this.items = items;
        this.layoutID = layoutID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.w(GlobalStaticVariables.LOG_TAG,"CUSTOM DRAWER");
        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(layoutID, parent, false);
            drawerHolder.ItemName = (TextView) view
                    .findViewById(R.id.drawerItemTitle);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawerIcon);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        DrawerListItem dItem = this.items.get(position);

        drawerHolder.icon.setImageDrawable(ContextCompat.getDrawable(context,dItem.getImageID()));
        drawerHolder.ItemName.setText(dItem.getTitle());
        Log.w(GlobalStaticVariables.LOG_TAG,"Position " + position);

        return view;
    }

    private static class DrawerItemHolder {
        TextView ItemName;
        ImageView icon;
    }
}
