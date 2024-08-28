package com.dimension.a3dimension.graphics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;


public class TinyDB {
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";
    private SharedPreferences preferences;

    public TinyDB(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Bitmap getImage(String str) {
        try {
            return BitmapFactory.decodeFile(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSavedImagePath() {
        return this.lastImagePath;
    }

    public String putImage(String str, String str2, Bitmap bitmap) {
        if (str == null || str2 == null || bitmap == null) {
            return null;
        }
        this.DEFAULT_APP_IMAGEDATA_DIRECTORY = str;
        String str3 = setupFullPath(str2);
        if (!str3.equals("")) {
            this.lastImagePath = str3;
            saveBitmap(str3, bitmap);
        }
        return str3;
    }

    public boolean putImageWithFullPath(String str, Bitmap bitmap) {
        return str != null && bitmap != null && saveBitmap(str, bitmap);
    }

    private String setupFullPath(String str) {
        File file = new File(Environment.getExternalStorageDirectory(), this.DEFAULT_APP_IMAGEDATA_DIRECTORY);
        if (isExternalStorageReadable() && isExternalStorageWritable() && !file.exists() && !file.mkdirs()) {
            Log.e("ERROR", "Failed to setup folder");
            return "";
        }
        return file.getPath() + '/' + str;
    }


    private boolean saveBitmap(String str, Bitmap bitmap) {
        boolean z;
        boolean z2 = false;
        boolean z3 = false;
        FileOutputStream fileOutputStream = null;
        if (str == null || bitmap == null) {
            return false;
        }
        File file = new File(str);
        if (file.exists() && !file.delete()) {
            return false;
        }
        try {
            z = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            z = false;
        }
        FileOutputStream fileOutputStream2;
        try {
            try {
                fileOutputStream = new FileOutputStream(file);
                try {

                    z2 = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                } catch (Exception e2) {

                    fileOutputStream2 = fileOutputStream;
                    e2.printStackTrace();
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.flush();
                            fileOutputStream2.close();
                            z2 = false;
                            z3 = true;
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            z2 = false;
                            z3 = false;
                            if (z) {
                            }
                        }
                        if (z) {
                        }
                    }
                    z2 = false;
                    z3 = false;
                    if (z) {
                    }
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream2 = fileOutputStream;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.flush();
                            fileOutputStream2.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
                e5.printStackTrace();
            }
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
                z3 = true;
            } catch (IOException e6) {
                e6.printStackTrace();
                z3 = false;
                return z && z2 && z3;
            }
            return z && z2 && z3;
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return z && z2 && z3;
    }

    public int getInt(String str) {
        return this.preferences.getInt(str, 0);
    }

    public ArrayList<Integer> getListInt(String str) {
        ArrayList arrayList = new ArrayList(Arrays.asList(TextUtils.split(this.preferences.getString(str, ""), "‚‗‚")));
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(Integer.valueOf(Integer.parseInt((String) it.next())));
        }
        return arrayList2;
    }

    public long getLong(String str) {
        return this.preferences.getLong(str, 0L);
    }

    public float getFloat(String str) {
        return this.preferences.getFloat(str, 0.0f);
    }

    public double getDouble(String str) {
        try {
            return Double.parseDouble(getString(str));
        } catch (NumberFormatException unused) {
            return 0.0d;
        }
    }

    public ArrayList<Double> getListDouble(String str) {
        ArrayList arrayList = new ArrayList(Arrays.asList(TextUtils.split(this.preferences.getString(str, ""), "‚‗‚")));
        ArrayList<Double> arrayList2 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(Double.valueOf(Double.parseDouble((String) it.next())));
        }
        return arrayList2;
    }

    public ArrayList<Long> getListLong(String str) {
        ArrayList arrayList = new ArrayList(Arrays.asList(TextUtils.split(this.preferences.getString(str, ""), "‚‗‚")));
        ArrayList<Long> arrayList2 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(Long.valueOf(Long.parseLong((String) it.next())));
        }
        return arrayList2;
    }

    public String getString(String str) {
        return this.preferences.getString(str, "");
    }

    public ArrayList<String> getListString(String str) {
        return new ArrayList<>(Arrays.asList(TextUtils.split(this.preferences.getString(str, ""), "‚‗‚")));
    }

    public boolean getBoolean(String str) {
        return this.preferences.getBoolean(str, false);
    }

