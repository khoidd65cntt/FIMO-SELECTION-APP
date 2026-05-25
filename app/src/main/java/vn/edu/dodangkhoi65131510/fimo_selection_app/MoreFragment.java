package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MoreFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail;
    private TextView btnWatchlist, btnHistory, btnSettings, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnWatchlist = view.findViewById(R.id.btnWatchlist);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        try {
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
        } catch (Exception e) {
            mAuth = null;
            currentUser = null;
        }

        if (currentUser != null) {
            tvProfileName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Người dùng FIMO");
            tvProfileEmail.setText(currentUser.getEmail());

            btnLogout.setText("Đăng xuất");
            btnLogout.setTextColor(Color.parseColor("#E50914"));
            btnLogout.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_power_off, 0, 0, 0);
        } else {
            tvProfileName.setText("Khách");
            tvProfileEmail.setText("Vui lòng đăng nhập để trải nghiệm");

            btnLogout.setText("Đăng nhập");
            btnLogout.setTextColor(Color.parseColor("#0984E3"));
            btnLogout.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_myplaces, 0, 0, 0);
        }

        btnWatchlist.setOnClickListener(v -> {
            if (currentUser == null) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                Toast.makeText(requireContext(), "Mở Danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        btnHistory.setOnClickListener(v -> {
            if (currentUser == null) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                Toast.makeText(requireContext(), "Mở Lịch sử xem phim", Toast.LENGTH_SHORT).show();
            }
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Mở Cài đặt", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            if (currentUser != null && mAuth != null) {
                mAuth.signOut();
                Toast.makeText(requireContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });

        return view;
    }
}