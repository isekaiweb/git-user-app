package submission.dicoding.fundamental.gituser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import submission.dicoding.fundamental.gituser.db.UserDao
import submission.dicoding.fundamental.gituser.other.Constants.Companion.TABLE_NAME

class UserConsumer : ContentProvider() {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface UserDaoEntryPoint{
        fun userDao(): UserDao
    }

    private fun getDao(context: Context): UserDao{
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(context,UserDaoEntryPoint::class.java)

        return hiltEntryPoint.userDao()
    }

    companion object {
        private const val AUTHORITY = "submission.dicoding.fundamental.gituser"
        private const val ID_FAVORITE_USER_DATA = 1
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)


        init {
            uriMatcher.addURI(AUTHORITY, TABLE_NAME, ID_FAVORITE_USER_DATA)
        }
    }


    override fun onCreate(): Boolean = false

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val app = context?.applicationContext ?: throw IllegalStateException()
        val userDao:UserDao = getDao(app)
        val cursor: Cursor?
        when (uriMatcher.match(uri)) {
            ID_FAVORITE_USER_DATA -> {
                cursor = userDao.findAll()
                if (context != null) {
                    cursor.setNotificationUri(context?.contentResolver, uri)
                }
            }
            else -> {
                cursor = null
            }
        }
        return cursor
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

}