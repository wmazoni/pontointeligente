package com.wmazoni.pontointeligente.services

import com.wmazoni.pontointeligente.documents.Funcionario

interface FuncionarioService {

    fun insert(funcionario: Funcionario): Funcionario

    fun findByCpf(cpf: String): Funcionario?

    fun findByEmail(email: String): Funcionario?

    fun findById(id: String): Funcionario?
}