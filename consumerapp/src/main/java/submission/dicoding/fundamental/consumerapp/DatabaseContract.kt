package submission.dicoding.fundamental.consumerapp

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {
    private const val AUTHORITY = "submission.dicoding.fundamental.gituser"
    private const val SCHEME = "content"


    internal class ConsumerUserColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "table_user"
            const val ID = "id"
            const val LOGIN = "login"
            const val AVATAR_URL = "avatar_url"
            const val NAME = "name"

            val CONTENT_URI = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}