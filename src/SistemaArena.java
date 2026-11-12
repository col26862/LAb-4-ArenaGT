import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
public final class SistemaArena {
    private final Map<String, Empresa> empresas = new LinkedHashMap<>();
    private final Map<String, Participante> participantes = new LinkedHashMap<>();
    private final Map<String, Torneo> torneos = new LinkedHashMap<>();
    private final List<Disciplina> disciplinas = List.of(new FutbolSala(), new FutbolPenales(), new Baloncesto(), new Voleibol(), new Relevos());
    public SistemaArena() { }
    public List<Disciplina> getDisciplinas() { return disciplinas; }
    public List<Participante> getParticipantes() { return List.copyOf(participantes.values()); }
    public List<Torneo> getTorneos() { return List.copyOf(torneos.values()); }
    public Participante registrarParticipante(String codigo, String nombre, String empresa) {
        String c = Validacion.codigo(codigo), n = Validacion.texto(nombre, "Participante"), e = Validacion.texto(empresa, "Empresa");
        Validacion.exigir(!participantes.containsKey(c), "Código de participante repetido.");
        String clave = Validacion.clave(e);
        Empresa representante = empresas.getOrDefault(clave, new Empresa(e));
        Participante p = new Participante(c, n, representante);
        empresas.putIfAbsent(clave, representante); participantes.put(c, p); return p;
    }
    public Torneo crearTorneo(String codigo, String nombre, Disciplina disciplina) {
        String c = Validacion.codigo(codigo);
        Validacion.exigir(!torneos.containsKey(c), "Código de torneo repetido.");
        Torneo t = new Torneo(c, nombre, disciplina); torneos.put(c, t); return t;
    }
    void agregarPrecargado(Torneo t) { Validacion.exigir(!torneos.containsKey(t.getCodigo()), "Torneo repetido."); torneos.put(t.getCodigo(), t); }
    public Participante participante(String codigo) {
        Participante p = participantes.get(Validacion.codigo(codigo)); Validacion.exigir(p != null, "Participante inexistente."); return p;
    }
    public Torneo torneo(String codigo) {
        Torneo t = torneos.get(Validacion.codigo(codigo)); Validacion.exigir(t != null, "Torneo inexistente."); return t;
    }
    public Evento evento(String codigo) {
        String c = Validacion.codigo(codigo);
        return todosEventos().stream().filter(e -> e.getCodigo().equals(c)).findFirst().orElseThrow(() -> new IllegalArgumentException("Evento inexistente."));
    }
    private List<Evento> todosEventos() { return torneos.values().stream().flatMap(t -> t.getEventos().stream()).toList(); }
    public Calendario calendarioGeneral() { return new Calendario(todosEventos()); }
    public Calendario calendarioTorneo(String codigo) { return new Calendario(torneo(codigo).getEventos()); }
    public Calendario calendarioParticipante(String codigo) {
        Participante p = participante(codigo);
        return new Calendario(todosEventos().stream().filter(e -> e.getParticipantes().contains(p)).toList());
    }
    public void inscribir(String torneo, String participante) { torneo(torneo).inscribir(participante(participante)); }
    public void programar(String codigo, LocalDate fecha, int hora, String sede) { evento(codigo).programar(fecha, hora, sede, calendarioGeneral()); }
    public BigDecimal ingresosTotales() { return torneos.values().stream().map(Torneo::getIngresos).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public BigDecimal ingresosDisciplina(Disciplina d) { return torneos.values().stream().filter(t -> t.getDisciplina().getNombre().equals(d.getNombre())).map(Torneo::getIngresos).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public BigDecimal premiosTotales() { return torneos.values().stream().map(Torneo::getPremioEntregado).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public List<FilaEmpresa> rankingEmpresas() {
        Map<Empresa, Integer> puntos = new LinkedHashMap<>(); empresas.values().forEach(e -> puntos.put(e, 0));
        for (Torneo t : torneos.values()) for (Posicion p : t.tabla()) puntos.merge(p.participante().getEmpresa(), p.puntos(), Integer::sum);
        List<FilaEmpresa> ranking = new ArrayList<>(); puntos.forEach((e, p) -> ranking.add(new FilaEmpresa(e, p)));
        Collections.sort(ranking); return List.copyOf(ranking);
    }
}
