package submission.dicoding.fundamental.gituser.ui.detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import submission.dicoding.fundamental.gituser.api.Resource
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.other.ConnectionChecker
import submission.dicoding.fundamental.gituser.other.Constants.Companion.CONVERSION_ERROR
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NETWORK_FAILURE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.NO_INTERNET_CONNECTION
import submission.dicoding.fundamental.gituser.repo.GetData
import submission.dicoding.fundamental.gituser.repo.StoreData
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getData: GetData,
    private val storeData: StoreData,
    app: Application,
) : ConnectionChecker(app) {

    val detailUser: MutableLiveData<Resource<UserDetail>> = MutableLiveData()
    private var detailUserResponse: UserDetail? = null


    val detailTypeUser: MutableLiveData<Resource<List<UserDetail>>> = MutableLiveData()
    private var detailTypeUserResponse: List<UserDetail>? = null

    val isProfileValid = MutableLiveData<Boolean>()


    fun getUserDetail(username: String) = viewModelScope.launch {
        safeGetDetailUser(username)
    }

    fun getDetailTypeUser(username: String, action: String) = viewModelScope.launch {
        safeGetDetailTypeUser(username, action)
    }

    fun insertFavoriteUser(user: UserDetail) = viewModelScope.launch {
        storeData.insertFavoriteUser(user)
    }

    fun deleteFavoriteUser(user: UserDetail) = viewModelScope.launch {
        storeData.deleteFavoriteUser(user)
    }

    suspend fun checkIfAlreadyFavorite(id: Long) = storeData.checkIfAlreadyFavorite(id)


    private fun handleResponseGetDetailUser(response: Response<UserDetail>):
            Resource<UserDetail> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                if (detailUserResponse == null) {
                    detailUserResponse = result
                }
                return Resource.Success(detailUserResponse ?: result)
            }
        }
        return Resource.Error(response.message())
    }


    private suspend fun safeGetDetailUser(username: String) {
        detailUser.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = getData.getDetailUser(username)
                detailUser.postValue(handleResponseGetDetailUser(response))
            } else {
                detailUser.postValue(Resource.Error(NO_INTERNET_CONNECTION))
            }

        } catch (e: Throwable) {
            if (e is IOException) {
                detailUser.postValue(Resource.Error(NETWORK_FAILURE))
            } else detailUser.postValue(Resource.Error(CONVERSION_ERROR))
        }
    }


    private fun handleResponseGetDetailTypeUser(response: Response<List<UserDetail>>):
            Resource<List<UserDetail>> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                if (detailTypeUserResponse == null) {
                    detailTypeUserResponse = result
                }
                return Resource.Success(detailTypeUserResponse ?: result)
            }
        }
        return Resource.Error(response.message())
    }


    private suspend fun safeGetDetailTypeUser(username: String, action: String) {
        detailTypeUser.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = getData.getDetailTypeUser(username, action)
                detailTypeUser.postValue(handleResponseGetDetailTypeUser(response))
            } else {
                detailTypeUser.postValue(Resource.Error(NO_INTERNET_CONNECTION))
            }

        } catch (e: Throwable) {
            if (e is IOException) {
                detailTypeUser.postValue(Resource.Error(NETWORK_FAILURE))
            } else detailTypeUser.postValue(Resource.Error(CONVERSION_ERROR))
        }

    }

}