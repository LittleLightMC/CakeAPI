package pro.darc.cake.utils

inline fun <reified C> Any.cast(): C = this as C
inline fun <reified C> Any.castOrNull(): C? = this as? C
