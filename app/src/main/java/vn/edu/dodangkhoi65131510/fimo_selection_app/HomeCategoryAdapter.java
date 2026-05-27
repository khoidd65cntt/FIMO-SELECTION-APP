package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {
    private Context context;
    private List<CategoryModel> list;

    public HomeCategoryAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel cat = list.get(position);
        holder.tvRowTitle.setText(cat.getTitle());
        MovieAdapter movieAdapter = new MovieAdapter(context, cat.getMovieList(), cat.getMediaType());
        holder.rvRowMovies.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rvRowMovies.setAdapter(movieAdapter);
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRowTitle;
        RecyclerView rvRowMovies;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRowTitle = itemView.findViewById(R.id.tvRowTitle);
            rvRowMovies = itemView.findViewById(R.id.rvRowMovies);
        }
    }
}