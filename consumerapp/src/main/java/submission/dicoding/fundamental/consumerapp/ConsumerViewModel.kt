package submission.dicoding.fundamental.consumerapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class ConsumerViewModel(
    app: Application
) : AndroidViewModel(app) {

    private var consumer = MutableLiveData<ArrayList<ConsumerModel>>()

    fun setConsumer(context: Context) {
        val cursor = context.contentResolver.query(
            DatabaseContract.ConsumerUserColumns.CONTENT_URI,
            null,
            null,
            null,
            null,
        )

        val listConverted = MappingHelper.mapCursorToArrayList(cursor)
        consumer.postValue(listConverted)
    }

    fun getConsumer(): LiveData<ArrayList<ConsumerModel>> = consumer

}