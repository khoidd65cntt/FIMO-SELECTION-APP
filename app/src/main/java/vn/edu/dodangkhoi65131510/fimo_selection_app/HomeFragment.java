package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerSlider;
    private List<Movie> sliderMovies;
    private List<Movie> movieList;

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

        // ================= XỬ LÝ SLIDER PHIM (TOP 5) =================
        viewPagerSlider = view.findViewById(R.id.viewPagerSlider);
        sliderMovies = new ArrayList<>();
        loadSliderData();

        SliderAdapter sliderAdapter = new SliderAdapter(requireContext(), sliderMovies);
        viewPagerSlider.setAdapter(sliderAdapter);

        TabLayout tabLayoutDots = view.findViewById(R.id.tabLayoutDots);
        new TabLayoutMediator(tabLayoutDots, viewPagerSlider, (tab, position) -> {}).attach();

        viewPagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000);
            }
        });

        // ĐÃ KHÔI PHỤC: Lắng nghe sự kiện click cho 3 nút bộ lọc Thể loại, Quốc gia, Năm
        view.findViewById(R.id.tvFilterGenre).setOnClickListener(v -> showDialog(R.layout.layout_dialog_genres, 0));
        view.findViewById(R.id.tvFilterCountry).setOnClickListener(v -> showDialog(R.layout.layout_dialog_country, 0));
        view.findViewById(R.id.tvFilterYear).setOnClickListener(v -> showDialog(R.layout.layout_dialog_year, 1));

        // ================= XỬ LÝ DANH SÁCH PHIM CUỘN NGANG =================
        RecyclerView rvBanners = view.findViewById(R.id.rvBanners);
        RecyclerView rvTop10 = view.findViewById(R.id.rvTop10);
        RecyclerView rvForYou = view.findViewById(R.id.rvForYou);

        movieList = new ArrayList<>();
        loadMoviesWithRealData();

        setupRecyclerView(rvBanners, new MovieAdapter(requireContext(), movieList));
        setupRecyclerView(rvTop10, new MovieAdapter(requireContext(), movieList));
        setupRecyclerView(rvForYou, new MovieAdapter(requireContext(), movieList));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    private void setupRecyclerView(RecyclerView rv, MovieAdapter adapter) {
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    // ĐÃ KHÔI PHỤC: Hàm hiển thị Dialog khi lựa chọn bộ lọc
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
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void loadSliderData() {
        String pkg = requireContext().getPackageName();
        // GIỮ NGUYÊN: Đủ 5 bộ phim kèm mô tả chi tiết như ban đầu của bạn
        sliderMovies.add(new Movie("s1", "Avatar: The Way of Water", "Jake Sully cùng gia đình mới của mình phải chiến đấu chống lại sự xâm lược của loài người để bảo vệ hành tinh Pandora xanh tươi.","android.resource://" + pkg + "/" + R.drawable.avatar2, 8.8));
        sliderMovies.add(new Movie("s2", "Interstellar: Hố Đen Tử Thần", "Hành trình xuyên không gian đầy cân não của nhóm phi hành gia đi tìm kiếm một ngôi nhà mới cho nhân loại khi Trái Đất sắp bị diệt vong.", "android.resource://" + pkg + "/" + R.drawable.interstellar, 8.9));
        sliderMovies.add(new Movie("s3", "Spider-man: No Way Home", "Khi danh tính bị lộ, Peter Parker nhờ đến phép thuật của Doctor Strange, vô tình mở ra đa vũ trụ và đối mặt với hàng loạt kẻ thù cũ.", "android.resource://" + pkg + "/" + R.drawable.spm_nohome, 8.7));
        sliderMovies.add(new Movie("s4", "The Dark Knight", "Cuộc đối đầu kinh điển giữa Người Dơi Batman và kẻ phản diện Joker đầy điên loạn trong thành phố tội phạm Gotham đen tối.", "android.resource://" + pkg + "/" + R.drawable.thedarknight, 9.0));
        sliderMovies.add(new Movie("s5", "Avengers: Endgame", "Trận chiến cuối cùng mang tính sống còn của biệt đội siêu anh hùng chống lại ác nhân Thanos để hồi sinh một nửa sinh linh trong vũ trụ.", "android.resource://" + pkg + "/" + R.drawable.avenger4, 9.0));
    }

    private void loadMoviesWithRealData() {
        String pkg = requireContext().getPackageName();
        movieList.add(new Movie("1", "Avatar: The Way of Water", "https://www.youtube.com/watch?v=d9MyW72ELq0", "android.resource://" + pkg + "/" + R.drawable.avatar2, 8.8));
        movieList.add(new Movie("2", "Avengers: Endgame", "https://www.youtube.com/watch?v=TcMBFSGVi1c", "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg", 9.0));
        movieList.add(new Movie("3", "Interstellar", "https://www.youtube.com/watch?v=zSWdZVtXT7E", "android.resource://" + pkg + "/" + R.drawable.interstellar, 8.9));
        movieList.add(new Movie("5", "Spider-Man: No Way Home", "https://www.youtube.com/watch?v=JfVOs4VSpmA", "android.resource://" + pkg + "/" + R.drawable.spm_nohome, 8.5));
    }

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
            Glide.with(context).load(movie.getAnhBiaUrl()).centerCrop().into(holder.imgSlider);

            // Xử lý nút bấm Xem Phim chuyển sang màn hình chi tiết
            holder.btnWatchSlider.setOnClickListener(v -> {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("MOVIE_TITLE", movie.getTieuDe());
                intent.putExtra("MOVIE_DESC", movie.getMoTa());
                intent.putExtra("MOVIE_POSTER", movie.getAnhBiaUrl());
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