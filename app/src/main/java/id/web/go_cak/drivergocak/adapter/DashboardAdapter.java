package id.web.go_cak.drivergocak.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Dashboard;

public class DashboardAdapter extends ArrayAdapter<Dashboard> {

    private int layoutResourceId;
    private Context context;
    private ArrayList<Dashboard> items;


    public DashboardAdapter(Context context, ArrayList<Dashboard> items) {
        super(context, R.layout.item_dashboard, items);
        this.context = context;
        this.items = items;
        this.layoutResourceId = R.layout.item_dashboard;
    }

    public int size() {
        return items.size();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Dashboard dashboard = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if (position == 2 || position == 6) {
//            holder.layoutItem.setVisibility(View.INVISIBLE);
//        }else {
//            holder.layoutItem.setVisibility(View.VISIBLE);
            holder.title.setText(dashboard.getTitleDashboard());
            Picasso.with(context).load(Integer.parseInt(dashboard.getImageDashboard())).into(holder.thumbnail);
//        }



        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.thumbnail)
        ImageView thumbnail;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.layout_item)
        CardView layoutItem;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}