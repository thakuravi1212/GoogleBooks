package com.avinash.googlebooks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Book class
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> book){
        super(context, 0, book);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        Book book = getItem(position);

        if(listItemView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.book_list_item, parent, false);
        }
        TextView titleView = (TextView) listItemView.findViewById(R.id.book_title);
        TextView authorView = (TextView) listItemView.findViewById(R.id.author_name);
        TextView priceView = (TextView) listItemView.findViewById(R.id.book_price);
        TextView descrpView = (TextView) listItemView.findViewById(R.id.book_description);
        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.book_thumbnail);

        titleView.setText(book.getmTitle());
        authorView.setText(book.getmAuthor());
        descrpView.setText(checkForEmptyDescription(book.getmDescription()));
        bookImage.setImageBitmap(book.getmThumbnail());
        priceView.setText(checkSalePrice(book.getmPrice()));
        return listItemView;
    }

    private String checkSalePrice(double price){
        // To check book is available for sale or not
        if(price == 0.0){
            return "NO_SALE";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return "₹ " + decimalFormat.format(price);
    }
    // Helper method to check for empty description
    private String checkForEmptyDescription(String str){
        if(str == null || str.isEmpty()){
            return str = "Description not available.";
        }
        return str;
    }

}
