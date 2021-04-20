package submission.dicoding.fundamental.gituser.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.repo.StoreData
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val storeData: StoreData,
) : ViewModel() {
    val getAllUserFavorite = storeData.getAllUserFavorite()
    fun deleteFavoriteUser(user: UserDetail) = viewModelScope.launch {
        storeData.deleteFavoriteUser(user)
    }

    fun insertFavoriteUser(user: UserDetail) = viewModelScope.launch {
        storeData.insertFavoriteUser(user)
    }
}