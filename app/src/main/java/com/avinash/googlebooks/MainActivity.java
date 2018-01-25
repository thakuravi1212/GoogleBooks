package com.avinash.googlebooks;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;
    private final String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=";
    private EditText searchEditText;
    private BookAdapter bookAdapter;
    private TextView emptyTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Code to make editText indicator transparent
        searchEditText =(EditText) findViewById(R.id.search_edit_text);
        searchEditText.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        
        ListView listView = (ListView) findViewById(R.id.books_list);
        emptyTextView = (TextView)findViewById(R.id.empty_state_textView);
        listView.setEmptyView(emptyTextView);
        ImageView searchButton = (ImageView) findViewById(R.id.search_button);
        progressBar =(ProgressBar) findViewById(R.id.progress_indicator);
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());


        boolean isConnected = checkInternetConnectivity();
        if(!isConnected){
            // Handle no internet connectivity
            emptyTextView.setText(R.string.no_internet);
        }else{
            emptyTextView.setText(R.string.empty_list_first_search);
        }
        listView.setAdapter(bookAdapter);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the behaviour of search button click
                String searchQuery = searchEditText.getText().toString();
                boolean isConnected = checkInternetConnectivity();
                if(searchQuery!=null&& !searchQuery.isEmpty() && isConnected){
                    hideSoftKeyboard(MainActivity.this);
                    emptyTextView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                }else if(!isConnected){
                    Toast.makeText(getBaseContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }else if(searchQuery.isEmpty()){
                    {
                        // Handle empty search query
                        Toast.makeText(getBaseContext(), "I can't find anonymous books.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = bookAdapter.getItem(position);
               /* if(book.getmBuyLink()!= null && !book.getmBuyLink().isEmpty()){
                    Uri webPage = Uri.parse(book.getmBuyLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                    if(intent.resolveActivity(getPackageManager())!= null){
                        startActivity(intent);
                    }


                }*/

                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                              intent.putExtra("Book", book);
                                startActivity(intent);

            }
        });


    }
    // Helper method to check for active internet connection
    // returns true if connectivity is available.
    private Boolean checkInternetConnectivity(){
        // Code to check the network connectivity status
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Method to generate dynamic urls based on search text
     * @param url takes url string
     * @return a url string
     */
    private String getSearchUrl(String url ){
        String params = searchEditText.getText().toString();
        params = params.replace(" ","+");
        url += params + "&filter=ebooks&prettyPrint=false";
        return url;
    }

    /**
     * Helper method to hide soft keyboard when search button is clicked.
     * @param activity takes current activity context
     */
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.wtf(LOG_TAG,"Loader Initiated! with id:" + BOOK_LOADER_ID);
        return new BookLoader(this, getSearchUrl(baseUrl));

    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        progressBar.setVisibility(View.GONE);
        bookAdapter.clear();
        if(books!=null && !books.isEmpty()){
            bookAdapter.addAll(books);
        }
        emptyTextView.setText(R.string.empty_list_text);

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.wtf(LOG_TAG,"Loader reset! with id:" + BOOK_LOADER_ID);
        bookAdapter.clear();
    }

}
