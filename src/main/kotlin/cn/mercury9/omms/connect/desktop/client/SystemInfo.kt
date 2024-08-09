package cn.mercury9.omms.connect.desktop.client

import icu.takeneko.omms.client.data.system.FileSystemInfo
import icu.takeneko.omms.client.data.system.MemoryInfo
import icu.takeneko.omms.client.data.system.NetworkInfo
import icu.takeneko.omms.client.data.system.ProcessorInfo
import icu.takeneko.omms.client.data.system.StorageInfo
import icu.takeneko.omms.client.data.system.SystemInfo
import icu.takeneko.omms.client.session.ClientSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed interface FetchSystemInfoState {
    data object Fetching : FetchSystemInfoState
    data class Error(val e: Throwable) : FetchSystemInfoState
    data class Success(val info: SystemInfo) : FetchSystemInfoState
}

suspend fun fetchSystemInfoFromServer(
    session: ClientSession?,
    stateListener: (FetchSystemInfoState) -> Unit
) {
    withContext(Dispatchers.IO) {
        if (session == null) {
            delay(1000)
            stateListener(FetchSystemInfoState.Success(exampleSystemInfo))
            return@withContext
        }
        try {
            ensureActive()
            future {
                session.fetchSystemInfoFromServer {
                    try {
                        stateListener(FetchSystemInfoState.Success(it))
                    } catch (e: Throwable) {
                        stateListener(FetchSystemInfoState.Error(e))
                    }
                }
            }.orTimeout(1, TimeUnit.MINUTES)
        } catch (e: Throwable) {
            stateListener(FetchSystemInfoState.Error(e))
        }
    }
}

private val exampleSystemInfo = SystemInfo(
    "ExampleOs",
    "v.example.0.0.0",
    "example64",
    FileSystemInfo().apply {
        fileSystemList.addAll(listOf(
            FileSystemInfo.FileSystem(
                10000,
                10240,
                "volume",
                "/",
                "ExampleFileSystem"
            ),
            FileSystemInfo.FileSystem(
                10000,
                10240,
                "volume",
                "/example",
                "ExampleFileSystem"
            )
        ))
    },
    MemoryInfo().apply {
        memoryTotal = 10240
        memoryUsed = 1024
        swapTotal = 10240
        swapUsed = 1024
    },
    NetworkInfo(
        "host",
        "domain",
        arrayOf("1.1.1.1"),
        "0.0.0.0",
        "1::"
    ),
    ProcessorInfo().apply {
        processorName = "Example CPU"
        processorId = "id"
        physicalCPUCount = 8
        logicalProcessorCount = 16
        cpuLoadAvg = 25.0
        cpuTemp = 32.0
    },
    StorageInfo()
)
