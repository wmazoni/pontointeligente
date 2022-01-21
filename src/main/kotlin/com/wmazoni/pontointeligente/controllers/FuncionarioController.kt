package com.wmazoni.pontointeligente.controllers

import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.dtos.FuncionarioDTO
import com.wmazoni.pontointeligente.response.Response
import com.wmazoni.pontointeligente.services.FuncionarioService
import com.wmazoni.pontointeligente.utils.SenhaUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/funcionarios")
class FuncionarioController(val funcionarioService: FuncionarioService) {

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: String, @Valid @RequestBody funcionarioDto: FuncionarioDTO,
        result: BindingResult
    ): ResponseEntity<Response<FuncionarioDTO>> {

        val response: Response<FuncionarioDTO> = Response<FuncionarioDTO>()
        val funcionario: Funcionario? = funcionarioService.findById(id)

        if (funcionario == null) {
            result.addError(ObjectError("funcionario", "Funcionário não encontrado."))
        }

        if (result.hasErrors()) {
            result.allErrors.forEach { erro -> erro.defaultMessage?.let { response.errors.add(it) } }
            return ResponseEntity.badRequest().body(response)
        }

        val funcAtualizar: Funcionario = atualizarDadosFuncionario(funcionario!!, funcionarioDto)
        funcionarioService.insert(funcAtualizar)
        response.data = converterFuncionarioDto(funcAtualizar)

        return ResponseEntity.ok(response)
    }

    private fun atualizarDadosFuncionario(
        funcionario: Funcionario,
        funcionarioDto: FuncionarioDTO
    ): Funcionario {
        var senha: String
        if (funcionarioDto.senha == null) {
            senha = funcionario.senha
        } else {
            senha = SenhaUtils().gerarBcrypt(funcionarioDto.senha)
        }

        return Funcionario(
            funcionarioDto.nome, funcionario.email, senha,
            funcionario.cpf, funcionario.perfil, funcionario.empresaId,
            funcionarioDto.valorHora?.toDouble(),
            funcionarioDto.qtdHorasTrabalhoDia?.toFloat(),
            funcionarioDto.qtdHorasAlmoco?.toFloat(),
            funcionario.id
        )
    }

    private fun converterFuncionarioDto(funcionario: Funcionario): FuncionarioDTO =
        FuncionarioDTO(
            funcionario.nome, funcionario.email, "",
            funcionario.valorHora.toString(), funcionario.qtdHorasTrabalhoDia.toString(),
            funcionario.qtdHorasAlmoco.toString(), funcionario.id
        )

}