package com.app.crudapp.repository;

import com.app.crudapp.domain.model.PersonaRegistro;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonaRegistroRepository extends MongoRepository<PersonaRegistro, String> {

    PersonaRegistro findByUsuario(String usuario);
}