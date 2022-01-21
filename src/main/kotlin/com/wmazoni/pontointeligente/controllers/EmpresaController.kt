package com.wmazoni.pontointeligente.controllers

import com.wmazoni.pontointeligente.documents.Empresa
import com.wmazoni.pontointeligente.dtos.EmpresaDTO
import com.wmazoni.pontointeligente.response.Response
import com.wmazoni.pontointeligente.services.EmpresaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/empresas")
class EmpresaController(val empresaService: EmpresaService) {

    @GetMapping("/cnpj/{cnpj}")
    fun buscarPorCnpj(@PathVariable("cnpj") cnpj: String): ResponseEntity<Response<EmpresaDTO>> {
        val response: Response<EmpresaDTO> = Response<EmpresaDTO>()
        val empresa: Empresa? = empresaService.findByCnpj(cnpj)

        if (empresa == null) {
            response.errors.add("Empresa não econtrada para o CNPJ ${cnpj}")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = converterEmpresaDto(empresa)
        return ResponseEntity.ok(response)
    }

    private fun converterEmpresaDto(empresa: Empresa): EmpresaDTO =
        EmpresaDTO(empresa.razaoSocial, empresa.cnpj, empresa.id)

}