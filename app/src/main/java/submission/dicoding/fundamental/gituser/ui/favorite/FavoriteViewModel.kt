package submission.dicoding.fundamental.gituser.ui.favorite

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import submission.dicoding.fundamental.gituser.repo.StoreData
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    storeData: StoreData,
) : ViewModel() {
    val getAllUserFavorite = storeData.getAllUserFavorite()
}