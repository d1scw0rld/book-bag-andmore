package com.discworld.booksbag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.discworld.booksbag.dto.Book;
import com.discworld.booksbag.dto.Field;

public class DBAdapter
{
   private static final String TAG = "DB";
	private static final String DATABASE_NAME = "books_bag.db";

   private static final String TABLE_BOOKS = "books";
   private static final String TABLE_FIELDS = "fields";
   private static final String TABLE_BOOK_FIELDS = "book_fields";
	private static final String TABLE_SCHOOLS = "schools";
	private static final String TABLE_CLASSES = "classes";
	private static final String TABLE_SERVICES = "services";
	private static final String TABLE_MODELS = "models";
	private static final String TABLE_PRESENCES = "presences";
	private static final int DATABASE_VERSION = 1;

   // Common column names
   private static final String KEY_ID = "_id";

   // BOOKS column names
   private static final String KEY_TTL = "title";
//   private static final String KEY_AUT1_ID = "author1_id";
//   private static final String KEY_AUT2_ID = "author2_id";
//   private static final String KEY_AUT3_ID = "author3_id";
   private static final String KEY_DSCR = "description";
//   private static final String KEY_SRS_ID = "series_id";
   private static final String KEY_VLM = "volume";
//   private static final String KEY_CTG_ID = "category_id";
//   private static final String KEY_LNG_ID = "language_id";
//   private static final String KEY_PBL_ID = "publisher_id";
//   private static final String KEY_PBL_LCT_ID = "publication_location_id";
   private static final String KEY_PBL_DT = "publication_date";
   private static final String KEY_PGS = "pages";
//   private static final String KEY_STS_ID = "status_id";
//   private static final String KEY_RTN_ID = "rating_id";
//   private static final String KEY_FMT_ID = "format_id";
//   private static final String KEY_LCT_ID = "location_id";
   private static final String KEY_PRC = "price";
   private static final String KEY_VL = "value";
   private static final String KEY_CND_ID = "condition_id";
   private static final String KEY_DUE_DT = "due_date";
   private static final String KEY_RD_DT = "read_date";
   private static final String KEY_EDN = "edition";
   private static final String KEY_ISBN = "isbn";
   private static final String KEY_WEB = "web";

   // FIELDS column names
   private static final String KEY_NM = "name";
   private static final String KEY_TP_ID = "type_id";

   // BOOK_FIELDS column names
   private static final String KEY_BK_ID = "book_id";
   private static final String KEY_FLD_ID = "field_id";

   // Table Create Statements
   // BOOKS table create statement
   private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + TABLE_BOOKS + " ("
         + KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
         + KEY_TTL + " TEXT, "
//         + KEY_AUT1_ID + " INTEGER, "
//         + KEY_AUT2_ID + " INTEGER, "
//         + KEY_AUT3_ID + "	INTEGER, "
         + KEY_DSCR + " TEXT, "
//         + KEY_SRS_ID + " INTEGER, "
         + KEY_VLM + " INTEGER, "
//         + KEY_CTG_ID + " INTEGER, "
//         + KEY_LNG_ID + " INTEGER, "
//         + KEY_PBL_ID + " INTEGER, "
//         + KEY_PBL_LCT_ID + " INTEGER, "
         + KEY_PBL_DT + " INTEGER, "
         + KEY_PGS + "INTEGER, "
//         + KEY_STS_ID + "INTEGER, "
//         + KEY_RTN_ID + "INTEGER, "
//         + KEY_FMT_ID + "INTEGER, "
//         + KEY_LCT_ID + "INTEGER, "
         + KEY_PRC + "INTEGER, "
         + KEY_VL + "INTEGER, "
         + KEY_CND_ID + "INTEGER, "
         + KEY_DUE_DT + "INTEGER, "
         + KEY_RD_DT + "INTEGER, "
         + KEY_EDN + "INTEGER, "
         + KEY_ISBN + " TEXT, "
         + KEY_WEB + " TEXT)";

   private static final int ID_KEY_ID = 0,
                            ID_KEY_TTL = 1,
                            ID_KEY_DSCR = 2,
                            ID_KEY_VLM = 3,
                            ID_KEY_PBL_DT = 4,
                            ID_KEY_PGS = 5,
                            ID_KEY_PRC = 6,
                            ID_KEY_VL = 7,
                            ID_KEY_DUE_DT = 9,
                            ID_KEY_RD_DT = 9,
                            ID_KEY_EDN = 10,
                            ID_KEY_ISBN = 11,
                            ID_KEY_WEB = 12,
                            ID_KEY_TP_ID = 1,
                            ID_KEY_NM = 2;

