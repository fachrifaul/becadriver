package id.web.go_cak.drivergocak.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Transaksi;

public class TransactionNewAdapter extends RecyclerView.Adapter<TransactionNewAdapter.ViewHolder> {

    private List<Transaksi> transaksiList = new ArrayList<>();
    private OnItemClickListener listener;

    public TransactionNewAdapter() {
        super();
    }

    public void add(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_transaction, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Transaksi transaksi = transaksiList.get(position);

        holder.nameCustomerTextView.setText(transaksi.getNama());
        holder.noTelpTextView.setText(transaksi.getTelp());
        holder.alamatTextView.setText(transaksi.getAlamatLengkap());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(transaksi, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(Transaksi transaksi, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name_customer_text_view) TextView nameCustomerTextView;
        @Bind(R.id.no_telp_text_view) TextView noTelpTextView;
        @Bind(R.id.alamat_text_view) TextView alamatTextView;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}