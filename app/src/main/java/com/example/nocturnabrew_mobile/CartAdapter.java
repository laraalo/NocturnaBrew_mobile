package com.example.nocturnabrew_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nocturnabrew_mobile.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private Runnable onTotalsChanged;

    public CartAdapter(Context context, List<CartItem> cartItems, Runnable onTotalsChanged) {
        this.context = context;
        this.cartItems = cartItems;
        this.onTotalsChanged = onTotalsChanged;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.name.setText(item.getProduct().getName());
        holder.price.setText("$" + item.getProduct().getPrice());
        holder.qty.setText(String.valueOf(item.getQuantity()));

        Glide.with(context)
                .load(item.getProduct().getUrl())
                .into(holder.image);

        holder.btnIncrease.setOnClickListener(v -> {
            item.increaseQuantity();
            notifyItemChanged(position);
            onTotalsChanged.run();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.decreaseQuantity();
            } else {
                CartManager.getInstance().removeProduct(item.getProduct().getProductId());
                notifyItemRemoved(position);
            }
            notifyDataSetChanged();
            onTotalsChanged.run();
        });

        holder.btnRemove.setOnClickListener(v -> {
            CartManager.getInstance().removeProduct(item.getProduct().getProductId());
            notifyItemRemoved(position);
            notifyDataSetChanged();
            onTotalsChanged.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, price, qty;
        ImageButton btnIncrease, btnDecrease, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.cartProductImage);
            name = itemView.findViewById(R.id.cartProductName);
            price = itemView.findViewById(R.id.cartProductPrice);
            qty = itemView.findViewById(R.id.cartProductQty);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
