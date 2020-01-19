package com.icebem.akt.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IOUtil {
    private static final int LENGTH_KB = 1024;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final String METHOD_GET = "GET";

    public static InputStream fromAssets(Context context, String path) throws IOException {
        return context.getAssets().open(path);
    }

    public static InputStream fromWeb(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(METHOD_GET);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(CONNECT_TIMEOUT);
        return connection.getInputStream();
    }

    public static String stream2String(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[LENGTH_KB];
        int len;
        try {
            while ((len = in.read(buffer)) != -1)
                out.write(buffer, 0, len);
        } finally {
            in.close();
        }
        return out.toString(StandardCharsets.UTF_8.name());
    }
}