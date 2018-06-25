package com.example.ethan.pokerjournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// DB for Poker App
public class DatabaseHelper extends SQLiteOpenHelper {

    // DB Info
    private static final String DATABASE_NAME = "pokerJournalDatabase";
    private static final int DATABASE_VERSION = 6;

    // Game & Bank Tables
    private static final String TABLE_GAMES = "games";
    private static final String TABLE_BANK = "bank";

    // Game Fields
    private static final String GAMES_ID = "id";
    private static final String GAMES_TYPE = "type"; // Poker Variation Type
    private static final String GAMES_BLINDS = "blinds";
    private static final String GAMES_LOC = "location";
    private static final String GAMES_DATE = "date";
    private static final String GAMES_DATE2 = "date2";
    private static final String GAMES_TIME = "time";
    private static final String GAMES_BUY_IN = "buyIn";
    private static final String GAMES_CASH_OUT = "cashOut";

    // Create Game Statements
    private static final String CREATE_TABLE_GAMES = "CREATE TABLE " + TABLE_GAMES +
            "(" +
            GAMES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            GAMES_TYPE + " TEXT NOT NULL," +
            GAMES_BLINDS + " TEXT NOT NULL," +
            GAMES_LOC + " TEXT NOT NULL," +
            GAMES_DATE + " TEXT NOT NULL," +
            GAMES_DATE2 + " TEXT NOT NULL," +
            GAMES_TIME + " REAL NOT NULL," +
            GAMES_BUY_IN + " REAL NOT NULL," +
            GAMES_CASH_OUT + " REAL NOT NULL" +
            ")";

    // Update on Games Table for Adding GAMES_BLINDS Field (Ver 2)
    private static final String DATABASE_ALTER_GAME_1 = "ALTER TABLE "
            + TABLE_GAMES + " ADD COLUMN " + GAMES_BLINDS + " string;";

    // Update on Games Table for Adding GAMES_DATE2 Field (Ver 3)
    private static final String DATABASE_ALTER_GAME_2 = "ALTER TABLE "
            + TABLE_GAMES + " ADD COLUMN " + GAMES_DATE2 + " string;";

    // Bank Transaction Fields
    private static final String BANK_ID = "id";
    private static final String BANK_TYPE = "type"; // Deposit or Withdraw
    private static final String BANK_AMOUNT = "amount";
    private static final String BANK_DATE = "date";
    private static final String BANK_DATE2 = "date2";

    // Create Bank Statements
    private static final String CREATE_TABLE_BANK = "CREATE TABLE " + TABLE_BANK +
            "(" +
            BANK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            BANK_TYPE + " TEXT NOT NULL," +
            BANK_AMOUNT + " REAL NOT NULL," +
            BANK_DATE + " TEXT NOT NULL," +
            BANK_DATE2 + " TEXT NOT NULL" +
            ")";

    // Update on Bank Table for Adding BANK_DATE Field (Ver 3)
    private static final String DATABASE_ALTER_BANK_1 = "ALTER TABLE "
            + TABLE_BANK + " ADD COLUMN " + BANK_DATE + " string;";

    // Update on Bank Table for Adding BANK_DATE Field (Ver 4)
    private static final String DATABASE_ALTER_BANK_2 = "ALTER TABLE "
            + TABLE_BANK + " ADD COLUMN " + BANK_DATE2 + " string;";

