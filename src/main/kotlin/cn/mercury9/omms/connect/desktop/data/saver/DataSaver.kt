package cn.mercury9.omms.connect.desktop.data.saver

interface DataSaver<T> {

    /**
     * Load from file.
     * Use `get()` to only get from memory.
     */
    fun load(): T

    /**
     * Get from memory.
     * Use `load()` to load from file.
     */
    fun get(): T

    /**
     * Save from memory to file.
     */
    fun save(): DataSaver<T>

    /**
     * Set to memory, and save to file.
     */
    fun set(data: T): DataSaver<T>
}