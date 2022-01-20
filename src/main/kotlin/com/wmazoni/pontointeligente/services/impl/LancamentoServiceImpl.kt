package com.wmazoni.pontointeligente.services.impl

import com.wmazoni.pontointeligente.documents.Lancamento
import com.wmazoni.pontointeligente.repositories.LancamentoRepository
import com.wmazoni.pontointeligente.services.LancamentoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class LancamentoServiceImpl(val lancamentoRepository: LancamentoRepository) : LancamentoService {
    override fun findByFuncionarioId(funcionarioId: String, pageRequest: PageRequest) =
        lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest)

    override fun findById(id: String) = lancamentoRepository.findById(id).get()

    override fun insert(lancamento: Lancamento) = lancamentoRepository.save(lancamento)

    override fun delete(id: String) = lancamentoRepository.deleteById(id)
}