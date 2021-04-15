package submission.dicoding.fundamental.gituser.other

abstract class Constants {
    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val TABLE_NAME = "table_user"
        const val DELAY_SEARCH = 500L
        const val DELAY_CLEAR_FOCUS = 2000L
        const val EXTRA_USER = "user"
        const val EXTRA_ACTION = "action"
        const val EXTRA_USERNAME="username"
        const val CONVERSION_ERROR="Conversion Error"
        const val NETWORK_FAILURE ="Network Failure"
        const val NO_INTERNET_CONNECTION = "No internet connection"
    }
}