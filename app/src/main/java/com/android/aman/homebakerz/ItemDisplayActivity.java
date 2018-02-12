package com.android.aman.homebakerz;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ItemDisplayActivity extends AppCompatActivity implements ItemDisplayAdapterClass.ItemClickListener {

    private DatabaseReference mInfoDatabaseReference;

    ItemDisplayAdapterClass itemDisplayAdapter;
    private static final int noOfColumns = 2;
    List<ItemInfoClass> itemInfoClass = new ArrayList<>();
    String database_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(HomeScreenActivity.DATABASE_NAME)) {
            database_name = intentThatStartedThisActivity.getStringExtra(HomeScreenActivity.DATABASE_NAME);
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ItemDisplayActivity.this, noOfColumns));
        itemDisplayAdapter = new ItemDisplayAdapterClass(getApplicationContext(), itemInfoClass, this);
        recyclerView.setAdapter(itemDisplayAdapter);

        mInfoDatabaseReference = FirebaseDatabase.getInstance().getReference(database_name);
        mInfoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemInfoClass itemInfo = snapshot.getValue(ItemInfoClass.class);
                    itemInfoClass.add(itemInfo);
                }
                itemDisplayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void OnItemClicked(View view, int itemPosition) {

        Intent intent = new Intent(this, ItemDetailedDescriptionActivity.class);
        intent.putExtra("KEY",itemInfoClass.get(itemPosition));
        startActivity(intent);
    }
}
