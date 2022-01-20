package com.wmazoni.pontointeligente.services.impl

import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.repositories.FuncionarioRepository
import com.wmazoni.pontointeligente.services.FuncionarioService
import org.springframework.stereotype.Service

@Service
class FuncionarioServiceImpl(val funcionarioRepository: FuncionarioRepository) : FuncionarioService {
    override fun insert(funcionario: Funcionario): Funcionario = funcionarioRepository.save(funcionario)

    override fun findByCpf(cpf: String): Funcionario? = funcionarioRepository.findByCpf(cpf)

    override fun findByEmail(email: String): Funcionario? = funcionarioRepository.findByEmail(email)

    override fun findById(id: String): Funcionario? = funcionarioRepository.findById(id).get()
}