package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private MovieAdapter searchAdapter;
    private List<Movie> allMovies;
    private List<Movie> filteredList;

    // Bộ đếm thời gian
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText edtSearchInput = view.findViewById(R.id.edtSearchInput);
        RecyclerView rvSearchResults = view.findViewById(R.id.rvSearchResults);

        initMovieData();
        filteredList = new ArrayList<>(allMovies);
        searchAdapter = new MovieAdapter(requireContext(), filteredList);

        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSearchResults.setAdapter(searchAdapter);

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
                    searchHandler.postDelayed(searchRunnable, 800);
                }
            });

            edtSearchInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                    filterMovies(edtSearchInput.getText().toString().trim());
                    return true;
                }
                return false;
            });
        }

        return view;
    }

    private void filterMovies(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(allMovies);
        } else {
            for (Movie m : allMovies) {
                if (m.getTieuDe().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(m);
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    private void initMovieData() {
        allMovies = new ArrayList<>();
        String pkgName = requireContext().getPackageName();
        allMovies.add(new Movie("1", "Avatar: The Way of Water", "", "android.resource://" + pkgName + "/" + R.drawable.avatar2, 8.8));
        allMovies.add(new Movie("a1", "Demon Slayer", "", "android.resource://" + pkgName + "/" + R.drawable.demonslayer_vohanthanh, 8.9));
        allMovies.add(new Movie("2", "Avengers: Endgame", "https://www.youtube.com/watch?v=TcMBFSGVi1c", "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg", 9.0));
        allMovies.add(new Movie("3", "Interstellar", "https://www.youtube.com/watch?v=zSWdZVtXT7E", "android.resource://" + pkgName + "/" + R.drawable.interstellar, 8.9));
        allMovies.add(new Movie("5", "Spider-Man: No Way Home", "https://www.youtube.com/watch?v=JfVOs4VSpmA", "android.resource://" + pkgName + "/" + R.drawable.spm_nohome, 8.5));
        allMovies.add(new Movie("6", "The Dark Knight", "https://www.youtube.com/watch?v=EXeTwQWrcwY", "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", 9.2));
        allMovies.add(new Movie("7", "Inception", "https://www.youtube.com/watch?v=YoHD9XEInc0", "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg", 8.7));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}