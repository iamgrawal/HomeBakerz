package com.android.aman.homebakerz;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemDisplayAdapterClass extends RecyclerView.Adapter<ItemDisplayAdapterClass.ViewHolder> {

    List<ItemInfoClass> itemInfoClass;
    Context context;

    public ItemDisplayAdapterClass(Context context, List<ItemInfoClass> itemInfoClass, ItemClickListener listener) {
        this.itemInfoClass = itemInfoClass;
        this.context = context;
        mItemClickListener = listener;
    }

    final private ItemClickListener mItemClickListener;

    public interface ItemClickListener{
        void OnItemClicked(View view, int itemPosition);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_description, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.OnItemClicked(view, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name, price, url;
        ItemInfoClass item = itemInfoClass.get(position);
        name = item.getName();
        price = item.getPrice().toString();
        url = item.getUrl();
        holder.itemName.setText(name);
        holder.itemPrice.setText(price);
        Glide.with(holder.imgUrl.getContext())
                .load(url)
                .into(holder.imgUrl);
    }

    @Override
    public int getItemCount() {
        return itemInfoClass.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemPrice;
        ImageView imgUrl;
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            imgUrl = (ImageView) itemView.findViewById(R.id.itemImgView);
        }
    }
}