   // FIELDS table create statement
   private static final String CREATE_TABLE_FIELDS = "CREATE TABLE " + TABLE_FIELDS + " ("
//         + KEY_TP_ID + " INTEGER, "
         + KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
         + KEY_NM + " TEXT)";

   // BOOK_FIELDS table create statement
   private static final String CREATE_TABLE_BOOK_FIELDS = "CREATE TABLE " + TABLE_BOOK_FIELDS + " ("
         + KEY_BK_ID + " INTEGER, "
         + KEY_FLD_ID + " INTEGER, "
         + KEY_TP_ID + " INTEGER)";

	private SQLiteDatabase db;
	private final Context context;
	private DBOpenHelper dbHelper;

   public final static int FLD_AUT = 1,
//                            FLD_AUT2 = 2,
//                            FLD_AUT3 = 3,
                           FLD_SRS = 2,
                           FLD_CTG = 3,
                           FLD_LNG = 4,
                           FLD_PBL = 5,
                           FLD_PBL_LCT = 6,
                           FLD_STS = 7,
                           FLD_RTN = 8,
                           FLD_FMT = 9,
                           FLD_LCT = 10,
                           FLD_CND = 11;

   public final static int ORD_TTL = 1,
                           ORD_AUT = 2;

	public DBAdapter(Context _context)
	{
		this.context = _context;
		dbHelper = new DBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void open() throws SQLiteException
	{
		try
		{
			db = dbHelper.getWritableDatabase();
		}
		catch(SQLiteException ex)
		{
			db = dbHelper.getReadableDatabase();
		}
	}

	public void close()
	{
		db.close();
	}

   public ArrayList<Field> getBooksOrder(String query)
   {
      ArrayList<Field> alBooks = new ArrayList<Field>();

//      String query = "b." + KEY_ID + ", b." + KEY_TTL + ", GROUP_CONCAT(f." + KEY_NM + ") AS authors "
//            + "FROM " + TABLE_BOOKS + " AS b LEFT JOIN " + TABLE_BOOK_FIELDS + " AS bf ON bf." + KEY_BK_ID + " = " + "b." + KEY_ID
//            + " LEFT JOIN " + TABLE_FIELDS + " AS f ON f." + KEY_ID + " = bf." + KEY_FLD_ID
//            + " WHERE bf." + KEY_TP_ID + " = " + FLD_AUT
//            + " GROUP BY b." + KEY_ID
//            + " ORDER BY b." + KEY_TTL;

      Cursor cursor = db.rawQuery(query, null);
      Field oBook;

      if(cursor.moveToFirst())
      {
         do
         {
            oBook = new Field(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
            alBooks.add(oBook);
         } while (cursor.moveToNext());
      }
      cursor.close();

      return alBooks;
   }

   public ArrayList<Field> getBooks(int iOrder)
   {
      String query = "";
      switch(iOrder)
      {
         case ORD_TTL:
            query = "b." + KEY_ID + ", b." + KEY_TTL + ", GROUP_CONCAT(f." + KEY_NM + ") AS authors "
                  + "FROM " + TABLE_BOOKS + " AS b LEFT JOIN " + TABLE_BOOK_FIELDS + " AS bf ON bf." + KEY_BK_ID + " = " + "b." + KEY_ID
                  + " LEFT JOIN " + TABLE_FIELDS + " AS f ON f." + KEY_ID + " = bf." + KEY_FLD_ID
                  + " WHERE bf." + KEY_TP_ID + " = " + FLD_AUT
                  + " GROUP BY b." + KEY_ID
                  + " ORDER BY b." + KEY_TTL;
         break;

         case ORD_AUT:
            query = "b." + KEY_ID + ", b." + KEY_TTL + ", GROUP_CONCAT(f." + KEY_NM + ") AS authors "
                  + "FROM " + TABLE_BOOKS + " AS b LEFT JOIN " + TABLE_BOOK_FIELDS + " AS bf ON bf." + KEY_BK_ID + " = " + "b." + KEY_ID
                  + " LEFT JOIN " + TABLE_FIELDS + " AS f ON f." + KEY_ID + " = bf." + KEY_FLD_ID
                  + " WHERE bf." + KEY_TP_ID + " = " + FLD_AUT
                  + " GROUP BY b." + KEY_ID
                  + " ORDER BY authors";
         break;

         default:
            return null;
      }

      return getBooksOrder(query);
   }

   public void insertBook(Book oBook)
   {
//      String sql = "INSERT INTO "+ TABLE_BOOKS +" VALUES (?,?,?,?,?,?,?,?,?,?);";
//      SQLiteStatement statement = db.compileStatement(sql);
      db.beginTransaction();
      try
      {

//         statement.clearBindings();

         ContentValues values = new ContentValues();
         values.put(KEY_TTL, oBook.sTitle);
         values.put(KEY_DSCR, oBook.sDescription);
         values.put(KEY_VLM, oBook.iVolume);
         values.put(KEY_PBL_DT, oBook.iPublicationDate);
         values.put(KEY_PGS, oBook.iPages);
         values.put(KEY_PRC, oBook.iPrice);
         values.put(KEY_VL, oBook.iValue);
         values.put(KEY_DUE_DT, oBook.iDueDate);
         values.put(KEY_RD_DT, oBook.iReadDate);
         values.put(KEY_EDN, oBook.iEdition);
         values.put(KEY_ISBN, oBook.sISBN);
         values.put(KEY_WEB, oBook.sWeb);

         long iBookID = db.insert(TABLE_BOOKS, null, values);


         for (Field oField : oBook.alFields)
         {
            if (oField.iID == 0)
            {
               values = new ContentValues();
               values.put(KEY_NM, oField.sName);
               oField.iID = db.insert(TABLE_FIELDS, null, values);
            }
         }

         for (Field oField : oBook.alFields)
         {
            values = new ContentValues();
            values.put(KEY_FLD_ID, oField.iID);
            values.put(KEY_TP_ID, oField.iTypeID);
            values.put(KEY_BK_ID, iBookID);
            db.insert(TABLE_BOOK_FIELDS, null, values);
         }

//      statement.bindString(KEY_TTL_ID, oBook.sTitle);
//      statement.bindString(KEY_DSCR_ID, oBook.sDescription);
//      statement.bindLong(KEY_VLM_ID, oBook.iVolume);
//      statement.bindLong(KEY_PBL_DT_ID, oBook.iPublicationDate);
//      statement.bindLong(KEY_PGS_ID, oBook.iPages);
//      statement.bindLong(KEY_PRC_ID, oBook.iPrice);
//      statement.bindLong(KEY_VL_ID, oBook.iValue);
//      statement.bindLong(KEY_RD_DT_ID, oBook.iReadDate);
//      statement.bindString(KEY_ISBN_ID, oBook.sISBN);
//      statement.bindString(KEY_WEB_ID, oBook.sWeb);
//
//      statement.execute();
         db.setTransactionSuccessful();
      }
      finally
      {
         db.endTransaction();
      }
      shrink();
   }

   public ArrayList<Field> getFieldValues(int iTypeID)
   {
      ArrayList<Field> alFieldValues = new ArrayList<>();

//      SELECT f.id, bf.type_id, f.name
//      FROM book_fields as bf LEFT JOIN fields AS f ON bf.field_id = f.id
//      WHERE bf.type_id =1
//      ORDER BY f.name

      String sql = "SELECT f." + KEY_ID + ", bf." + KEY_TP_ID + ", f." + KEY_NM
            + " FROM " + TABLE_BOOK_FIELDS + " as bf LEFT JOIN " + TABLE_FIELDS + " AS f ON bf." + KEY_FLD_ID + " = f." + KEY_ID
            + " WHERE bf." + KEY_TP_ID + " = " + iTypeID
            + "ORDER BY f." + KEY_NM;

      Cursor cursor = db.rawQuery(sql, null);

      Field oField;
      ArrayList<Field> alFields = new ArrayList<>();
      if(cursor.moveToFirst())
      {
         do
         {
            oField = new Field(Integer.parseInt(cursor.getString(ID_KEY_ID)),
                               Integer.parseInt(cursor.getString(ID_KEY_TP_ID)),
                               cursor.getString(ID_KEY_NM));

            alFieldValues.add(oField);
         } while (cursor.moveToNext());
      }

      return alFieldValues;
   }

   public Book getBook(long iBookID)
   {
      Book oBook = null;

      Cursor cursor = db.query(TABLE_BOOKS,
                               null,
                               KEY_ID + " = " + iBookID,
                               null,
                               null,
                               null,
                               null);

      if(cursor.moveToFirst())
      {
         oBook = new Book(Integer.parseInt(cursor.getString(ID_KEY_ID)),
                          cursor.getString(ID_KEY_TTL),
                          cursor.getString(ID_KEY_DSCR),
                          Integer.parseInt(cursor.getString(ID_KEY_VLM)),
                          Integer.parseInt(cursor.getString(ID_KEY_PBL_DT)),
                          Integer.parseInt(cursor.getString(ID_KEY_PGS)),
                          Integer.parseInt(cursor.getString(ID_KEY_PRC)),
                          Integer.parseInt(cursor.getString(ID_KEY_VL)),
                          Integer.parseInt(cursor.getString(ID_KEY_DUE_DT)),
                          Integer.parseInt(cursor.getString(ID_KEY_RD_DT)),
                          Integer.parseInt(cursor.getString(ID_KEY_EDN)),
                          cursor.getString(ID_KEY_ISBN),
                          cursor.getString(ID_KEY_WEB));
      }

      String sql = "SELECT f." + KEY_ID + ", bf." + KEY_TP_ID + ", f." + KEY_NM
                  +" FROM " + TABLE_BOOK_FIELDS + " as bf LEFT JOIN " + TABLE_FIELDS + " AS f ON bf." + KEY_FLD_ID + " = f." + KEY_ID
                  +" WHERE bf." + KEY_ID + " = " + iBookID;

      cursor = db.rawQuery(sql, null);

      Field oField;
      ArrayList<Field> alFields = new ArrayList<>();
      if(cursor.moveToFirst())
      {
         do
         {
            oField = new Field(Integer.parseInt(cursor.getString(ID_KEY_ID)),
                               Integer.parseInt(cursor.getString(ID_KEY_TP_ID)),
                               cursor.getString(ID_KEY_NM));

            alFields.add(oField);
         } while (cursor.moveToNext());
      }
      oBook.alFields = alFields;

      return oBook;
   }

   public boolean deleteBook(long iBookID)
   {
      boolean result = true;

      db.beginTransaction();
      try
      {
         db.delete(TABLE_BOOK_FIELDS, KEY_BK_ID + " = " + iBookID, null);
         db.delete(TABLE_BOOKS, KEY_ID + " = " + iBookID, null);
      }
      catch (Exception e)
      {
         Log.e(TAG, e.getMessage());
         result = false;
      }
      finally
      {
         db.endTransaction();
         shrink();
      }

      return result;
   }

   public boolean updateBook(Book oBook)
   {
      ContentValues oValues;
      boolean result = true;

      db.beginTransaction();
      try
      {
         for (Field oField : oBook.alFields)
         {
            if (oField.iID == 0)
            {
               oValues = new ContentValues();
               oValues.put(KEY_NM, oField.sName);
               oField.iID = db.insert(TABLE_FIELDS, null, oValues);
            }
         }

         db.delete(TABLE_BOOK_FIELDS, KEY_BK_ID + " = " + oBook.iID, null);

         for (Field oField : oBook.alFields)
         {
            oValues = new ContentValues();
            oValues.put(KEY_FLD_ID, oField.iID);
            oValues.put(KEY_TP_ID, oField.iTypeID);
            oValues.put(KEY_BK_ID, oBook.iID);
            db.insert(TABLE_BOOK_FIELDS, null, oValues);
         }

         oValues = new ContentValues();
         oValues.put(KEY_TTL, oBook.sTitle);
         oValues.put(KEY_DSCR, oBook.sDescription);
         oValues.put(KEY_VLM, oBook.iVolume);
         oValues.put(KEY_PBL_DT, oBook.iPublicationDate);
         oValues.put(KEY_PGS, oBook.iPages);
         oValues.put(KEY_PRC, oBook.iPrice);
         oValues.put(KEY_VL, oBook.iValue);
         oValues.put(KEY_DUE_DT, oBook.iDueDate);
         oValues.put(KEY_RD_DT, oBook.iReadDate);
         oValues.put(KEY_EDN, oBook.iEdition);
         oValues.put(KEY_ISBN, oBook.sISBN);
         oValues.put(KEY_WEB, oBook.sWeb);
         db.update(TABLE_PRESENCES,
                   oValues,
                   KEY_ID + " = ?" + oBook.iID,
                   null);

         db.setTransactionSuccessful();
      }
      catch (Exception e)
      {
         Log.e(TAG, e.getMessage());
         result = false;
      }
      finally
      {
         db.endTransaction();
         shrink();
      }
      return result;
   }

   public void shrink()
   {
      db.execSQL("VACUUM");
   }

   private static class DBOpenHelper extends SQLiteOpenHelper
	{
		public DBOpenHelper(Context context, String name, CursorFactory factory, int version)
		{
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			_db.execSQL(CREATE_TABLE_BOOKS);
			_db.execSQL(CREATE_TABLE_FIELDS);
			_db.execSQL(CREATE_TABLE_BOOK_FIELDS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion)
		{
			Log.w("TaskDBAdapter", "Upgrade from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data");
			// on upgrade drop older tables
			_db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
			_db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELDS);
			_db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FIELDS);

			// create new tables
			onCreate(_db);
		}
	}

}
