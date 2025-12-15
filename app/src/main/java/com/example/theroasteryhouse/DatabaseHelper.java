package com.example.theroasteryhouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.theroasteryhouse.models.MenuItem;
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

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_INGREDIENT_ID = "id";
    public static final String COLUMN_INGREDIENT_NAME = "name";
    public static final String COLUMN_INGREDIENT_INFO = "info";
    public static final String COLUMN_INGREDIENT_IMAGE_URI = "image_uri";
    public static final String COLUMN_INGREDIENT_TYPE = "type";
    public static final String COLUMN_INGREDIENT_PRICE = "price";


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

    String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + "(" +
            COLUMN_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_INGREDIENT_NAME + " TEXT NOT NULL," +
            COLUMN_INGREDIENT_INFO + " TEXT," +
            COLUMN_INGREDIENT_IMAGE_URI + " TEXT," +
            COLUMN_INGREDIENT_TYPE + " TEXT," +
            COLUMN_INGREDIENT_PRICE + " REAL" +
         ")";
 db.execSQL(CREATE_INGREDIENTS_TABLE);
}


public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRINKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESSERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
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

    public boolean updateUserData(int id, String firstName, String lastName, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }
    public boolean updateUserPassword(int id, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PASSWORD, newPassword);

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

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(
                TABLE_USERS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rowsDeleted > 0;
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor drinksCursor = db.query(TABLE_DRINKS, null, null, null, null, null, null);
        if (drinksCursor.moveToFirst()) {
            do {
                int id = drinksCursor.getInt(drinksCursor.getColumnIndexOrThrow(COLUMN_DRINK_ID));
                String name = drinksCursor.getString(drinksCursor.getColumnIndexOrThrow(COLUMN_DRINK_NAME));
                items.add(new MenuItem(id, name, "drink"));
            } while (drinksCursor.moveToNext());
        }
        drinksCursor.close();

        Cursor dessertsCursor = db.query(TABLE_DESSERTS, null, null, null, null, null, null);
        if (dessertsCursor.moveToFirst()) {
            do {
                int id = dessertsCursor.getInt(dessertsCursor.getColumnIndexOrThrow(COLUMN_DESSERT_ID));
                String name = dessertsCursor.getString(dessertsCursor.getColumnIndexOrThrow(COLUMN_DESSERT_NAME));

                items.add(new MenuItem(id, name, "dessert"));
            } while (dessertsCursor.moveToNext());
        }
        dessertsCursor.close();
        db.close();

        return items;
    }

    public void deleteDrink(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DRINKS, COLUMN_DRINK_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteDessert(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DESSERTS, COLUMN_DESSERT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor getDrinkById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DRINKS, null, COLUMN_DRINK_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public Cursor getDessertById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DESSERTS, null, COLUMN_DESSERT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }


    public boolean updateDrink(int id, String name, String category, double priceS, double priceM, double priceL) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DRINK_NAME, name);
        values.put(COLUMN_DRINK_CATEGORY, category);
        values.put(COLUMN_DRINK_PRICE_S, priceS);
        values.put(COLUMN_DRINK_PRICE_M, priceM);
        values.put(COLUMN_DRINK_PRICE_L, priceL);

        int result = db.update(TABLE_DRINKS, values, COLUMN_DRINK_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean updateDessert(int id, String name, String category, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESSERT_NAME, name);
        values.put(COLUMN_DESSERT_CATEGORY, category);
        values.put(COLUMN_DESSERT_PRICE, price);

        int result = db.update(TABLE_DESSERTS, values, COLUMN_DESSERT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public long addIngredient(String name, String info, String imageUri, String type, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_NAME, name);
        values.put(COLUMN_INGREDIENT_INFO, info);
        values.put(COLUMN_INGREDIENT_IMAGE_URI, imageUri);
        values.put(COLUMN_INGREDIENT_TYPE, type);
        values.put(COLUMN_INGREDIENT_PRICE, price);
        long id = db.insert(TABLE_INGREDIENTS, null, values);
        db.close();
        return id;
    }

    public Cursor getAllIngredients() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_INGREDIENTS, null, null, null, null, null, null);
    }

    public Cursor getIngredientById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_INGREDIENTS, null, COLUMN_INGREDIENT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }
    public List<com.example.theroasteryhouse.models.Ingredient> getIngredientsByType(String type) {
        List<com.example.theroasteryhouse.models.Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_INGREDIENTS, null, COLUMN_INGREDIENT_TYPE + "=?", new String[]{type}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME));
                String info = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_INFO));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_IMAGE_URI));

                // --- Читаємо нові поля ---
                String itemType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_TYPE));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_PRICE));

                // Використовуємо оновлений конструктор
                list.add(new com.example.theroasteryhouse.models.Ingredient(id, name, info, imageUri, itemType, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean updateIngredient(int id, String name, String info, String imageUri, String type, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_NAME, name);
        values.put(COLUMN_INGREDIENT_INFO, info);
        if (imageUri != null) {
            values.put(COLUMN_INGREDIENT_IMAGE_URI, imageUri);
        }
        int result = db.update(TABLE_INGREDIENTS, values, COLUMN_INGREDIENT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean deleteIngredient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public List<MenuItem> getMenuItemsByCategory(String categoryFilter) {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (categoryFilter.equals("Desery")) {
            Cursor cursor = db.query(TABLE_DESSERTS, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DESSERT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESSERT_NAME));
                    items.add(new MenuItem(id, name, "dessert"));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        else {
            String selection = COLUMN_DRINK_CATEGORY + "=?";
            String[] selectionArgs = { categoryFilter };

            Cursor cursor = db.query(TABLE_DRINKS, null, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DRINK_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRINK_NAME));
                    items.add(new MenuItem(id, name, "drink"));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return items;
    }



}

