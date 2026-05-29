package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mainRootLayout;
    private BottomNavigationView bottomNav;
    private ImageView icLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainRootLayout = findViewById(R.id.mainRootLayout);
        bottomNav = findViewById(R.id.bottomNavigation);
        icLogin = findViewById(R.id.icLogin);

        icLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_more) {
                selectedFragment = new MoreFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
            return false;
        });

        SharedPreferences prefs = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isNightMode = prefs.getBoolean("isNightMode", true);
        applyTheme(isNightMode, false);
    }

    public void applyTheme(boolean isNightMode, boolean animate) {
        int bgFrom = isNightMode ? Color.parseColor("#F5F5F5") : Color.parseColor("#141414");
        int bgTo = isNightMode ? Color.parseColor("#141414") : Color.parseColor("#F5F5F5");

        int navFrom = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#1F1F1F");
        int navTo = isNightMode ? Color.parseColor("#1F1F1F") : Color.parseColor("#FFFFFF");

        int iconFrom = isNightMode ? Color.parseColor("#000000") : Color.parseColor("#FFFFFF");
        int iconTo = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");

        if (animate) {
            ValueAnimator bgAnim = ValueAnimator.ofObject(new ArgbEvaluator(), bgFrom, bgTo);
            bgAnim.setDuration(400);
            bgAnim.addUpdateListener(anim -> mainRootLayout.setBackgroundColor((int) anim.getAnimatedValue()));
            bgAnim.start();

            ValueAnimator navAnim = ValueAnimator.ofObject(new ArgbEvaluator(), navFrom, navTo);
            navAnim.setDuration(400);
            navAnim.addUpdateListener(anim -> bottomNav.setBackgroundColor((int) anim.getAnimatedValue()));
            navAnim.start();

            ValueAnimator iconAnim = ValueAnimator.ofObject(new ArgbEvaluator(), iconFrom, iconTo);
            iconAnim.setDuration(400);
            iconAnim.addUpdateListener(anim -> icLogin.setColorFilter((int) anim.getAnimatedValue()));
            iconAnim.start();
        } else {
            mainRootLayout.setBackgroundColor(bgTo);
            bottomNav.setBackgroundColor(navTo);
            icLogin.setColorFilter(iconTo);
        }

        int unselectedColor = isNightMode ? Color.parseColor("#A0A0A0") : Color.parseColor("#666666");
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked},
                new int[] {-android.R.attr.state_checked}
        };
        int[] colors = new int[] {
                Color.parseColor("#E50914"),
                unselectedColor
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNav.setItemIconTintList(colorStateList);
        bottomNav.setItemTextColor(colorStateList);
    }
}