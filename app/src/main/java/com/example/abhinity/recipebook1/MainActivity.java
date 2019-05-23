package com.example.abhinity.recipebook1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.abhinity.recipebook1.MySingleton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener{

    ListView list;
    EditText search;
    Button button;
    String[] labels = new String[10];
    String[] authors = new String[10];
    String[] srcs = new String[10];
    JSONArray hits;
    Button takePicture;
    private static final String TAG = "MainActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Feature feature;
    Bitmap bitmap;
    ProgressBar imageProgressBar;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDnmDhFchckZxxnvMoc6gauyKrdolxpDMU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = (EditText) findViewById(R.id.searchText);
        button = (Button) findViewById(R.id.searchButton);
        list = (ListView) findViewById(R.id.list_view);
        takePicture = findViewById(R.id.imageRec);
        feature = new Feature();
        feature.setType("LABEL_DETECTION");
        feature.setMaxResults(10);

        imageProgressBar = findViewById(R.id.imageProgressBar);
        imageProgressBar.setVisibility(View.GONE);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromCamera();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageProgressBar.setVisibility(View.VISIBLE);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String searchKey = search.getText().toString();
                //Toast.makeText(MainActivity.this,searchKey,Toast.LENGTH_SHORT).show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.edamam.com/search?q="+searchKey+"&app_id=34d33bb0&app_key=26cc813ab6ecc3896b115af301f86a23",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                                imageProgressBar.setVisibility(View.GONE);
                                Document document = Jsoup.parse(response);
                                Element body = document.body();
                                try {
                                    JSONObject jsonObject = new JSONObject(body.text());
                                    hits = jsonObject.getJSONArray("hits");
                                    for(int i = 0; i< hits.length(); i++)
                                    {
                                        JSONObject myobj = hits.getJSONObject(i).getJSONObject("recipe");
                                        String author = myobj.getString("source");
                                        String label = myobj.getString("label");
                                        String src = myobj.getString("image");
                                        labels[i] = label;
                                        authors[i] = author;
                                        srcs[i] = src;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this,"Request failed",Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);
                //Log.d("Server",s);

                MyAdapter myAdapter = new MyAdapter(MainActivity.this, labels, authors, srcs);
                list.setAdapter(myAdapter);
                list.setOnItemClickListener(MainActivity.this);
            }
        });

        if (checkPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture.setVisibility(View.VISIBLE);
        } else {
            takePicture.setVisibility(View.INVISIBLE);
            makeRequest(Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this,RecipeActivity.class);
        try {
            intent.putExtra("recipe",hits.getJSONObject(position).getJSONObject("recipe").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            callCloudVision(bitmap, feature);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                takePicture.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap, final Feature feature) {
        imageProgressBar.setVisibility(View.VISIBLE);
        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                search.setText(result);
                imageProgressBar.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        List<EntityAnnotation> entityAnnotations;

        String message = "";
        entityAnnotations = imageResponses.getLabelAnnotations();
        message = formatAnnotation(entityAnnotations);
        return message;
    }

    private String formatAnnotation(List<EntityAnnotation> entityAnnotation) {
        String message = "";

        if (entityAnnotation != null) {
            /*for (EntityAnnotation entity : entityAnnotation) {
                message = message + "    " + entity.getDescription() + " " + entity.getScore();
                message += "\n";
            }*/
            message = entityAnnotation.get(0).getDescription();
        } else {
            message = "Nothing Found";
        }
        return message;
    }

}


class MyAdapter extends ArrayAdapter<String>{
    Context context;
    String[] labels;
    String[] authors;
    String[] srcs;
    MyAdapter(Context c, String[] labels, String[] authors, String[] srcs){
        super(c,R.layout.list_row,R.id.labelView,labels);
        this.context=c;
        this.authors = authors;
        this.srcs = srcs;
        this.labels=labels;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);
        final ImageView imageView = (ImageView) row.findViewById(R.id.img);
        TextView label = (TextView) row.findViewById(R.id.labelView);
        TextView author = (TextView) row.findViewById(R.id.authorView);
        label.setText(labels[position]);
        author.setText("By- "+authors[position]);
        //For the image, sending the request now, thank you very much!
        ImageRequest imageRequest = new ImageRequest(srcs[position], new Response.Listener<Bitmap>(){

            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }},imageView.getMaxHeight(),imageView.getMaxWidth(),ImageView.ScaleType.CENTER_CROP,null, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(this.getContext()).addtoRQ(imageRequest);
        return row;
    }
}
