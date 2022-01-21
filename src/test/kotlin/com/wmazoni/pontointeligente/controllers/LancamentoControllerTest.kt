package com.wmazoni.pontointeligente.controllers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.documents.Lancamento
import com.wmazoni.pontointeligente.dtos.LancamentoDTO
import com.wmazoni.pontointeligente.enums.PerfilEnum
import com.wmazoni.pontointeligente.enums.TipoEnum
import com.wmazoni.pontointeligente.services.FuncionarioService
import com.wmazoni.pontointeligente.services.LancamentoService
import com.wmazoni.pontointeligente.utils.SenhaUtils
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.text.SimpleDateFormat
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureDataMongo
class LancamentoControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    @MockBean
    private val lancamentoService: LancamentoService? = null

    @MockBean
    private val funcionarioService: FuncionarioService? = null

    private val urlBase: String = "/api/lancamentos/"
    private val idFuncionario: String = "1"
    private val idLancamento: String = "1"
    private val tipo: String = TipoEnum.INICIO_TRABALHO.name
    private val data: Date = Date()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @Test
    @WithMockUser
    @Throws(Exception::class)
    fun testCadastrarLancamento() {
        val lancamento: Lancamento = obterDadosLancamento()
        BDDMockito.given<Funcionario>(funcionarioService?.findById(idFuncionario))
            .willReturn(funcionario())
        BDDMockito.given(lancamentoService?.insert(obterDadosLancamento()))
            .willReturn(lancamento)

        mvc!!.perform(
            MockMvcRequestBuilders.post(urlBase)
            .content(obterJsonRequisicaoPost())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.tipo").value(tipo))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.data").value(dateFormat.format(data)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.funcionarioId").value(idFuncionario))
            .andExpect(MockMvcResultMatchers.jsonPath("$.erros").isEmpty())
    }

    @Test
    @WithMockUser
    @Throws(Exception::class)
    fun testCadastrarLancamentoFuncionarioIdInvalido() {
        BDDMockito.given<Funcionario>(funcionarioService?.findById(idFuncionario))
            .willReturn(null)

        mvc!!.perform(
            MockMvcRequestBuilders.post(urlBase)
            .content(obterJsonRequisicaoPost())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.erros").value("Funcionário não encontrado. ID inexistente."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = arrayOf("ADMIN"))
    @Throws(Exception::class)
    fun testRemoverLancamento() {
        BDDMockito.given<Lancamento>(lancamentoService?.findById(idLancamento))
            .willReturn(obterDadosLancamento())

        mvc!!.perform(
            MockMvcRequestBuilders.delete(urlBase + idLancamento)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @WithMockUser
    @Throws(Exception::class)
    fun testRemoverLancamentoAcessoNegado() {
        BDDMockito.given<Lancamento>(lancamentoService?.findById(idLancamento))
            .willReturn(obterDadosLancamento())

        mvc!!.perform(
            MockMvcRequestBuilders.delete(urlBase + idLancamento)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isForbidden())
    }

    @Throws(JsonProcessingException::class)
    private fun obterJsonRequisicaoPost(): String {
        val lancamentoDto: LancamentoDTO = LancamentoDTO(
            dateFormat.format(data), tipo, "Descrição",
            "1.234,4.234", idFuncionario)
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(lancamentoDto)
    }

    private fun obterDadosLancamento(): Lancamento =
        Lancamento(data, TipoEnum.valueOf(tipo), idFuncionario,
            "Descrição", "1.243,4.345", idLancamento)

    private fun funcionario(): Funcionario =
        Funcionario("Nome", "email@email.com", SenhaUtils().gerarBcrypt("123456"),
            "23145699876", PerfilEnum.ROLE_USUARIO, idFuncionario)

}