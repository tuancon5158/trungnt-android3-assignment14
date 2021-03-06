package techkids.mad3.theweather;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by TrungNT on 6/1/2016.
 */
public class StorageDataBroadCastReceive extends BroadcastReceiver {
    private WeatherDBHelper sqliteOpenDbHelper;
    private SQLiteDatabase database;
    private ContentValues contentValues;
    private Bundle getBundleData;
    private String minTemp, maxTemp, mainTemp, descriptionTemp;
    private SimpleDateFormat simpleDateFormat;
    private Date currentDate, createAtDate;
    private SQLiteDatabase db;
    private int countRows;

    @Override
    public void onReceive(Context context, Intent intent) {
        getBundleData = intent.getExtras();
        minTemp = getBundleData.getString("minTemp");
        maxTemp = getBundleData.getString("minTemp");
        mainTemp = getBundleData.getString("mainTemp");
        descriptionTemp = getBundleData.getString("descriptionTemp");

        try{

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            currentDate = new Date();
            sqliteOpenDbHelper = new WeatherDBHelper(context);
            db = sqliteOpenDbHelper.getWritableDatabase();
            List<Weather> weathers = sqliteOpenDbHelper.getAllContacts();
            countRows = sqliteOpenDbHelper.getWeatherCount();
            if (countRows==0)
                insertDataToSQLite(context, minTemp, maxTemp, mainTemp, descriptionTemp);
            else
                for (Weather wt : weathers)
                {
                    Log.d("create at", simpleDateFormat.parse(wt.getCreateAt()).toString());
                    createAtDate = simpleDateFormat.parse(wt.getCreateAt().toString());
                    if (createAtDate.compareTo(currentDate)<0) {
                        deleteWeaherByCurrentDateTime(context, wt.getId());
                        Log.d("delete", "delete successful");
                        insertDataToSQLite(context, minTemp, maxTemp, mainTemp, descriptionTemp);
                        Log.d("Msg", "Weather " + wt.getId() + " before current date " + wt.getCreateAt().toString());
                        Log.d("Count1", String.valueOf(weathers.size()));
                        Log.d("_id", wt.getId() + "");
                    }
                }


        }catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("count" , String.valueOf(sqliteOpenDbHelper.getWeatherCount()));

}

    //method sotorage data in SQLite
    private void insertDataToSQLite(Context context,
                                    String minTemp,
                                    String maxTemp,
                                    String mainTemp,
                                    String descriptionTemp)
    {
        sqliteOpenDbHelper = new WeatherDBHelper(context);
        database = sqliteOpenDbHelper.getWritableDatabase();

        try{
            contentValues = new ContentValues();
            contentValues.put(WeatherDBHelper.DESCRIPTION, descriptionTemp);
            contentValues.put(WeatherDBHelper.TEMPERATURE_MIN, minTemp);
            contentValues.put(WeatherDBHelper.TEMPERATURE_MAX, maxTemp);
            contentValues.put(WeatherDBHelper.TEMPERATURE, mainTemp);

            database.beginTransaction();
            database.insert(WeatherDBHelper.WEATHER_TABLE_NAME, null, contentValues);
            database.setTransactionSuccessful();
            database.endTransaction();

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            database.close();
        }
    }

    //Phuong thuc xoa du lieu theo ID
    private void deleteWeaherByCurrentDateTime(Context context, Integer id)
    {
        sqliteOpenDbHelper = new WeatherDBHelper(context);
        database = sqliteOpenDbHelper.getWritableDatabase();
        database.delete(WeatherDBHelper.WEATHER_TABLE_NAME, WeatherDBHelper._ID + " = ?", new String[] {String.valueOf(id)});
    }
}