    public ArrayList<Boolean> getListBoolean(String str) {
        ArrayList<String> listString = getListString(str);
        ArrayList<Boolean> arrayList = new ArrayList<>();
        Iterator<String> it = listString.iterator();
        while (it.hasNext()) {
            if (it.next().equals("true")) {
                arrayList.add(true);
            } else {
                arrayList.add(false);
            }
        }
        return arrayList;
    }

    public ArrayList<Object> getListObject(String str, Class<?> cls) {
        Gson gson = new Gson();
        ArrayList<String> listString = getListString(str);
        ArrayList<Object> arrayList = new ArrayList<>();
        Iterator<String> it = listString.iterator();
        while (it.hasNext()) {
            arrayList.add(gson.fromJson(it.next(), (Class<Object>) cls));
        }
        return arrayList;
    }

    public <T> T getObject(String str, Class<T> cls) {
        T t = (T) new Gson().fromJson(getString(str), (Class<Object>) cls);
        if (t != null) {
            return t;
        }
        throw null;
    }

    public void putInt(String str, int i) {
        checkForNullKey(str);
        this.preferences.edit().putInt(str, i).apply();
    }

    public void putListInt(String str, ArrayList<Integer> arrayList) {
        checkForNullKey(str);
        this.preferences.edit().putString(str, TextUtils.join("‚‗‚", (Integer[]) arrayList.toArray(new Integer[arrayList.size()]))).apply();
    }

    public void putLong(String str, long j) {
        checkForNullKey(str);
        this.preferences.edit().putLong(str, j).apply();
    }

    public void putListLong(String str, ArrayList<Long> arrayList) {
        checkForNullKey(str);
        this.preferences.edit().putString(str, TextUtils.join("‚‗‚", (Long[]) arrayList.toArray(new Long[arrayList.size()]))).apply();
    }

    public void putFloat(String str, float f) {
        checkForNullKey(str);
        this.preferences.edit().putFloat(str, f).apply();
    }

    public void putDouble(String str, double d) {
        checkForNullKey(str);
        putString(str, String.valueOf(d));
    }

    public void putListDouble(String str, ArrayList<Double> arrayList) {
        checkForNullKey(str);
        this.preferences.edit().putString(str, TextUtils.join("‚‗‚", (Double[]) arrayList.toArray(new Double[arrayList.size()]))).apply();
    }

    public void putString(String str, String str2) {
        checkForNullKey(str);
        checkForNullValue(str2);
        this.preferences.edit().putString(str, str2).apply();
    }

    public void putListString(String str, ArrayList<String> arrayList) {
        checkForNullKey(str);
        this.preferences.edit().putString(str, TextUtils.join("‚‗‚", (String[]) arrayList.toArray(new String[arrayList.size()]))).apply();
    }

    public void putBoolean(String str, boolean z) {
        checkForNullKey(str);
        this.preferences.edit().putBoolean(str, z).apply();
    }

    public void putListBoolean(String str, ArrayList<Boolean> arrayList) {
        checkForNullKey(str);
        ArrayList<String> arrayList2 = new ArrayList<>();
        Iterator<Boolean> it = arrayList.iterator();
        while (it.hasNext()) {
            if (it.next().booleanValue()) {
                arrayList2.add("true");
            } else {
                arrayList2.add("false");
            }
        }
        putListString(str, arrayList2);
    }

    public void putObject(String str, Object obj) {
        checkForNullKey(str);
        putString(str, new Gson().toJson(obj));
    }

    public void putListObject(String str, ArrayList<SurfaceShape> arrayList) {
        checkForNullKey(str);
        Gson gson = new Gson();
        ArrayList<String> arrayList2 = new ArrayList<>();
        Iterator<SurfaceShape> it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(gson.toJson(it.next()));
        }
        putListString(str, arrayList2);
    }

    public void remove(String str) {
        this.preferences.edit().remove(str).apply();
    }

    public boolean deleteImage(String str) {
        return new File(str).delete();
    }

    public void clear() {
        this.preferences.edit().clear().apply();
    }

    public Map<String, ?> getAll() {
        return this.preferences.getAll();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.preferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public static boolean isExternalStorageWritable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static boolean isExternalStorageReadable() {
        String externalStorageState = Environment.getExternalStorageState();
        return "mounted".equals(externalStorageState) || "mounted_ro".equals(externalStorageState);
    }

    private void checkForNullKey(String str) {
        if (str == null) {
            throw null;
        }
    }

    private void checkForNullValue(String str) {
        if (str == null) {
            throw null;
        }
    }
}
