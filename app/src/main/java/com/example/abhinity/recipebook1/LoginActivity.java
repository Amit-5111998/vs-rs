package com.example.abhinity.recipebook1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText login_user;
    EditText login_pass;
    Button login_button;
    String username;
    String password;
    ProgressDialog progressDialog;
    String URL = "http://abhinitymerai3.000webhostapp.com/connect/login.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_user = (EditText)findViewById(R.id.login_user);
        login_pass = (EditText)findViewById(R.id.login_pass);
        login_button = (Button)findViewById(R.id.login_button);
        initial();
    }
    private void initial() {
        login_button.setOnClickListener((View.OnClickListener) this);

    }
    public void GetValueFromEditText() {
        username = login_user.getText().toString().trim();
        password = login_pass.getText().toString().trim();


    }

    @Override
    public void onClick(View view) {
        if (view == login_button) {


            GetValueFromEditText();

            if (username.equals("")) {
                login_user.setError("Username required");
            } else if (password == null) {
                login_pass.setError("Password requrired");
            }
            if(!(username.equals("") || password.equals("")))
            {
                submitForm();
            }
        }
    }
    private void submitForm() {
        progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait, We are Inserting Your Data on Server");
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String ServerResponse) {
                            //Toast.makeText(LoginActivity.this, ServerResponse.toString(), Toast.LENGTH_LONG).show();
                            // Hiding the progress dialog after all task complete.
                            if(ServerResponse.equals("Success"));
                            {
                                Intent intent = new Intent(LoginActivity.this, RecipeList.class);
                                startActivity(intent);
                            }
                            progressDialog.dismiss();

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
                            Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();

                    // Adding All values to Params.
                    params.put("name", username);
                    params.put("password", password);
                    Log.e("PARAMS", String.valueOf(params));
                    return params;
                }

            };

            // Creating RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

            // Adding the StringRequest object into requestQueue.
            requestQueue.add(stringRequest);

        }

    }


