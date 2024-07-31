package cn.mercury9.omms.connect.desktop.data

import kotlinx.serialization.Serializable

@Serializable
data class OmmsServer(
    var name: String,
    var ip: String,
    var port: Int,
    var code: Int? = null,
    var emoji: String? = null,
)

enum class NameLegalState {
    Legal,
    Blank
}

enum class IpLegalState {
    Legal,
    Blank
}

enum class PortLegalState {
    Legal,
    Blank,
    OutOfRange
}

fun checkName(name: String): NameLegalState {
    if (name.isEmpty()) {
        return NameLegalState.Blank
    }
    return NameLegalState.Legal
}
fun checkIp(ip: String): IpLegalState {
    if (ip.isBlank()) {
        return IpLegalState.Blank
    }
    return IpLegalState.Legal
}
fun checkPort(port: String): PortLegalState {
    if (port.isBlank()) {
        return PortLegalState.Blank
    } else {
        val portNumber = port.toIntOrNull()
        if (portNumber == null || portNumber !in 0..65535) {
            return PortLegalState.OutOfRange
        }
    }
    return PortLegalState.Legal
}
