package com.wmazoni.pontointeligente.services

import com.wmazoni.pontointeligente.documents.Lancamento
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface LancamentoService {

    fun findByFuncionarioId(funcionarioId: String, pageRequest: PageRequest): Page<Lancamento>

    fun findById(id: String): Lancamento?

    fun insert(lancamento: Lancamento): Lancamento

    fun delete(id: String)
}