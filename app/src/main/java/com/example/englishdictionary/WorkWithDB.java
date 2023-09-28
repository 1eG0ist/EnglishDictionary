package com.example.englishdictionary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkWithDB {

    private SQLiteDatabase mDb;

    public WorkWithDB(Context context) {
        try {
            mDb = getmDb(context);
        } catch (IOException ex){
            System.out.println("Ошибка при создании в методе-конструкторе WorkWithDB");
        }

    }

    private SQLiteDatabase getmDb(Context context) throws IOException {
        try {
            SQLiteDatabase _mDb;
            DatabaseHelper mDBHelper = new DatabaseHelper(context);

            try {
                _mDb = mDBHelper.getWritableDatabase();
            } catch (SQLException mSQLException) {
                throw mSQLException;
            }
            return _mDb;
        } catch (Exception exception) {
            System.out.println("You have exception in getmDb method");
            throw exception;
        }
    }

    public ArrayList<String> getEmails() {
        try {
            Cursor cursor = mDb.rawQuery("SELECT email FROM users", null);
            cursor.moveToFirst();

            ArrayList<String> result = new ArrayList<>();
            if (cursor.getCount() == 0) {
                return result;
            }
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
            return result;
        } catch (Exception exception) {
            System.out.println("You have exception in getEmails method");
            throw exception;
        }
    }

    public ArrayList<Object[]> getAllUsers() {
        Cursor cursor = mDb.rawQuery("SELECT * FROM users", null);
        cursor.moveToFirst();
        ArrayList<Object[]> result = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            result.add(new Object[] {cursor.getString(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5),
                cursor.getString(6), cursor.getString(7)
            });
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public void createNewUser(String nickname, String email, String password,
            int last_learned_word, int count_words_per_time, int is_the_music_on, int is_online_now) {
        try {
//            mDb.execSQL(
//                    "INSERT INTO users (user_id, nickname, email, password, last_learned_word, count_words_per_time, is_the_music_on, is_online_now) VALUES " +
//                    "('" + 1 + "', '" + nickname + "', '" + email + "', '" + password + "', '" + last_learned_word +
//                    "', '" + count_words_per_time + "', '" + is_the_music_on + "', '" + is_online_now + "')"
//            );
            ContentValues contentValues = new ContentValues();
            contentValues.put("nickname", nickname);
            contentValues.put("email", email);
            contentValues.put("password", password);
            contentValues.put("last_learned_word", last_learned_word);
            contentValues.put("count_words_per_time", count_words_per_time);
            contentValues.put("is_the_music_on", is_the_music_on);
            contentValues.put("is_online_now", is_online_now);
            mDb.insert("users", null, contentValues);
        } catch (Exception exception) {
            System.out.println("You have exception" + exception);
        }
    }

    public HashMap<String, Object> getUserInfo (int user_id) {
        try {
            @SuppressLint("DefaultLocale") Cursor cursor = mDb.rawQuery(String.format("SELECT * FROM users WHERE user_id = '%d'", user_id), null);
            cursor.moveToFirst();

            HashMap<String, Object> result =  new HashMap<String, Object>() {{
                put("user_id", cursor.getString(0));
                put("nickname", cursor.getString(1));
                put("email", cursor.getString(2));
                put("password", cursor.getString(3));
                put("last_learned_word", cursor.getString(4));
                put("count_words_per_time", cursor.getString(5));
                put("is_the_music_on", cursor.getString(6));
                put("is_online_now", cursor.getString(7));
            }};
            cursor.close();
            return result;
        } catch (Exception exception) {
            System.out.println("You have exception in getUserInfo method");
            throw exception;
        }
    }

    public int getOnlineUser() {
        try {
            Cursor cursor = mDb.rawQuery("SELECT user_id FROM users WHERE is_online_now = 1", null);
            if (cursor.getCount() == 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToFirst();
                int res = cursor.getInt(0);
                cursor.close();
                return res;
            }
        } catch (Exception exception){
            System.out.println("You have exception in getOnlineUser method");
            throw exception;
        }
    }

    public void putOnlineUserToOffline() {
        try {
            mDb.execSQL("UPDATE users SET is_online_now = 0 WHERE is_online_now = 1");
        } catch (Exception exception){
            System.out.println("You have exception in putOnlineUserToOffline method");
            throw exception;
        }
    }

    public int getUserIdByEmail(String email) {
        try {
            Cursor cursor = mDb.rawQuery("SELECT user_id FROM users WHERE email = '" + email + "'", null);
            cursor.moveToFirst();
            int result;
            if (cursor.getCount() == 0) {
                result = 0;
            } else {
                result = Integer.parseInt(cursor.getString(0));
            }
            cursor.close();
            return result;
        } catch (Exception exception){
            System.out.println("You have exception in getUserIdByEmail method");
            throw exception;
        }
    }

    public void putUserToOnline(String email) {
        try {
            mDb.execSQL("UPDATE users SET is_online_now = 1 WHERE email = '" + email + "'");
        } catch (Exception exception){
            System.out.println("You have exception in putUserToOnline method");
            throw exception;
        }
    }

    public String[][] getWords(int first_word_number, int count_of_words) {
        try {
            int last_word_number = first_word_number + count_of_words;
            Cursor cursor = mDb.rawQuery("SELECT * FROM eng_words WHERE word_id >= " +
                    first_word_number + " AND word_id < " + last_word_number, null);
            String[][] result = new String[count_of_words][];
            cursor.moveToFirst();
            int counter = 0;
            while (!cursor.isAfterLast()) {
                result[counter] = new String[] {cursor.getString(1), cursor.getString(3)};
                counter++;
                cursor.moveToNext();
            }
            cursor.close();
            return result;
        } catch (Exception exception){
            System.out.println("You have exception in getWords method");
            throw exception;
        }
    }

    public void updateCountWordsPerTime(int user_id, int new_value) {
        try {
            mDb.execSQL("UPDATE users SET count_words_per_time = '" + new_value + "' WHERE user_id = '" + user_id + "'");
        } catch (Exception exception){
            System.out.println("You have exception in updateCountWordsPerTime method");
            throw exception;
        }
    }

    public int getCountWordsPerTime(int user_id) {
        try {
            Cursor cursor = mDb.rawQuery("SELECT count_words_per_time FROM users WHERE user_id = '" + user_id + "'", null);
            cursor.moveToFirst();
            int result = Integer.parseInt(cursor.getString(0));
            cursor.close();
            return result;
        } catch (Exception exception){
            System.out.println("You have exception in getCountWordsPerTime method");
            throw exception;
        }
    }

    public int getLastLearnedWordNumber(int user_id) {
        try {
            Cursor cursor = mDb.rawQuery("SELECT last_learned_word FROM users WHERE user_id = '" + user_id + "'", null);
            cursor.moveToFirst();
            int result = cursor.getInt(0);
            cursor.close();
            return result;
        } catch (Exception exception){
            System.out.println("You have exception in getLastLearnedWordNumber method");
            throw exception;
        }
    }

    public void updateLastLearnedWord(int user_id) {
        try {
            Cursor cursor = mDb.rawQuery("SELECT last_learned_word FROM users WHERE user_id = '" + user_id + "'", null);
            cursor.moveToFirst();
            int old_last_learned = Integer.parseInt(cursor.getString(0));
            cursor.close();
            int count_word_per_time = getCountWordsPerTime(user_id);
            int new_last_learned = old_last_learned + count_word_per_time;
            mDb.execSQL("UPDATE users SET last_learned_word = '" + new_last_learned + "' WHERE user_id = '" + user_id + "'");
        } catch (Exception exception){
            System.out.println("You have exception in updateLastLearnedWord method");
            throw exception;
        }
    }
}
