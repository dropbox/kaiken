package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

interface TimeMessageProvider {
    fun tellTheTime(): String
}

class RealTimeMessageProvider : TimeMessageProvider {
    override fun tellTheTime(): String {
        val datetime: Date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat()

        return "Today is ${dateFormat.format(datetime)}"
    }
}
