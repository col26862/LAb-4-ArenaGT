import java.math.BigDecimal;
import java.util.*;
public final class Baloncesto extends DisciplinaEncuentros {
    public Baloncesto() { super("Baloncesto", 4, 6, new BigDecimal("500")); }
    @Override public List<Aporte> evaluar(List<Participante> p, DatosResultado d) {
        int[] m = marcador(p, d, false); int a = m[0], b = m[1];
        Validacion.exigir(a != b, "Baloncesto no admite empates.");
        return aportes(p, a, b, a > b ? 2 : 1, b > a ? 2 : 1, a > b ? 0 : 1);
    }
}
