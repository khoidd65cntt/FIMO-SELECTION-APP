package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private FavoriteAdapter adapter;
    private List<FavoriteItem> list;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ImageView btnBack = findViewById(R.id.btnBackFavorites);
        rvFavorites = findViewById(R.id.rvFavorites);

        btnBack.setOnClickListener(v -> finish());

        list = new ArrayList<>();
        adapter = new FavoriteAdapter(list);
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 3));
        rvFavorites.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        loadFavorites();
    }

    private void loadFavorites() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Favorites");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    FavoriteItem item = data.getValue(FavoriteItem.class);
                    if (item != null) {
                        list.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class FavoriteItem {
        public String id;
        public String title;
        public String desc;
        public String poster;
        public String mediaType;

        public FavoriteItem() {}

        public FavoriteItem(String id, String title, String desc, String poster, String mediaType) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.poster = poster;
            this.mediaType = mediaType;
        }
    }

    private class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
        private final List<FavoriteItem> items;

        public FavoriteAdapter(List<FavoriteItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FavoriteItem item = items.get(position);
            if (holder.imgPoster != null) Glide.with(holder.itemView.getContext()).load(item.poster).centerCrop().into(holder.imgPoster);
            if (holder.tvTitle != null) holder.tvTitle.setText(item.title);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), MovieDetailActivity.class);
                intent.putExtra("MOVIE_ID", item.id);
                intent.putExtra("MOVIE_TITLE", item.title);
                intent.putExtra("MOVIE_DESC", item.desc);
                intent.putExtra("MOVIE_POSTER", item.poster);
                intent.putExtra("MEDIA_TYPE", item.mediaType);
                holder.itemView.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgPoster;
            TextView tvTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgPoster = itemView.findViewById(R.id.imgMoviePoster);
                tvTitle = itemView.findViewById(R.id.tvMovieTitle);
            }
        }
    }
}