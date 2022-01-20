package com.wmazoni.pontointeligente.services

import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.enums.PerfilEnum
import com.wmazoni.pontointeligente.repositories.FuncionarioRepository
import com.wmazoni.pontointeligente.utils.SenhaUtils
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataMongo
class FuncionarioServiceTest {

    @MockBean
    private val funcionarioRepository: FuncionarioRepository? = null

    @Autowired
    private val funcionarioService: FuncionarioService? = null

    private val email: String = "email@email.com"
    private val cpf: String = "34234855948"
    private val id: String = "1"

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        BDDMockito.given(funcionarioRepository?.save(Mockito.any(Funcionario::class.java)))
            .willReturn(funcionario())
        BDDMockito.given(funcionarioRepository?.findById(id)).willReturn(Optional.of(funcionario()))
        BDDMockito.given(funcionarioRepository?.findByEmail(email)).willReturn(funcionario())
        BDDMockito.given(funcionarioRepository?.findByCpf(cpf)).willReturn(funcionario())
    }

    @Test
    fun testPersistirFuncionario() {
        val funcionario: Funcionario? = this.funcionarioService?.insert(funcionario())
        assertNotNull(funcionario)
    }

    @Test
    fun testBuscarFuncionarioPorId() {
        val funcionario: Funcionario? = this.funcionarioService?.findById(id)
        assertNotNull(funcionario)
    }

    @Test
    fun testBuscarFuncionarioPorEmail() {
        val funcionario: Funcionario? = this.funcionarioService?.findByEmail(email)
        assertNotNull(funcionario)
    }

    @Test
    fun testBuscarFuncionarioPorCpf() {
        val funcionario: Funcionario? = this.funcionarioService?.findByCpf(cpf)
        assertNotNull(funcionario)
    }

    private fun funcionario(): Funcionario =
        Funcionario(
            "Nome", email, SenhaUtils().gerarBcrypt("123456"),
            cpf, PerfilEnum.ROLE_USUARIO, id
        )
}