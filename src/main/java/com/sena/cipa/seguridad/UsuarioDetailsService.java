package com.sena.cipa.seguridad;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sena.cipa.dao.UsuarioDao;
import com.sena.cipa.modelo.Usuario;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioDao usuarioDao;

    public UsuarioDetailsService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Se acepta el correo con mayusculas o espacios de mas al inicio o al final.
        String limpio = correo == null ? "" : correo.trim();
        Usuario usuario = usuarioDao.findByCorreoIgnoreCase(limpio)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con el correo " + correo));
        return new UsuarioPrincipal(usuario);
    }
}
