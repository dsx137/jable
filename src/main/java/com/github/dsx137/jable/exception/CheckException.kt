package com.github.dsx137.jable.exception

import java.util.function.Function

/**
 * # 用来检查点什么东西
 */
class CheckException @JvmOverloads constructor(
    state: String, // state是检查失败的状态，比如"无效的密码"
    reason: String, // 检查失败的原因
    attachment: Any? = null, // 附加对象
    extractor: (Any?) -> String = Any?::toString, // 附加对象的提取器
) : IllegalArgumentException("$state: $reason${attachment?.let { " -> [${extractor(attachment)}]" } ?: ""}") {

    companion object {

        /**
         * # 检查候选者能否通过验证
         *
         * ```kotlin
         * fun checkMailSyntax(mail: String?) {
         *     CheckException.check("无效的邮箱", mail, listOf(
         *         "邮箱不能为空" to { it.isNullOrEmpty() },
         *         "邮箱格式不正确" to { !it!!.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$".toRegex()) }
         *     ))
         * }
         *```
         *
         * @param state      失败状态信息
         * @param candidate  待检查对象
         * @param validators 验证器列表
         * @throws CheckException 如果候选者的状态未通过验证，则抛出异常
         */
        @JvmName("checkWithBooleanValidators")
        fun <T> check(state: String, candidate: T, validators: List<Pair<String, T.() -> Boolean>>) {
            validators.firstOrNull { (_, validator) -> validator(candidate) }?.let {
                throw CheckException(state, it.first, candidate)
            }
        }

        /**
         * # 兼容Java的方法
         *
         * ```java
         * String username = " ";
         * CheckException.check("tata", username, List.of(
         *         candidate -> candidate.equals("小明")? "小明已经存在" : null,
         *         candidate -> candidate.equals("小红")? "小红已经存在" : null
         * ));
         * ```
         *
         * @param state      失败状态信息
         * @param candidate  待检查对象
         * @param validators 验证器列表
         * @throws CheckException 如果候选者的状态未通过验证，则抛出异常
         */
        @JvmStatic
        @JvmName("checkWithStringValidators")
        fun <T> check(state: String, candidate: T, validators: List<Function<T, String?>>) {
            validators.forEach { validator ->
                validator.apply(candidate)?.let { throw CheckException(state, it, candidate) }
            }
        }
    }
}

infix fun <T> String.to(function: (T) -> Boolean): Pair<String, Function<T, Boolean>> {
    return Pair(this, Function(function))
}
