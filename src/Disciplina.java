import java.math.BigDecimal;
import java.util.*;
public abstract class Disciplina {
    private final String nombre;
    private final int minimo;
    private final int maximo;
    private final BigDecimal cuota;
    protected Disciplina(String nombre, int minimo, int maximo, BigDecimal cuota) {
        this.nombre = Validacion.texto(nombre, "Disciplina");
        Validacion.exigir(minimo > 0 && maximo >= minimo && cuota != null && cuota.signum() > 0, "Configuración de disciplina inválida.");
        this.minimo = minimo; this.maximo = maximo; this.cuota = cuota;
    }
    public String getNombre() { return nombre; }
    public int getMinimo() { return minimo; }
    public int getMaximo() { return maximo; }
    public BigDecimal getCuota() { return cuota; }
    public BigDecimal cotizar(int cantidad) {
        // Es una estimación; no exige el mínimo de apertura de un torneo.
        Validacion.exigir(cantidad > 0, "La cantidad debe ser positiva.");
        return cuota.multiply(BigDecimal.valueOf(cantidad));
    }
    public BigDecimal premio(int cantidad) { return cotizar(cantidad).multiply(new BigDecimal("0.50")); }
    public void validarInscripcion(List<Participante> inscritos, Participante nuevo) {
        Validacion.exigir(nuevo != null, "Participante obligatorio.");
        Validacion.exigir(inscritos.size() < maximo, "El torneo está lleno.");
        Validacion.exigir(inscritos.stream().noneMatch(p -> p.getCodigo().equals(nuevo.getCodigo())), "Participante ya inscrito.");
        Validacion.exigir(inscritos.stream().filter(p -> p.getEmpresa().equals(nuevo.getEmpresa())).count() < 2, "La empresa ya tiene dos participantes.");
    }
    public abstract List<Evento> crearEventos(Torneo torneo);
    public abstract List<Aporte> evaluar(List<Participante> participantes, DatosResultado datos);
    public abstract DatosResultado leerResultado(EntradaConsola entrada, Evento evento);
    @Override public String toString() { return nombre; }
}
