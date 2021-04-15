package submission.dicoding.fundamental.gituser.db

import androidx.lifecycle.LiveData
import androidx.room.*
import submission.dicoding.fundamental.gituser.models.UserDetail

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteUser(user: UserDetail)

    @Delete
    suspend fun deleteFavoriteUser(user: UserDetail)

    @Query("SELECT count(*) FROM table_user WHERE table_user.id = :id")
    suspend fun checkIfAlreadyFavorite(id: Long): Int

    @Query("SELECT * FROM table_user")
    fun getAllUserFavorite(): LiveData<List<UserDetail>>
}