package pro.darc.cake.module.extensions

inline infix fun<T> Boolean.then(action: () -> T): T? {
    return if (this) action()
    else null
}
