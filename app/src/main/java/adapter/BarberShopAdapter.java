package adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barberme.R;

import java.util.List;

import userData.BarberShop;

public class BarberShopAdapter extends RecyclerView.Adapter<BarberShopAdapter.BarberShopViewHolder> {

    private List<BarberShop> barberShops;
    private boolean showEdit;
    MyBarberShopListener listener;
    private Context context;

    public BarberShopAdapter(List<BarberShop> barberShops, boolean showEdit) {
        this.barberShops = barberShops;
        this.showEdit = showEdit;
    }

    public interface MyBarberShopListener
    {
        void onBarberShopClick(int position,View view);
        void onEditBarberShopClick(int position,View view);
    }

    public void setListener(MyBarberShopListener listener){this.listener=listener;}
    @NonNull
    @Override
    public BarberShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.barber_cardview, parent, false);
        BarberShopAdapter.BarberShopViewHolder pictureViewHolder = new BarberShopAdapter.BarberShopViewHolder(view);
        context = parent.getContext();
        return pictureViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BarberShopViewHolder holder, int position) {
        BarberShop barberShop = barberShops.get(position);
        if(barberShop.getImages() != null || barberShop.getImages().size() != 0)
            Glide.with(context).load(barberShop.getImages().get(0)).into(holder.picture);
        holder.title.setText(barberShop.getName());
        holder.city.setText(barberShop.getCity());
    }

    @Override
    public int getItemCount() {
        return barberShops.size();
    }

    class BarberShopViewHolder extends RecyclerView.ViewHolder
    {
        ImageView picture;
        TextView title;
        TextView city;
        ImageView edit;
        public BarberShopViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.barebrshop_picture);
            title = itemView.findViewById(R.id.barebrshop_name);
            city = itemView.findViewById(R.id.barebrshop_city);
            edit = itemView.findViewById(R.id.edit_barebrshop_bt);
            edit.setVisibility(showEdit? View.VISIBLE : View.GONE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        listener.onEditBarberShopClick(getAdapterPosition(),view);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        listener.onBarberShopClick(getAdapterPosition(),v);
                    }
                }
            });
        }
    }

}
