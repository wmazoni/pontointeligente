package com.wmazoni.pontointeligente.repositories

import com.wmazoni.pontointeligente.documents.Funcionario
import org.springframework.data.mongodb.repository.MongoRepository

interface FuncionarioRepository: MongoRepository<Funcionario, String> {

    fun findByCpf(cpf: String): Funcionario?
    fun findByEmail(email: String): Funcionario?

}