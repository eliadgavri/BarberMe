package model;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barberme.R;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {

    ArrayList<Uri> pictures;
    Context context;

    class PictureViewHolder extends RecyclerView.ViewHolder
    {
        ImageView picture;
        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.picture_box);
        }
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pictures_cardview, parent, false);
        PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
        context = parent.getContext();
        return pictureViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        Uri picture = pictures.get(position);
        Glide.with(context).load(pictures.get(position)).into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }
}
