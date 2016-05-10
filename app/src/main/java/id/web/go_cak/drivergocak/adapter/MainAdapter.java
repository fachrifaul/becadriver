package id.web.go_cak.drivergocak.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Dashboard;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList<Dashboard> dashboardList;
    private Context context;
    private OnItemClickListener listener;

    public MainAdapter(Context context) {
        super();
        int[] title = {R.string.transport, R.string.history, R.string.help};
        int[] thumbnail = {
                R.drawable.gocaktransport,
                R.drawable.gocakkeuangan,
                R.drawable.gocakhelp
        };

        dashboardList = new ArrayList(title.length);
        for (int i = 0; i < title.length; i++) {
            dashboardList.add(new Dashboard(
                    i + "",
                    context.getString(title[i]),
                    String.valueOf(thumbnail[i])
            ));
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dashboard, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Dashboard dashboard = dashboardList.get(position);

        Picasso.with(context).load(Integer.parseInt(dashboard.getImageDashboard())).into(holder.thunmImageView);
        holder.titleTextView.setText(dashboard.getTitleDashboard());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(dashboard, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dashboardList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Dashboard item, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.thumb_image_view) ImageView thunmImageView;
        @Bind(R.id.title_text_view) TextView titleTextView;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}