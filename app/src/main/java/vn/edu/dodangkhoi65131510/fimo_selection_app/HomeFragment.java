package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerSlider;
    private SliderAdapter sliderAdapter;

    private List<Movie> sliderMovies;
    private List<Movie> movieList;
    private List<Movie> animeList;

    private MovieAdapter movieAdapter;
    private MovieAdapter animeAdapter;

    private static final String API_KEY = "3509f85d40f81d254b5afc2d8beaa8e1";

    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerSlider != null && sliderMovies != null && !sliderMovies.isEmpty()) {
                int currentItem = viewPagerSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % sliderMovies.size();
                viewPagerSlider.setCurrentItem(nextItem);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPagerSlider = view.findViewById(R.id.viewPagerSlider);
        sliderMovies = new ArrayList<>();
        sliderAdapter = new SliderAdapter(requireContext(), sliderMovies);
        viewPagerSlider.setAdapter(sliderAdapter);

        TabLayout tabLayoutDots = view.findViewById(R.id.tabLayoutDots);
        new TabLayoutMediator(tabLayoutDots, viewPagerSlider, (tab, position) -> {}).attach();

        viewPagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                if (sliderAdapter.getItemCount() > 0) {
                    sliderHandler.postDelayed(sliderRunnable, 5000);
                }
            }
        });

        view.findViewById(R.id.tvFilterGenre).setOnClickListener(v -> showDialog(R.layout.layout_dialog_genres, 0));
        view.findViewById(R.id.tvFilterCountry).setOnClickListener(v -> showDialog(R.layout.layout_dialog_country, 0));
        view.findViewById(R.id.tvFilterYear).setOnClickListener(v -> showDialog(R.layout.layout_dialog_year, 1));

        RecyclerView rvBanners = view.findViewById(R.id.rvBanners);
        RecyclerView rvTop10 = view.findViewById(R.id.rvTop10);
        RecyclerView rvForYou = view.findViewById(R.id.rvForYou);

        movieList = new ArrayList<>();
        animeList = new ArrayList<>();

        // TRUYỀN BIẾN PHÂN LOẠI VÀO ĐÂY ĐỂ TRÁNH LỖI PHIM MA
        movieAdapter = new MovieAdapter(requireContext(), movieList, "movie");
        animeAdapter = new MovieAdapter(requireContext(), animeList, "tv");

        setupRecyclerView(rvBanners, movieAdapter);
        setupRecyclerView(rvTop10, animeAdapter);
        setupRecyclerView(rvForYou, movieAdapter);

        fetchNowPlayingForSlider();
        fetchPopularMoviesFromApi();
        fetchPopularAnimeFromApi();

        return view;
    }

    private void setupRecyclerView(RecyclerView rv, MovieAdapter adapter) {
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    private void fetchNowPlayingForSlider() {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.getNowPlayingMovies(API_KEY, "vi-VN", "VN", 1);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sliderMovies.clear();
                    List<Movie> results = response.body().getResults();
                    int limit = Math.min(results.size(), 5);
                    for (int i = 0; i < limit; i++) sliderMovies.add(results.get(i));
                    sliderAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi tải Slider: " + t.getMessage());
            }
        });
    }

    private void fetchPopularMoviesFromApi() {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.getPopularMovies(API_KEY, "vi-VN", 1);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {}
        });
    }

    private void fetchPopularAnimeFromApi() {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.getPopularAnime(API_KEY, "vi-VN", "16", "ja", "popularity.desc", 1);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    animeList.clear();
                    List<Movie> results = response.body().getResults();
                    int limit = Math.min(results.size(), 10);
                    for (int i = 0; i < limit; i++) animeList.add(results.get(i));
                    animeAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {}
        });
    }

    private void showDialog(int layoutId, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(layoutId, null);
        if (type == 1) {
            GridLayout grid = dialogView.findViewById(R.id.gridYears);
            if (grid != null) {
                for (int i = 2026; i >= 1980; i--) {
                    TextView tv = new TextView(requireContext());
                    tv.setText(String.valueOf(i));
                    tv.setTextColor(Color.WHITE);
                    tv.setGravity(Gravity.CENTER);
                    tv.setPadding(20, 30, 20, 30);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = 0;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    tv.setLayoutParams(params);
                    grid.addView(tv);
                }
            }
        }
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onPause() { super.onPause(); sliderHandler.removeCallbacks(sliderRunnable); }

    @Override
    public void onResume() {
        super.onResume();
        if (sliderAdapter != null && sliderAdapter.getItemCount() > 0) sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); sliderHandler.removeCallbacks(sliderRunnable); }

    private class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
        private List<Movie> movies;
        private Context context;

        public SliderAdapter(Context context, List<Movie> movies) {
            this.context = context;
            this.movies = movies;
        }

        @NonNull
        @Override
        public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
            Movie movie = movies.get(position);
            holder.tvTitle.setText(movie.getTieuDe());
            holder.tvDesc.setText(movie.getMoTa());
            Glide.with(context).load(movie.getBackdropPath()).centerCrop().into(holder.imgSlider);

            holder.btnWatchSlider.setOnClickListener(v -> {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("MOVIE_ID", String.valueOf(movie.getId()));
                intent.putExtra("MOVIE_TITLE", movie.getTieuDe());
                intent.putExtra("MOVIE_DESC", movie.getMoTa());
                intent.putExtra("MOVIE_POSTER", movie.getAnhBiaUrl());
                // Cố định Slider là phim lẻ
                intent.putExtra("MEDIA_TYPE", "movie");
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return movies.size(); }

        class SliderViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSlider;
            TextView tvTitle, tvDesc;
            Button btnWatchSlider;
            public SliderViewHolder(@NonNull View itemView) {
                super(itemView);
                imgSlider = itemView.findViewById(R.id.imgSlider);
                tvTitle = itemView.findViewById(R.id.tvSliderTitle);
                tvDesc = itemView.findViewById(R.id.tvSliderDesc);
                btnWatchSlider = itemView.findViewById(R.id.btnWatchSlider);
            }
        }
    }
}