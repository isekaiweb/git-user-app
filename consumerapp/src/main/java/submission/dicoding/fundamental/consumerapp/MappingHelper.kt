package submission.dicoding.fundamental.consumerapp

import android.database.Cursor

object MappingHelper {
    fun mapCursorToArrayList(cursor: Cursor?): ArrayList<ConsumerModel> {
        val consumer = ArrayList<ConsumerModel>()
        cursor?.apply {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseContract.ConsumerUserColumns.ID))
                val login =
                    getString(getColumnIndexOrThrow(DatabaseContract.ConsumerUserColumns.LOGIN))
                val avatar_url =
                    getString(getColumnIndexOrThrow(DatabaseContract.ConsumerUserColumns.AVATAR_URL))
                val name =
                    getString(getColumnIndexOrThrow(DatabaseContract.ConsumerUserColumns.NAME))

                consumer.add(
                    ConsumerModel(id, login, avatar_url, name)
                )
            }
        }

        return consumer
    }
}