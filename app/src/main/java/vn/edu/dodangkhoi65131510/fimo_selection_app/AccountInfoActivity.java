package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AccountInfoActivity extends AppCompatActivity {

    private EditText edtInfoName;
    private TextView tvInfoEmail, btnGoToChangePassword;
    private Button btnSaveInfo;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btnBack = findViewById(R.id.btnBack);
        edtInfoName = findViewById(R.id.edtInfoName);
        tvInfoEmail = findViewById(R.id.tvInfoEmail);
        btnSaveInfo = findViewById(R.id.btnSaveInfo);
        btnGoToChangePassword = findViewById(R.id.btnGoToChangePassword);

        if (currentUser != null) {
            edtInfoName.setText(currentUser.getDisplayName());
            tvInfoEmail.setText(currentUser.getEmail());
        }

        btnBack.setOnClickListener(v -> finish());

        btnSaveInfo.setOnClickListener(v -> {
            if (currentUser == null) return;

            String newName = edtInfoName.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Tên hiển thị không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Đã lưu tên hiển thị thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật tên thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnGoToChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(AccountInfoActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}