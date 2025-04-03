package com.app.crudapp.service;

import com.app.crudapp.domain.model.PersonaRegistro;
import com.app.crudapp.exception.UsuarioNoEncontradoException;
import com.app.crudapp.repository.PersonaRegistroRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaRegistroService {

    @Autowired
    private PersonaRegistroRepository personaRegistroRepository;

    public List<PersonaRegistro> findAll() {
        return personaRegistroRepository.findAll();
    }

    public PersonaRegistro save(PersonaRegistro personaRegistro) {
        return personaRegistroRepository.save(personaRegistro);
    }

    public PersonaRegistro buscarPorUsuario(String usuario){
        return personaRegistroRepository.findByUsuario(usuario);
    }

    public PersonaRegistro buscarPorId(String id){
        Optional<PersonaRegistro> persona = personaRegistroRepository.findById(id);

        return persona.orElseThrow(() -> new UsuarioNoEncontradoException("ID no válido: " + id));

    }

    public ResponseEntity<?> deleteById(String id) {
        try {
            personaRegistroRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Persona eliminada satisfactoriamente: " + id);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada: " + id);
        }
    }

    public PersonaRegistro validarInicio(String usuario, String contraseña){

        PersonaRegistro persona = personaRegistroRepository.findByUsuario(usuario);
        if(persona != null) {
            if (BCrypt.checkpw(contraseña, persona.getContraseña())) {
                return persona;
            } else {
                return null;
            }
        }else{
            return null;
        }
    }

    public Boolean validarRegistro(PersonaRegistro personaRegistro){
        PersonaRegistro persona = personaRegistroRepository.findByUsuario(personaRegistro.getUsuario());

        if(persona != null){
            return false;
        }else{
            return true;
        }
    }

}
