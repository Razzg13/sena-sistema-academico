package com.sena.cipa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sena.cipa.config.CuentasDemo.Cuenta;
import com.sena.cipa.dao.UsuarioDao;
import com.sena.cipa.modelo.Usuario;
import com.sena.cipa.modelo.enums.EstadoGeneral;

@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;
    private final CuentasDemo cuentasDemo;

    public DatosIniciales(UsuarioDao usuarioDao, PasswordEncoder passwordEncoder, CuentasDemo cuentasDemo) {
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
        this.cuentasDemo = cuentasDemo;
    }

    @Override
    public void run(String... args) {
        for (Cuenta cuenta : CuentasDemo.CUENTAS) {
            Usuario usuario = usuarioDao.findByCorreoIgnoreCase(cuenta.correo()).orElse(null);
            if (usuario == null) {
                if (cuenta.seCreaSola()) {
                    usuarioDao.save(new Usuario(cuenta.documento(), cuenta.nombre(), cuenta.correo(),
                            null, passwordEncoder.encode(cuenta.clave()), cuenta.rol()));
                    log.info("Usuario creado -> correo: {} / clave: {} / rol: {}", cuenta.correo(), cuenta.clave(), cuenta.rol());
                }
                continue;
            }
            if (cuentasDemo.activo() && necesitaArreglo(usuario, cuenta)) {
                usuario.setClave(passwordEncoder.encode(cuenta.clave()));
                usuario.setRol(cuenta.rol());
                usuario.setEstado(EstadoGeneral.ACTIVO);
                usuarioDao.save(usuario);
                log.info("Cuenta de prueba restablecida -> correo: {} / clave: {} / rol: {}", cuenta.correo(), cuenta.clave(), cuenta.rol());
            }
        }
    }

    private boolean necesitaArreglo(Usuario usuario, Cuenta cuenta) {
        return usuario.getEstado() != EstadoGeneral.ACTIVO
                || usuario.getRol() != cuenta.rol()
                || usuario.getClave() == null
                || !passwordEncoder.matches(cuenta.clave(), usuario.getClave());
    }
}
