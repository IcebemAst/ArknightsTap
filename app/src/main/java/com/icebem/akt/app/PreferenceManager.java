package com.icebem.akt.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import com.icebem.akt.BuildConfig;
import com.icebem.akt.R;
import com.icebem.akt.model.RecruitTag;
import com.icebem.akt.service.GestureService;
import com.icebem.akt.util.RandomUtil;

public class PreferenceManager {
    private static final int PACKAGE_EN = 2;
    private static final int PACKAGE_JP = 3;
    private static final String KEY_A = "point_a";
    private static final String KEY_B = "point_b";
    private static final String KEY_X = "point_x";
    private static final String KEY_Y = "point_y";
    private static final String KEY_W = "point_w";
    private static final String KEY_H = "point_h";
    private static final String KEY_TIMER_TIME = "timer_time";
    private static final String KEY_PRO = "pro";
    private static final String KEY_VERSION = "version";
    private static final String KEY_AUTO_UPDATE = "auto_update";
    private static final String KEY_CHECK_LAST_TIME = "check_last_time";
    private static final String KEY_LAUNCH_GAME = "launch_game";
    private static final String KEY_LAUNCH_PACKAGE = "launch_package";
    private static final String KEY_ASCENDING_STAR = "ascending_star";
    private static final String KEY_RECRUIT_PREVIEW = "recruit_preview";
    private static final String KEY_SCROLL_TO_RESULT = "scroll_to_result";
    private static final int[] TIMER_CONFIG = {0, 10, 15, 30, 45, 60, 90, 120};
    private static final int TIMER_POSITION = 1;
    private static final int UPDATE_TIME = 3500;
    private static final int CHECK_TIME = 28800000;
    private Context context;
    private SharedPreferences preferences;

    public PreferenceManager(Context context) {
        this.context = context;
        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        if (!dataUpdated()) {
            int[] res = ResolutionConfig.getResolution(context);
            for (int[] cfg : ResolutionConfig.RESOLUTION_CONFIG) {
                if (res[0] == cfg[0] && res[1] == cfg[1]) {
                    preferences.edit().putInt(KEY_A, cfg[2]).apply();
                    preferences.edit().putInt(KEY_B, cfg[3]).apply();
                    preferences.edit().putInt(KEY_X, cfg[4]).apply();
                    preferences.edit().putInt(KEY_Y, cfg[5]).apply();
                    preferences.edit().putInt(KEY_W, res[0] - RandomUtil.RANDOM_P).apply();
                    preferences.edit().putInt(KEY_H, res[1] / 4 - RandomUtil.RANDOM_P).apply();
                    break;
                }
            }
            setVersionCode();
        }
    }

    public int getA() {
        return preferences.getInt(KEY_A, 0);
    }

    public int getB() {
        return preferences.getInt(KEY_B, 0);
    }

    public int getX() {
        return preferences.getInt(KEY_X, 0);
    }

    public int getY() {
        return preferences.getInt(KEY_Y, 0);
    }

    public int getW() {
        return preferences.getInt(KEY_W, 0);
    }

    public int getH() {
        return preferences.getInt(KEY_H, 0);
    }

    public int getUpdateTime() {
        return UPDATE_TIME;
    }

    public void setTimerTime(int position) {
        preferences.edit().putInt(KEY_TIMER_TIME, TIMER_CONFIG[position]).apply();
    }

    public int getTimerTime() {
        return preferences.getInt(KEY_TIMER_TIME, TIMER_CONFIG[TIMER_POSITION]);
    }

    public void setPro(boolean pro) {
        preferences.edit().putBoolean(KEY_PRO, pro).apply();
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, GestureService.class.getName()), pro ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public boolean isPro() {
        return preferences.getBoolean(KEY_PRO, false);
    }

    private void setVersionCode() {
        preferences.edit().putInt(KEY_VERSION, BuildConfig.VERSION_CODE).apply();
    }

    private int getVersionCode() {
        return preferences.getInt(KEY_VERSION, 0);
    }

    public boolean dataUpdated() {
        return getVersionCode() == BuildConfig.VERSION_CODE && getA() > 0 && getB() > 0 && getX() > 0 && getY() > 0 && getW() > 0 && getH() > 0;
    }

    public String[] getTimerStrings(Context context) {
        String[] strings = new String[TIMER_CONFIG.length];
        for (int i = 0; i < TIMER_CONFIG.length; i++)
            strings[i] = TIMER_CONFIG[i] == 0 ? context.getString(R.string.info_timer_none) : context.getString(R.string.info_timer_min, TIMER_CONFIG[i]);
        return strings;
    }

    public int getTimerPosition() {
        for (int i = 0; i < TIMER_CONFIG.length; i++) {
            if (getTimerTime() == TIMER_CONFIG[i])
                return i;
        }
        return TIMER_POSITION;
    }

    private void setCheckLastTime() {
        preferences.edit().putLong(KEY_CHECK_LAST_TIME, System.currentTimeMillis()).apply();
    }

    private long getCheckLastTime() {
        return preferences.getLong(KEY_CHECK_LAST_TIME, 0);
    }

    public boolean autoUpdate() {
        // 每隔8小时自动获取更新
        if (preferences.getBoolean(KEY_AUTO_UPDATE, true) && System.currentTimeMillis() - getCheckLastTime() > CHECK_TIME) {
            setCheckLastTime();
            return true;
        } else return false;
    }

    public boolean launchGame() {
        return preferences.getBoolean(KEY_LAUNCH_GAME, true);
    }

    @Nullable
    public String getLaunchPackage() {
        return preferences.getString(KEY_LAUNCH_PACKAGE, null);
    }

    @Nullable
    public String getDefaultLaunchPackage() {
        for (String packageName : context.getResources().getStringArray(R.array.launch_package_values)) {
            if (context.getPackageManager().getLaunchIntentForPackage(packageName) != null)
                return packageName;
        }
        return null;
    }

    public int getTranslationIndex() {
        String packageName = getLaunchPackage();
        String[] packageArray = getContext().getResources().getStringArray(R.array.launch_package_values);
        if (packageName == null) {
            packageName = getDefaultLaunchPackage();
        }
        if (packageName == null || packageName.equals(packageArray[PACKAGE_EN]))
            return RecruitTag.INDEX_EN;
        if (packageName.equals(packageArray[PACKAGE_JP]))
            return RecruitTag.INDEX_JP;
        return RecruitTag.INDEX_CN;
    }

    public boolean ascendingStar() {
        return preferences.getBoolean(KEY_ASCENDING_STAR, true);
    }

    public boolean recruitPreview() {
        return preferences.getBoolean(KEY_RECRUIT_PREVIEW, false);
    }

    public boolean scrollToResult() {
        return preferences.getBoolean(KEY_SCROLL_TO_RESULT, true);
    }

    public Context getContext() {
        return context;
    }
}