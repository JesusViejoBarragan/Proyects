package com.app.crudapp.controller;

import com.app.crudapp.domain.model.PersonaRegistro;
import com.app.crudapp.service.PersonaRegistroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;


@Controller
public class UserController {

    @Autowired
    private PersonaRegistroService personaRegistroService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/inicioSesion")
    public String inicioSesion() {
        return "inicio-sesion";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<PersonaRegistro> personas = personaRegistroService.findAll();
        model.addAttribute("personas", personas);

        return "admin";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        PersonaRegistro persona = (PersonaRegistro) session.getAttribute("usuario");

        if (persona == null) {
            return "redirect:/inicioSesion";
        }
        model.addAttribute("persona", persona); // Pasar objeto al HTML
        return "perfil";
    }

    @GetMapping("/modificarPerfil")
    public String modificarPerfil(HttpSession session, Model model) {

        PersonaRegistro persona = (PersonaRegistro) session.getAttribute("usuario");
        model.addAttribute("persona", persona); // Pasar objeto al HTML

        return "modificarDatos";
    }

    @GetMapping("/modificarPerfilADMIN")
    public String modificarPerfilADMIN(HttpSession session, Model model) {

        PersonaRegistro persona = (PersonaRegistro) session.getAttribute("usuario");
        model.addAttribute("persona", persona);

        return "modificarDatosADMIN";
    }

    @PostMapping("/registro")
    public String registrarPersona(PersonaRegistro personaRegistro) {

        if(personaRegistroService.validarRegistro(personaRegistro)) {
            personaRegistro.setRol("USER");
            //Encriptamos la contraseña
            String contraseña = BCrypt.hashpw(personaRegistro.getContraseña(), BCrypt.gensalt());
            personaRegistro.setContraseña(contraseña);

            System.out.println("Registrando: " + personaRegistro.getUsuario());
            personaRegistroService.save(personaRegistro);

            return "redirect:/inicioSesion";
        }
        else{
            System.out.println("Ese nombre de usuario ya está registrado");
            return "redirect:/registro";
        }
    }

    @PostMapping("/inicioSesion")
    public String procesarLogin(
            @RequestParam("usuario") String usuario,
            @RequestParam("contraseña") String contraseña,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        PersonaRegistro personaRegistro = personaRegistroService.buscarPorUsuario(usuario);

        if(usuario != null && contraseña != null){
            if(personaRegistroService.validarInicio(usuario, contraseña) != null){
                if(personaRegistro.getRol().equals("ADMIN")){
                    System.out.println("Credenciales correctas");
                    return "redirect:/admin";
                }else {
                    session.setAttribute("usuario", personaRegistro);
                    System.out.println("Credenciales correctas");
                    return "redirect:/perfil";
                }
            }else{
                redirectAttrs.addFlashAttribute("error", "Credenciales incorrectas");
                System.out.println("Credenciales incorrectas");
                return "redirect:/inicioSesion";
            }
        }else{
            return "redirect:/inicioSesion";
        }

    }

    @PostMapping("/modificarPerfil")
    public String modificarDatos(@RequestParam String nombre,
                                 @RequestParam String apellidos,
                                 @RequestParam(required = false) String contraseñaActual,
                                 @RequestParam(required = false) String nuevaContraseña,
                                 @RequestParam(required = false) String confirmarContraseña,
                                 HttpSession session,
                                 RedirectAttributes redirectAttrs) {

        PersonaRegistro persona = (PersonaRegistro) session.getAttribute("usuario");

        persona.setNombre(nombre);
        persona.setApellidos(apellidos);

        // Validar cambio de contraseña (solo si se completaron los campos)
        if (contraseñaActual != null && !contraseñaActual.isEmpty() &&
                nuevaContraseña != null && !nuevaContraseña.isEmpty()) {

            // Verificar contraseña actual
            if (!BCrypt.checkpw(contraseñaActual, persona.getContraseña())) {
                redirectAttrs.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/modificarPerfil";
            }

            // Verificar coincidencia de nuevas contraseñas
            if (!nuevaContraseña.equals(confirmarContraseña)) {
                redirectAttrs.addFlashAttribute("error", "Las nuevas contraseñas no coinciden");
                return "redirect:/modificarPerfil";
            }
            String contraseñaEncriptada = BCrypt.hashpw(nuevaContraseña, BCrypt.gensalt());
            persona.setContraseña(contraseñaEncriptada);
        }
        // Guardar cambios
        personaRegistroService.save(persona);
        session.setAttribute("usuario", persona); // Actualizar sesión

        redirectAttrs.addFlashAttribute("exito", "Perfil actualizado correctamente");

        return "redirect:/perfil";
    }

    @PostMapping("/perfil")
    public String borrarUsuario(HttpSession session){
        PersonaRegistro persona = (PersonaRegistro) session.getAttribute("usuario");

        personaRegistroService.deleteById(persona.getId());
        System.out.println("Borrado correctamente");
        return "redirect:/";
    }

    @PostMapping("/admin")
    public String borrarUsuarioDesdeAdmin(HttpSession session, String id){
        PersonaRegistro persona = (PersonaRegistro) session.getAttribute(id);

        personaRegistroService.deleteById(id);
        System.out.println("Borrado correctamente");
        return "redirect:/admin";
    }

    @PostMapping("/admin/seleccionarUsuario")
    public String seleccionarUsuario(@RequestParam String id, HttpSession session) {
        PersonaRegistro usuarioSeleccionado = personaRegistroService.buscarPorId(id); // Busca el usuario en DB
        session.setAttribute("usuario", usuarioSeleccionado); // Actualiza la sesión
        return "redirect:/modificarPerfilADMIN"; // Redirige al formulario de edición
    }

    @PostMapping("/modificarPerfilADMIN")
    public String modificarDatosADMIN(@RequestParam String nombre,
                                 @RequestParam String apellidos,
                                 @RequestParam String rol,
                                 @RequestParam(required = false) String contraseñaActual,
                                 @RequestParam(required = false) String nuevaContraseña,
                                 @RequestParam(required = false) String confirmarContraseña,
                                 HttpSession session,
                                 RedirectAttributes redirectAttrs) {

        PersonaRegistro persona = (PersonaRegistro) session.getAttribute("usuario");

        persona.setNombre(nombre);
        persona.setApellidos(apellidos);
        persona.setRol(rol);

        // Validar cambio de contraseña (solo si se completaron los campos)
        if (contraseñaActual != null && !contraseñaActual.isEmpty() &&
                nuevaContraseña != null && !nuevaContraseña.isEmpty()) {

            // Verificar contraseña actual
            if (!BCrypt.checkpw(contraseñaActual, persona.getContraseña())) {
                redirectAttrs.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/modificarPerfilADMIN";
            }

            // Verificar coincidencia de nuevas contraseñas
            if (!nuevaContraseña.equals(confirmarContraseña)) {
                redirectAttrs.addFlashAttribute("error", "Las nuevas contraseñas no coinciden");
                return "redirect:/modificarPerfilADMIN";
            }
            String contraseñaEncriptada = BCrypt.hashpw(nuevaContraseña, BCrypt.gensalt());
            persona.setContraseña(contraseñaEncriptada);
        }
        // Guardar cambios
        personaRegistroService.save(persona);
        session.setAttribute("usuario", persona); // Actualizar sesión

        redirectAttrs.addFlashAttribute("exito", "Perfil actualizado correctamente");

        return "redirect:/admin";
    }

}
