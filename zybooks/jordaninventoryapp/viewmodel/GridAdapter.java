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
import com.zybooks.jordaninventoryapp.R;
import com.zybooks.jordaninventoryapp.model.Item;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    private List<Item> itemList = new ArrayList<>();
    private boolean needsSorted = true;
    private List<Item> filteredList = new ArrayList<>();
    private final int UPDATE_REQUEST_CODE = 1;
    private final String TAG = "GridAdapter";
    private InventoryListViewModel viewModel;
    private Context context;

    public GridAdapter(Context context, InventoryListViewModel viewModel) {
        Log.d(TAG, "GridAdapter()");
        this.context = context;
        this.viewModel = viewModel;

        viewModel.getItems().observe((LifecycleOwner) context, items -> {
            this.itemList = items;

            // the list only needs to be sorted when the app is initialized
            if (needsSorted) {
                itemList.sort(Comparator.naturalOrder());
            }
            needsSorted = false;
            Log.d(TAG, items.toString());
            notifyDataSetChanged();
        });
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setFilteredList(List<Item> itemList) {
        this.filteredList = itemList;
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
        Item item = filteredList.get(position);
        holder.item.setText("Name: " + item.getName());
        holder.quantity.setText("Quantity: " + item.getQuantity());

        // for button click
        holder.deleteButton.setOnClickListener(v -> {
            // sets the update position field in the viewModel
            viewModel.deleteItem(item);
            filteredList.remove(position);
            notifyItemRemoved(position);
        });

        holder.editButton.setOnClickListener(v -> {
            // sets the update position field in the viewModel
            viewModel.updatePosition(position);

            Intent intent = new Intent(context, AddItemActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("quantity", String.valueOf(item.getQuantity()));
            intent.putExtra("description", item.getDescription());
            intent.putExtra("id", item.getId());
            ((Activity) context).startActivityForResult(intent, UPDATE_REQUEST_CODE);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void setItems(List<Item> newItemList) {
        Log.d(TAG, "setItems");
        itemList = newItemList;
        filteredList = newItemList;
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