    // DB Helper Run
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Run SQLite DB with Both Games and Bank Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GAMES);
        db.execSQL(CREATE_TABLE_BANK);
    }

    // Updates Version of SQLite DB Accordingly
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Update For Adding Blinds Field
        if (oldVersion > 0) {
            db.execSQL(DATABASE_ALTER_GAME_2);
        }

        // Update For Adding Bank Transaction Date Field
        if (oldVersion > 0 ) {
            db.execSQL(DATABASE_ALTER_BANK_2);
        }
    }

    // Game DB Methods
    public void createGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GAMES_TYPE, game.getType());
        values.put(GAMES_BLINDS, game.getBlinds());
        values.put(GAMES_LOC, game.getLocation());
        values.put(GAMES_DATE, game.getDate());
        values.put(GAMES_DATE2, game.getDate2());
        values.put(GAMES_TIME, game.getTime());
        values.put(GAMES_BUY_IN, game.getBuyIn());
        values.put(GAMES_CASH_OUT, game.getCashOut());

        long game_return = db.insert(TABLE_GAMES, null, values);
    }

    // Get All Games
    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<Game>();
        String selectQuery = "SELECT * FROM " + TABLE_GAMES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Game g = new Game();
                g.setId(c.getInt(c.getColumnIndex(GAMES_ID)));
                g.setType(c.getString(c.getColumnIndex(GAMES_TYPE)));
                g.setBlinds(c.getString(c.getColumnIndex(GAMES_BLINDS)));
                g.setLocation(c.getString(c.getColumnIndex(GAMES_LOC)));
                g.setDate(c.getString(c.getColumnIndex(GAMES_DATE)));
                g.setDate2(c.getString(c.getColumnIndex(GAMES_DATE2)));
                g.setTime(c.getDouble(c.getColumnIndex(GAMES_TIME)));
                g.setBuyIn(c.getDouble(c.getColumnIndex(GAMES_BUY_IN)));
                g.setCashOut(c.getDouble(c.getColumnIndex(GAMES_CASH_OUT)));

                games.add(g);
            } while (c.moveToNext());
        }

        return games;
    }

    // Gets a Specific Game
    public Game getGame(int gameId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * From " + TABLE_GAMES + " WHERE " + GAMES_ID + " = " + gameId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Game g = new Game();
        g.setId(c.getInt(c.getColumnIndex(GAMES_ID)));
        g.setType(c.getString(c.getColumnIndex(GAMES_TYPE)));
        g.setBlinds(c.getString(c.getColumnIndex(GAMES_BLINDS)));
        g.setLocation(c.getString(c.getColumnIndex(GAMES_LOC)));
        g.setDate(c.getString(c.getColumnIndex(GAMES_DATE)));
        g.setDate2(c.getString(c.getColumnIndex(GAMES_DATE2)));
        g.setTime(c.getDouble(c.getColumnIndex(GAMES_TIME)));
        g.setBuyIn(c.getDouble(c.getColumnIndex(GAMES_BUY_IN)));
        g.setCashOut(c.getDouble(c.getColumnIndex(GAMES_CASH_OUT)));

        return g;
    }

    // Edit Games in DB
    public void editGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GAMES_TYPE, game.getType());
        values.put(GAMES_BLINDS, game.getBlinds());
        values.put(GAMES_LOC, game.getLocation());
        values.put(GAMES_DATE, game.getDate());
        values.put(GAMES_DATE2, game.getDate2());
        values.put(GAMES_TIME, game.getTime());
        values.put(GAMES_BUY_IN, game.getBuyIn());
        values.put(GAMES_CASH_OUT, game.getCashOut());
        values.put(GAMES_ID, game.getId());

        long game_return = db.replace(TABLE_GAMES, GAMES_ID, values);
    }

    // Delete All Games from DB
    public void clearGames() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAMES, null, null);
    }

    // Deletes a Specific Game
    public void deleteGame(int gameId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAMES, GAMES_ID + " = ?", new String[] {String.valueOf(gameId)});
    }

    // Bank DB Methods
    public void createBank(Bank bank) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BANK_AMOUNT, bank.getAmount());
        values.put(BANK_TYPE, bank.getType());
        values.put(BANK_DATE, bank.getDate());
        values.put(BANK_DATE2, bank.getDate2());

        long bank_return = db.insert(TABLE_BANK, null, values);
    }

    // Get All Bank Transactions
    public List<Bank> getAllBanks() {
        List<Bank> banks = new ArrayList<Bank>();
        String selectQuery = "SELECT * FROM " + TABLE_BANK;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Bank b = new Bank();
                b.setAmount(c.getDouble(c.getColumnIndex(BANK_AMOUNT)));
                b.setType(c.getString(c.getColumnIndex(BANK_TYPE)));
                b.setId(c.getInt(c.getColumnIndex(BANK_ID)));
                b.setDate(c.getString(c.getColumnIndex(BANK_DATE)));
                b.setDate2(c.getString(c.getColumnIndex(BANK_DATE2)));
                banks.add(b);
            } while (c.moveToNext());
        }
        return banks;
    }

    // Gets a Specific Bank Transaction
    public Bank getBank(int bankId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * From " + TABLE_BANK + " WHERE " + BANK_ID + " = " + bankId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Bank b = new Bank();
        b.setAmount(c.getDouble(c.getColumnIndex(BANK_AMOUNT)));
        b.setType(c.getString(c.getColumnIndex(BANK_TYPE)));
        b.setId(c.getInt(c.getColumnIndex(BANK_ID)));
        b.setDate(c.getString(c.getColumnIndex(BANK_DATE)));
        b.setDate2(c.getString(c.getColumnIndex(BANK_DATE2)));

        return b;
    }

    // Edit Bank Transaction in DB
    public void editBank(Bank bank) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BANK_AMOUNT, bank.getAmount());
        values.put(BANK_TYPE, bank.getType());
        values.put(BANK_DATE, bank.getDate());
        values.put(BANK_DATE2, bank.getDate2());
        values.put(BANK_ID, bank.getId());

        long bank_return = db.replace(TABLE_BANK, BANK_ID, values);
    }

    // Deletes All Bank Transactions from DB
    public void clearBank() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BANK, null, null);
    }

    // Deletes a Specific Bank Transaction
    public void deleteBank(int bankId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BANK, BANK_ID + " = ?", new String[] {String.valueOf(bankId)});
    }
}
