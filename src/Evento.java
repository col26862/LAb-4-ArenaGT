import java.time.LocalDate;
import java.util.List;
public interface Evento {
    String getCodigo();
    Torneo getTorneo();
    List<Participante> getParticipantes();
    EstadoEvento getEstado();
    Programacion getProgramacion();
    List<Aporte> getAportes();
    java.util.Optional<DatosResultado> getResultado();
    void programar(LocalDate fecha, int hora, String sede, Calendario calendario);
    void registrarResultado(DatosResultado datos);
}
