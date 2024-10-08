package cn.mercury9.omms.connect.desktop.data

enum class NameLegalState {
    Legal,
    Blank,
    TooLong
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

enum class CodeLegalState {
    Legal,
    Blank,
}

fun checkName(name: String): NameLegalState {
    if (name.isEmpty()) {
        return NameLegalState.Blank
    } else if (name.length > 64) {
        return NameLegalState.TooLong
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
fun checkCode(code: String): CodeLegalState {
    if (code.isBlank()) {
        return CodeLegalState.Blank
    }
    return CodeLegalState.Legal
}

fun getHashedCode(code: String?): String? =
    if (code != null) {
        icu.takeneko.omms.client.util.Util.getChecksumMD5(code)
    } else {
        null
    }
