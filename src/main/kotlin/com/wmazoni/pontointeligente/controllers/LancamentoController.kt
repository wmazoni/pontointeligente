package com.wmazoni.pontointeligente.controllers

import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.documents.Lancamento
import com.wmazoni.pontointeligente.dtos.LancamentoDTO
import com.wmazoni.pontointeligente.enums.TipoEnum
import com.wmazoni.pontointeligente.response.Response
import com.wmazoni.pontointeligente.services.FuncionarioService
import com.wmazoni.pontointeligente.services.LancamentoService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import javax.validation.Valid

@RestController
@RequestMapping("/api/lancamentos")
class LancamentoController(
    val lancamentoService: LancamentoService,
    val funcionarioService: FuncionarioService
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @Value("\${paginacao.qtd_por_pagina}")
    val qtdPorPagina: Int = 15

    @GetMapping("/funcionario/{funcionarioId}")
    fun findByFuncionarioId(
        @PathVariable("funcionarioId") funcionarioId: String,
        @RequestParam(value = "pag", defaultValue = "0") pag: Int,
        @RequestParam(value = "ord", defaultValue = "id") ord: String,
        @RequestParam(value = "dir", defaultValue = "DESC") dir: String
    ):
            ResponseEntity<Response<Page<LancamentoDTO>>> {

        val response: Response<Page<LancamentoDTO>> = Response<Page<LancamentoDTO>>()

        val pageRequest: PageRequest = PageRequest.of(pag, qtdPorPagina, Sort.Direction.valueOf(dir), ord)
        val lancamentos: Page<Lancamento> =
            lancamentoService.findByFuncionarioId(funcionarioId, pageRequest)

        val lancamentosDto: Page<LancamentoDTO> =
            lancamentos.map { lancamento -> converterLancamentoDto(lancamento) }

        response.data = lancamentosDto
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: String): ResponseEntity<Response<LancamentoDTO>> {
        val response: Response<LancamentoDTO> = Response<LancamentoDTO>()
        val lancamento: Lancamento? = lancamentoService.findById(id)

        if (lancamento == null) {
            response.errors.add("Lançamento não encontrado para o id $id")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = converterLancamentoDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun insert(
        @Valid @RequestBody lancamentoDto: LancamentoDTO,
        result: BindingResult
    ): ResponseEntity<Response<LancamentoDTO>> {
        val response: Response<LancamentoDTO> = Response<LancamentoDTO>()
        validarFuncionario(lancamentoDto, result)

        if (result.hasErrors()) {
            //TODO for (erro in result.allErrors) response.erros.add(erro.defaultMessage)
            result.allErrors.forEach { erro -> erro.defaultMessage?.let { response.errors.add(it) } }
            return ResponseEntity.badRequest().body(response)
        }

        var lancamento: Lancamento = converterDtoParaLancamento(lancamentoDto, result)
        lancamento = lancamentoService.insert(lancamento)
        response.data = converterLancamentoDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: String, @Valid @RequestBody lancamentoDto: LancamentoDTO,
        result: BindingResult
    ): ResponseEntity<Response<LancamentoDTO>> {

        val response: Response<LancamentoDTO> = Response<LancamentoDTO>()
        validarFuncionario(lancamentoDto, result)
        lancamentoDto.id = id
        var lancamento: Lancamento = converterDtoParaLancamento(lancamentoDto, result)

        if (result.hasErrors()) {
            result.allErrors.forEach { erro -> erro.defaultMessage?.let { response.errors.add(it) } }
            return ResponseEntity.badRequest().body(response)
        }

        lancamento = lancamentoService.insert(lancamento)
        response.data = converterLancamentoDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping(value = ["/{id}"])
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Response<String>> {

        val response: Response<String> = Response<String>()
        val lancamento: Lancamento? = lancamentoService.findById(id)

        if (lancamento == null) {
            response.errors.add("Erro ao remover lançamento. Registro não encontrado para o id $id")
            return ResponseEntity.badRequest().body(response)
        }

        lancamentoService.delete(id)
        return ResponseEntity.ok(Response<String>())
    }

    private fun validarFuncionario(lancamentoDto: LancamentoDTO, result: BindingResult) {
        if (lancamentoDto.funcionarioId == null) {
            result.addError(ObjectError("funcionario", "Funcionário não informado."))
            return
        }

        val funcionario: Funcionario? = funcionarioService.findById(lancamentoDto.funcionarioId)
        if (funcionario == null) {
            result.addError(ObjectError("funcionario", "Funcionário não encontrado. ID inexistente."));
        }
    }

    private fun converterLancamentoDto(lancamento: Lancamento): LancamentoDTO =
        LancamentoDTO(
            dateFormat.format(lancamento.data), lancamento.tipo.toString(),
            lancamento.descricao, lancamento.localizacao,
            lancamento.funcionarioId, lancamento.id
        )

    private fun converterDtoParaLancamento(
        lancamentoDto: LancamentoDTO,
        result: BindingResult
    ): Lancamento {
        if (lancamentoDto.id != null) {
            val lanc: Lancamento? = lancamentoService.findById(lancamentoDto.id!!)
            if (lanc == null) result.addError(ObjectError("lancamento", "Lançamento não encontrado."))
        }

        return Lancamento(
            dateFormat.parse(lancamentoDto.data), TipoEnum.valueOf(lancamentoDto.tipo!!),
            lancamentoDto.funcionarioId!!, lancamentoDto.descricao,
            lancamentoDto.localizacao, lancamentoDto.id
        )
    }

}