import java.time.LocalDate;
import java.util.List;
public final class Encuentro implements Evento {
    private final GestionEvento gestion;
    public Encuentro(String codigo, Torneo torneo, Participante a, Participante b) {
        Validacion.exigir(a != null && b != null && !a.getCodigo().equals(b.getCodigo()), "Equipos distintos obligatorios.");
        gestion = new GestionEvento(codigo, torneo, List.of(a, b));
    }
    @Override public String getCodigo() { return gestion.getCodigo(); }
    @Override public Torneo getTorneo() { return gestion.getTorneo(); }
    @Override public List<Participante> getParticipantes() { return gestion.getParticipantes(); }
    @Override public EstadoEvento getEstado() { return gestion.getEstado(); }
    @Override public Programacion getProgramacion() { return gestion.getProgramacion(); }
    @Override public List<Aporte> getAportes() { return gestion.getAportes(); }
    @Override public java.util.Optional<DatosResultado> getResultado() { return gestion.getResultado(); }
    @Override public void programar(LocalDate fecha, int hora, String sede, Calendario calendario) { gestion.programar(this, fecha, hora, sede, calendario); }
    @Override public void registrarResultado(DatosResultado datos) { gestion.registrarResultado(datos); }
    @Override public String toString() {
        return getCodigo() + " | " + getTorneo().getNombre() + " | " + getParticipantes().stream().map(Participante::getNombre).collect(java.util.stream.Collectors.joining(" / ")) + " | " + (getProgramacion() == null ? "SIN PROGRAMAR" : getProgramacion()) + " | " + getEstado();
    }
}
