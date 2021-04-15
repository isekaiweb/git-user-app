package submission.dicoding.fundamental.gituser.other

import android.icu.text.CompactDecimalFormat
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

internal object Function {
    fun converterNumber(num: Long): String =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompactDecimalFormat.getInstance(Locale.US, CompactDecimalFormat.CompactStyle.SHORT)
                .format(num)
        } else {
            if (num < 1000)
                num.toString()
            else {
                val exp = (ln(num.toDouble()) / ln(1000.0)).toInt()
                val typeNumber = arrayOf("K", "M", "B", "T", "P", "E")
                String.format("%.1f %c", num / 1000.0.pow(exp.toDouble()), typeNumber[exp - 1])
            }
        }

    fun setVisibilityView(text: Any?, textView: TextView) {
        if (text != null && text != "") {
            textView.text = text.toString()
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    }

    fun converterSize(bytes: Long): String = when {
        bytes == Long.MIN_VALUE || bytes < 0 -> "N/A"
        bytes < 1024L -> "$bytes B"
        bytes <= 0xfffccccccccccccL shr 40 -> "%.1f KiB".format(bytes.toDouble() / (0x1 shl 10))
        bytes <= 0xfffccccccccccccL shr 30 -> "%.1f MiB".format(bytes.toDouble() / (0x1 shl 20))
        bytes <= 0xfffccccccccccccL shr 20 -> "%.1f GiB".format(bytes.toDouble() / (0x1 shl 30))
        bytes <= 0xfffccccccccccccL shr 10 -> "%.1f TiB".format(bytes.toDouble() / (0x1 shl 40))
        bytes <= 0xfffccccccccccccL -> "%.1f PiB".format((bytes shr 10).toDouble() / (0x1 shl 40))
        else -> "%.1f EiB".format((bytes shr 20).toDouble() / (0x1 shl 40))
    }

    fun visibilityView(itemView: View?, visible: Boolean) {
        itemView?.isVisible = visible
    }

    fun isEmailValid(email: String?): Boolean {
        if (email != null) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        return false
    }

}