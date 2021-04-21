package submission.dicoding.fundamental.gituser.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.view.View
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import submission.dicoding.fundamental.gituser.R
import submission.dicoding.fundamental.gituser.other.Constants
import submission.dicoding.fundamental.gituser.other.Function.customColorPrimaryBlackSnackBar
import submission.dicoding.fundamental.gituser.ui.GitMainActivity
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "no name"
        private const val NOTIFICATION_CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 1
        private const val ID_REPEATING = 101
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        sendNotification(context)
    }

    private fun sendNotification(context: Context?) {
        val intent = Intent(context, GitMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

//        val pendingIntent =
//            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle("Reminder".toUpperCase(Locale.ROOT))
            .setContentText("Time to find someone")
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setSmallIcon(R.drawable.ic_github)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    fun setRepeatingAlarm(
        context: Context?,
        time: String,
        message: String,
        view: View
    ) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).also {
            it.putExtra(Constants.EXTRA_MESSAGE, message)
        }
        val timeArray = time.split(":".toRegex()).dropLastWhile {
            it.isEmpty()
        }.toTypedArray()

        val calendar = Calendar.getInstance().also {
            it.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            it.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            it.set(Calendar.SECOND, 0)
        }



        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            createPendingIntent(context, intent)
        )



        customColorPrimaryBlackSnackBar(
            Snackbar.make(
                view,
                "Activate Alarm",
                Snackbar.LENGTH_SHORT
            ), context
        )

    }

    private fun createPendingIntent(context: Context, intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)


    fun cancelAlarm(context: Context, view: View) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        createPendingIntent(context, intent).apply {
            cancel()
            alarmManager.cancel(this)
        }

        customColorPrimaryBlackSnackBar(
            Snackbar.make(
                view,
                "Canceled Alarm",
                Snackbar.LENGTH_SHORT
            ), context
        )
    }

}