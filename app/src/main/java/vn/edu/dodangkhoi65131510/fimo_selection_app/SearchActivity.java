package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private MovieAdapter searchAdapter;
    private List<Movie> allMovies;
    private List<Movie> filteredList;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private static final String API_KEY = "3509f85d40f81d254b5afc2d8beaa8e1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);

            EditText edtSearchInput = findViewById(R.id.edtSearchInput);
            RecyclerView rvSearchResults = findViewById(R.id.rvSearchResults);
            ImageView btnBackSearch = findViewById(R.id.btnBackSearch);

            if (btnBackSearch != null) {
                btnBackSearch.setOnClickListener(v -> finish());
            }

            allMovies = new ArrayList<>();
            filteredList = new ArrayList<>();
            searchAdapter = new MovieAdapter(this, filteredList);

            if (rvSearchResults != null) {
                rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
                rvSearchResults.setAdapter(searchAdapter);
            }

            String filterType = getIntent().getStringExtra("FILTER_TYPE");
            String filterValue = getIntent().getStringExtra("FILTER_VALUE");

            if (filterType != null && filterValue != null) {
                if (edtSearchInput != null) {
                    edtSearchInput.setText(filterType + ": " + filterValue);
                }
                fetchFilteredMovies(filterType, filterValue);
            } else {
                initMovieData();
                filteredList.addAll(allMovies);
                searchAdapter.notifyDataSetChanged();
            }

            if (edtSearchInput != null) {
                edtSearchInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (searchRunnable != null) {
                            searchHandler.removeCallbacks(searchRunnable);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        searchRunnable = () -> filterMovies(s.toString().trim());
                        searchHandler.postDelayed(searchRunnable, 300);
                    }
                });
            }
        } catch (Exception e) {
            // Khối an toàn: Bắt mọi lỗi khởi tạo để không văng app
            Toast.makeText(this, "Lỗi khởi tạo Search: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void fetchFilteredMovies(String type, String value) {
        String genreId = null;
        String countryCode = null;
        String year = null;

        if ("Thể loại".equals(type)) {
            genreId = getGenreId(value);
        } else if ("Quốc gia".equals(type)) {
            countryCode = getCountryCode(value);
        } else if ("Năm".equals(type)) {
            year = value;
        }

        try {
            TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
            Call<MovieResponse> call = apiService.getMoviesByFilter(API_KEY, "vi-VN", genreId, countryCode, year, 1);

            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                        allMovies.clear();
                        allMovies.addAll(response.body().getResults());

                        filteredList.clear();
                        filteredList.addAll(allMovies);
                        searchAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Toast.makeText(SearchActivity.this, "Lỗi kết nối máy chủ phim", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Throwable t) {
            Toast.makeText(this, "Lỗi API: Hãy kiểm tra file TmdbApi.java", Toast.LENGTH_LONG).show();
        }
    }

    private String getGenreId(String genreName) {
        String lower = genreName.toLowerCase().trim();
        if (lower.contains("hành động")) return "28";
        if (lower.contains("tình cảm") || lower.contains("lãng mạn")) return "10749";
        if (lower.contains("hài hước")) return "35";
        if (lower.contains("kinh dị")) return "27";
        if (lower.contains("hoạt hình")) return "16";
        if (lower.contains("viễn tưởng")) return "878";
        if (lower.contains("tâm lý")) return "18";
        if (lower.contains("hình sự")) return "80";
        if (lower.contains("phiêu lưu")) return "12";
        if (lower.contains("tài liệu")) return "99";
        if (lower.contains("gia đình")) return "10751";
        if (lower.contains("chiến tranh")) return "10752";
        if (lower.contains("âm nhạc")) return "10402";
        if (lower.contains("bí ẩn")) return "9648";
        return null;
    }

    private String getCountryCode(String countryName) {
        String lower = countryName.toLowerCase().trim();
        if (lower.contains("hàn quốc")) return "KR";
        if (lower.contains("nhật bản")) return "JP";
        if (lower.contains("trung quốc")) return "CN";
        if (lower.contains("việt nam")) return "VN";
        if (lower.contains("mỹ") || lower.contains("âu mỹ")) return "US";
        if (lower.contains("thái lan")) return "TH";
        if (lower.contains("ấn độ")) return "IN";
        if (lower.contains("đài loan")) return "TW";
        if (lower.contains("hồng kông")) return "HK";
        if (lower.contains("anh")) return "GB";
        if (lower.contains("pháp")) return "FR";
        if (lower.contains("đức")) return "DE";
        return null;
    }

    private void filterMovies(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(allMovies);
        } else {
            for (Movie m : allMovies) {
                if (m.getTieuDe() != null && m.getTieuDe().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(m);
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    private void initMovieData() {
        allMovies.clear();
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
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}