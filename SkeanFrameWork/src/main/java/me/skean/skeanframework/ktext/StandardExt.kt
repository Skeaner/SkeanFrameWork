package me.skean.skeanframework.ktext


inline fun <R> runIf(condition: Boolean, block: () -> R?, orElse: () -> R? = { null }): R? {
    return if (condition) run(block) else run(orElse)
}

inline fun <T, R> T.runIf(condition: Boolean, block: T.() -> R?, orElse: T.() -> R? = { null }): R? {
    return if (condition) this.run(block) else run(orElse)
}

inline fun <T, R> withIf(receiver: T, condition: Boolean, block: T.() -> R?, orElse: T.() -> R? = { null }): R? {
    return if (condition) with(receiver, block) else with(receiver, orElse)
}

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit, orElse: T.() -> Unit = { }): T {
    return if (condition) this.apply(block) else this.apply(orElse)
}

inline fun <T> T.alsoIf(condition: Boolean, block: (T) -> Unit, orElse: (T) -> Unit = {}): T {
    return if (condition) this.also(block) else this.also(orElse)
}

inline fun <T, R> T.letIf(condition: Boolean, block: (T) -> R?, orElse: (T) -> R?): R? {
    return if (condition) this.let(block) else this.let(orElse)
}

