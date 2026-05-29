package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeHelper {

    public static void applyTheme(Activity activity) {
        if (activity == null) return;
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

            injectGlobalButtons(activity, (ViewGroup) root, isNightMode);
        }
    }

    private static void injectGlobalButtons(Activity activity, ViewGroup root, boolean isNightMode) {
        int containerId = 1999999;
        LinearLayout floatLayout = root.findViewById(containerId);

        float density = activity.getResources().getDisplayMetrics().density;
        int size40 = (int) (40 * density);
        int margin12 = (int) (12 * density);
        int margin16 = (int) (16 * density);

        if (floatLayout == null) {
            floatLayout = new LinearLayout(activity);
            floatLayout.setId(containerId);
            floatLayout.setOrientation(LinearLayout.HORIZONTAL);
            floatLayout.setGravity(Gravity.CENTER_VERTICAL);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.END;

            int bottomMargin = activity.getClass().getSimpleName().equals("MainActivity") ? (int) (70 * density) : margin16;
            params.setMargins(0, 0, margin16, bottomMargin);
            floatLayout.setLayoutParams(params);

            ImageView btnTheme = new ImageView(activity);
            btnTheme.setId(1999998);
            LinearLayout.LayoutParams themeParams = new LinearLayout.LayoutParams(size40, size40);
            themeParams.setMargins(0, 0, margin12, 0);
            btnTheme.setLayoutParams(themeParams);
            btnTheme.setBackgroundResource(R.drawable.circle_background);
            btnTheme.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#252525")));
            int padding = (int) (10 * density);
            btnTheme.setPadding(padding, padding, padding, padding);
            btnTheme.setColorFilter(Color.parseColor("#E0E0E0"));

            TextView btnScroll = new TextView(activity);
            btnScroll.setId(1999997);
            btnScroll.setLayoutParams(new LinearLayout.LayoutParams(size40, size40));
            btnScroll.setBackgroundResource(R.drawable.circle_background);
            btnScroll.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E50914")));
            btnScroll.setGravity(Gravity.CENTER);
            btnScroll.setText("▲");
            btnScroll.setTextColor(Color.WHITE);
            btnScroll.setTextSize(16);
            btnScroll.setTypeface(null, android.graphics.Typeface.BOLD);
            btnScroll.setVisibility(View.GONE);

            floatLayout.addView(btnTheme);
            floatLayout.addView(btnScroll);
            root.addView(floatLayout);

            btnTheme.setOnClickListener(v -> {
                SharedPreferences prefs = activity.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
                boolean newMode = !prefs.getBoolean("isNightMode", true);
                prefs.edit().putBoolean("isNightMode", newMode).apply();

                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).applyTheme(newMode, true);
                }
                applyTheme(activity);
            });
        }

        ImageView btnTheme = root.findViewById(1999998);
        if (btnTheme != null) {
            btnTheme.setImageResource(isNightMode ? R.drawable.ic_sun : R.drawable.ic_moon);
        }

        TextView btnScroll = root.findViewById(1999997);
        if (btnScroll != null) {
            setupSmartScroll(root, btnScroll);
        }
    }

    private static void setupSmartScroll(View view, TextView btnScroll) {
        View scrollableView = findScrollableView(view);
        if (scrollableView instanceof NestedScrollView) {
            NestedScrollView nsv = (NestedScrollView) scrollableView;
            nsv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                btnScroll.setVisibility(scrollY > 400 ? View.VISIBLE : View.GONE);
            });
            btnScroll.setOnClickListener(v -> nsv.smoothScrollTo(0, 0));
        } else if (scrollableView instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) scrollableView;
            rv.clearOnScrollListeners();
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int offset = recyclerView.computeVerticalScrollOffset();
                    btnScroll.setVisibility(offset > 400 ? View.VISIBLE : View.GONE);
                }
            });
            btnScroll.setOnClickListener(v -> rv.smoothScrollToPosition(0));
        } else {
            btnScroll.setVisibility(View.GONE);
        }
    }

    private static View findScrollableView(View view) {
        if (view instanceof NestedScrollView || view instanceof RecyclerView) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View found = findScrollableView(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }

    public static void changeColorsSafe(View view, int textColor, boolean isNightMode) {
        if (view == null) return;

        if (view.getId() == 1999999 || view.getId() == 1999998 || view.getId() == 1999997) return;

        if (view instanceof ViewGroup) {
            String viewName = view.getClass().getName();
            if (!viewName.contains("BottomNavigation") && !viewName.contains("RecyclerView") && !viewName.contains("ViewPager2")) {
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
                    if (currentColor == Color.parseColor("#888888") || currentColor == Color.parseColor("#666666") || currentColor == Color.parseColor("#555555") || currentColor == Color.parseColor("#777777") || currentColor == Color.parseColor("#999999") || currentColor == Color.parseColor("#CCCCCC")) {
                        tv.setTextColor(isNightMode ? Color.parseColor("#888888") : Color.parseColor("#555555"));
                    } else {
                        tv.setTextColor(textColor);
                    }
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