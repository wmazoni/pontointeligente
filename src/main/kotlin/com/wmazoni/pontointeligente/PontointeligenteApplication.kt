package com.wmazoni.pontointeligente

import com.wmazoni.pontointeligente.documents.Empresa
import com.wmazoni.pontointeligente.documents.Funcionario
import com.wmazoni.pontointeligente.enums.PerfilEnum
import com.wmazoni.pontointeligente.repositories.EmpresaRepository
import com.wmazoni.pontointeligente.repositories.FuncionarioRepository
import com.wmazoni.pontointeligente.utils.SenhaUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PontointeligenteApplication(val empresaRepository: EmpresaRepository, val funcionarioRepository: FuncionarioRepository) :
	CommandLineRunner {
	override fun run(vararg args: String?) {
		empresaRepository.deleteAll()
		funcionarioRepository.deleteAll()

		var empresa: Empresa = Empresa("Empresa", "35417492000108")
		empresa = empresaRepository.save(empresa)

		var admin: Funcionario
				= Funcionario("Admin", "admin@empresa.com",
			SenhaUtils().gerarBcrypt("123456"), "42357696001",
			PerfilEnum.ROLE_ADMIN, empresa.id!!)
		admin = funcionarioRepository.save(admin)

		var funcionario: Funcionario
				= Funcionario("Funcionario", "funcionario@empresa.com",
			SenhaUtils().gerarBcrypt("654321"), "17347006023",
			PerfilEnum.ROLE_USUARIO, empresa.id!!)
		funcionario = funcionarioRepository.save(funcionario)

		println("Empresa ID: " + empresa.id)
		println("Admin ID: " + admin.id)
		println("Funcionario ID: " + funcionario.id)
	}
}

fun main(args: Array<String>) {
	runApplication<PontointeligenteApplication>(*args)
}
