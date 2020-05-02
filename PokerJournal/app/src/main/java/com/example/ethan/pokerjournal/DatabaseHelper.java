package com.example.ethan.pokerjournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// DB for Poker App
public class DatabaseHelper extends SQLiteOpenHelper
{

    // DB Info
    private static final String DATABASE_NAME = "pokerJournalDatabase";
    private static final int DATABASE_VERSION = 9;

    // Session & Bank Tables
    private static final String TABLE_SESSIONS = "sessions";
    private static final String TABLE_BANK = "bank";

    // Session Fields
    private static final String SESSIONS_ID = "id";
    private static final String SESSIONS_TYPE = "type"; // Poker Variation Type
    private static final String SESSIONS_BLINDS = "blinds";
    private static final String SESSIONS_LOC = "location";
    private static final String SESSIONS_DATE = "date";
    private static final String SESSIONS_TIME = "time";
    private static final String SESSIONS_BUY_IN = "buyIn";
    private static final String SESSIONS_CASH_OUT = "cashOut";

    // Create Session Statements
    private static final String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSIONS + "(" + SESSIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + SESSIONS_TYPE + " TEXT NOT NULL," + SESSIONS_BLINDS + " TEXT NOT NULL," + SESSIONS_LOC + " TEXT NOT NULL," + SESSIONS_DATE + " TEXT NOT NULL," + SESSIONS_TIME + " REAL NOT NULL," + SESSIONS_BUY_IN + " REAL NOT NULL," + SESSIONS_CASH_OUT + " REAL NOT NULL" + ")";

    // Update on Sessions Table for Adding SESSIONS_BLINDS Field (Ver 2)
    private static final String DATABASE_ALTER_SESSION_1 = "ALTER TABLE " + TABLE_SESSIONS + " ADD COLUMN " + SESSIONS_BLINDS + " string;";

    // Bank Transaction Fields
    private static final String BANK_ID = "id";
    private static final String BANK_TYPE = "type"; // Deposit or Withdraw
    private static final String BANK_AMOUNT = "amount";
    private static final String BANK_DATE = "date";

    // Create Bank Statements
    private static final String CREATE_TABLE_BANK = "CREATE TABLE " + TABLE_BANK + "(" + BANK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + BANK_TYPE + " TEXT NOT NULL," + BANK_AMOUNT + " REAL NOT NULL," + BANK_DATE + " TEXT NOT NULL)";

    // Update on Bank Table for Adding BANK_DATE Field (Ver 3)
    private static final String DATABASE_ALTER_BANK_1 = "ALTER TABLE " + TABLE_BANK + " ADD COLUMN " + BANK_DATE + " string;";

