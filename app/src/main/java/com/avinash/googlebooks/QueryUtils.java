package com.avinash.googlebooks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper methods related to requesting and receiving books data from Google Books api.
 */
class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){
    }

    /**
     * Public helper method to fetch books data and calling all the networking code within this method.
     * @param requestUrl takes current request url
     * @return List of {@link} Book objects.
     */

    public static List<Book> fetchBooksData(String requestUrl){
        URL url = createURL(requestUrl);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG,"Error while closing input Stream", e);
        }
        List<Book> books = extractBookFromJSON(jsonResponse);
        return books;
    }

    /**
     *  Helper method to create {@link}URL object
     * @param stringUrl takes a String url
     * @return URL object
     */
    @Nullable
    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.wtf(LOG_TAG,"Error while creating URL", e);
            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = " ";
        // If url is null return early
        if(url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(2000);
            urlConnection.connect();
            if(urlConnection.getResponseCode()== 200){
                Log.wtf(LOG_TAG,"Connection Success!");
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.wtf(LOG_TAG, "error while connecting, http error code: " + urlConnection.getResponseCode());
                return null;
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Error reading the input stream", e);
        }finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /**
     * Make an HTTP request to the given imageURL and return a Bitmap as the response.
     */
    private static Bitmap makeHttpRequest(String imageUrl)throws IOException{
        Bitmap thumbnail = null;
        if(imageUrl == null){
            return thumbnail;
        }

        URL url = createURL(imageUrl);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                thumbnail = BitmapFactory.decodeStream(inputStream);
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Error reading the input stream", e);
        }finally {
        if(urlConnection != null) {
            urlConnection.disconnect();
        }
        if(inputStream != null){
            inputStream.close();
        }
    }
        return thumbnail;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    @NonNull
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    @Nullable
    private static List<Book> extractBookFromJSON(final String JSON_RESPONSE){
        // Empty book List that we can start adding books to.
        List<Book> books = new ArrayList<>();

        try{
            JSONObject root = new JSONObject(JSON_RESPONSE);
            JSONArray items = root.getJSONArray("items");

            for(int i = 0;i < items.length();i++){
                String authorNames = "";
                double price;
                String description;
                String buyLink = "";
                JSONObject element = items.getJSONObject(i);
                String id = element.getString("id");
                JSONObject volumeInfo = element.getJSONObject("volumeInfo");
                JSONObject searchInfo = element.optJSONObject("searchInfo");
                // When book has no description

                description = volumeInfo.optString("description");
                if(description == null){
                    description = searchInfo.optString("textSnippet");
                }
                String title = volumeInfo.getString("title");
                JSONArray authors = volumeInfo.optJSONArray("authors");
                // When book has more than one author
                if(authors!=null){
                    if(authors.length()> 1){
                        for (int j = 0;j < authors.length();j++ ){
                            authorNames += authors.getString(j);
                            authorNames += " ";
                        }
                    } else{
                        authorNames = authors.getString(0);
                    }
                }
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imageLinks.getString("thumbnail");
                // Loading the bitmap from given imageUrl
                Bitmap bookThumbnail = makeHttpRequest(imageUrl);
                JSONObject saleInfo = element.getJSONObject("saleInfo");
                String saleability = saleInfo.getString("saleability");
                // When book is not for sale
                if(saleability.equals("NOT_FOR_SALE" )|| saleInfo.optJSONObject("retailPrice")==null){
                    // set price to 0.0
                    price = 0.0;
                }else{
                    price = saleInfo.optJSONObject("retailPrice").getDouble("amount");
                    buyLink = saleInfo.getString("buyLink");
                }
                books.add(new Book(id, title, authorNames,bookThumbnail, description, price, buyLink));
            }
            // returns a list of books
            return books;

        }catch(JSONException e) {
            Log.e(LOG_TAG,"Problem parsing the json results", e);
            e.printStackTrace();
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem closing the input stream of imageUrl",e);
        }
        return null;
    }



}
