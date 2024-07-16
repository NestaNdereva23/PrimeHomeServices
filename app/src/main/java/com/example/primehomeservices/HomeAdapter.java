package com.example.primehomeservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class HomeAdapter extends FirebaseRecyclerAdapter<HomeModel, HomeAdapter.MyViewHolder> {
    String img = "https://imgs.search.brave.com/HTrvJl1gg3yGzOl9UuJ0HRc7ixME7LyOfuTlrjBirCk/rs:fit:500:0:0:0/g:ce/aHR0cHM6Ly9jZG40/Lmljb25maW5kZXIu/Y29tL2RhdGEvaWNv/bnMvbG9nb3MtYW5k/LWJyYW5kcy81MTIv/MTcxX0ltZGJfbG9n/b19sb2dvcy01MTIu/cG5n";

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HomeAdapter(@NonNull FirebaseRecyclerOptions<HomeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull HomeModel model) {
        holder.name.setText(model.getName());
         //method to load the image into the ImageView,
        Glide.with(holder.image.getContext())
                 .load(model.getImageUrl())
                 .placeholder(com.firebase.ui.auth.R.drawable.common_google_signin_btn_icon_dark)
                 .error(com.firebase.ui.auth.R.color.colorPrimary)
                 .into(holder.image);

//       Picasso.get().load(model.getImageUrl()).into(holder.image);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        Button button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.serviceImage);
            name = itemView.findViewById(R.id.serviceText);
            button = itemView.findViewById(R.id.BookButton); // Assuming there is a button in your layout
        }
    }
}
