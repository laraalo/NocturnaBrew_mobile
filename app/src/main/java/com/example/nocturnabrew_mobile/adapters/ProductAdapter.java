package com.example.nocturnabrew_mobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.nocturnabrew_mobile.R;
import com.example.nocturnabrew_mobile.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());

        int radiusPx = (int) (12 * context.getResources().getDisplayMetrics().density);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(new CenterCrop(), new RoundedCorners(radiusPx));

        Glide.with(context)
                .load(product.getUrl())
                .apply(requestOptions)
                .into(holder.productImage);

        if (!product.isAvailable()) {

            // PRODUCTO AGOTADO
            holder.productPrice.setText("Agotado");
            holder.productPrice.setTextColor(Color.RED);

            holder.addButton.setEnabled(false);
            holder.addButton.setAlpha(0.3f);

        } else {

            // PRODUCTO DISPONIBLE
            holder.productPrice.setText("$" + product.getPrice());
            holder.productPrice.setTextColor(Color.parseColor("#4B1E2F"));
            holder.productPrice.setTypeface(null, Typeface.BOLD);

            holder.addButton.setEnabled(true);
            holder.addButton.setAlpha(1f);

            holder.addButton.setOnClickListener(v -> {

                SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
                String email = prefs.getString("USER_EMAIL", null);

                CartManager
                        .getInstance()
                        .addProduct(product);

                CartManager
                        .getInstance()
                        .saveCart(context, email);



                Toast.makeText(context,
                        product.getName() + " agregado al carrito",
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productDescription, productPrice;
        ImageButton addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }



}



