package com.example.abhinity.recipebook1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    Button sign_up_id;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    String username="", email="", password="";

    String URL = "http://abhinitymerai3.000webhostapp.com/connect/connect.php";
    //String URL = "http://methewsam.000webhost.com/Register.php";
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(SignupActivity.this);

        sign_up_id = findViewById(R.id.sign_up_id);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        initial();
    }
    private void initial() {


        sign_up_id.setOnClickListener((View.OnClickListener) this);

    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
            progressDialog.setMessage("Please Wait, We are Inserting Your Data on Server");
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String ServerResponse) {
                            Toast.makeText(SignupActivity.this, ServerResponse.toString(), Toast.LENGTH_LONG).show();
                            // Hiding the progress dialog after all task complete.
                            progressDialog.dismiss();
                            if(ServerResponse.equals("Success"))
                            {
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                            };
                            // Showing response message coming from server.
                            // Toast.makeText(SignupActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            // Hiding the progress dialog after all task complete.
                            progressDialog.dismiss();

                            // Showing error message if something goes wrong.
                            Toast.makeText(SignupActivity.this, volleyError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();

                    // Adding All values to Params.
                    params.put("name", username);
                    params.put("email", email);
                    params.put("password", password);
                    Log.e("PARAMS", String.valueOf(params));
                    return params;
                }

            };

            // Creating RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);

            // Adding the StringRequest object into requestQueue.
            requestQueue.add(stringRequest);

        }

    public void GetValueFromEditText() {
        username = inputUsername.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
    }

    @Override
    public void onClick(View view) {

        if (view == sign_up_id) {


            GetValueFromEditText();

            if (username.equals("")) {
                inputUsername.setError("Username required");
            } else if (email == null) {
                inputEmail.setError("Email required");
            } else if (password == null) {
                inputPassword.setError("Password required");
            }
            if(!(username.equals("") || email.equals("") || password.equals("")))
            {
                submitForm();
            }
        }
    }
}
