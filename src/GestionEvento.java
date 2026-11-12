import java.time.LocalDate;
import java.util.*;
/** Componente interno compartido mediante composición, sin herencia de eventos. */
final class GestionEvento {
    private final String codigo;
    private final Torneo torneo;
    private final List<Participante> participantes;
    private EstadoEvento estado = EstadoEvento.SIN_PROGRAMAR;
    private Programacion programacion;
    private DatosResultado resultado;
    private List<Aporte> aportes = List.of();
    GestionEvento(String codigo, Torneo torneo, List<Participante> participantes) {
        this.codigo = Validacion.codigo(codigo);
        Validacion.exigir(torneo != null && participantes != null && !participantes.isEmpty(), "Evento incompleto.");
        this.torneo = torneo; this.participantes = List.copyOf(participantes);
    }
    String getCodigo() { return codigo; }
    Torneo getTorneo() { return torneo; }
    List<Participante> getParticipantes() { return participantes; }
    EstadoEvento getEstado() { return estado; }
    Programacion getProgramacion() { return programacion; }
    List<Aporte> getAportes() { return aportes; }
    java.util.Optional<DatosResultado> getResultado() { return java.util.Optional.ofNullable(resultado); }
    void programar(Evento propio, LocalDate fecha, int hora, String sede, Calendario calendario) {
        Validacion.exigir(estado != EstadoEvento.FINALIZADO, "Un evento finalizado no se reprograma.");
        Validacion.exigir(torneo.getEstado() == EstadoTorneo.EN_JUEGO, "El torneo no está en juego.");
        Programacion nueva = new Programacion(fecha, hora, sede);
        calendario.validarDisponibilidad(propio, nueva);
        programacion = nueva; estado = EstadoEvento.PROGRAMADO;
    }
    void registrarResultado(DatosResultado datos) {
        Validacion.exigir(estado == EstadoEvento.PROGRAMADO, "Solo un evento programado y sin resultado acepta resultados.");
        Validacion.exigir(torneo.getEstado() == EstadoTorneo.EN_JUEGO, "El torneo no está en juego.");
        List<Aporte> nuevos = List.copyOf(torneo.getDisciplina().evaluar(participantes, datos));
        // Todas las validaciones terminan antes de publicar el resultado.
        resultado = datos; aportes = nuevos; estado = EstadoEvento.FINALIZADO;
        torneo.actualizarFinalizacion();
    }
}
