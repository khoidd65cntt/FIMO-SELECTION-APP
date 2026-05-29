package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private List<Movie> newRecommendedList;
    private List<Movie> newTvShowsList;
    private List<Movie> newTheatersList;
    private List<Movie> newMoviesList;

    private MovieAdapter movieAdapter;
    private MovieAdapter animeAdapter;
    private MovieAdapter newRecommendedAdapter;
    private MovieAdapter newTvShowsAdapter;
    private MovieAdapter newTheatersAdapter;
    private MovieAdapter newMoviesAdapter;

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

        view.findViewById(R.id.tvFilterGenre).setOnClickListener(v -> showDialog(R.layout.layout_dialog_genres, 0, "Thể loại"));
        view.findViewById(R.id.tvFilterCountry).setOnClickListener(v -> showDialog(R.layout.layout_dialog_country, 0, "Quốc gia"));
        view.findViewById(R.id.tvFilterYear).setOnClickListener(v -> showDialog(R.layout.layout_dialog_year, 1, "Năm"));

        androidx.core.widget.NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);
        TextView btnScrollTop = view.findViewById(R.id.btnScrollTop);
        ImageView btnThemeToggle = view.findViewById(R.id.btnThemeToggle);

        if (nestedScrollView != null && btnScrollTop != null) {
            nestedScrollView.setOnScrollChangeListener((androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY > 400) btnScrollTop.setVisibility(View.VISIBLE);
                else btnScrollTop.setVisibility(View.GONE);
            });
            btnScrollTop.setOnClickListener(v -> nestedScrollView.smoothScrollTo(0, 0));
        }

        if (btnThemeToggle != null) {
            btnThemeToggle.setOnClickListener(v -> {
                SharedPreferences prefs = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
                boolean fromNight = prefs.getBoolean("isNightMode", true);
                boolean newNight = !fromNight;
                prefs.edit().putBoolean("isNightMode", newNight).apply();

                applyHomeTheme(newNight);

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).applyTheme(newNight, true);
                }
            });
        }

        RecyclerView rvBanners = view.findViewById(R.id.rvBanners);
        RecyclerView rvTop10 = view.findViewById(R.id.rvTop10);
        RecyclerView rvNewRecommended = view.findViewById(R.id.rvNewRecommended);
        RecyclerView rvNewTvShows = view.findViewById(R.id.rvNewTvShows);
        RecyclerView rvNewTheaters = view.findViewById(R.id.rvNewTheaters);
        RecyclerView rvNewMovies = view.findViewById(R.id.rvNewMovies);

        movieList = new ArrayList<>();
        animeList = new ArrayList<>();
        newRecommendedList = new ArrayList<>();
        newTvShowsList = new ArrayList<>();
        newTheatersList = new ArrayList<>();
        newMoviesList = new ArrayList<>();

        movieAdapter = new MovieAdapter(requireContext(), movieList, "movie");
        animeAdapter = new MovieAdapter(requireContext(), animeList, "tv");
        newRecommendedAdapter = new MovieAdapter(requireContext(), newRecommendedList, "movie");
        newTvShowsAdapter = new MovieAdapter(requireContext(), newTvShowsList, "tv");
        newTheatersAdapter = new MovieAdapter(requireContext(), newTheatersList, "movie");
        newMoviesAdapter = new MovieAdapter(requireContext(), newMoviesList, "movie");

        setupRecyclerView(rvBanners, movieAdapter);
        setupRecyclerView(rvTop10, animeAdapter);
        setupRecyclerView(rvNewRecommended, newRecommendedAdapter);
        setupRecyclerView(rvNewTvShows, newTvShowsAdapter);
        setupRecyclerView(rvNewTheaters, newTheatersAdapter);
        setupRecyclerView(rvNewMovies, newMoviesAdapter);

        fetchNowPlayingForSlider();
        fetchPopularMoviesFromApi();
        fetchPopularAnimeFromApi();
        fetchNewRecommended();
        fetchNewTvShows();
        fetchNewTheaters();
        fetchNewMovies();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sliderAdapter != null && sliderAdapter.getItemCount() > 0) {
            sliderHandler.postDelayed(sliderRunnable, 5000);
        }
        SharedPreferences prefs = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        applyHomeTheme(prefs.getBoolean("isNightMode", true));
    }

    private void applyHomeTheme(boolean isNightMode) {
        View view = getView();
        if (view == null) return;

        androidx.core.widget.NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);
        if (nestedScrollView != null) {
            nestedScrollView.setBackgroundColor(isNightMode ? Color.parseColor("#141414") : Color.parseColor("#F5F5F5"));
        }

        int textColor = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");

        TextView[] textViewsToAnimate = {
                view.findViewById(R.id.tvTitleHot), view.findViewById(R.id.tvTitleTop10),
                view.findViewById(R.id.tvTitleNewRec), view.findViewById(R.id.tvTitleNewTv),
                view.findViewById(R.id.tvTitleNewTheaters), view.findViewById(R.id.tvTitleNewMovies),
                view.findViewById(R.id.tvFilterCountry), view.findViewById(R.id.tvFilterYear)
        };

        for (TextView tv : textViewsToAnimate) {
            if (tv != null) tv.setTextColor(textColor);
        }

        int[] rvIds = {R.id.rvBanners, R.id.rvTop10, R.id.rvNewRecommended, R.id.rvNewTvShows, R.id.rvNewTheaters, R.id.rvNewMovies};
        for (int id : rvIds) {
            RecyclerView rv = view.findViewById(id);
            if (rv != null) {
                for (int i = 0; i < rv.getChildCount(); i++) {
                    updateItemTextColor(rv.getChildAt(i), textColor);
                }
            }
        }

        ImageView btnThemeToggle = view.findViewById(R.id.btnThemeToggle);
        if (btnThemeToggle != null) {
            btnThemeToggle.setImageResource(isNightMode ? R.drawable.ic_sun : R.drawable.ic_moon);
        }
    }

    private void updateItemTextColor(View view, int color) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                updateItemTextColor(vg.getChildAt(i), color);
            }
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            int current = tv.getCurrentTextColor();
            if (current != Color.parseColor("#E50914") && current != Color.parseColor("#FF9800") && current != Color.parseColor("#FF5722")) {
                tv.setTextColor(color);
            }
        }
    }

    private void setupRecyclerView(RecyclerView rv, MovieAdapter adapter) {
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);

        rv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                SharedPreferences prefs = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
                boolean isNightMode = prefs.getBoolean("isNightMode", true);
                int textColor = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");
                updateItemTextColor(view, textColor);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {}
        });
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
            public void onFailure(Call<MovieResponse> call, Throwable t) {}
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

    private void fetchNewRecommended() {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.getPopularMovies(API_KEY, "vi-VN", 2);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newRecommendedList.clear();
                    newRecommendedList.addAll(response.body().getResults());
                    newRecommendedAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {}
        });
    }

    private void fetchNewTvShows() {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.getPopularAnime(API_KEY, "vi-VN", "16", "ja", "popularity.desc", 2);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newTvShowsList.clear();
                    newTvShowsList.addAll(response.body().getResults());
                    newTvShowsAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {}
        });
    }

    private void fetchNewTheaters() {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.getNowPlayingMovies(API_KEY, "vi-VN", "VN", 2);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newTheatersList.clear();
                    newTheatersList.addAll(response.body().getResults());
                    newTheatersAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {}
        });
    }

    private void fetchNewMovies() {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.getPopularMovies(API_KEY, "vi-VN", 3);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newMoviesList.clear();
                    newMoviesList.addAll(response.body().getResults());
                    newMoviesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {}
        });
    }

    private void showDialog(int layoutId, int type, String filterName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(layoutId, null);

        SharedPreferences prefs = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isNightMode = prefs.getBoolean("isNightMode", true);
        int dialogTextColor = isNightMode ? Color.WHITE : Color.BLACK;
        int dialogBgColor = isNightMode ? Color.parseColor("#252525") : Color.parseColor("#FFFFFF");

        dialogView.setBackgroundColor(dialogBgColor);
        updateItemTextColor(dialogView, dialogTextColor);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (type == 1) {
            GridLayout grid = dialogView.findViewById(R.id.gridYears);
            if (grid != null) {
                for (int i = 2026; i >= 1980; i--) {
                    String yearValue = String.valueOf(i);
                    TextView tv = new TextView(requireContext());
                    tv.setText(yearValue);
                    tv.setTextColor(dialogTextColor);
                    tv.setGravity(Gravity.CENTER);
                    tv.setPadding(20, 30, 20, 30);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = 0;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    tv.setLayoutParams(params);

                    tv.setOnClickListener(v -> {
                        dialog.dismiss();
                        openFilterPage(filterName, yearValue);
                    });

                    grid.addView(tv);
                }
            }
        } else {
            setClickListenersForViewGroup((ViewGroup) dialogView, dialog, filterName);
        }

        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setClickListenersForViewGroup(ViewGroup vg, AlertDialog dialog, String filterType) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) {
                setClickListenersForViewGroup((ViewGroup) child, dialog, filterType);
            } else if (child instanceof TextView) {
                TextView tv = (TextView) child;
                String textValue = tv.getText().toString().trim();

                if (!textValue.isEmpty() && !textValue.toUpperCase().contains("CHỌN")) {
                    tv.setOnClickListener(v -> {
                        dialog.dismiss();
                        openFilterPage(filterType, textValue);
                    });
                }
            }
        }
    }

    private void openFilterPage(String filterType, String filterValue) {
        try {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            intent.putExtra("FILTER_TYPE", filterType);
            intent.putExtra("FILTER_VALUE", filterValue);
            startActivity(intent);
        } catch (Exception e) {}
    }

    @Override
    public void onPause() { super.onPause(); sliderHandler.removeCallbacks(sliderRunnable); }

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