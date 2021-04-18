package submission.dicoding.fundamental.gituser.repo

import submission.dicoding.fundamental.gituser.db.UserDao
import submission.dicoding.fundamental.gituser.models.UserDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreData @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun insertFavoriteUser(user: UserDetail) = userDao.insertFavoriteUser(user)
    suspend fun deleteFavoriteUser(user: UserDetail) = userDao.deleteFavoriteUser(user)
    suspend fun checkIfAlreadyFavorite(id: Long) = userDao.checkIfAlreadyFavorite(id)

    fun getAllUserFavorite() = userDao.getAllUserFavorite()
}