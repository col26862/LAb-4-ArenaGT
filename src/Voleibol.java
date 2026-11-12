import java.math.BigDecimal;
import java.util.*;
public final class Voleibol extends DisciplinaEncuentros {
    public Voleibol() { super("Voleibol", 4, 6, new BigDecimal("450")); }
    @Override public List<Aporte> evaluar(List<Participante> p, DatosResultado d) {
        int[] m = marcador(p, d, false); int a = m[0], b = m[1];
        Validacion.exigir((a == 3 && b < 3) || (b == 3 && a < 3), "Voleibol: ganador con 3 sets y perdedor con 0, 1 o 2.");
        int ganador = a > b ? 0 : 1, menor = Math.min(a, b);
        return aportes(p, a, b, ganador == 0 ? (menor == 2 ? 2 : 3) : (menor == 2 ? 1 : 0), ganador == 1 ? (menor == 2 ? 2 : 3) : (menor == 2 ? 1 : 0), ganador);
    }
}
