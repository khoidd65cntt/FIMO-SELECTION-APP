package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        EditText edtRegName = findViewById(R.id.edtRegName);
        EditText edtRegEmail = findViewById(R.id.edtRegEmail);
        EditText edtRegPassword = findViewById(R.id.edtRegPassword);
        EditText edtRegConfirmPassword = findViewById(R.id.edtRegConfirmPassword);
        CheckBox cbAgree = findViewById(R.id.cbAgree);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);
        ImageView imgToggleRegPassword = findViewById(R.id.imgToggleRegPassword);
        ImageView imgToggleRegConfirmPassword = findViewById(R.id.imgToggleRegConfirmPassword);

        imgToggleRegPassword.setAlpha(0.5f);
        imgToggleRegConfirmPassword.setAlpha(0.5f);

        imgToggleRegPassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            toggleVisibility(edtRegPassword, imgToggleRegPassword, isPasswordVisible);
        });

        imgToggleRegConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            toggleVisibility(edtRegConfirmPassword, imgToggleRegConfirmPassword, isConfirmPasswordVisible);
        });

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String name = edtRegName.getText().toString().trim();
            String email = edtRegEmail.getText().toString().trim();
            String password = edtRegPassword.getText().toString().trim();
            String confirmPassword = edtRegConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbAgree.isChecked()) {
                Toast.makeText(this, "Bạn phải đồng ý với Điều kiện sử dụng FIMO để tiếp tục", Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                });
                            }
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại, email có thể đã tồn tại", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvLogin.setOnClickListener(v -> finish());
    }

    private void toggleVisibility(EditText editText, ImageView icon, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setAlpha(1.0f);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setAlpha(0.5f);
        }
        editText.setSelection(editText.getText().length());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeHelper.applyTheme(this);
    }
}