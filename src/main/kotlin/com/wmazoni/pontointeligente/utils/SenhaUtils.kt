package com.wmazoni.pontointeligente.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class SenhaUtils {
    fun gerarBcrypt(senha: String): String = BCryptPasswordEncoder().encode(senha)
}