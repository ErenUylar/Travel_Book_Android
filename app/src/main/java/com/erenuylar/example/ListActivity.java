package com.erenuylar.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.erenuylar.example.databinding.ActivityListBinding;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding binding;
    private ArrayList<Travel> travelArrayList;
    private SQLiteDatabase database;
    RecyleAdapter recyleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        travelArrayList = new ArrayList<>();

        database = this.openOrCreateDatabase("Travel", MODE_PRIVATE, null);

        binding.recyleView.setLayoutManager(new LinearLayoutManager(this));
        recyleAdapter = new RecyleAdapter(travelArrayList);
        binding.recyleView.setAdapter(recyleAdapter);

        getData();
    }

    private void getData() {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM travel", null);
            int idIx = cursor.getColumnIndex("id");
            int travelIx = cursor.getColumnIndex("travelName");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIx);
                String name = cursor.getString(travelIx);
                Travel travel = new Travel(id, name);
                travelArrayList.add(travel);
            }
            recyleAdapter.notifyDataSetChanged();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.travel_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.newAdd) {
            Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
            intent.putExtra("info", "new");
            startActivity(intent);
            overridePendingTransition(R.anim.sag, R.anim.sol);
        }
        return super.onOptionsItemSelected(item);
    }
}