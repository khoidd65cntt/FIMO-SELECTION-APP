package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;
    private HistoryAdapter adapter;
    private List<HistoryItem> list;
    private FirebaseAuth mAuth;
    private static final String DB_URL = "https://fimo-selection-app-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageView btnBack = findViewById(R.id.btnBackHistory);
        rvHistory = findViewById(R.id.rvHistory);

        tvEmptyHistory = null;
        int emptyId = getResources().getIdentifier("tvEmptyHistory", "id", getPackageName());
        if (emptyId != 0) {
            tvEmptyHistory = findViewById(emptyId);
        }

        btnBack.setOnClickListener(v -> finish());

        list = new ArrayList<>();
        adapter = new HistoryAdapter(list);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        loadHistory();
    }

    private void loadHistory() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            if (tvEmptyHistory != null) {
                tvEmptyHistory.setText("Vui lòng đăng nhập để xem lịch sử");
                tvEmptyHistory.setVisibility(View.VISIBLE);
            }
            rvHistory.setVisibility(View.GONE);
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(user.getUid()).child("History");

        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    HistoryItem item = new HistoryItem();
                    item.id = data.child("id").getValue(String.class);
                    item.title = data.child("title").getValue(String.class);
                    item.desc = data.child("desc").getValue(String.class);
                    item.poster = data.child("poster").getValue(String.class);
                    item.mediaType = data.child("mediaType").getValue(String.class);

                    if (item.id != null) {
                        list.add(0, item);
                    }
                }
                adapter.notifyDataSetChanged();

                if (list.isEmpty()) {
                    if (tvEmptyHistory != null) {
                        tvEmptyHistory.setText("Bạn chưa xem bộ phim nào");
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                    }
                    rvHistory.setVisibility(View.GONE);
                } else {
                    if (tvEmptyHistory != null) {
                        tvEmptyHistory.setVisibility(View.GONE);
                    }
                    rvHistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Lỗi kết nối Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class HistoryItem {
        public String id;
        public String title;
        public String desc;
        public String poster;
        public String mediaType;
        public long timestamp;

        public HistoryItem() {}
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<HistoryItem> items;

        public HistoryAdapter(List<HistoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = items.get(position);
            if (holder.imgPoster != null && item.poster != null && !item.poster.isEmpty()) {
                Glide.with(holder.itemView.getContext()).load(item.poster).centerCrop().into(holder.imgPoster);
            }
            if (holder.tvTitle != null) {
                holder.tvTitle.setText(item.title != null ? item.title : "");
            }
            if (holder.tvDesc != null) {
                holder.tvDesc.setText(item.desc != null ? item.desc : "");
            }
            if (holder.tvType != null) {
                if (item.mediaType != null && item.mediaType.equalsIgnoreCase("tv")) {
                    holder.tvType.setText("PHIM BỘ");
                    holder.tvType.setBackgroundColor(Color.parseColor("#E50914"));
                } else {
                    holder.tvType.setText("PHIM LẺ");
                    holder.tvType.setBackgroundColor(Color.parseColor("#1E88E5"));
                }
            }

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
            TextView tvDesc;
            TextView tvType;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgPoster = itemView.findViewById(R.id.imgHistoryPoster);
                tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
                tvDesc = itemView.findViewById(R.id.tvHistoryDesc);
                tvType = itemView.findViewById(R.id.tvHistoryType);
            }
        }
    }
}