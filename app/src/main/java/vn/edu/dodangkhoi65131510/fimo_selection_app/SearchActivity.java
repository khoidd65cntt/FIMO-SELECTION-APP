package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private MovieAdapter searchAdapter;
    private List<Movie> allMovies;
    private List<Movie> filteredList;

    // Handler to handle typing delay for stable Vietnamese Telex input
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Connects to the activity_search.xml layout
        setContentView(R.layout.activity_search);

        EditText edtSearchInput = findViewById(R.id.edtSearchInput);
        RecyclerView rvSearchResults = findViewById(R.id.rvSearchResults);
        ImageView btnBackSearch = findViewById(R.id.btnBackSearch);

        // Back button closes the Activity and returns to the previous screen
        if (btnBackSearch != null) {
            btnBackSearch.setOnClickListener(v -> finish());
        }

        initMovieData();
        filteredList = new ArrayList<>(allMovies);
        searchAdapter = new MovieAdapter(this, filteredList);

        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        rvSearchResults.setAdapter(searchAdapter);

        if (edtSearchInput != null) {
            edtSearchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Cancel the previous pending search request while user is typing
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Schedule search execution 300ms after user stops typing to prevent keyboard focus glitch
                    searchRunnable = () -> filterMovies(s.toString().trim());
                    searchHandler.postDelayed(searchRunnable, 300);
                }
            });
        }
    }

    private void filterMovies(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(allMovies);
        } else {
            for (Movie m : allMovies) {
                // Case-insensitive text matching filter
                if (m.getTieuDe().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(m);
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    private void initMovieData() {
        allMovies = new ArrayList<>();
        String pkgName = getPackageName();

        allMovies.add(new Movie("1", "Avatar: The Way of Water", "", "android.resource://" + pkgName + "/" + R.drawable.avatar2, 8.8));
        allMovies.add(new Movie("a1", "Demon Slayer", "", "android.resource://" + pkgName + "/" + R.drawable.demonslayer_vohanthanh, 8.9));
        allMovies.add(new Movie("2", "Avengers: Endgame", "https://www.youtube.com/watch?v=TcMBFSGVi1c", "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg", 9.0));
        allMovies.add(new Movie("3", "Interstellar", "https://www.youtube.com/watch?v=zSWdZVtXT7E", "android.resource://" + pkgName + "/" + R.drawable.interstellar, 8.9));
        allMovies.add(new Movie("5", "Spider-Man: No Way Home", "https://www.youtube.com/watch?v=JfVOs4VSpmA", "android.resource://" + pkgName + "/" + R.drawable.spm_nohome, 8.5));
        allMovies.add(new Movie("6", "The Dark Knight", "https://www.youtube.com/watch?v=EXeTwQWrcwY", "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", 9.2));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent memory leaks by removing callbacks when activity is destroyed
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}