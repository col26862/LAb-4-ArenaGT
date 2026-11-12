import java.math.BigDecimal;
import java.util.*;
public final class FutbolPenales extends FutbolSala {
    public FutbolPenales() { super("Fútbol sala con penales", new BigDecimal("750")); }
    @Override public List<Aporte> evaluar(List<Participante> p, DatosResultado d) {
        int[] m = marcador(p, d, true);
        if (m[0] != m[1]) {
            Validacion.exigir(d.getPenales().isEmpty(), "No se registran penales cuando hay ganador en goles.");
            return puntuarGoles(p, m[0], m[1]);
        }
        Validacion.exigir(d.getPenales().keySet().equals(d.getValores().keySet()), "El empate requiere los penales de ambos equipos.");
        int a = d.getPenales().get(p.get(0).getCodigo()), b = d.getPenales().get(p.get(1).getCodigo());
        Validacion.exigir(a >= 0 && b >= 0 && a != b, "Los penales deben ser no negativos y distintos.");
        return aportes(p, m[0], m[1], a > b ? 2 : 1, b > a ? 2 : 1, a > b ? 0 : 1);
    }
    @Override public DatosResultado leerResultado(EntradaConsola e, Evento evento) {
        DatosResultado d = super.leerResultado(e, evento);
        List<Participante> p = evento.getParticipantes();
        if (d.entero(p.get(0)) != d.entero(p.get(1))) return d;
        return DatosResultado.penales(p, d.entero(p.get(0)), d.entero(p.get(1)), e.entero("Penales del primer equipo: "), e.entero("Penales del segundo equipo: "));
    }
}
