
package fpt.edu.vn.stickershop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.stickershop.models.Product;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StickerShop.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_LUCKY_BOX = "lucky_box";
    public static final String TABLE_WISHLIST = "wishlist";

    // Users table columns
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";

    // Products table columns
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_IMAGE = "image_url";
    public static final String COLUMN_PRODUCT_TYPE = "type";

    // Cart table columns
    public static final String COLUMN_CART_ID = "cart_id";
    public static final String COLUMN_USER_ID_FK = "user_id";
    public static final String COLUMN_PRODUCT_ID_FK = "product_id";
    public static final String COLUMN_QUANTITY = "quantity";

    // Orders table columns
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_TOTAL = "total";
    public static final String COLUMN_ORDER_ADDRESS = "address";

    // Lucky Box table columns
    public static final String COLUMN_LUCKY_BOX_ID = "lucky_box_id";
    public static final String COLUMN_LUCKY_BOX_NAME = "box_name";
    public static final String COLUMN_LUCKY_BOX_PRICE = "price";
    public static final String COLUMN_LUCKY_BOX_ITEMS = "items";

    // Wishlist table columns
    public static final String COLUMN_WISHLIST_ID = "wishlist_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NAME + " TEXT)";
        db.execSQL(createUsersTable);

        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_PRICE + " REAL, " +
                COLUMN_PRODUCT_IMAGE + " TEXT, " +
                COLUMN_PRODUCT_TYPE + " TEXT)";
        db.execSQL(createProductsTable);

        String createCartTable = "CREATE TABLE " + TABLE_CART + " (" +
                COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_PRODUCT_ID_FK + " INTEGER, " +
                COLUMN_QUANTITY + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_PRODUCT_ID_FK + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "))";
        db.execSQL(createCartTable);

        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_ORDER_STATUS + " TEXT, " +
                COLUMN_ORDER_TOTAL + " REAL, " +
                COLUMN_ORDER_ADDRESS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createOrdersTable);

        String createLuckyBoxTable = "CREATE TABLE " + TABLE_LUCKY_BOX + " (" +
                COLUMN_LUCKY_BOX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LUCKY_BOX_NAME + " TEXT, " +
                COLUMN_LUCKY_BOX_PRICE + " REAL, " +
                COLUMN_LUCKY_BOX_ITEMS + " TEXT)";
        db.execSQL(createLuckyBoxTable);

        String createWishlistTable = "CREATE TABLE " + TABLE_WISHLIST + " (" +
                COLUMN_WISHLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_PRODUCT_ID_FK + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_PRODUCT_ID_FK + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "))";
        db.execSQL(createWishlistTable);

        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUCKY_BOX);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Insert sample users
        ContentValues user1 = new ContentValues();
        user1.put(COLUMN_EMAIL, "user1@example.com");
        user1.put(COLUMN_PASSWORD, "password123");
        user1.put(COLUMN_NAME, "John Doe");
        db.insert(TABLE_USERS, null, user1);

        ContentValues user2 = new ContentValues();
        user2.put(COLUMN_EMAIL, "user2@example.com");
        user2.put(COLUMN_PASSWORD, "pass456");
        user2.put(COLUMN_NAME, "Jane Smith");
        db.insert(TABLE_USERS, null, user2);

        ContentValues user3 = new ContentValues();
        user3.put(COLUMN_EMAIL, "admin@example.com");
        user3.put(COLUMN_PASSWORD, "admin789");
        user3.put(COLUMN_NAME, "Admin User");
        db.insert(TABLE_USERS, null, user3);

        // Insert sample products
        ContentValues product1 = new ContentValues();
        product1.put(COLUMN_PRODUCT_NAME, "Cute Cat Sticker");
        product1.put(COLUMN_PRODUCT_PRICE, 2.99);
        product1.put(COLUMN_PRODUCT_IMAGE, "https://mystickermania.com/cdn/stickers/cute-cats/cute-cat-want-play-512x512.png");
        product1.put(COLUMN_PRODUCT_TYPE, "sticker");
        long product1Id = db.insert(TABLE_PRODUCTS, null, product1);

        ContentValues product2 = new ContentValues();
        product2.put(COLUMN_PRODUCT_NAME, "Funny Dog Sticker");
        product2.put(COLUMN_PRODUCT_PRICE, 2.49);
        product2.put(COLUMN_PRODUCT_IMAGE, "https://i.pinimg.com/474x/b1/22/90/b12290f6533dacdadfcecfa5d955cd07.jpg");
        product2.put(COLUMN_PRODUCT_TYPE, "sticker");
        long product2Id = db.insert(TABLE_PRODUCTS, null, product2);

        ContentValues product3 = new ContentValues();
        product3.put(COLUMN_PRODUCT_NAME, "Anime Character Sticker");
        product3.put(COLUMN_PRODUCT_PRICE, 3.99);
        product3.put(COLUMN_PRODUCT_IMAGE, "https://i.pinimg.com/736x/1d/d8/bc/1dd8bca12fd95c54877b4c8e7fcb2ffa.jpg");
        product3.put(COLUMN_PRODUCT_TYPE, "sticker");
        long product3Id = db.insert(TABLE_PRODUCTS, null, product3);

        ContentValues product4 = new ContentValues();
        product4.put(COLUMN_PRODUCT_NAME, "Rainbow Unicorn Sticker");
        product4.put(COLUMN_PRODUCT_PRICE, 2.99);
        product4.put(COLUMN_PRODUCT_IMAGE, "https://i.etsystatic.com/33278253/r/il/19a507/5652069310/il_fullxfull.5652069310_c9y9.jpg");
        product4.put(COLUMN_PRODUCT_TYPE, "sticker");
        long product4Id = db.insert(TABLE_PRODUCTS, null, product4);

        ContentValues product5 = new ContentValues();
        product5.put(COLUMN_PRODUCT_NAME, "Kawaii Food Sticker");
        product5.put(COLUMN_PRODUCT_PRICE, 1.49);
        product5.put(COLUMN_PRODUCT_IMAGE, "https://img.freepik.com/free-vector/hand-drawn-funny-sticker-set_23-2148382132.jpg?semt=ais_hybrid&w=740");
        product5.put(COLUMN_PRODUCT_TYPE, "sticker");
        long product5Id = db.insert(TABLE_PRODUCTS, null, product5);

        // Insert sample cart items (user 1)
        ContentValues cart1 = new ContentValues();
        cart1.put(COLUMN_USER_ID_FK, 1);
        cart1.put(COLUMN_PRODUCT_ID_FK, product1Id);
        cart1.put(COLUMN_QUANTITY, 2);
        db.insert(TABLE_CART, null, cart1);

        ContentValues cart2 = new ContentValues();
        cart2.put(COLUMN_USER_ID_FK, 1);
        cart2.put(COLUMN_PRODUCT_ID_FK, product3Id);
        cart2.put(COLUMN_QUANTITY, 1);
        db.insert(TABLE_CART, null, cart2);

        // Insert sample orders (user 1 and user 2)
        ContentValues order1 = new ContentValues();
        order1.put(COLUMN_USER_ID_FK, 1);
        order1.put(COLUMN_ORDER_STATUS, "Pending");
        order1.put(COLUMN_ORDER_TOTAL, 5.47);
        order1.put(COLUMN_ORDER_ADDRESS, "123 Example Street, City");
        db.insert(TABLE_ORDERS, null, order1);

        ContentValues order2 = new ContentValues();
        order2.put(COLUMN_USER_ID_FK, 2);
        order2.put(COLUMN_ORDER_STATUS, "Delivered");
        order2.put(COLUMN_ORDER_TOTAL, 7.98);
        order2.put(COLUMN_ORDER_ADDRESS, "456 Sample Road, Town");
        db.insert(TABLE_ORDERS, null, order2);

        // Insert sample lucky box
        ContentValues luckyBox1 = new ContentValues();
        luckyBox1.put(COLUMN_LUCKY_BOX_NAME, "Mystery Sticker Box");
        luckyBox1.put(COLUMN_LUCKY_BOX_PRICE, 9.99);
        luckyBox1.put(COLUMN_LUCKY_BOX_ITEMS, "[\"Cute Cat Sticker\",\"Funny Dog Sticker\",\"Rare Unicorn Sticker\"]");
        db.insert(TABLE_LUCKY_BOX, null, luckyBox1);

        ContentValues luckyBox2 = new ContentValues();
        luckyBox2.put(COLUMN_LUCKY_BOX_NAME, "Anime Surprise Box");
        luckyBox2.put(COLUMN_LUCKY_BOX_PRICE, 12.99);
        luckyBox2.put(COLUMN_LUCKY_BOX_ITEMS, "[\"Anime Character Sticker\",\"Kawaii Food Sticker\"]");
        db.insert(TABLE_LUCKY_BOX, null, luckyBox2);

        // Insert sample wishlist items (user 2)
        ContentValues wishlist1 = new ContentValues();
        wishlist1.put(COLUMN_USER_ID_FK, 2);
        wishlist1.put(COLUMN_PRODUCT_ID_FK, product4Id);
        db.insert(TABLE_WISHLIST, null, wishlist1);

        ContentValues wishlist2 = new ContentValues();
        wishlist2.put(COLUMN_USER_ID_FK, 2);
        wishlist2.put(COLUMN_PRODUCT_ID_FK, product5Id);
        db.insert(TABLE_WISHLIST, null, wishlist2);
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean addUser(String email, String password, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NAME, name);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS,
                null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_TYPE));

                productList.add(new Product(id, name, price, imageUrl, type));
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return productList;
    }
}