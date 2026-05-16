package com.nammaraste.utils;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonDataManager {

    public static JSONArray readJsonArrayFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return new JSONArray(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONObject readJsonObjectFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONArray readFromInternal(Context context, String fileName) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            if (!file.exists()) return new JSONArray();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return new JSONArray(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void writeToInternal(Context context, String fileName, JSONArray data) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            FileWriter writer = new FileWriter(file);
            writer.write(data.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDamageReport(Context context, JSONObject report) {
        try {
            JSONArray reports = readFromInternal(context, "user_damage_reports.json");
            report.put("id", reports.length() + 100);
            reports.put(report);
            writeToInternal(context, "user_damage_reports.json", reports);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getAllDamageReports(Context context) {
        JSONArray assetReports = readJsonArrayFromAssets(context, "damage_reports.json");
        JSONArray userReports = readFromInternal(context, "user_damage_reports.json");
        for (int i = 0; i < userReports.length(); i++) {
            try {
                assetReports.put(userReports.getJSONObject(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return assetReports;
    }

    public static int getReportCountForRoad(Context context, int roadId) {
        JSONArray allReports = getAllDamageReports(context);
        int count = 0;
        for (int i = 0; i < allReports.length(); i++) {
            try {
                if (allReports.getJSONObject(i).getInt("roadId") == roadId) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}
