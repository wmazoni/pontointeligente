package com.wmazoni.pontointeligente.services

import com.wmazoni.pontointeligente.documents.Empresa

interface EmpresaService {
    fun findByCnpj(cnpj: String): Empresa?
    fun insert(empresa: Empresa): Empresa
}