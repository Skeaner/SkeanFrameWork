@file:JvmName("DateTimeExt")

package me.skean.skeanframework.ktext

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Skean on 2025/06/04.
 */
@JvmOverloads
fun LocalDate.format(pattern: String = "yyyy-MM-dd"): String = this.format(DateTimeFormatter.ofPattern(pattern))


@JvmOverloads
fun LocalDateTime.format(pattern: String = "yyyy-MM-dd HH:mm:ss"): String = this.format(DateTimeFormatter.ofPattern(pattern))