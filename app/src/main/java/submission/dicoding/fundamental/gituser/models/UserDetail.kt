package submission.dicoding.fundamental.gituser.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import submission.dicoding.fundamental.gituser.other.Constants.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)

@Parcelize
data class UserDetail(
    @PrimaryKey
    val id: Long?,
    val login: String?,
    val avatar_url: String,
    val blog: String?,
    val company: String?,
    val email: String?,
    val followers: Long?,
    val following: Long?,
    val html_url: String?,
    val location: String?,
    val name: String?,
    val public_repos: Long?,
//   another var from repo data
    val default_branch: String?,
    val description: String?,
    val forks_count: Long?,
    val language: String?,
    val stargazers_count: Long?,
    val size: Long?,
    val watchers_count: Long?
) : Parcelable