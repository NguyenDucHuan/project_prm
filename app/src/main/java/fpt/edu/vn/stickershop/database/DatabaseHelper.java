
package fpt.edu.vn.stickershop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.models.CartItem;
import fpt.edu.vn.stickershop.models.InventoryItem;
import fpt.edu.vn.stickershop.models.LuckyWheel;
import fpt.edu.vn.stickershop.models.OrderDetails;
import fpt.edu.vn.stickershop.models.OrderItem;
import fpt.edu.vn.stickershop.models.Product;
import fpt.edu.vn.stickershop.models.WheelItem;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StickerShop.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String TABLE_LUCKY_WHEELS = "lucky_wheels";
    public static final String TABLE_WHEEL_ITEMS = "wheel_items";
    public static final String TABLE_USER_INVENTORY = "user_inventory";

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
    public static final String COLUMN_ORDER_TIMESTAMP = "order_timestamp";
    public static final String COLUMN_ORDER_ITEM_COUNT = "item_count";

    // Order Items table
    public static final String COLUMN_ORDER_ITEM_ID = "order_item_id";
    public static final String COLUMN_ORDER_ID_FK = "order_id_fk";
    public static final String COLUMN_ORDER_ITEM_PRODUCT_ID = "product_id";
    public static final String COLUMN_ORDER_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ORDER_ITEM_PRICE = "unit_price";
    public static final String COLUMN_ORDER_ITEM_TOTAL = "total_price";

    // Lucky Wheel tables
    public static final String COLUMN_WHEEL_ID = "wheel_id";
    public static final String COLUMN_WHEEL_NAME = "wheel_name";
    public static final String COLUMN_WHEEL_COST = "wheel_cost";
    public static final String COLUMN_WHEEL_DESCRIPTION = "wheel_description";

    public static final String COLUMN_WHEEL_ITEM_ID = "wheel_item_id";
    public static final String COLUMN_WHEEL_ID_FK = "wheel_id_fk";
    public static final String COLUMN_ITEM_PRODUCT_ID = "product_id";
    public static final String COLUMN_ITEM_PROBABILITY = "probability";
    public static final String COLUMN_ITEM_QUANTITY = "quantity";

    public static final String COLUMN_INVENTORY_ID = "inventory_id";
    public static final String COLUMN_INVENTORY_USER_ID = "user_id";
    public static final String COLUMN_INVENTORY_PRODUCT_ID = "product_id";
    public static final String COLUMN_INVENTORY_QUANTITY = "quantity";
    public static final String COLUMN_INVENTORY_DATE_OBTAINED = "date_obtained";

    // Orders table columns
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_TOTAL = "total";
    public static final String COLUMN_ORDER_ADDRESS = "address";

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

        // Cập nhật bảng orders với các cột mới
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_ORDER_STATUS + " TEXT, " +
                COLUMN_ORDER_TOTAL + " REAL, " +
                COLUMN_ORDER_ADDRESS + " TEXT, " +
                COLUMN_ORDER_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_ORDER_ITEM_COUNT + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
                ")";
        db.execSQL(createOrdersTable);

        // Tạo bảng order_items
        String createOrderItemsTable = "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                COLUMN_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_ID_FK + " INTEGER, " +
                COLUMN_ORDER_ITEM_PRODUCT_ID + " INTEGER, " +
                COLUMN_ORDER_ITEM_QUANTITY + " INTEGER, " +
                COLUMN_ORDER_ITEM_PRICE + " REAL, " +
                COLUMN_ORDER_ITEM_TOTAL + " REAL, " +
                "FOREIGN KEY(" + COLUMN_ORDER_ID_FK + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ORDER_ITEM_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")" +
                ")";
        db.execSQL(createOrderItemsTable);
        // Tạo bảng lucky_wheels
        String createWheelsTable = "CREATE TABLE " + TABLE_LUCKY_WHEELS + " (" +
                COLUMN_WHEEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WHEEL_NAME + " TEXT NOT NULL, " +
                COLUMN_WHEEL_COST + " REAL NOT NULL, " +
                COLUMN_WHEEL_DESCRIPTION + " TEXT" +
                ")";
        db.execSQL(createWheelsTable);
        // Tạo bảng wheel_items
        String createWheelItemsTable = "CREATE TABLE " + TABLE_WHEEL_ITEMS + " (" +
                COLUMN_WHEEL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WHEEL_ID_FK + " INTEGER, " +
                COLUMN_ITEM_PRODUCT_ID + " INTEGER, " +
                COLUMN_ITEM_PROBABILITY + " REAL, " +
                COLUMN_ITEM_QUANTITY + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_WHEEL_ID_FK + ") REFERENCES " + TABLE_LUCKY_WHEELS + "(" + COLUMN_WHEEL_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ITEM_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")" +
                ")";
        db.execSQL(createWheelItemsTable);
        // Tạo bảng user_inventory
        String createInventoryTable = "CREATE TABLE " + TABLE_USER_INVENTORY + " (" +
                COLUMN_INVENTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INVENTORY_USER_ID + " INTEGER, " +
                COLUMN_INVENTORY_PRODUCT_ID + " INTEGER, " +
                COLUMN_INVENTORY_QUANTITY + " INTEGER, " +
                COLUMN_INVENTORY_DATE_OBTAINED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_INVENTORY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_INVENTORY_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")" +
                ")";
        db.execSQL(createInventoryTable);

        insertSampleLuckyWheelData(db);
        insertSampleData(db);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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

    }

    private void insertSampleLuckyWheelData(SQLiteDatabase db) {
        // Insert sample lucky wheel
        ContentValues wheelValues = new ContentValues();
        wheelValues.put(COLUMN_WHEEL_NAME, "Mystery Sticker Wheel");
        wheelValues.put(COLUMN_WHEEL_COST, 5.0);
        wheelValues.put(COLUMN_WHEEL_DESCRIPTION, "Spin to win amazing stickers!");
        long wheelId = db.insert(TABLE_LUCKY_WHEELS, null, wheelValues);

        // Insert wheel items with different probabilities
        insertWheelItem(db, wheelId, 1, 40.0, 1); // Common item - 40%
        insertWheelItem(db, wheelId, 2, 30.0, 1); // Common item - 30%
        insertWheelItem(db, wheelId, 3, 20.0, 1); // Uncommon item - 20%
        insertWheelItem(db, wheelId, 4, 8.0, 1);  // Rare item - 8%
        insertWheelItem(db, wheelId, 5, 2.0, 1);  // Super rare item - 2%
    }

    private void insertWheelItem(SQLiteDatabase db, long wheelId, int productId, double probability, int quantity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WHEEL_ID_FK, wheelId);
        values.put(COLUMN_ITEM_PRODUCT_ID, productId);
        values.put(COLUMN_ITEM_PROBABILITY, probability);
        values.put(COLUMN_ITEM_QUANTITY, quantity);
        db.insert(TABLE_WHEEL_ITEMS, null, values);
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

    public void addToCart(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            // Check if product already exists in cart
            Cursor cursor = db.query(TABLE_CART,
                    new String[]{COLUMN_QUANTITY},
                    COLUMN_USER_ID_FK + " = ? AND " + COLUMN_PRODUCT_ID_FK + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)},
                    null, null, null);

            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID_FK, userId);
            values.put(COLUMN_PRODUCT_ID_FK, productId);
            
            if (cursor.moveToFirst()) {
                // Update quantity if product exists
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                values.put(COLUMN_QUANTITY, currentQuantity + quantity);
                db.update(TABLE_CART, values,
                        COLUMN_USER_ID_FK + " = ? AND " + COLUMN_PRODUCT_ID_FK + " = ?",
                        new String[]{String.valueOf(userId), String.valueOf(productId)});
            } else {
                // Insert new cart item if product doesn't exist
                values.put(COLUMN_QUANTITY, quantity);
                db.insert(TABLE_CART, null, values);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding to cart", e);
        }
    }
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c." + COLUMN_CART_ID + ", " +
                "c." + COLUMN_QUANTITY + ", " +
                "p." + COLUMN_PRODUCT_ID + ", " +
                "p." + COLUMN_PRODUCT_NAME + ", " +
                "p." + COLUMN_PRODUCT_PRICE + ", " +
                "p." + COLUMN_PRODUCT_IMAGE + ", " +
                "p." + COLUMN_PRODUCT_TYPE +
                " FROM " + TABLE_CART + " c " +
                "INNER JOIN " + TABLE_PRODUCTS + " p ON c." + COLUMN_PRODUCT_ID_FK +
                " = p." + COLUMN_PRODUCT_ID +
                " WHERE c." + COLUMN_USER_ID_FK + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_ID));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_TYPE));

                cartItems.add(new CartItem(cartId, productId, name, price, imageUrl, type, quantity));
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return cartItems;
    }

    public void updateCartQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQuantity);

        db.update(TABLE_CART, values,
                COLUMN_PRODUCT_ID_FK + " = ?",
                new String[]{String.valueOf(productId)});
        db.close();
    }

    public void removeFromCart(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART,
                COLUMN_PRODUCT_ID_FK + " = ?",
                new String[]{String.valueOf(productId)});
        db.close();
    }
    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_CART, 
                     COLUMN_USER_ID_FK + " = ?",
                     new String[]{String.valueOf(userId)});
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error clearing cart", e);
        }
    }
    public long saveOrderWithItems(int userId, String address, List<CartItem> cartItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        long orderId = -1;

        try {
            db.beginTransaction();

            // Calculate totals
            double totalAmount = 0;
            int totalItems = 0;
            for (CartItem item : cartItems) {
                totalAmount += item.getTotalPrice();
                totalItems += item.getQuantity();
            }

            // Insert order
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_USER_ID_FK, userId);
            orderValues.put(COLUMN_ORDER_STATUS, "Pending");
            orderValues.put(COLUMN_ORDER_TOTAL, totalAmount);
            orderValues.put(COLUMN_ORDER_ADDRESS, address);
            orderValues.put(COLUMN_ORDER_ITEM_COUNT, totalItems);

            orderId = db.insert(TABLE_ORDERS, null, orderValues);

            if (orderId != -1) {
                // Insert order items
                for (CartItem item : cartItems) {
                    ContentValues itemValues = new ContentValues();
                    itemValues.put(COLUMN_ORDER_ID_FK, orderId);
                    itemValues.put(COLUMN_ORDER_ITEM_PRODUCT_ID, item.getProductId());
                    itemValues.put(COLUMN_ORDER_ITEM_QUANTITY, item.getQuantity());
                    itemValues.put(COLUMN_ORDER_ITEM_PRICE, item.getProductPrice());
                    itemValues.put(COLUMN_ORDER_ITEM_TOTAL, item.getTotalPrice());

                    db.insert(TABLE_ORDER_ITEMS, null, itemValues);
                }

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error saving order with items", e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return orderId;
    }

    public OrderDetails getOrderDetails(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        OrderDetails orderDetails = null;

        try {
            // Get order info
            String orderQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDER_ID + " = ?";
            Cursor orderCursor = db.rawQuery(orderQuery, new String[]{String.valueOf(orderId)});

            if (orderCursor.moveToFirst()) {
                String status = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
                double total = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL));
                String address = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_ADDRESS));

                String timestamp = "N/A";
                int itemCount = 0;
                try {
                    timestamp = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_TIMESTAMP));
                } catch (Exception e) {
                    // Column doesn't exist, use default
                }
                try {
                    itemCount = orderCursor.getInt(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_ITEM_COUNT));
                } catch (Exception e) {
                    // Column doesn't exist, use default
                }

                // Mock order items với hình ảnh
                List<OrderItem> orderItems = getMockOrderItems(orderId);

                orderDetails = new OrderDetails((int)orderId, status, total, address, timestamp, itemCount, orderItems);
            }

            orderCursor.close();

        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error getting order details", e);
        } finally {
            db.close();
        }

        return orderDetails;
    }

    private List<OrderItem> getMockOrderItems(long orderId) {
        List<OrderItem> items = new ArrayList<>();

        // Mock data với hình ảnh từ drawable resources
        String[] productNames = {
                "Cute Cat Sticker Pack",
                "Funny Dog Stickers",
                "Emoji Set Premium",
                "Nature Collection",
                "Space Adventure Pack"
        };

        String[] productImages = {
                String.valueOf(R.drawable.ic_product_placeholder), // Bạn có thể thay bằng hình thật
                "https://example.com/dog_stickers.jpg",
                String.valueOf(R.drawable.ic_product_placeholder),
                "https://example.com/nature_stickers.jpg",
                String.valueOf(R.drawable.ic_product_placeholder)
        };

        double[] unitPrices = {2.99, 1.99, 4.99, 3.49, 5.99};
        int[] quantities = {2, 1, 1, 3, 1};

        // Tạo 2-4 items cho mỗi order
        int itemCount = (int) (Math.random() * 3) + 2; // 2-4 items

        for (int i = 0; i < itemCount && i < productNames.length; i++) {
            int productId = (int) (orderId * 10 + i + 1);
            String productName = productNames[i];
            String productImage = productImages[i];
            int quantity = quantities[i];
            double unitPrice = unitPrices[i];
            double totalPrice = unitPrice * quantity;

            items.add(new OrderItem(productId, productName, productImage, quantity, unitPrice, totalPrice));
        }

        return items;
    }

    public List<LuckyWheel> getAllLuckyWheels() {
        List<LuckyWheel> wheels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LUCKY_WHEELS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WHEEL_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WHEEL_NAME));
            double cost = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WHEEL_COST));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WHEEL_DESCRIPTION));

            List<WheelItem> items = getWheelItems(id);
            wheels.add(new LuckyWheel(id, name, cost, description, items));
        }

        cursor.close();
        db.close();
        return wheels;
    }
    public List<InventoryItem> getUserInventory(int userId) {
        List<InventoryItem> inventoryItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT i.*, p." + COLUMN_PRODUCT_NAME + ", p." + COLUMN_PRODUCT_IMAGE +
                " FROM " + TABLE_USER_INVENTORY + " i " +
                "JOIN " + TABLE_PRODUCTS + " p ON i." + COLUMN_INVENTORY_PRODUCT_ID + " = p." + COLUMN_PRODUCT_ID +
                " WHERE i." + COLUMN_INVENTORY_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_ID));
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_PRODUCT_ID));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_QUANTITY));
            String dateObtained = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_DATE_OBTAINED));
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
            String productImage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));

            inventoryItems.add(new InventoryItem(id, userId, productId, productName, productImage, quantity, dateObtained));
        }

        cursor.close();
        db.close();
        return inventoryItems;
    }
    public List<WheelItem> getWheelItems(int wheelId) {
        List<WheelItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT wi.*, p." + COLUMN_PRODUCT_NAME + ", p." + COLUMN_PRODUCT_IMAGE +
                " FROM " + TABLE_WHEEL_ITEMS + " wi " +
                "JOIN " + TABLE_PRODUCTS + " p ON wi." + COLUMN_ITEM_PRODUCT_ID + " = p." + COLUMN_PRODUCT_ID +
                " WHERE wi." + COLUMN_WHEEL_ID_FK + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(wheelId)});

        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WHEEL_ITEM_ID));
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_PRODUCT_ID));
            double probability = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ITEM_PROBABILITY));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY));
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
            String productImage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));

            items.add(new WheelItem(itemId, wheelId, productId, productName, productImage, probability, quantity));
        }

        cursor.close();
        db.close();
        return items;
    }

    public boolean addToInventory(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if item already exists in inventory
        Cursor cursor = db.query(TABLE_USER_INVENTORY,
                new String[]{COLUMN_INVENTORY_QUANTITY},
                COLUMN_INVENTORY_USER_ID + "=? AND " + COLUMN_INVENTORY_PRODUCT_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(productId)},
                null, null, null);

        ContentValues values = new ContentValues();
        boolean success;

        if (cursor.moveToFirst()) {
            // Update existing item
            int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_QUANTITY));
            values.put(COLUMN_INVENTORY_QUANTITY, currentQuantity + quantity);

            int rowsUpdated = db.update(TABLE_USER_INVENTORY, values,
                    COLUMN_INVENTORY_USER_ID + "=? AND " + COLUMN_INVENTORY_PRODUCT_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});
            success = rowsUpdated > 0;
        } else {
            // Insert new item
            values.put(COLUMN_INVENTORY_USER_ID, userId);
            values.put(COLUMN_INVENTORY_PRODUCT_ID, productId);
            values.put(COLUMN_INVENTORY_QUANTITY, quantity);

            long result = db.insert(TABLE_USER_INVENTORY, null, values);
            success = result != -1;
        }

        cursor.close();
        db.close();
        return success;
    }
}