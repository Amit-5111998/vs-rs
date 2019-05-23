package com.example.abhinity.recipebook1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RecieveRecipeAdapterClass extends RecyclerView.Adapter<RecieveRecipeAdapterClass.ViewHolder> {

    private List<reciveRecipe> recieveList;
    private Context mCtx;
    private Picasso picasso;
    private RecipeListClickListener listener;


    public RecieveRecipeAdapterClass(List<reciveRecipe> recieveList, Context mCtx,Picasso picasso,RecipeListClickListener listener) {
        this.recieveList = recieveList;
        this.mCtx = mCtx;
        this.picasso = picasso;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe, parent, false);
        return new RecieveRecipeAdapterClass.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindviewholder(recieveList.get(position), position,listener);
    }

    @Override
    public int getItemCount() {
        return recieveList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView recipename;
        TextView recipeTime;
        ImageView imgrecipe;

        public ViewHolder(View itemView) {
            super(itemView);
            recipename = (TextView) itemView.findViewById(R.id.recipename);
            recipeTime = (TextView) itemView.findViewById(R.id.recipetime);
            imgrecipe = (ImageView)itemView.findViewById(R.id.imgrecipe);
        }

        public void bindviewholder(final reciveRecipe reciveRecipe, int position, final RecipeListClickListener listener) {

            recipename.setText(String.valueOf(reciveRecipe.getRecipename()));
            recipeTime.setText(String.valueOf(reciveRecipe.getRecipeTime()));
            picasso.load(recieveList.get(position).getRecipeimage()).placeholder(R.drawable.ic_launcher_background)   // optional
                    .error(R.drawable.ic_launcher_background).into(imgrecipe);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRecipeItemClickListener(reciveRecipe);
                }
            });

        }
    }

}
