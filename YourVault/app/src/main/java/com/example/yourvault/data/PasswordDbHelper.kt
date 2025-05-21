package com.example.yourvault.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.yourvault.data.models.PasswordEntry
import com.example.yourvault.security.CryptoUtils
import javax.crypto.SecretKey

private const val DB_NAME = "vault.db"
private const val DB_VERSION = 1
private const val TABLE = "passwords"
private const val COL_ID = "id"
private const val COL_SERVICE = "service"
private const val COL_USER = "username"
private const val COL_HASH = "password_hash"

class PasswordDbHelper(context: Context, masterKey: SecretKey) //el constructor recibe la clave, para tenerla tras el login
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private val key = masterKey

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
        val encrypted = CryptoUtils.encrypt(entry.passwordHash, key)
        return writableDatabase.insert(TABLE, null, ContentValues().apply {
            put(COL_SERVICE, entry.name)
            put(COL_USER, entry.username)
            put(COL_HASH, encrypted)
        })
    }

    fun update(entry: PasswordEntry): Int { //el metodo ya no es el estandard ya que ahora usa el hash que recibe de cyptoutils
        val encrypted = CryptoUtils.encrypt(entry.passwordHash, key)
        return writableDatabase.update(TABLE,
            ContentValues().apply {
                put(COL_SERVICE, entry.name)
                put(COL_USER, entry.username)
                put(COL_HASH, encrypted)
            },
            "$COL_ID=?",
            arrayOf(entry.id.toString())
        )
    }

    fun getAll(USER_ID: String): List<PasswordEntry> {
        val cursor = readableDatabase.query(
            TABLE,
            null,
            "$COL_USER=?",
            arrayOf(USER_ID),
            null, null,
            "$COL_ID ASC")

        return mutableListOf<PasswordEntry>().also { list ->
            while (cursor.moveToNext()) {
                val decrypted = CryptoUtils.decrypt(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_HASH)), key
                )
                list += PasswordEntry(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICE)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER)),
                    passwordHash = decrypted
                )
            }
            cursor.close()
        }
    }

    fun delete(id: Int): Int =
        writableDatabase.delete(TABLE, "$COL_ID=?", arrayOf(id.toString()))
}
