package com.example.abhinity.recipebook1;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {
    TextView recipeName, authorName, ingredients, dietLabels, healthLabels, nutrients;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        recipeName = (TextView) findViewById(R.id.recipeName);
        authorName = (TextView) findViewById(R.id.authorName);
        ingredients = (TextView) findViewById(R.id.ingredients);
        dietLabels = (TextView) findViewById(R.id.dietLabels);
        healthLabels = (TextView) findViewById(R.id.healthLabels);
        nutrients = (TextView) findViewById(R.id.nutrients);
        imageView = (ImageView) findViewById(R.id.img);
        try {
            JSONObject recipe = new JSONObject(getIntent().getExtras().getString("recipe"));
            String author = recipe.getString("source");
            String label = recipe.getString("label");
            String src = recipe.getString("image");
            JSONArray ingred = recipe.getJSONArray("ingredientLines");
            JSONArray diet = recipe.getJSONArray("dietLabels");
            JSONArray health = recipe.getJSONArray("healthLabels");
            JSONObject nutri = recipe.getJSONObject("totalNutrients");

            ImageRequest imageRequest = new ImageRequest(src, new Response.Listener<Bitmap>(){

                @Override
                public void onResponse(Bitmap response) {
                    imageView.setImageBitmap(response);
                }},imageView.getMaxHeight(),imageView.getMaxWidth(),ImageView.ScaleType.CENTER_CROP,null, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            MySingleton.getInstance(this).addtoRQ(imageRequest);

            recipeName.setText(label);
            authorName.setText("By- "+author);
            ingredients.setText("Ingredients: \n");
            dietLabels.setText("Diet Labels: \n");
            healthLabels.setText("Health Labels: \n");
            nutrients.setText("Nutrients: \n");
            for(int i = 0; i<ingred.length(); i++)
            {
                ingredients.append(ingred.get(i).toString()+"\n");
            }
            for(int i = 0; i<diet.length(); i++)
            {
                dietLabels.append(diet.get(i).toString()+"\n");
            }
            for(int i = 0; i<health.length(); i++)
            {
                healthLabels.append(health.get(i).toString()+"\n");
            }

            JSONObject energy = nutri.getJSONObject("ENERC_KCAL");
            JSONObject fat = nutri.getJSONObject("FAT");
            JSONObject sugar = nutri.getJSONObject("SUGAR");
            JSONObject vitc = nutri.getJSONObject("VITC");

            nutrients.append(energy.getString("label")+"- "+energy.getDouble("quantity")+" "+energy.getString("unit")+"\n");
            nutrients.append(fat.getString("label")+"- "+fat.getDouble("quantity")+" "+fat.getString("unit")+"\n");
            nutrients.append(sugar.getString("label")+"- "+sugar.getDouble("quantity")+" "+sugar.getString("unit")+"\n");
            nutrients.append(vitc.getString("label")+"- "+vitc.getDouble("quantity")+" "+vitc.getString("unit")+"\n");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
