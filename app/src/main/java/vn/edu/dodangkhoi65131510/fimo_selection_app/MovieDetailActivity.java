package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView imgPoster = findViewById(R.id.imgDetailPoster);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        ImageView btnBack = findViewById(R.id.btnBackDetail);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        String title = getIntent().getStringExtra("MOVIE_TITLE");
        String desc = getIntent().getStringExtra("MOVIE_DESC");
        String poster = getIntent().getStringExtra("MOVIE_POSTER");

        if (title != null) tvTitle.setText(title);
        if (desc != null) tvDesc.setText(desc);
        if (poster != null && imgPoster != null) {
            Glide.with(this).load(poster).into(imgPoster);
        }
    }
}