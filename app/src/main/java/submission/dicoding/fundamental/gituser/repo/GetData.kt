package submission.dicoding.fundamental.gituser.repo

import submission.dicoding.fundamental.gituser.api.GitHubAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetData @Inject constructor(
        private val gitHubAPI: GitHubAPI
) {

    suspend fun getDetailUser(username: String) = gitHubAPI.getDetailUser(username)

    suspend fun getDetailTypeUser(username: String, action: String) =
            gitHubAPI.getDetailTypeUser(username, action)

    suspend fun searchUsers(username: String) = gitHubAPI.searchUsers(username)

}