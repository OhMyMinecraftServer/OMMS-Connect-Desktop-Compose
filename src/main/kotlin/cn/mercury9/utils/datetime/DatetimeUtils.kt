package cn.mercury9.utils.datetime

import kotlinx.datetime.*
import kotlinx.datetime.format.char

/**
 * YYYY-MM-DD hh:mm:ss
 */
val LocalDateTime.Formats.default
    get() = LocalDateTime.Format {
            date(LocalDate.Formats.ISO)
            char(' ')
            hour(); char(':'); minute(); char(':'); second()
        }

/**
 * 作为毫秒精度时间戳转换为 `Instant`
 */
fun Long.toInstantAsMilliseconds() = Instant.fromEpochMilliseconds(this)

/**
 * 使用当前系统时区转换为 `LocalDateTime`
 */
fun Instant.toLocalDateTime() = this.toLocalDateTime(TimeZone.currentSystemDefault())
