package com.sophie.miller.roomwithlivedata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.sophie.miller.roomwithlivedata.database.ProductDatabase;
import com.sophie.miller.roomwithlivedata.databinding.ActivityMainBinding;
import com.sophie.miller.roomwithlivedata.objects.Product;
import com.sophie.miller.roomwithlivedata.utils.AppExecutors;
import com.sophie.miller.roomwithlivedata.utils.DatabaseAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainActivityBinding;
    ProductDatabase database;
    DatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainActivityBinding.getRoot());

        database = ProductDatabase.getInstance(this);
        databaseAdapter = new DatabaseAdapter(this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().getDatabaseExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        database.productDao().deleteProduct(databaseAdapter.getProducts().get(viewHolder.getAdapterPosition()));
                    }
                });
            }
        }).attachToRecyclerView(mainActivityBinding.mainRecyclerView);

        mainActivityBinding.mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainActivityBinding.mainRecyclerView.setAdapter(databaseAdapter);
        mainActivityBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product(mainActivityBinding.productName.getText().toString(), mainActivityBinding.description.getText().toString(), Double.parseDouble(mainActivityBinding.price.getText().toString()));
                ProductDatabase database = ProductDatabase.getInstance(MainActivity.this);
                database.productDao().insertProduct(product);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final LiveData<List<Product>> products = database.productDao().loadAllProducts();
        products.observe(MainActivity.this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                databaseAdapter.setItems(products);
            }
        });
    }
}
