import java.math.BigDecimal;
import java.util.*;
public final class Torneo {
    private final String codigo;
    private final String nombre;
    private final Disciplina disciplina;
    private final List<Participante> inscritos = new ArrayList<>();
    private List<Evento> eventos = List.of();
    private EstadoTorneo estado = EstadoTorneo.EN_INSCRIPCION;
    private BigDecimal ingresos = BigDecimal.ZERO;
    private BigDecimal premioEntregado = BigDecimal.ZERO;
    private boolean demostracion;
    private boolean cargaInicial;
    public Torneo(String codigo, String nombre, Disciplina disciplina) {
        this.codigo = Validacion.codigo(codigo); this.nombre = Validacion.texto(nombre, "Torneo");
        Validacion.exigir(disciplina != null, "Disciplina obligatoria."); this.disciplina = disciplina;
    }
    static Torneo precargado(String codigo, String nombre, Disciplina disciplina, List<Participante> iniciales) {
        Torneo t = new Torneo(codigo, nombre, disciplina); t.demostracion = true; t.cargaInicial = true;
        for (Participante p : iniciales) { disciplina.validarInscripcion(List.copyOf(t.inscritos), p); t.inscritos.add(p); }
        return t;
    }
    void terminarPrecarga() { Validacion.exigir(cargaInicial, "La precarga ya terminó."); cargaInicial = false; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public EstadoTorneo getEstado() { return estado; }
    public List<Participante> getInscritos() { return List.copyOf(inscritos); }
    public List<Evento> getEventos() { return eventos; }
    public BigDecimal getIngresos() { return ingresos; }
    public BigDecimal getPremioEntregado() { return premioEntregado; }
    public boolean esDemostracion() { return demostracion; }
    public void inscribir(Participante p) {
        Validacion.exigir(estado == EstadoTorneo.EN_INSCRIPCION, "Las inscripciones están cerradas.");
        disciplina.validarInscripcion(getInscritos(), p);
        inscritos.add(p); ingresos = ingresos.add(disciplina.getCuota());
    }
    public void cerrarInscripciones() {
        Validacion.exigir(estado == EstadoTorneo.EN_INSCRIPCION, "El torneo ya cerró inscripciones.");
        Validacion.exigir(inscritos.size() >= disciplina.getMinimo(), "No se alcanza el mínimo de " + disciplina.getMinimo());
        List<Evento> generados = List.copyOf(disciplina.crearEventos(this));
        Validacion.exigir(!generados.isEmpty(), "La disciplina no generó eventos.");
        eventos = generados; estado = EstadoTorneo.EN_JUEGO;
    }
    // Interno: se llama después de un resultado; no existe finalización manual pública.
    void actualizarFinalizacion() {
        if (estado == EstadoTorneo.EN_JUEGO && eventos.stream().allMatch(e -> e.getEstado() == EstadoEvento.FINALIZADO)) {
            estado = EstadoTorneo.FINALIZADO;
            // La precarga no cobra; un cierre posterior sí entrega el premio reglamentario.
            premioEntregado = cargaInicial ? BigDecimal.ZERO : disciplina.premio(inscritos.size());
        }
    }
    public List<Posicion> tabla() {
        Map<String, long[]> acumulados = new HashMap<>();
        for (Participante p : inscritos) acumulados.put(p.getCodigo(), new long[3]);
        for (Evento e : eventos) for (Aporte a : e.getAportes()) {
            long[] fila = acumulados.get(a.participante().getCodigo());
            fila[0] += a.puntos(); fila[1] += a.diferencia(); fila[2] += a.victorias();
        }
        List<Posicion> tabla = new ArrayList<>();
        for (Participante p : inscritos) { long[] f = acumulados.get(p.getCodigo()); tabla.add(new Posicion(p, (int) f[0], f[1], (int) f[2])); }
        Collections.sort(tabla); return List.copyOf(tabla);
    }
    public Optional<Participante> campeon() { return estado == EstadoTorneo.FINALIZADO ? Optional.of(tabla().get(0).participante()) : Optional.empty(); }
    @Override public String toString() { return codigo + " - " + nombre + " | " + disciplina + " | " + estado + " | inscritos: " + inscritos.size() + (demostracion ? " | INSCRIPCIONES INICIALES SIN COBRO" : ""); }
}
