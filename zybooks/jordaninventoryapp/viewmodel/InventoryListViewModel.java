package com.zybooks.jordaninventoryapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;
import android.telephony.SmsManager;
import android.widget.Toast;
import com.zybooks.jordaninventoryapp.model.ApiResponse;
import com.zybooks.jordaninventoryapp.model.Item;
import com.zybooks.jordaninventoryapp.repo.HTTPService;
import com.zybooks.jordaninventoryapp.repo.InventoryRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryListViewModel extends AndroidViewModel {

    private final HTTPService repository;
    final MutableLiveData<List<Item>> allItems = new MutableLiveData<>();
    private final static String TAG = "InventoryListViewModel";
    private int updatePosition = 0;

    public InventoryListViewModel(Application application) {
        super(application);

        repository = InventoryRepository.getInstance().getService();
        Call<ApiResponse> call = repository.getItems();
        Log.d(TAG, "checkUserExists(user)");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, response.body().toString());
                    allItems.postValue(response.body().getItems());
                } else {
                    Log.d(TAG, "Error: " + response.message());
                    allItems.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Item>> getItems() {
        return allItems;
    }

    public void addItem(Item item) {
        Call<ApiResponse> call = repository.createItem(item);
        Log.d(TAG, "addItem");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    List<Item> currentItems = allItems.getValue();
                    if (status.equals("success") && currentItems != null) {
                        // index uses a binary search variant that returns the index
                        // of the list where the item should go to remain sorted
                        int index = getAddPosition(currentItems, item);
                        currentItems.add(index, item);
                        allItems.postValue(currentItems);
                    }
                    Log.d(TAG, "AddItem " + status);
                } else {
                    Log.d(TAG, "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "Network error");
            }
        });
    }

    public void updatePosition(int position) {
        updatePosition = position;
    }

    public void updateItem(Item item) {
        if (item.getQuantity() < 1) {
            String message = item.getName() + "has run out.";
            send_sms("6505551212", message);
        }
        Call<ApiResponse> call = repository.updateItem(item);
        Log.d(TAG, "updateItem");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    List<Item> currentItems = allItems.getValue();
                    if (status.equals("success") && currentItems != null) {
                        int index = getAddPosition(currentItems, item);
                        // remove old item add new item to the list
                        currentItems.remove(updatePosition);
                        currentItems.add(index, item);
                        allItems.postValue(currentItems);
                    }
                    Log.d(TAG, "AddItem " + status);
                } else {
                    Log.d(TAG, "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "Network error");
            }
        });
    }

    public void deleteItem(Item item) {
        Log.d(TAG, "deleteItem");
        new Thread(() -> {
            Call<ApiResponse> call = repository.deleteItem(item);
            Log.d(TAG, "updateItem");

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String status = response.body().getStatus();
                        Log.d(TAG, "AddItem " + status);
                    } else {
                        Log.d(TAG, "Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d(TAG, "Network error");
                }
            });
        }).start();
    }

    private void send_sms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(InventoryListViewModel.this.getApplication(), "SMS sent.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(InventoryListViewModel.this.getApplication(), "Could not send SMS.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "" + e.getMessage());
        }
    }

    // gets the index from the sorted list where the item should go
    private int getAddPosition(List<Item> items, Item target) {
        int n = items.size();
        int l = 0;
        int r = n - 1;
        int boundary_index = -1;
        int mid;

        while (l <= r) {
            mid = l + (r - l) / 2;
            if (items.get(mid).getName().compareToIgnoreCase(target.getName()) >= 0) {
                boundary_index = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return boundary_index;
    }
}
