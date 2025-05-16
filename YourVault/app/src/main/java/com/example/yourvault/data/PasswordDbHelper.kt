package com.example.yourvault.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.yourvault.data.models.PasswordEntry

private const val DB_NAME = "vault.db"
private const val DB_VERSION = 1
private const val TABLE = "passwords"
private const val COL_ID = "id"
private const val COL_SERVICE = "service"
private const val COL_USER = "username"
private const val COL_HASH = "password_hash"

class PasswordDbHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sql = """
      CREATE TABLE $TABLE (
        $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_SERVICE TEXT NOT NULL,
        $COL_USER TEXT NOT NULL,
        $COL_HASH TEXT NOT NULL
      );
    """
        db.execSQL(sql.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    fun insert(entry: PasswordEntry): Long {
        val cv = ContentValues().apply {
            put(COL_SERVICE, entry.service)
            put(COL_USER, entry.username)
            put(COL_HASH, entry.passwordHash)
        }
        return writableDatabase.insert(TABLE, null, cv)
    }

    fun getAll(): List<PasswordEntry> {
        val cursor: Cursor = readableDatabase.query(
            TABLE, null, null, null, null, null, "$COL_ID ASC"
        )
        val list = mutableListOf<PasswordEntry>()
        with(cursor) {
            while (moveToNext()) {
                list += PasswordEntry(
                    getInt(getColumnIndexOrThrow(COL_ID)),
                    getString(getColumnIndexOrThrow(COL_SERVICE)),
                    getString(getColumnIndexOrThrow(COL_USER)),
                    getString(getColumnIndexOrThrow(COL_HASH))
                )
            }
            close()
        }
        return list
    }

    fun delete(id: Int): Int =
        writableDatabase.delete(TABLE, "$COL_ID=?", arrayOf(id.toString()))
}
