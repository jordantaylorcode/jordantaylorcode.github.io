package com.zybooks.jordaninventoryapp.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.jordaninventoryapp.AddItemActivity;
import com.zybooks.jordaninventoryapp.MainActivity;
import com.zybooks.jordaninventoryapp.R;
import com.zybooks.jordaninventoryapp.model.Item;
import com.zybooks.jordaninventoryapp.repo.ItemDao;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {
    private List<Item> itemList = new ArrayList<>();
    private final String TAG = "GridAdapter";
    private InventoryListViewModel viewModel;
    private Context context;
    public GridAdapter(Context context, InventoryListViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;

        viewModel.getItems().observe((LifecycleOwner) context, items -> {
            this.itemList = items;
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "GridViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_items, parent, false);

        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        Log.d(TAG, String.valueOf(position));
        Item item = itemList.get(position);
        holder.item.setText("Name: " + item.getName());
        holder.quantity.setText("Quantity: " + String.valueOf(item.getQuantity()));

        // for button click
//        holder.editButton.setOnClickListener(v -> editClick(item));
        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "delete clicked");
            viewModel.deleteItem(item);
            itemList.remove(position);
            notifyItemRemoved(position);
        });

        holder.editButton.setOnClickListener(v -> {
            Log.d(TAG, "update clicked");
            Intent intent = new Intent(context, AddItemActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("quantity", String.valueOf(item.getQuantity()));
            intent.putExtra("description", item.getDescription());
            intent.putExtra("id", item.getId());
            ((Activity) context).startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<Item> newItemList) {
        Log.d(TAG, "setItems");
        itemList = newItemList;
        notifyDataSetChanged();
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        TextView quantity;
        String TAG = "GridViewHolder";
        Button editButton;
        Button deleteButton;

        public GridViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "GridViewHolder");
            item = itemView.findViewById(R.id.item1);
            quantity = itemView.findViewById(R.id.quantity1);
            editButton = itemView.findViewById(R.id.button31);
            deleteButton = itemView.findViewById(R.id.button32);
        }
    }
}
