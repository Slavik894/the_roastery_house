package com.example.theroasteryhouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.theroasteryhouse.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "coffee_app.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_DRINKS = "drinks";
    public static final String COLUMN_DRINK_ID = "id";
    public static final String COLUMN_DRINK_NAME = "name";
    public static final String COLUMN_DRINK_CATEGORY = "category";
    public static final String COLUMN_DRINK_PRICE_S = "price_small";
    public static final String COLUMN_DRINK_PRICE_M = "price_medium";
    public static final String COLUMN_DRINK_PRICE_L = "price_large";

    public static final String TABLE_DESSERTS = "desserts";
    public static final String COLUMN_DESSERT_ID = "id";
    public static final String COLUMN_DESSERT_NAME = "name";
    public static final String COLUMN_DESSERT_CATEGORY = "category";
    public static final String COLUMN_DESSERT_PRICE = "price";

    public static final String TABLE_ADDONS = "addons";
    public static final String COLUMN_ADDON_ID = "id";
    public static final String COLUMN_ADDON_NAME = "name";
    public static final String COLUMN_ADDON_TYPE = "type";
    public static final String COLUMN_ADDON_PRICE = "price";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
}

public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIRST_NAME + " TEXT NOT NULL," +
                COLUMN_LAST_NAME + " TEXT NOT NULL," +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                COLUMN_PASSWORD + " TEXT NOT NULL" +
                ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_DRINKS_TABLE = "CREATE TABLE " + TABLE_DRINKS + "(" +
                COLUMN_DRINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DRINK_NAME + " TEXT NOT NULL," +
                COLUMN_DRINK_CATEGORY + " TEXT NOT NULL," +
                COLUMN_DRINK_PRICE_S + " REAL," +
                COLUMN_DRINK_PRICE_M + " REAL," +
                COLUMN_DRINK_PRICE_L + " REAL" +
                ")";
        db.execSQL(CREATE_DRINKS_TABLE);

        String CREATE_DESSERTS_TABLE = "CREATE TABLE " + TABLE_DESSERTS + "(" +
                COLUMN_DESSERT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DESSERT_NAME + " TEXT NOT NULL," +
                COLUMN_DESSERT_CATEGORY + " TEXT NOT NULL," +
                COLUMN_DESSERT_PRICE + " REAL" +
                ")";
        db.execSQL(CREATE_DESSERTS_TABLE);

        String CREATE_ADDONS_TABLE = "CREATE TABLE " + TABLE_ADDONS + "(" +
                COLUMN_ADDON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ADDON_NAME + " TEXT NOT NULL," +
                COLUMN_ADDON_TYPE + " TEXT NOT NULL," +
                COLUMN_ADDON_PRICE + " REAL" +
                ")";
        db.execSQL(CREATE_ADDONS_TABLE);
}


public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRINKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESSERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDONS);
        onCreate(db);

}

public long addUser(String firstName, String lastName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
}

public boolean updateUser(int id, String firstName, String lastName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }


public boolean userExists(String email) {
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
            COLUMN_EMAIL + " = ?",
            new String[]{email}, null, null, null);

    boolean exists = cursor.getCount() > 0;
    cursor.close();
    db.close();
    return exists;

}
public boolean checkUserCredentials(String email, String password) {
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
            COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + " =?",
            new String[]{email, password}, null, null, null);

    boolean isValid = cursor.getCount() > 0;
    cursor.close();
    db.close();
    return isValid;
}

public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cusror = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + " =?",
                new String[]{email, password}, null, null, null);

        int id = -1;
        if (cusror.moveToFirst()) {
            id = cusror.getInt(0);
        }
        cusror.close();
        db.close();
        return id;
}

public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_ID + "= ?", new String[]{String.valueOf(userId)}, null, null, null);

    }

public long addDrink(String name, String category, double priceSmall, double priceMedium, double priceLarge) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put(COLUMN_DRINK_NAME, name);
    values.put(COLUMN_DRINK_CATEGORY, category);
    values.put(COLUMN_DRINK_PRICE_S, priceSmall);
    values.put(COLUMN_DRINK_PRICE_M, priceMedium);
    values.put(COLUMN_DRINK_PRICE_L, priceLarge);

    long id = db.insert(TABLE_DRINKS, null, values);
    db.close();
    return id;
}

public Cursor getDrinksByCategory(String category) {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.query(TABLE_DRINKS, null, COLUMN_DRINK_CATEGORY + " = ?", new String[]{category}, null, null, COLUMN_DRINK_NAME + " ASC");
    }

public long addDessert(String name, String category, double price) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put(COLUMN_DESSERT_NAME, name);
    values.put(COLUMN_DESSERT_CATEGORY, category);
    values.put(COLUMN_DESSERT_PRICE, price);

    long id = db.insert(TABLE_DESSERTS, null, values);
    db.close();
    return id;
}

public long addAddon(String name, String type, double price) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put(COLUMN_ADDON_NAME, name);
    values.put(COLUMN_ADDON_TYPE, type);
    values.put(COLUMN_ADDON_PRICE, price);

    long id = db.insert(TABLE_ADDONS, null, values);
    db.close();
    return id;
}

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_FIRST_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));

                users.add(new User(id, firstName, lastName, email));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return users;
    }


}

