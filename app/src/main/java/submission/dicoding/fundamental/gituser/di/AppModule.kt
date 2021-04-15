package submission.dicoding.fundamental.gituser.di

import android.content.Context
import androidx.room.Room
import submission.dicoding.fundamental.gituser.api.GitHubAPI
import submission.dicoding.fundamental.gituser.db.GitHubUserDatabase
import submission.dicoding.fundamental.gituser.other.Constants.Companion.BASE_URL
import submission.dicoding.fundamental.gituser.other.Constants.Companion.TABLE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

}