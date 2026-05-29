package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MoreFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvProfileName, tvProfileEmail;
    private LinearLayout layoutProfileHeader;
    private TextView btnAccountInfo, btnLibrary, btnGift, btnFavorites, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && imgAvatar != null) {
                    Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imgAvatar);

                    String savedPath = saveImageToInternalStorage(uri);
                    if (savedPath != null) {
                        saveAvatarPathToPrefs(savedPath);
                        Toast.makeText(requireContext(), "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }
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

        btnLibrary.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                startActivity(new Intent(requireContext(), HistoryActivity.class));
            }
        });

        btnGift.setOnClickListener(v -> Toast.makeText(requireContext(), "Nhập mã quà tặng", Toast.LENGTH_SHORT).show());

        btnFavorites.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                startActivity(new Intent(requireContext(), FavoritesActivity.class));
            }
        });

        btnLogout.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
                imgAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
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
        applyTheme();
    }

    private void applyTheme() {
        SharedPreferences prefs = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isNightMode = prefs.getBoolean("isNightMode", true);

        View root = getView();
        if (root != null) {
            root.setBackgroundColor(isNightMode ? Color.parseColor("#141414") : Color.parseColor("#F5F5F5"));
            int textColor = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");
            int boxColor = isNightMode ? Color.parseColor("#252525") : Color.parseColor("#E0E0E0");

            updateMoreTheme((ViewGroup) root, textColor, boxColor);
        }
    }

    private void updateMoreTheme(ViewGroup parent, int textColor, int boxColor) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);

            if (child.getId() == R.id.imgAvatar) continue;

            if (child instanceof ViewGroup) {
                if (child.getBackground() != null && !(child instanceof RecyclerView)) {
                    child.setBackgroundTintList(ColorStateList.valueOf(boxColor));
                }
                updateMoreTheme((ViewGroup) child, textColor, boxColor);
            } else if (child instanceof TextView) {
                TextView tv = (TextView) child;
                if (tv.getCurrentTextColor() != Color.parseColor("#E50914") && tv.getCurrentTextColor() != Color.parseColor("#FF5722")) {
                    tv.setTextColor(textColor);
                }
                if (tv.getBackground() != null) {
                    tv.setBackgroundTintList(ColorStateList.valueOf(boxColor));
                }
            }
        }
    }

    private void updateUserProfile() {
        if (mAuth == null) return;

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            tvProfileName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Người dùng FIMO");

            tvProfileEmail.setText(currentUser.getEmail());
            tvProfileEmail.setVisibility(View.VISIBLE);

            btnAccountInfo.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);

            loadAvatarFromPrefs();
        } else {
            tvProfileName.setText("Đăng nhập");
            tvProfileName.setTextColor(Color.parseColor("#FF5722"));
            tvProfileEmail.setVisibility(View.GONE);
            btnAccountInfo.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            imgAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            String uid = (currentUser != null) ? currentUser.getUid() : "default_user";
            File file = new File(requireContext().getFilesDir(), "avatar_" + uid + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private void saveAvatarPathToPrefs(String path) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("FimoAppPrefs", Context.MODE_PRIVATE);
        String uid = (currentUser != null) ? currentUser.getUid() : "default_user";
        prefs.edit().putString("avatar_path_" + uid, path).apply();
    }

    private void loadAvatarFromPrefs() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("FimoAppPrefs", Context.MODE_PRIVATE);
        String uid = (currentUser != null) ? currentUser.getUid() : "default_user";
        String savedPath = prefs.getString("avatar_path_" + uid, null);

        if (savedPath != null) {
            File imgFile = new File(savedPath);
            if (imgFile.exists()) {
                Glide.with(this)
                        .load(imgFile)
                        .circleCrop()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .signature(new ObjectKey(imgFile.lastModified()))
                        .into(imgAvatar);
            }
        }
    }
}