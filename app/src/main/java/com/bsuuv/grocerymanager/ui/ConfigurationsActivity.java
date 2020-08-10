package com.bsuuv.grocerymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsuuv.grocerymanager.R;
import com.bsuuv.grocerymanager.db.entity.FoodItemEntity;
import com.bsuuv.grocerymanager.ui.adapters.ConfigurationsListAdapter;
import com.bsuuv.grocerymanager.util.RecyclerViewUtil;
import com.bsuuv.grocerymanager.viewmodel.FoodItemViewModel;

import java.util.Objects;

/**
 * An activity containing a list of all food-items the user has configured
 * to show in his grocery list on a certain time. Gives options to create
 * new food-items and edit existing ones.
 */
public class ConfigurationsActivity extends AppCompatActivity {

    public static final int FOOD_ITEM_EDIT_REQUEST = 2;

    private static final int FOOD_ITEM_CREATE_REQUEST = 1;

    private ConfigurationsListAdapter mAdapter;
    private FoodItemViewModel mFoodItemViewModel;
    private RecyclerView mRecyclerView;
    private TextView mRecyclerViewPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurations);
        initMembers();
        configureUi();
        setUpViewModel();
    }

    private void initMembers() {
        this.mRecyclerViewPlaceHolder = findViewById(R.id.config_recyclerview_placeholder);
        this.mFoodItemViewModel = new ViewModelProvider(this).get(FoodItemViewModel.class);
        this.mRecyclerView = findViewById(R.id.config_recyclerview);
        this.mAdapter = new ConfigurationsListAdapter(this);
    }

    private void configureUi() {
        setTitle("Food-items");
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper helper = initializeItemTouchHelper();
        helper.attachToRecyclerView(mRecyclerView);
    }

    private ItemTouchHelper initializeItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int deletedPosition = viewHolder.getAdapterPosition();
                mFoodItemViewModel.delete(mAdapter.getFoodItemAtPosition(deletedPosition));
            }
        });
    }

    private void setUpViewModel() {
        mFoodItemViewModel.getFoodItems().observe(this, foodItemEntities -> {
            setRecyclerViewVisibility(foodItemEntities.size());
            mAdapter.setFoodItems(foodItemEntities);
        });
    }

    private void setRecyclerViewVisibility(int size) {
        if (size > 0) {
            RecyclerViewUtil.toggleRecyclerViewVisibility(mRecyclerView, mRecyclerViewPlaceHolder
                    , View.VISIBLE, 0);
        } else {
            RecyclerViewUtil.toggleRecyclerViewVisibility(mRecyclerView, mRecyclerViewPlaceHolder
                    , View.GONE, R.string.no_grocery_items);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent fromNewFoodItem) {
        super.onActivityResult(requestCode, resultCode, fromNewFoodItem);

        if (foodItemCreationSuccesful(requestCode, resultCode, fromNewFoodItem)) {
            FoodItemEntity result = createFoodItemFromIntent(fromNewFoodItem);
            mFoodItemViewModel.insert(result);
        } else if (foodItemEditSuccesful(requestCode, resultCode, fromNewFoodItem)) {
            FoodItemEntity result = updateFoodItemByIntent(fromNewFoodItem);
            mFoodItemViewModel.update(result);
        }
    }

    private boolean foodItemCreationSuccesful(int requestCode, int resultCode,
                                              Intent fromNewFoodItem) {
        return requestCode == FOOD_ITEM_CREATE_REQUEST &&
                resultCode == RESULT_OK &&
                fromNewFoodItem != null;
    }

    private FoodItemEntity createFoodItemFromIntent(Intent fromNewFoodItem) {
        String label = Objects.requireNonNull(fromNewFoodItem.getStringExtra("label"));
        String brand = fromNewFoodItem.getStringExtra("brand");
        int amount = fromNewFoodItem.getIntExtra("amount", 0);
        String unit = fromNewFoodItem.getStringExtra("unit");
        String info = fromNewFoodItem.getStringExtra("info");
        int timeFrame = fromNewFoodItem.getIntExtra("time_frame", 0);
        int frequency = fromNewFoodItem.getIntExtra("frequency", 0);
        String imageUri = fromNewFoodItem.getStringExtra("uri");
        double initCountdownValue = fromNewFoodItem.getDoubleExtra("frequencyQuotient", 0.0);

        return new FoodItemEntity(label, brand, info, amount, unit, timeFrame, frequency, imageUri,
                initCountdownValue);
    }

    private boolean foodItemEditSuccesful(int requestCode, int resultCode, Intent fromNewFoodItem) {
        return requestCode == FOOD_ITEM_EDIT_REQUEST &&
                resultCode == RESULT_OK &&
                fromNewFoodItem != null;
    }

    private FoodItemEntity updateFoodItemByIntent(Intent fromNewFoodItem) {
        String label = Objects.requireNonNull(fromNewFoodItem.getStringExtra("label"));
        String brand = fromNewFoodItem.getStringExtra("brand");
        int amount = fromNewFoodItem.getIntExtra("amount", 0);
        String unit = fromNewFoodItem.getStringExtra("unit");
        String info = fromNewFoodItem.getStringExtra("info");
        int timeFrame = fromNewFoodItem.getIntExtra("time_frame", 0);
        int frequency = fromNewFoodItem.getIntExtra("frequency", 0);
        String imageUri = fromNewFoodItem.getStringExtra("uri");
        int id = fromNewFoodItem.getIntExtra("id", 0);
        double countdownValue = fromNewFoodItem.getDoubleExtra("countdownValue", 0);

        return new FoodItemEntity(id, label, brand, info, amount, unit, timeFrame, frequency,
                imageUri, countdownValue);
    }

    /**
     * Called when the floating action button in this activity is pressed. Launches
     * <code>NewFoodItemActivity</code> for creating a new <code>FoodItem</code>.
     *
     * @param view The view that has been clicked, in this case, the FAB.
     *             Default parameter required by the system.
     */
    public void onFabClick(View view) {
        Intent toNewFoodItem = new Intent(this, NewFoodItem.class);
        startActivityForResult(toNewFoodItem, FOOD_ITEM_CREATE_REQUEST);
    }
}
