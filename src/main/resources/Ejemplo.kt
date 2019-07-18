package com.ahorraseguros.kotlinmark43.controllers.catalogosTI

import com.ahorraseguros.kotlinmark43.models.catalogosTI.EquiposAsModel

import com.ahorraseguros.kotlinmark43.repositories.catalogosTI.EquipoAsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import kotlin.collections.HashMap
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@CrossOrigin
@RestController
@RequestMapping("/v1/equipos")
class EquipoAsController {

    @Autowired
    lateinit var equipoAsRepository: EquipoAsRepository

    @GetMapping
    fun findAll(): MutableList<EquiposAsModel> = equipoAsRepository.findAll()

    @GetMapping("{id}")
    fun finById(@PathVariable("id") id: Long): ResponseEntity<Any> {
        val response = HashMap<String, Any>()
        val e: Optional<EquiposAsModel>
        try {
            e = equipoAsRepository.findById(id)
        } catch (e: DataAccessException) {
            response["message"] = "Something go wrong consulting database"
            response["error"] = "${e.message} : ${e.mostSpecificCause.message}"
            return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        if (!e.isPresent) {
            response["message"] = "The id: ${id} not exist!"
            return ResponseEntity(response, HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(e.get(), HttpStatus.OK)

    }

    @PostMapping
    fun create(@Valid @RequestBody tipoUsuarioModels: EquiposAsModel): ResponseEntity<Any>{
        val response = HashMap<String, Any>()
        val e:EquiposAsModel
        try {
            e = equipoAsRepository.save(tipoUsuarioModels)
        } catch (e: DataAccessException) {
            response["message"] = "Something go wrong inserting at database"
            response["error"] = "${e.message} : ${e.mostSpecificCause.message}"
            return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        response["message"] = "The equipo has been created"
        response["equipo"] = e
        return  ResponseEntity(response, HttpStatus.CREATED)
    }


}