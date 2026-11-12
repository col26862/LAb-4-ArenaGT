import java.math.BigDecimal;
import java.util.*;
public class FutbolSala extends DisciplinaEncuentros {
    public FutbolSala() { this("Fútbol sala", new BigDecimal("600")); }
    protected FutbolSala(String nombre, BigDecimal cuota) { super(nombre, 4, 8, cuota); }
    protected List<Aporte> puntuarGoles(List<Participante> p, int a, int b) {
        return aportes(p, a, b, a > b ? 3 : a == b ? 1 : 0, b > a ? 3 : a == b ? 1 : 0, a == b ? -1 : a > b ? 0 : 1);
    }
    @Override public List<Aporte> evaluar(List<Participante> p, DatosResultado d) {
        int[] m = marcador(p, d, false);
        return puntuarGoles(p, m[0], m[1]);
    }
}
