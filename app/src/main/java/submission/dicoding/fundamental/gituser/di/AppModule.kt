package submission.dicoding.fundamental.gituser.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import submission.dicoding.fundamental.gituser.api.GitHubAPI
import submission.dicoding.fundamental.gituser.db.GitHubUserDatabase
import submission.dicoding.fundamental.gituser.other.Constants.Companion.BASE_URL
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_FIRST_TIME_TOGGLE
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_IS_REMINDED
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_LAST_SEARCH
import submission.dicoding.fundamental.gituser.other.Constants.Companion.KEY_USERNAME
import submission.dicoding.fundamental.gituser.other.Constants.Companion.SHARED_PREFERENCES_NAME
import submission.dicoding.fundamental.gituser.other.Constants.Companion.TABLE_NAME
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun provideMovieAPI(retrofit: Retrofit): GitHubAPI =
        retrofit.create(GitHubAPI::class.java)


    @Provides
    @Singleton
    fun provideGitHubUserDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        GitHubUserDatabase::class.java,
        TABLE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideMovieDao(db: GitHubUserDatabase) = db.getUserDao()


    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideUserName(sharedPref: SharedPreferences) =
        sharedPref.getString(KEY_USERNAME, "")

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)


    @Singleton
    @Provides
    fun provideIsReminded(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_IS_REMINDED, false)


    @Singleton
    @Provides
    fun provideLastSearch(sharedPref: SharedPreferences) =
        sharedPref.getString(KEY_LAST_SEARCH, "A")
}