package com.android.aman.homebakerz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ItemDetailedDescriptionActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name, price, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailed_description);

        Intent intentThatStartedThisActivity = getIntent();
        ItemInfoClass itemDetails = intentThatStartedThisActivity.getParcelableExtra("KEY");

        imageView = (ImageView) findViewById(R.id.itemDetailedDescriptionImgView);
        name = (TextView) findViewById(R.id.itemDetailedDescriptionItemNameTxtView);
        price = (TextView) findViewById(R.id.itemDetailedDescriptionItemPriceTxtView);
        description = (TextView) findViewById(R.id.itemDetailedDescriptionItemDescriptionTxtView);

        Glide.with(getApplicationContext())
                .load(itemDetails.getUrl())
                .into(imageView);
        name.setText(itemDetails.getName());
        price.setText(itemDetails.getPrice().toString());
    }
}
