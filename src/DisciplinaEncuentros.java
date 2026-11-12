import java.math.BigDecimal;
import java.util.*;
public abstract class DisciplinaEncuentros extends Disciplina {
    protected DisciplinaEncuentros(String nombre, int minimo, int maximo, BigDecimal cuota) { super(nombre, minimo, maximo, cuota); }
    @Override public List<Evento> crearEventos(Torneo torneo) {
        List<Evento> eventos = new ArrayList<>();
        List<Participante> p = torneo.getInscritos();
        for (int i = 0; i < p.size(); i++) for (int j = i + 1; j < p.size(); j++)
            eventos.add(new Encuentro(torneo.getCodigo() + "-E" + (eventos.size() + 1), torneo, p.get(i), p.get(j)));
        return List.copyOf(eventos);
    }
    protected int[] marcador(List<Participante> p, DatosResultado d, boolean permitePenales) {
        Validacion.exigir(p.size() == 2, "Un encuentro requiere dos participantes.");
        Set<String> codigos = Set.of(p.get(0).getCodigo(), p.get(1).getCodigo());
        Validacion.exigir(d.getValores().keySet().equals(codigos) && d.getDescalificados().isEmpty(), "El resultado debe contener exactamente los dos equipos.");
        Validacion.exigir(permitePenales || d.getPenales().isEmpty(), "Esta disciplina no utiliza penales.");
        return new int[]{d.entero(p.get(0)), d.entero(p.get(1))};
    }
    protected List<Aporte> aportes(List<Participante> p, int a, int b, int puntosA, int puntosB, int ganador) {
        return List.of(new Aporte(p.get(0), puntosA, a - b, ganador == 0 ? 1 : 0), new Aporte(p.get(1), puntosB, b - a, ganador == 1 ? 1 : 0));
    }
    @Override public DatosResultado leerResultado(EntradaConsola entrada, Evento evento) {
        List<Participante> p = evento.getParticipantes();
        int a = entrada.entero("Marcador de " + p.get(0).getNombre() + ": ");
        int b = entrada.entero("Marcador de " + p.get(1).getNombre() + ": ");
        return DatosResultado.marcador(p, a, b);
    }
}
