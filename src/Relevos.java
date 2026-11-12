import java.math.BigDecimal;
import java.util.*;
public final class Relevos extends Disciplina {
    private static final int[] PUNTOS = {10, 8, 6, 5, 4, 3, 2, 1};
    public Relevos() { super("Relevos", 3, 8, new BigDecimal("300")); }
    @Override public List<Evento> crearEventos(Torneo torneo) { return List.of(new Carrera(torneo.getCodigo() + "-C1", torneo, torneo.getInscritos())); }
    @Override public List<Aporte> evaluar(List<Participante> p, DatosResultado d) {
        Set<String> codigos = new HashSet<>(); p.forEach(x -> codigos.add(x.getCodigo()));
        Set<String> recibidos = new HashSet<>(d.getValores().keySet()); recibidos.addAll(d.getDescalificados());
        Validacion.exigir(recibidos.equals(codigos) && Collections.disjoint(d.getValores().keySet(), d.getDescalificados()) && d.getPenales().isEmpty(), "Indique un tiempo o DQ por participante, sin códigos extra.");
        TreeSet<BigDecimal> tiempos = new TreeSet<>();
        for (BigDecimal t : d.getValores().values()) Validacion.exigir(t.signum() > 0 && tiempos.add(t), "Los tiempos deben ser positivos y distintos.");
        List<Participante> orden = new ArrayList<>(p);
        orden.removeIf(x -> d.getDescalificados().contains(x.getCodigo()));
        orden.sort(Comparator.comparing(x -> d.getValores().get(x.getCodigo())));
        List<Aporte> salida = new ArrayList<>();
        for (int i = 0; i < orden.size(); i++) salida.add(new Aporte(orden.get(i), PUNTOS[i], 0, i == 0 ? 1 : 0));
        for (Participante x : p) if (d.getDescalificados().contains(x.getCodigo())) salida.add(new Aporte(x, 0, 0, 0));
        return List.copyOf(salida);
    }
    @Override public DatosResultado leerResultado(EntradaConsola e, Evento evento) {
        Map<String, BigDecimal> tiempos = new LinkedHashMap<>(); Set<String> dq = new HashSet<>();
        for (Participante p : evento.getParticipantes()) {
            String valor = e.texto("Segundos de " + p.getNombre() + " o DQ: ");
            if (valor.equalsIgnoreCase("DQ")) dq.add(p.getCodigo());
            else tiempos.put(p.getCodigo(), e.decimalDesde(valor));
        }
        return new DatosResultado(tiempos, Map.of(), dq);
    }
}
