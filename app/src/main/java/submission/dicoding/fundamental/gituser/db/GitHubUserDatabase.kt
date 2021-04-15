package submission.dicoding.fundamental.gituser.db

import androidx.room.Database
import androidx.room.RoomDatabase
import submission.dicoding.fundamental.gituser.models.UserDetail


@Database(
    entities = [UserDetail::class],
    version = 1,
    exportSchema = false
)
abstract class GitHubUserDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}