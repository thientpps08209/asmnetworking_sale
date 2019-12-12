package com.example.ps08209_ps08304_ASMNETWORKING.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ps08209_ps08304_ASMNETWORKING.R;
import com.example.ps08209_ps08304_ASMNETWORKING.model.Products;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Products> productsList;
    private ArrayList<Products> productsListFiltered;

    private Locale localeVN = new Locale("vi", "VN");
    private NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

    // *** Implement on item click
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }  // ***

    public ProductAdapter(Context context, ArrayList<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.productsListFiltered = new ArrayList<>(productsList);
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_one_item, viewGroup, false);

        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_tenSp, tv_nsx, tv_giaBan;
        ImageView iv_icon;
        CardView parent_layout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_tenSp = itemView.findViewById(R.id.tv_tenSp);
            tv_nsx = itemView.findViewById(R.id.tv_nsx);
            tv_giaBan = itemView.findViewById(R.id.tv_giaBan);
            iv_icon = itemView.findViewById(R.id.iv_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductAdapter.ViewHolder holder, int position) {

        final Products products = productsList.get(position);
        //format gia ban
        double VND = products.getGiaBan();
        final String giaBan = currencyVN.format(VND);
        String hinh = products.getHinhSp().trim();

        holder.tv_tenSp.setText(products.getTenSp());
        holder.tv_nsx.setText(products.getNsx());
        holder.tv_giaBan.setText(giaBan);

        try {
            Picasso.with(context.getApplicationContext())
                    .load(hinh)
                    .into(holder.iv_icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productsList == null ? 0 : productsList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<Products> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(productsListFiltered);
            } else {
                for (Products productsItem : productsListFiltered) {
                    //format gia ban
                    double VND = productsItem.getGiaBan();
                    Locale localeVN = new Locale("vi", "VN");
                    NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
                    String giaBan = currencyVN.format(VND);
                    String charString = charSequence.toString().toLowerCase();
                    if (productsItem.getTenSp().toLowerCase().contains(charString)
                            || giaBan.contains(charString)
                            || productsItem.getNsx().toLowerCase().contains(charString)) {
                        filteredList.add(productsItem);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            productsList = (ArrayList<Products>) filterResults.values;
            notifyDataSetChanged();
        }
    };

}
