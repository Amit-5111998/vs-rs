package com.example.abhinity.recipebook1;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Admin on 04-06-2018.
 */

public class MySingleton {
    public static MySingleton mySingleton;
    private RequestQueue requestQueue;
    private static Context ctx;

    private MySingleton(Context context)
    {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context)
    {
        if(mySingleton == null)
        {
            mySingleton = new MySingleton(context);
        }
        return mySingleton;
    }

    public <T> void addtoRQ(Request<T> request){
        requestQueue.add(request);
    }

}
