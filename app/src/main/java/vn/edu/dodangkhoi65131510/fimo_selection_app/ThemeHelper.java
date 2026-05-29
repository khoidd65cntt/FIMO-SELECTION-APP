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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeHelper {

    public static void applyFragmentTheme(Fragment fragment) {
        if (fragment == null || fragment.getContext() == null || fragment.getView() == null) return;

        SharedPreferences prefs = fragment.getContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isNightMode = prefs.getBoolean("isNightMode", true);
        int textColor = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");

        View root = fragment.getView();

        int bgColor = isNightMode ? Color.parseColor("#141414") : Color.parseColor("#F5F5F5");
        root.setBackgroundColor(bgColor);

        Runnable task = () -> changeColorsSafe(root, textColor, isNightMode);

        root.post(task);
        root.postDelayed(task, 150);
        root.postDelayed(task, 350);

        if (fragment.getActivity() != null) {
            applyTheme(fragment.getActivity());
        }
    }

    public static void applyTheme(Activity activity) {
        if (activity == null) return;
        SharedPreferences prefs = activity.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isNightMode = prefs.getBoolean("isNightMode", true);

        View root = activity.getWindow().getDecorView().findViewById(android.R.id.content);

        if (root instanceof ViewGroup) {
            ViewGroup vgRoot = (ViewGroup) root;
            int bgColor = isNightMode ? Color.parseColor("#141414") : Color.parseColor("#F5F5F5");
            vgRoot.setBackgroundColor(bgColor);

            if (vgRoot.getChildCount() > 0) {
                View layoutRoot = vgRoot.getChildAt(0);
                if (layoutRoot.getBackground() == null || layoutRoot.getBackground() instanceof ColorDrawable) {
                    layoutRoot.setBackgroundColor(bgColor);
                }
            }

            int textColor = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");

            Runnable themeTask = () -> {
                changeColorsSafe(vgRoot, textColor, isNightMode);
                injectGlobalButtons(activity, vgRoot, isNightMode);
            };

            vgRoot.post(themeTask);
            vgRoot.postDelayed(themeTask, 100);
            vgRoot.postDelayed(themeTask, 300);
        }
    }

    private static void injectGlobalButtons(Activity activity, ViewGroup root, boolean isNightMode) {
        String containerTag = "FIMO_FLOAT_CONTAINER";
        String themeBtnTag = "FIMO_BTN_THEME";
        String scrollBtnTag = "FIMO_BTN_SCROLL";

        LinearLayout floatLayout = root.findViewWithTag(containerTag);

        float density = activity.getResources().getDisplayMetrics().density;
        int size40 = (int) (40 * density);
        int margin12 = (int) (12 * density);
        int margin16 = (int) (16 * density);

        if (floatLayout == null) {
            floatLayout = new LinearLayout(activity);
            floatLayout.setTag(containerTag);
            floatLayout.setOrientation(LinearLayout.HORIZONTAL);
            floatLayout.setGravity(Gravity.CENTER_VERTICAL);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.END;

            int bottomMargin = activity.getClass().getSimpleName().equals("MainActivity") ? (int) (70 * density) : margin16;
            params.setMargins(0, 0, margin16, bottomMargin);
            floatLayout.setLayoutParams(params);

            ImageView btnTheme = new ImageView(activity);
            btnTheme.setTag(themeBtnTag);
            LinearLayout.LayoutParams themeParams = new LinearLayout.LayoutParams(size40, size40);
            themeParams.setMargins(0, 0, margin12, 0);
            btnTheme.setLayoutParams(themeParams);
            btnTheme.setBackgroundResource(R.drawable.circle_background);
            btnTheme.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#252525")));
            int padding = (int) (10 * density);
            btnTheme.setPadding(padding, padding, padding, padding);
            btnTheme.setColorFilter(Color.parseColor("#E0E0E0"));

            TextView btnScroll = new TextView(activity);
            btnScroll.setTag(scrollBtnTag);
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

        ImageView btnTheme = root.findViewWithTag(themeBtnTag);
        if (btnTheme != null) {
            btnTheme.setImageResource(isNightMode ? R.drawable.ic_sun : R.drawable.ic_moon);
        }

        TextView btnScroll = root.findViewWithTag(scrollBtnTag);
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

    // Màng chắn bảo vệ: Dò tìm xem View này có nằm trong Slider hay Banner không
    private static boolean isProtectedFromTheme(View view) {
        View current = view;
        while (current != null) {
            if (current.getId() != View.NO_ID) {
                try {
                    String name = current.getResources().getResourceEntryName(current.getId()).toLowerCase();
                    // Nếu nó nằm trong các khu vực ảnh này, lập tức giương khiên bảo vệ
                    if (name.contains("slider") || name.contains("banner") || name.contains("player")) {
                        return true;
                    }
                } catch (Exception ignored) {}
            }
            if (current.getParent() instanceof View) {
                current = (View) current.getParent();
            } else {
                break;
            }
        }
        return false;
    }

    public static void changeColorsSafe(View view, int textColor, boolean isNightMode) {
        if (view == null) return;

        if ("FIMO_FLOAT_CONTAINER".equals(view.getTag()) || "FIMO_BTN_THEME".equals(view.getTag()) || "FIMO_BTN_SCROLL".equals(view.getTag())) {
            return;
        }

        boolean isProtected = isProtectedFromTheme(view);
        boolean isBox = false;

        if (view instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) view;
            rv.clearOnChildAttachStateChangeListeners();
            rv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(@NonNull View child) {
                    SharedPreferences prefs = rv.getContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
                    boolean currentNightMode = prefs.getBoolean("isNightMode", true);
                    int currentTextColor = currentNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");
                    changeColorsSafe(child, currentTextColor, currentNightMode);
                }
                @Override
                public void onChildViewDetachedFromWindow(@NonNull View child) {}
            });
        }

        if (view instanceof ViewGroup) {
            String viewName = view.getClass().getName();
            if (!viewName.contains("BottomNavigation") && !viewName.contains("RecyclerView") && !viewName.contains("ViewPager2") && !viewName.contains("ScrollView")) {
                Drawable bg = view.getBackground();
                if (bg instanceof ColorDrawable) {
                    int color = ((ColorDrawable) bg).getColor();
                    if (!isNightMode && (color == Color.parseColor("#141414") || color == Color.parseColor("#1F1F1F") || color == Color.parseColor("#0A0A0A") || color == Color.BLACK)) {
                        view.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    } else if (isNightMode && (color == Color.parseColor("#F5F5F5") || color == Color.WHITE)) {
                        view.setBackgroundColor(Color.parseColor("#141414"));
                    }
                } else if (bg != null && !(bg instanceof android.graphics.drawable.BitmapDrawable)) {
                    if (!isProtected) isBox = true;
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

                if (isProtected) {
                    // Cứu các chữ trong Slider: ÉP cứng màu trắng để nổi trên nền ảnh
                    tv.setTextColor(Color.WHITE);
                } else if (currentColor != Color.parseColor("#E50914") && currentColor != Color.parseColor("#FF5722") && currentColor != Color.RED) {
                    if (currentColor == Color.parseColor("#888888") || currentColor == Color.parseColor("#666666") || currentColor == Color.parseColor("#555555") || currentColor == Color.parseColor("#777777") || currentColor == Color.parseColor("#999999") || currentColor == Color.parseColor("#CCCCCC")) {
                        tv.setTextColor(isNightMode ? Color.parseColor("#888888") : Color.parseColor("#555555"));
                    } else {
                        tv.setTextColor(textColor);
                    }
                }

                if (tv.getBackground() != null && !(tv instanceof EditText)) {
                    if (!(tv.getBackground() instanceof ColorDrawable && ((ColorDrawable) tv.getBackground()).getColor() == Color.TRANSPARENT)) {
                        if (!isProtected) isBox = true;
                    }
                }
            }
            if (view instanceof EditText) {
                int boxColor = isNightMode ? Color.parseColor("#252525") : Color.parseColor("#E0E0E0");
                view.setBackgroundTintList(ColorStateList.valueOf(boxColor));
                ((EditText) view).setHintTextColor(isNightMode ? Color.parseColor("#888888") : Color.parseColor("#666666"));
            }
        } else if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            boolean shouldTint = false;

            if (iv.getAlpha() > 0f && iv.getAlpha() <= 0.6f) {
                shouldTint = true;
            }

            if (iv.getId() != View.NO_ID) {
                try {
                    String resName = iv.getResources().getResourceEntryName(iv.getId()).toLowerCase();
                    if (resName.contains("avatar") || resName.contains("poster") || resName.contains("banner") || resName.contains("slider") || resName.contains("imgcast")) {
                        shouldTint = false;
                    } else if (resName.contains("back") || resName.contains("bg") || resName.contains("background") || resName.contains("logo") || resName.contains("watermark")) {
                        shouldTint = true;
                    }
                } catch (Exception ignored) {}
            }

            if (shouldTint) {
                if (isNightMode) {
                    iv.setColorFilter(Color.parseColor("#FFFFFF"));
                } else {
                    iv.setColorFilter(Color.parseColor("#222222"));
                }
            }
        }

        // Cứu tag "THỊNH HÀNH": Vì bị isProtected chặn nên isBox = false, nền Đỏ sẽ không bị quét thành Xám/Đen
        if (isBox && !isProtected) {
            ColorStateList bgTint = view.getBackgroundTintList();
            boolean isRedBg = bgTint != null && (bgTint.getDefaultColor() == Color.parseColor("#E50914") || bgTint.getDefaultColor() == Color.parseColor("#FF5722"));

            if (!isRedBg) {
                int boxColor = isNightMode ? Color.parseColor("#252525") : Color.parseColor("#E0E0E0");
                view.setBackgroundTintList(ColorStateList.valueOf(boxColor));
            }
        }
    }
}