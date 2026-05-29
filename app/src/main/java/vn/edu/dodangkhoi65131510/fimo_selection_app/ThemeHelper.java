package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ThemeHelper {

    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isNightMode = prefs.getBoolean("isNightMode", true);

        View root = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (root != null) {
            int bgColor = isNightMode ? Color.parseColor("#141414") : Color.parseColor("#F5F5F5");
            root.setBackgroundColor(bgColor);

            if (root instanceof ViewGroup && ((ViewGroup) root).getChildCount() > 0) {
                View layoutRoot = ((ViewGroup) root).getChildAt(0);
                if (layoutRoot.getBackground() == null || layoutRoot.getBackground() instanceof ColorDrawable) {
                    layoutRoot.setBackgroundColor(bgColor);
                }
            }

            int textColor = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");
            changeColorsSafe(root, textColor, isNightMode);
        }
    }

    public static void changeColorsSafe(View view, int textColor, boolean isNightMode) {
        if (view == null) return;

        if (view instanceof ViewGroup) {
            String viewName = view.getClass().getName();
            if (!viewName.contains("BottomNavigation") && !viewName.contains("RecyclerView")) {
                Drawable bg = view.getBackground();
                if (bg instanceof ColorDrawable) {
                    int color = ((ColorDrawable) bg).getColor();
                    if (!isNightMode && (color == Color.parseColor("#141414") || color == Color.parseColor("#1F1F1F") || color == Color.parseColor("#0A0A0A") || color == Color.BLACK)) {
                        view.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    } else if (isNightMode && (color == Color.parseColor("#F5F5F5") || color == Color.WHITE)) {
                        view.setBackgroundColor(Color.parseColor("#141414"));
                    }
                }
            }

            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                changeColorsSafe(vg.getChildAt(i), textColor, isNightMode);
            }
        } else if (view instanceof TextView) {
            if (!(view instanceof Button)) {
                TextView tv = (TextView) view;
                int currentColor = tv.getCurrentTextColor();

                if (currentColor != Color.parseColor("#E50914") && currentColor != Color.parseColor("#FF5722") && currentColor != Color.RED) {
                    tv.setTextColor(textColor);
                }

                if (tv.getBackground() != null && !(tv instanceof EditText)) {
                    ColorStateList bgTint = tv.getBackgroundTintList();
                    boolean isRedBg = bgTint != null && bgTint.getDefaultColor() == Color.parseColor("#E50914");

                    if (!isRedBg) {
                        int tagBgColor = isNightMode ? Color.parseColor("#333333") : Color.parseColor("#E0E0E0");
                        tv.setBackgroundTintList(ColorStateList.valueOf(tagBgColor));
                    }
                }
            }
            if (view instanceof EditText) {
                int boxColor = isNightMode ? Color.parseColor("#252525") : Color.parseColor("#E0E0E0");
                view.setBackgroundTintList(ColorStateList.valueOf(boxColor));
                ((EditText) view).setHintTextColor(isNightMode ? Color.parseColor("#888888") : Color.parseColor("#666666"));
            }
        }
    }
}