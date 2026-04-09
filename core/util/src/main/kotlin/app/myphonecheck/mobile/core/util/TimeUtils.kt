package app.myphonecheck.mobile.core.util

import kotlin.math.abs

object TimeUtils {

    fun getRelativeTimeString(timestampMillis: Long): String {
        val currentTime = System.currentTimeMillis()
        val diffMillis = currentTime - timestampMillis

        if (diffMillis < 0) {
            return "in the future"
        }

        val seconds = diffMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
            hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
            days < 7 -> if (days == 1L) "1 day ago" else "$days days ago"
            weeks < 4 -> if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
            months < 12 -> if (months == 1L) "1 month ago" else "$months months ago"
            else -> if (years == 1L) "1 year ago" else "$years years ago"
        }
    }

    fun formatTimestamp(timestampMillis: Long): String {
        val javaDate = java.util.Date(timestampMillis)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
        return format.format(javaDate)
    }

    fun formatDate(timestampMillis: Long): String {
        val javaDate = java.util.Date(timestampMillis)
        val format = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
        return format.format(javaDate)
    }

    fun formatTime(timestampMillis: Long): String {
        val javaDate = java.util.Date(timestampMillis)
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
        return format.format(javaDate)
    }

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    fun isSameWeek(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.WEEK_OF_YEAR) == cal2.get(java.util.Calendar.WEEK_OF_YEAR)
    }

    fun getDaysBetween(timestamp1: Long, timestamp2: Long): Int {
        val diffMillis = abs(timestamp1 - timestamp2)
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getStartOfDay(timestampMillis: Long): Long {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = timestampMillis }
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfDay(timestampMillis: Long): Long {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = timestampMillis }
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 59)
        cal.set(java.util.Calendar.SECOND, 59)
        cal.set(java.util.Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
