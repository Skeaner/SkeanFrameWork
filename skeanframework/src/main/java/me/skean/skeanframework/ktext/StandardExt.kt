package me.skean.skeanframework.ktext


inline fun <R> runIf(condition: Boolean, defaultValue: R? = null, block: () -> R): R? {
    return if (condition) run(block) else defaultValue
}

inline fun <T, R> T.runIf(condition: Boolean, defaultValue: R? = null, block: T.() -> R): R? {
    return if (condition) this.run(block) else defaultValue
}

inline fun <T, R> withIf(receiver: T, condition: Boolean, defaultValue: R? = null, block: T.() -> R): R? {
    return if (condition) with(receiver, block) else defaultValue
}

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return if (condition) this.apply(block)
    else this
}

inline fun <T> T.alsoIf(condition: Boolean, block: (T) -> Unit): T {
    return if (condition) this.also(block)
    else this
}

inline fun <T, R> T.letIf(condition: Boolean, defaultValue: R? = null, block: (T) -> R): R? {
    return if (condition) this.let(block) else defaultValue
}

