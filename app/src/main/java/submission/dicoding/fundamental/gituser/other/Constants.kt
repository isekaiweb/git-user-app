package submission.dicoding.fundamental.gituser.other

abstract class Constants {
    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val TABLE_NAME = "table_user"

        const val DELAY_SEARCH = 500L
        const val DELAY_CHECK_DATA_EMPTY = 50L


        const val EXTRA_USER = "user"
        const val EXTRA_ACTION = "action"
        const val EXTRA_MESSAGE = "message"

        const val CONVERSION_ERROR = "Conversion Error"
        const val NETWORK_FAILURE = "Network Failure"
        const val NO_INTERNET_CONNECTION = "No internet connection"

        const val SHARED_PREFERENCES_NAME = "sharedPref"
        const val KEY_FIRST_TIME_TOGGLE = "first_time?"
        const val KEY_USERNAME = "key_username"
        const val EXTRA_DESTINATION = "destination"
        const val DESTINATION_PROFILE = "from profile fragment"

        const val KEY_LAST_SEARCH = "A"
        const val KEY_IS_REMINDED = "i don't know"
    }
}