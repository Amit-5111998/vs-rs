package com.example.abhinity.recipebook1;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeList extends AppCompatActivity {
    RecyclerView RvUser;
    RecieveRecipeAdapterClass adapter;
    String URL = "http://abhinitymerai3.000webhostapp.com/connect/displayrecipe.php";
    List<reciveRecipe> recieveList= new ArrayList<>();
    RequestQueue requestQueue ;
    RecyclerView.LayoutManager mLayoutManager;
    private Picasso picasso;
    private String video;
    ImageView imgrecipe;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipelist);
        imgrecipe = findViewById(R.id.imgrecipe);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        search = findViewById(R.id.searchr);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecipeList.this,MainActivity.class));
            }
        });
        picasso = new Picasso.Builder(this).memoryCache(new LruCache(2)).build();
        requestQueue = Volley.newRequestQueue(this);
        RvUser = (RecyclerView) findViewById(R.id.RvUser);
        /*insert();*/

        setadapter();
        show();
    }
    public void show()
    {
        StringRequest obreq = new StringRequest(Request.Method.POST, URL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter

                new Response.Listener<String>() {
                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(String response) {
                        Log.e("URL",URL);

                        Log.e("RES",response);

                        try {
                            recieveList.clear();
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONArray Array = obj.getJSONArray("User");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < Array.length(); i++) {
                                reciveRecipe info=new reciveRecipe();
                                //getting the json object of the particular index inside the array
                                JSONObject Object = Array.getJSONObject(i);
                                //creating a hero object and giving them the values from json object

                                info.setRecipename(Object.getString("recipename"));
                                info.setRecipeTime(Object.getString("recipetime"));
                                info.setRecipeimage(Object.getString("recipeimage"));
                                info.setRecipevideo(Object.getString("recipevideo"));
                                video = Object.getString("recipevideo");
                                //adding the hero to herolist
                                recieveList.add(info);

                                Log.e("bbbb",info.getRecipename());
                            }
                            Log.e("123", String.valueOf(recieveList.size()));
                            //creating custom adapter object

                            //adding the adapter to listview


                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", String.valueOf(error));
                    }
                }

        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(obreq);
    }
    public void setadapter()
    {

        adapter = new RecieveRecipeAdapterClass(recieveList, RecipeList.this, picasso, new RecipeListClickListener() {
            @Override
            public void onRecipeItemClickListener(reciveRecipe recipe) {
                Log.e("name",recipe.getRecipename());
                Intent intent = new Intent(RecipeList.this,RecipeVideo.class);
                intent.putExtra("video",recipe.getRecipevideo());
                Log.e("VIDEO",video);
                startActivity(intent);
            }
        });
        RvUser.setAdapter(adapter);
        RvUser.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
    }


}
