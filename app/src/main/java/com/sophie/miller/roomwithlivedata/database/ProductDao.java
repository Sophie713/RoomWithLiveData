package com.sophie.miller.roomwithlivedata.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sophie.miller.roomwithlivedata.objects.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id")
    LiveData<List<Product>> loadAllProducts();

    @Insert
    void insertProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProduct(Product product);

    @Query("SELECT productDescription FROM products WHERE id = :id")
    LiveData<String> getProductDesctiption(int id);

    @Query("SELECT productName FROM products WHERE id = :id")
    LiveData<String> getProductName(int id);

    @Query("SELECT price FROM products WHERE id = :id")
    LiveData<Double> getProductPrice(int id);
}
