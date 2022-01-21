package com.wmazoni.pontointeligente.controllers

import com.wmazoni.pontointeligente.documents.Empresa
import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.dtos.CadastroPJDTO
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
@RequestMapping("/api/cadastrar-pj")
class CadastroPJController(val empresaService: EmpresaService,
                           val funcionarioService: FuncionarioService
) {

    @PostMapping
    fun cadastrar(@Valid @RequestBody cadastroPJDto: CadastroPJDTO,
                  result: BindingResult
    ): ResponseEntity<Response<CadastroPJDTO>> {
        val response: Response<CadastroPJDTO> = Response<CadastroPJDTO>()

        validarDadosExistentes(cadastroPJDto, result)
        if (result.hasErrors()) {
            result.allErrors.forEach { erro -> erro.defaultMessage?.let { response.errors.add(it) } }
            return ResponseEntity.badRequest().body(response)
        }

        var empresa: Empresa = converterDtoParaEmpresa(cadastroPJDto)
        empresa = empresaService.insert(empresa)

        var funcionario: Funcionario = converterDtoParaFuncionario(cadastroPJDto, empresa)
        funcionario = funcionarioService.insert(funcionario)
        response.data = converterCadastroPJDto(funcionario, empresa)

        return ResponseEntity.ok(response)
    }

    private fun validarDadosExistentes(cadastroPJDto: CadastroPJDTO, result: BindingResult) {
        val empresa: Empresa? = empresaService.findByCnpj(cadastroPJDto.cnpj)
        if (empresa != null) {
            result.addError(ObjectError("empresa", "Empresa já existente."))
        }

        val funcionarioCpf: Funcionario? = funcionarioService.findByCpf(cadastroPJDto.cpf)
        if (funcionarioCpf != null) {
            result.addError(ObjectError("funcionario", "CPF já existente."))
        }

        val funcionarioEmail: Funcionario? = funcionarioService.findByEmail(cadastroPJDto.email)
        if (funcionarioEmail != null) {
            result.addError(ObjectError("funcionario", "Email já existente."))
        }
    }

    private fun converterDtoParaEmpresa(cadastroPJDto: CadastroPJDTO): Empresa =
        Empresa(cadastroPJDto.razaoSocial, cadastroPJDto.cnpj)


    private fun converterDtoParaFuncionario(cadastroPJDto: CadastroPJDTO, empresa: Empresa) =
        Funcionario(cadastroPJDto.nome, cadastroPJDto.email,
            SenhaUtils().gerarBcrypt(cadastroPJDto.senha), cadastroPJDto.cpf,
            PerfilEnum.ROLE_ADMIN, empresa.id.toString())

    private fun converterCadastroPJDto(funcionario: Funcionario, empresa: Empresa): CadastroPJDTO =
        CadastroPJDTO(funcionario.nome, funcionario.email, "", funcionario.cpf,
            empresa.cnpj, empresa.razaoSocial, funcionario.id)

}