package com.icebem.akt.util;

import com.icebem.akt.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class AppUtil {
    public static final String URL_ALIPAY_API = "intent://platformapi/startapp?saId=10000007&qrcode=https://qr.alipay.com/tsx02922ajwj6xekqyd1rbf#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
    public static final String URL_QQ_API = "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3DN_OjFuCOkERq58jO2KoJEDD2a48vzB53";
    public static final String URL_PROJECT = "https://github.com/IcebemAst/ArknightsTap";
    public static final String URL_RELEASE_LATEST = "https://github.com/IcebemAst/ArknightsTap/releases/latest";
    public static final String URL_RELEASE_LATEST_API = "https://api.github.com/repos/IcebemAst/ArknightsTap/releases/latest";
    public static final String URL_RELEASE_DATA = "https://raw.githubusercontent.com/IcebemAst/ArknightsTap/master/app/release/output.json";

    public static boolean isLatestVersion() throws IOException, JSONException {
        int version = new JSONArray(IOUtil.stream2String(IOUtil.fromWeb(AppUtil.URL_RELEASE_DATA))).getJSONObject(0).getJSONObject("apkData").getInt("versionCode");
        return BuildConfig.VERSION_CODE >= version;
    }
}