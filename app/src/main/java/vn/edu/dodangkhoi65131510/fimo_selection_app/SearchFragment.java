package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText edtSearch;
    private RecyclerView rvSearchResults;
    private MovieAdapter searchAdapter;
    private List<Movie> searchResultList;

    private static final String API_KEY = "3509f85d40f81d254b5afc2d8beaa8e1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        searchResultList = new ArrayList<>();
        searchAdapter = new MovieAdapter(requireContext(), searchResultList);

        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvSearchResults.setAdapter(searchAdapter);

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchMoviesFromApi(query);
                }
                return true;
            }
            return false;
        });

        return view;
    }

    private void searchMoviesFromApi(String query) {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        Call<MovieResponse> call = apiService.searchMovies(API_KEY, "vi-VN", query, 1);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResultList.clear();
                    searchResultList.addAll(response.body().getResults());
                    searchAdapter.notifyDataSetChanged();

                    if (searchResultList.isEmpty()) {
                        Toast.makeText(requireContext(), "Không tìm thấy phim nào hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Search Error: " + t.getMessage());
            }
        });
    }
}