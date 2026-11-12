import java.math.BigDecimal;
import java.util.*;
public final class DatosResultado {
    private final Map<String, BigDecimal> valores;
    private final Map<String, Integer> penales;
    private final Set<String> descalificados;
    public DatosResultado(Map<String, BigDecimal> valores, Map<String, Integer> penales, Set<String> descalificados) {
        this.valores = Map.copyOf(valores);
        this.penales = Map.copyOf(penales);
        this.descalificados = Set.copyOf(descalificados);
    }
    public Map<String, BigDecimal> getValores() { return valores; }
    public Map<String, Integer> getPenales() { return penales; }
    public Set<String> getDescalificados() { return descalificados; }
    public int entero(Participante p) {
        BigDecimal valor = valores.get(p.getCodigo());
        Validacion.exigir(valor != null, "Falta marcador de " + p.getCodigo());
        try { int n = valor.intValueExact(); Validacion.exigir(n >= 0, "Marcador negativo."); return n; }
        catch (ArithmeticException ex) { throw new IllegalArgumentException("El marcador debe ser entero y estar en rango."); }
    }
    @Override public String toString() { return "Valores: " + valores + (penales.isEmpty() ? "" : " | Penales: " + penales) + (descalificados.isEmpty() ? "" : " | DQ: " + descalificados); }
    public static DatosResultado marcador(java.util.List<Participante> participantes, int a, int b) {
        return new DatosResultado(Map.of(participantes.get(0).getCodigo(), BigDecimal.valueOf(a), participantes.get(1).getCodigo(), BigDecimal.valueOf(b)), Map.of(), Set.of());
    }
    public static DatosResultado penales(java.util.List<Participante> p, int a, int b, int pa, int pb) {
        return new DatosResultado(marcador(p, a, b).getValores(), Map.of(p.get(0).getCodigo(), pa, p.get(1).getCodigo(), pb), Set.of());
    }
}
