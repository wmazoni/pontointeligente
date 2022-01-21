package com.wmazoni.pontointeligente.controllers

import com.wmazoni.pontointeligente.documents.Empresa
import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.dtos.CadastroPFDTO
import com.wmazoni.pontointeligente.enums.PerfilEnum
import com.wmazoni.pontointeligente.response.Response
import com.wmazoni.pontointeligente.services.EmpresaService
import com.wmazoni.pontointeligente.services.FuncionarioService
import com.wmazoni.pontointeligente.utils.SenhaUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/cadastrar-pf")
class CadastroPFController(val empresaService: EmpresaService,
                           val funcionarioService: FuncionarioService
) {

    @PostMapping
    fun cadastrar(@Valid @RequestBody cadastroPFDto: CadastroPFDTO,
                  result: BindingResult
    ): ResponseEntity<Response<CadastroPFDTO>> {
        val response: Response<CadastroPFDTO> = Response<CadastroPFDTO>()

        val empresa: Empresa? = empresaService.findByCnpj(cadastroPFDto.cnpj)
        validarDadosExistentes(cadastroPFDto, empresa, result)

        if (result.hasErrors()) {
            result.allErrors.forEach { erro -> erro.defaultMessage?.let { response.errors.add(it) } }

            return ResponseEntity.badRequest().body(response)
        }

        var funcionario: Funcionario = converterDtoParaFuncionario(cadastroPFDto, empresa!!)

        funcionario = funcionarioService.insert(funcionario)
        response.data = converterCadastroPFDto(funcionario, empresa!!)

        return ResponseEntity.ok(response)
    }

    private fun validarDadosExistentes(cadastroPFDto: CadastroPFDTO, empresa: Empresa?,
                                       result: BindingResult
    ) {
        if (empresa == null) {
            result.addError(ObjectError("empresa", "Empresa não cadastrada."))
        }

        val funcionarioCpf: Funcionario? = funcionarioService.findByCpf(cadastroPFDto.cpf)
        if (funcionarioCpf != null) {
            result.addError(ObjectError("funcionario", "CPF já existente."))
        }

        val funcionarioEmail: Funcionario? = funcionarioService.findByEmail(cadastroPFDto.email)
        if (funcionarioEmail != null) {
            result.addError(ObjectError("funcionario", "Email já existente."))
        }
    }

    private fun converterDtoParaFuncionario(cadastroPFDto: CadastroPFDTO, empresa: Empresa) =
        Funcionario(cadastroPFDto.nome, cadastroPFDto.email,
            SenhaUtils().gerarBcrypt(cadastroPFDto.senha), cadastroPFDto.cpf,
            PerfilEnum.ROLE_USUARIO, empresa.id.toString(),
            cadastroPFDto.valorHora?.toDouble(), cadastroPFDto.qtdHorasTrabalhoDia?.toFloat(),
            cadastroPFDto.qtdHorasAlmoco?.toFloat(), cadastroPFDto.id)


    private fun converterCadastroPFDto(funcionario: Funcionario, empresa: Empresa): CadastroPFDTO  =
        CadastroPFDTO(funcionario.nome, funcionario.email, "", funcionario.cpf,
            empresa.cnpj, empresa.id.toString(),funcionario.valorHora.toString(),
            funcionario.qtdHorasTrabalhoDia.toString(),
            funcionario.qtdHorasTrabalhoDia.toString(),
            funcionario.id)
}