package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MoreFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvProfileName, tvProfileEmail;
    private LinearLayout layoutProfileHeader;
    private TextView btnAccountInfo, btnLibrary, btnGift, btnFavorites, btnContact, btnSettings, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && imgAvatar != null) {
                    imgAvatar.setImageURI(uri);
                    Toast.makeText(requireContext(), "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        layoutProfileHeader = view.findViewById(R.id.layoutProfileHeader);
        btnAccountInfo = view.findViewById(R.id.btnAccountInfo);
        btnLibrary = view.findViewById(R.id.btnLibrary);
        btnGift = view.findViewById(R.id.btnGift);
        btnFavorites = view.findViewById(R.id.btnFavorites);
        btnContact = view.findViewById(R.id.btnContact);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();

        layoutProfileHeader.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });

        imgAvatar.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                pickImageLauncher.launch("image/*");
            }
        });

        btnAccountInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AccountInfoActivity.class);
            startActivity(intent);
        });

        btnLibrary.setOnClickListener(v -> Toast.makeText(requireContext(), "Mở Thư viện", Toast.LENGTH_SHORT).show());
        btnGift.setOnClickListener(v -> Toast.makeText(requireContext(), "Nhập mã quà tặng", Toast.LENGTH_SHORT).show());

        btnFavorites.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                Toast.makeText(requireContext(), "Mở Sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        btnContact.setOnClickListener(v -> Toast.makeText(requireContext(), "Thông tin liên hệ", Toast.LENGTH_SHORT).show());
        btnSettings.setOnClickListener(v -> Toast.makeText(requireContext(), "Cài đặt ứng dụng", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
                Toast.makeText(requireContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserProfile();
    }

    private void updateUserProfile() {
        if (mAuth == null) return;

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            tvProfileName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Người dùng FIMO");
            tvProfileName.setTextColor(Color.parseColor("#FFFFFF"));

            tvProfileEmail.setText(currentUser.getEmail());
            tvProfileEmail.setVisibility(View.VISIBLE);

            btnAccountInfo.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvProfileName.setText("Đăng nhập");
            tvProfileName.setTextColor(Color.parseColor("#FF5722"));
            tvProfileEmail.setVisibility(View.GONE);
            btnAccountInfo.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }
    }
}