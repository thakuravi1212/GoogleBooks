package com.avinash.googlebooks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView titleView = findViewById(R.id.book_title);
        TextView authorView = findViewById(R.id.author_name);
        TextView priceView = findViewById(R.id.book_price);
        TextView descrpView = findViewById(R.id.book_description);
        ImageView bookImage = findViewById(R.id.book_thumbnail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Book book = getIntent().getParcelableExtra("Book");

        if (book != null) {
            titleView.setText(book.getmTitle());
            authorView.setText(book.getmAuthor());
            descrpView.setText(checkForEmptyDescription(book.getmDescription()));
            bookImage.setImageBitmap(book.getmThumbnail());
            priceView.setText(checkSalePrice(book.getmPrice()));
        }

    }

    private String checkForEmptyDescription(String str) {
        if (str == null || str.isEmpty()) {
            return str = "Description not available.";
        }
        return str;
    }

    private String checkSalePrice(double price) {
        // To check book is available for sale or not
        if (price == 0.0) {
            return "NO_SALE";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return "₹ " + decimalFormat.format(price);
    }

}
