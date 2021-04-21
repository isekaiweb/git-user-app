package submission.dicoding.fundamental.gituser.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import submission.dicoding.fundamental.gituser.BuildConfig.API_KEY
import submission.dicoding.fundamental.gituser.models.UserDetail
import submission.dicoding.fundamental.gituser.models.UserResponse


interface GitHubAPI {
    @GET("users/{username}/{action}")
    @Headers("Authorization: token $API_KEY")
    suspend fun getDetailTypeUser(
        @Path("username") username: String,
        @Path("action") action: String
    ): Response<List<UserDetail>>

    @GET("users/{username}")
    @Headers("Authorization: token $API_KEY")
    suspend fun getDetailUser(
        @Path("username") username: String
    ): Response<UserDetail>


    @GET("search/users")
    @Headers("Authorization: token $API_KEY")
    suspend fun searchUsers(
        @Query("q") query: String
    ): Response<UserResponse>
}