    // DB Helper Run
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Run SQLite DB with Both Sessions and Bank Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_SESSIONS);
        db.execSQL(CREATE_TABLE_BANK);
    }

    // Updates Version of SQLite DB Accordingly
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        /*// Update For Adding Blinds Field
        if (oldVersion > 1) {
            db.execSQL(DATABASE_ALTER_SESSION_2);
        }

        // Update For Adding Bank Transaction Date Field
        if (oldVersion > 1) {
            db.execSQL(DATABASE_ALTER_BANK_2);
        }*/
    }

    // Session DB Methods
    public void createSession(Session session)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SESSIONS_TYPE, session.getType());
        values.put(SESSIONS_BLINDS, session.getBlinds());
        values.put(SESSIONS_LOC, session.getLocation());
        values.put(SESSIONS_DATE, session.getDate());
        values.put(SESSIONS_TIME, session.getTime());
        values.put(SESSIONS_BUY_IN, session.getBuyIn());
        values.put(SESSIONS_CASH_OUT, session.getCashOut());

        long session_return = db.insert(TABLE_SESSIONS, null, values);
    }

    // Get All Sessions
    public List<Session> getAllSessions()
    {
        List<Session> sessions = new ArrayList<Session>();
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst())
        {
            do
            {
                Session g = new Session();
                g.setId(c.getInt(c.getColumnIndex(SESSIONS_ID)));
                g.setType(c.getString(c.getColumnIndex(SESSIONS_TYPE)));
                g.setBlinds(c.getString(c.getColumnIndex(SESSIONS_BLINDS)));
                g.setLocation(c.getString(c.getColumnIndex(SESSIONS_LOC)));
                g.setDate(c.getString(c.getColumnIndex(SESSIONS_DATE)));
                g.setTime(c.getInt(c.getColumnIndex(SESSIONS_TIME)));
                g.setBuyIn(c.getInt(c.getColumnIndex(SESSIONS_BUY_IN)));
                g.setCashOut(c.getInt(c.getColumnIndex(SESSIONS_CASH_OUT)));

                sessions.add(g);
            } while (c.moveToNext());
        }

        return sessions;
    }

    // Gets a Specific Session
    public Session getSession(int sessionId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * From " + TABLE_SESSIONS + " WHERE " + SESSIONS_ID + " = " + sessionId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            c.moveToFirst();
        }

        Session g = new Session();
        g.setId(c.getInt(c.getColumnIndex(SESSIONS_ID)));
        g.setType(c.getString(c.getColumnIndex(SESSIONS_TYPE)));
        g.setBlinds(c.getString(c.getColumnIndex(SESSIONS_BLINDS)));
        g.setLocation(c.getString(c.getColumnIndex(SESSIONS_LOC)));
        g.setDate(c.getString(c.getColumnIndex(SESSIONS_DATE)));
        g.setTime(c.getInt(c.getColumnIndex(SESSIONS_TIME)));
        g.setBuyIn(c.getInt(c.getColumnIndex(SESSIONS_BUY_IN)));
        g.setCashOut(c.getInt(c.getColumnIndex(SESSIONS_CASH_OUT)));

        return g;
    }

    // Edit Sessions in DB
    public void editSession(Session session)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SESSIONS_TYPE, session.getType());
        values.put(SESSIONS_BLINDS, session.getBlinds());
        values.put(SESSIONS_LOC, session.getLocation());
        values.put(SESSIONS_DATE, session.getDate());
        values.put(SESSIONS_TIME, session.getTime());
        values.put(SESSIONS_BUY_IN, session.getBuyIn());
        values.put(SESSIONS_CASH_OUT, session.getCashOut());
        values.put(SESSIONS_ID, session.getId());

        long session_return = db.replace(TABLE_SESSIONS, SESSIONS_ID, values);
    }

    // Delete All Sessions from DB
    public void clearSessions()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SESSIONS, null, null);
    }

    // Deletes a Specific Session
    public void deleteSession(int sessionId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SESSIONS, SESSIONS_ID + " = ?", new String[]{String.valueOf(sessionId)});
    }

    // Bank DB Methods
    public void createBank(Bank bank)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BANK_AMOUNT, bank.getAmount());
        values.put(BANK_TYPE, bank.getType());
        values.put(BANK_DATE, bank.getDate());

        long bank_return = db.insert(TABLE_BANK, null, values);
    }

    // Get All Bank Transactions
    public List<Bank> getAllBanks()
    {
        List<Bank> banks = new ArrayList<Bank>();
        String selectQuery = "SELECT * FROM " + TABLE_BANK;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst())
        {
            do
            {
                Bank b = new Bank();
                b.setAmount(c.getInt(c.getColumnIndex(BANK_AMOUNT)));
                b.setType(c.getString(c.getColumnIndex(BANK_TYPE)));
                b.setId(c.getInt(c.getColumnIndex(BANK_ID)));
                b.setDate(c.getString(c.getColumnIndex(BANK_DATE)));
                banks.add(b);
            } while (c.moveToNext());
        }
        return banks;
    }

    // Gets a Specific Bank Transaction
    public Bank getBank(int bankId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * From " + TABLE_BANK + " WHERE " + BANK_ID + " = " + bankId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            c.moveToFirst();
        }

        Bank b = new Bank();
        b.setAmount(c.getInt(c.getColumnIndex(BANK_AMOUNT)));
        b.setType(c.getString(c.getColumnIndex(BANK_TYPE)));
        b.setId(c.getInt(c.getColumnIndex(BANK_ID)));
        b.setDate(c.getString(c.getColumnIndex(BANK_DATE)));

        return b;
    }

    // Edit Bank Transaction in DB
    public void editBank(Bank bank)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BANK_AMOUNT, bank.getAmount());
        values.put(BANK_TYPE, bank.getType());
        values.put(BANK_DATE, bank.getDate());
        values.put(BANK_ID, bank.getId());

        long bank_return = db.replace(TABLE_BANK, BANK_ID, values);
    }

    // Deletes All Bank Transactions from DB
    public void clearBank()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BANK, null, null);
    }

    // Deletes a Specific Bank Transaction
    public void deleteBank(int bankId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BANK, BANK_ID + " = ?", new String[]{String.valueOf(bankId)});
    }
}
