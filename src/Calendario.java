import java.util.*;
public final class Calendario implements Iterable<Evento> {
    private static final Comparator<Evento> ORDEN = Comparator
        .comparing((Evento e) -> e.getProgramacion().fecha())
        .thenComparingInt(e -> e.getProgramacion().hora())
        .thenComparing(e -> e.getProgramacion().sede(), String.CASE_INSENSITIVE_ORDER)
        .thenComparing(Evento::getCodigo);
    private final List<Evento> eventos;
    public Calendario(Collection<Evento> todos) {
        List<Evento> lista = new ArrayList<>();
        for (Evento e : todos) if (e.getProgramacion() != null) lista.add(e);
        lista.sort(ORDEN); eventos = List.copyOf(lista);
    }
    public void validarDisponibilidad(Evento propio, Programacion nueva) {
        for (Evento otro : eventos) {
            if (otro == propio) continue;
            Programacion p = otro.getProgramacion();
            if (!p.fecha().equals(nueva.fecha()) || p.hora() != nueva.hora()) continue;
            Validacion.exigir(!p.sede().equalsIgnoreCase(nueva.sede()), "Conflicto: sede ocupada por " + otro.getCodigo());
            boolean coinciden = otro.getParticipantes().stream().anyMatch(a -> propio.getParticipantes().stream().anyMatch(b -> a.getCodigo().equals(b.getCodigo())));
            Validacion.exigir(!coinciden, "Conflicto: participante ocupado en " + otro.getCodigo());
        }
    }
    @Override public Iterator<Evento> iterator() { return eventos.iterator(); }
    public int size() { return eventos.size(); }
}
