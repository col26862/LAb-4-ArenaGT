import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;

/** Pruebas ejecutables sin bibliotecas externas. Lanza AssertionError si falla un caso. */
public final class Pruebas {
    private static int numero;
    private static final LocalDate FECHA = LocalDate.of(2026, 12, 1);
    private Pruebas() { }
    private static void comprobar(String nombre, Object esperado, Supplier<Object> accion) {
        Object obtenido = accion.get(); numero++;
        boolean ok = Objects.equals(String.valueOf(esperado), String.valueOf(obtenido));
        System.out.println(numero + ". " + nombre + " | Esperado: " + esperado + " | Obtenido: " + obtenido + " | " + (ok ? "OK" : "FALLO"));
        if (!ok) throw new AssertionError(nombre);
    }
    private static String estado(SistemaArena s) {
        StringBuilder b = new StringBuilder(s.ingresosTotales() + ":" + s.premiosTotales() + s.getParticipantes() + s.rankingEmpresas());
        for (Torneo t : s.getTorneos()) {
            b.append(t).append(t.getIngresos()).append(t.getPremioEntregado()).append(t.tabla());
            for (Evento e : t.getEventos()) b.append(e).append(e.getAportes()).append(e.getResultado());
        }
        return b.toString();
    }
    private static void rechaza(String nombre, SistemaArena s, Runnable accion) {
        String antes = estado(s);
        comprobar(nombre + " (rechazo y estado intacto)", true, () -> {
            boolean rechazo = false;
            try { accion.run(); } catch (IllegalArgumentException ex) { rechazo = true; }
            return rechazo && antes.equals(estado(s));
        });
    }
    private static SistemaArena base() {
        SistemaArena s = new SistemaArena();
        for (int i = 1; i <= 9; i++) s.registrarParticipante("P" + i, new String[]{"Alfa", "Bravo", "Cóndor", "Delta", "Eco", "Fénix", "Gamma", "Hotel", "India"}[i - 1], "Empresa " + ((i - 1) / 2 + 1));
        return s;
    }
    private static Torneo inscribir(SistemaArena s, String codigo, Disciplina d, int n) {
        Torneo t = s.crearTorneo(codigo, "Prueba " + codigo, d);
        for (int i = 1; i <= n; i++) s.inscribir(codigo, "P" + i);
        return t;
    }
    private static String puntos(List<Aporte> aportes) {
        return aportes.stream().map(a -> a.puntos() + "/" + a.diferencia() + "/" + a.victorias()).toList().toString();
    }
    private static DatosResultado carrera(List<Participante> p, String... valores) {
        Map<String, BigDecimal> tiempos = new HashMap<>(); Set<String> dq = new HashSet<>();
        for (int i = 0; i < p.size(); i++) if (valores[i].equals("DQ")) dq.add(p.get(i).getCodigo()); else tiempos.put(p.get(i).getCodigo(), new BigDecimal(valores[i]));
        return new DatosResultado(tiempos, Map.of(), dq);
    }
    private static void terminar(SistemaArena s, Torneo t, boolean carrera) {
        t.cerrarInscripciones(); int i = 0;
        for (Evento e : t.getEventos()) {
            s.programar(e.getCodigo(), FECHA.plusDays(i++), 10, "Sede A");
            DatosResultado d = carrera ? carrera(e.getParticipantes(), "50.1", "51.9", "DQ") : DatosResultado.marcador(e.getParticipantes(), 3, 1);
            e.registrarResultado(d);
        }
    }
    public static void main(String[] args) {
        System.out.println("ArenaGT - Evidencia reproducible de pruebas. Formato deportivo: puntos/diferencia/victorias.");
        List<Disciplina> deportes = new SistemaArena().getDisciplinas();
        String[] cuotas = {"600", "750", "500", "450", "300"};
        for (int i = 0; i < deportes.size(); i++) {
            Disciplina d = deportes.get(i); int n = d.getMinimo();
            BigDecimal total = new BigDecimal(cuotas[i]).multiply(BigDecimal.valueOf(n));
            comprobar(d + " cuota", cuotas[i], () -> d.getCuota().toPlainString());
            comprobar(d + " cotización de " + n, total.toPlainString(), () -> d.cotizar(n).toPlainString());
            SistemaArena s = base(); Torneo t = inscribir(s, "X", d, n);
            comprobar(d + " ingreso cobrado", total.toPlainString(), () -> s.ingresosTotales().toPlainString());
            comprobar(d + " ingreso por disciplina", total.toPlainString(), () -> s.ingresosDisciplina(d).toPlainString());
            terminar(s, t, i == 4);
            comprobar(d + " finalización automática", EstadoTorneo.FINALIZADO, t::getEstado);
            comprobar(d + " premio del 50%", total.multiply(new BigDecimal("0.50")).toPlainString(), () -> s.premiosTotales().toPlainString());
            comprobar(d + " campeón disponible", true, () -> t.campeon().isPresent());
        }
        SistemaArena s = base(); List<Participante> par = s.getParticipantes().subList(0, 2);
        comprobar("Fútbol 3-1", "[3/2/1, 0/-2/0]", () -> puntos(new FutbolSala().evaluar(par, DatosResultado.marcador(par, 3, 1))));
        comprobar("Fútbol empate 2-2", "[1/0/0, 1/0/0]", () -> puntos(new FutbolSala().evaluar(par, DatosResultado.marcador(par, 2, 2))));
        comprobar("Fútbol derrota 0-2", "[0/-2/0, 3/2/1]", () -> puntos(new FutbolSala().evaluar(par, DatosResultado.marcador(par, 0, 2))));
        comprobar("Penales 2-2, tanda 4-3", "[2/0/1, 1/0/0]", () -> puntos(new FutbolPenales().evaluar(par, DatosResultado.penales(par, 2, 2, 4, 3))));
        comprobar("Penales 0-0, tanda 3-4", "[1/0/0, 2/0/1]", () -> puntos(new FutbolPenales().evaluar(par, DatosResultado.penales(par, 0, 0, 3, 4))));
        comprobar("Penales victoria reglamentaria 2-1", "[3/1/1, 0/-1/0]", () -> puntos(new FutbolPenales().evaluar(par, DatosResultado.marcador(par, 2, 1))));
        comprobar("Baloncesto 80-75", "[2/5/1, 1/-5/0]", () -> puntos(new Baloncesto().evaluar(par, DatosResultado.marcador(par, 80, 75))));
        comprobar("Voleibol 3-0", "[3/3/1, 0/-3/0]", () -> puntos(new Voleibol().evaluar(par, DatosResultado.marcador(par, 3, 0))));
        comprobar("Voleibol 3-1", "[3/2/1, 0/-2/0]", () -> puntos(new Voleibol().evaluar(par, DatosResultado.marcador(par, 3, 1))));
        comprobar("Voleibol 3-2", "[2/1/1, 1/-1/0]", () -> puntos(new Voleibol().evaluar(par, DatosResultado.marcador(par, 3, 2))));
        comprobar("Voleibol 2-3", "[1/-1/0, 2/1/1]", () -> puntos(new Voleibol().evaluar(par, DatosResultado.marcador(par, 2, 3))));
        List<Participante> ocho = s.getParticipantes().subList(0, 8);
        comprobar("Relevos puestos 1-8", "[10/0/1, 8/0/0, 6/0/0, 5/0/0, 4/0/0, 3/0/0, 2/0/0, 1/0/0]", () -> puntos(new Relevos().evaluar(ocho, carrera(ocho, "1", "2", "3", "4", "5", "6", "7", "8"))));
        List<Participante> tres = s.getParticipantes().subList(0, 3);
        comprobar("Relevos DQ no ocupa lugar", "[10/0/1, 8/0/0, 0/0/0]", () -> puntos(new Relevos().evaluar(tres, carrera(tres, "DQ", "52.4", "50.1"))));
        comprobar("Relevos todos DQ", "[0/0/0, 0/0/0, 0/0/0]", () -> puntos(new Relevos().evaluar(tres, carrera(tres, "DQ", "DQ", "DQ"))));
        Torneo t = inscribir(s, "X", new Baloncesto(), 4);
        String antes = estado(s); t.getDisciplina().cotizar(2);
        comprobar("Cotización sin cambios ni cobros", true, () -> antes.equals(estado(s)));
        rechaza("Cantidad cero", s, () -> t.getDisciplina().cotizar(0));
        rechaza("Cantidad negativa", s, () -> t.getDisciplina().cotizar(-1));
        rechaza("Código participante repetido (p1)", s, () -> s.registrarParticipante("p1", "Otro", "Otra"));
        rechaza("Código participante vacío", s, () -> s.registrarParticipante(" ", "Otro", "Otra"));
        rechaza("Código torneo repetido", s, () -> s.crearTorneo("x", "Otro", new Relevos()));
        rechaza("Código torneo vacío", s, () -> s.crearTorneo("", "Otro", new Relevos()));
        rechaza("Nombre vacío", s, () -> s.registrarParticipante("P10", "", "Nueva empresa"));
        rechaza("Empresa vacía", s, () -> s.registrarParticipante("P10", "Nuevo", ""));
        rechaza("Participante duplicado en torneo", s, () -> s.inscribir("X", "P1"));
        s.registrarParticipante("P10", "Tercero", " EMPRESA 1 ");
        rechaza("Tercer participante de la misma empresa", s, () -> s.inscribir("X", "P10"));
        s.inscribir("X", "P5"); s.inscribir("X", "P6");
        rechaza("Máximo de seis en baloncesto", s, () -> s.inscribir("X", "P7"));
        Torneo vacio = s.crearTorneo("V", "Vacío", new FutbolSala());
        rechaza("Cierre sin mínimo", s, vacio::cerrarInscripciones);
        comprobar("Sin campeón antes de finalizar", true, () -> t.campeon().isEmpty());
        t.cerrarInscripciones();
        comprobar("Seis inscritos generan quince encuentros", 15, () -> t.getEventos().size());
        comprobar("Cierre lleva a EN_JUEGO", EstadoTorneo.EN_JUEGO, t::getEstado);
        Evento e = t.getEventos().get(0);
        comprobar("Evento nuevo SIN_PROGRAMAR", EstadoEvento.SIN_PROGRAMAR, e::getEstado);
        rechaza("Inscripción después de cerrar", s, () -> s.inscribir("X", "P7"));
        rechaza("Segundo cierre", s, t::cerrarInscripciones);
        rechaza("Resultado sin programación", s, () -> e.registrarResultado(DatosResultado.marcador(e.getParticipantes(), 2, 1)));
        rechaza("Hora 24", s, () -> s.programar(e.getCodigo(), FECHA, 24, "A"));
        rechaza("Hora negativa", s, () -> s.programar(e.getCodigo(), FECHA, -1, "A"));
        rechaza("Sede vacía", s, () -> s.programar(e.getCodigo(), FECHA, 10, " "));
        s.programar(e.getCodigo(), FECHA, 10, "A");
        comprobar("Programación lleva a PROGRAMADO", EstadoEvento.PROGRAMADO, e::getEstado);
        Evento otro = t.getEventos().get(1);
        rechaza("Conflicto sede", s, () -> s.programar(otro.getCodigo(), FECHA, 10, "a"));
        rechaza("Conflicto participante", s, () -> s.programar(otro.getCodigo(), FECHA, 10, "B"));
        s.programar(otro.getCodigo(), FECHA.plusDays(1), 11, "B");
        rechaza("Reprogramación conflictiva conserva fecha previa", s, () -> s.programar(otro.getCodigo(), FECHA, 10, "B"));
        s.programar(e.getCodigo(), FECHA.plusDays(2), 12, "C");
        comprobar("Reprogramación válida", new Programacion(FECHA.plusDays(2), 12, "C"), e::getProgramacion);
        rechaza("Baloncesto empatado 5-5", s, () -> e.registrarResultado(DatosResultado.marcador(e.getParticipantes(), 5, 5)));
        rechaza("Marcador negativo", s, () -> e.registrarResultado(DatosResultado.marcador(e.getParticipantes(), -1, 5)));
        Map<String, BigDecimal> fraccion = new HashMap<>(DatosResultado.marcador(e.getParticipantes(), 2, 1).getValores()); fraccion.put("P1", new BigDecimal("2.5"));
        rechaza("Marcador fraccionario", s, () -> e.registrarResultado(new DatosResultado(fraccion, Map.of(), Set.of())));
        e.registrarResultado(DatosResultado.marcador(e.getParticipantes(), 5, 3));
        comprobar("Resultado lleva a FINALIZADO", EstadoEvento.FINALIZADO, e::getEstado);
        rechaza("Resultado repetido", s, () -> e.registrarResultado(DatosResultado.marcador(e.getParticipantes(), 2, 1)));
        rechaza("Reprogramar finalizado", s, () -> s.programar(e.getCodigo(), FECHA, 15, "D"));
        SistemaArena demo = DatosIniciales.crear();
        comprobar("Carga seis participantes", 6, () -> demo.getParticipantes().size());
        comprobar("Carga cinco torneos", 5, () -> demo.getTorneos().size());
        comprobar("Carga cuatro en juego", 4L, () -> demo.getTorneos().stream().filter(x -> x.getEstado() == EstadoTorneo.EN_JUEGO).count());
        comprobar("Carga sin ingresos", 0, () -> demo.ingresosTotales().signum());
        comprobar("Carga sin premios", 0, () -> demo.premiosTotales().signum());
        comprobar("Desempate integrado por diferencia", "P1", () -> demo.torneo("T1").tabla().get(0).participante().getCodigo());
        comprobar("Desempate integrado por victorias", true, () -> {
            List<Posicion> filas = demo.torneo("T3").tabla();
            Posicion a = filas.stream().filter(x -> x.participante().getCodigo().equals("P1")).findFirst().orElseThrow();
            Posicion b = filas.stream().filter(x -> x.participante().getCodigo().equals("P2")).findFirst().orElseThrow();
            return a.puntos() == b.puntos() && a.diferencia() == b.diferencia() && b.victorias() > a.victorias() && filas.indexOf(b) < filas.indexOf(a);
        });
        comprobar("Desempate por nombre", "P1", () -> { SistemaArena orden = base(); return inscribir(orden, "N", new Relevos(), 3).tabla().get(0).participante().getCodigo(); });
        comprobar("Ranking suma todas las tablas", true, () -> {
            for (FilaEmpresa f : demo.rankingEmpresas()) {
                int suma = demo.getTorneos().stream().flatMap(x -> x.tabla().stream()).filter(x -> x.participante().getEmpresa().equals(f.empresa())).mapToInt(Posicion::puntos).sum();
                if (suma != f.puntos()) return false;
            }
            return true;
        });
        comprobar("Ranking empatado por nombre", "Empresa 1", () -> base().rankingEmpresas().get(0).empresa().getNombre());
        comprobar("Ranking orden descendente", true, () -> {
            List<FilaEmpresa> f = demo.rankingEmpresas();
            for (int i = 1; i < f.size(); i++) if (f.get(i-1).compareTo(f.get(i)) > 0) return false; return true;
        });
        Evento penal = demo.evento("T2-E2");
        rechaza("Penales faltantes con empate", demo, () -> penal.registrarResultado(DatosResultado.marcador(penal.getParticipantes(), 1, 1)));
        rechaza("Penales empatados", demo, () -> penal.registrarResultado(DatosResultado.penales(penal.getParticipantes(), 1, 1, 3, 3)));
        rechaza("Penales negativos", demo, () -> penal.registrarResultado(DatosResultado.penales(penal.getParticipantes(), 1, 1, -1, 3)));
        rechaza("Penales sobrantes sin empate", demo, () -> penal.registrarResultado(DatosResultado.penales(penal.getParticipantes(), 2, 1, 3, 4)));
        Evento volley = demo.evento("T4-E4");
        rechaza("Voleibol sin ganador con tres sets", demo, () -> volley.registrarResultado(DatosResultado.marcador(volley.getParticipantes(), 2, 1)));
        rechaza("Voleibol 3-3", demo, () -> volley.registrarResultado(DatosResultado.marcador(volley.getParticipantes(), 3, 3)));
        rechaza("Voleibol cuatro sets", demo, () -> volley.registrarResultado(DatosResultado.marcador(volley.getParticipantes(), 4, 1)));
        Programacion ocupado = demo.evento("T1-E1").getProgramacion();
        rechaza("Conflicto participante entre torneos", demo, () -> demo.programar(penal.getCodigo(), ocupado.fecha(), ocupado.hora(), "Sede distinta"));
        Torneo relevo = inscribir(demo, "RN", new Relevos(), 4); relevo.cerrarInscripciones(); Evento carrera = relevo.getEventos().get(0);
        demo.programar(carrera.getCodigo(), FECHA.plusYears(1), 8, "Piscina");
        rechaza("Relevos tiempos iguales 50 y 50.0", demo, () -> carrera.registrarResultado(carrera(carrera.getParticipantes(), "50", "50.0", "52", "53")));
        rechaza("Relevos tiempo cero", demo, () -> carrera.registrarResultado(carrera(carrera.getParticipantes(), "0", "51", "52", "53")));
        rechaza("Relevos tiempo negativo", demo, () -> carrera.registrarResultado(carrera(carrera.getParticipantes(), "-1", "51", "52", "53")));
        rechaza("Relevos resultado incompleto", demo, () -> carrera.registrarResultado(new DatosResultado(Map.of("P1", BigDecimal.ONE), Map.of(), Set.of())));
        comprobar("Un solo evento en relevos", 1, () -> relevo.getEventos().size());
        carrera.registrarResultado(carrera(carrera.getParticipantes(), "50.1", "51.9", "52.4", "DQ"));
        comprobar("Relevos cierre automático", EstadoTorneo.FINALIZADO, relevo::getEstado);
        comprobar("Torneo nuevo finalizado paga premio", "600.00", () -> relevo.getPremioEntregado().toPlainString());
        comprobar("Relevos precargado finalizado sin premio", 0, () -> demo.torneo("T5").getPremioEntregado().signum());
        Torneo precargado = demo.torneo("T1");
        for (Evento pendiente : precargado.getEventos()) if (pendiente.getEstado() != EstadoEvento.FINALIZADO) pendiente.registrarResultado(DatosResultado.marcador(pendiente.getParticipantes(), 2, 1));
        comprobar("Precargado finalizado después de la carga paga premio", "1200.00", () -> precargado.getPremioEntregado().toPlainString());
        comprobar("Diferencia acumulada no desborda int", true, () -> {
            SistemaArena grande = base(); Torneo tg = inscribir(grande, "G", new FutbolSala(), 4); tg.cerrarInscripciones(); int dia = 0;
            for (Evento partido : tg.getEventos()) {
                grande.programar(partido.getCodigo(), FECHA.plusDays(dia++), 10, "G");
                partido.registrarResultado(DatosResultado.marcador(partido.getParticipantes(), Integer.MAX_VALUE, 0));
            }
            return tg.tabla().get(0).diferencia() == 3L * Integer.MAX_VALUE;
        });
        // Mezcla encuentros/carreras de torneos distintos en una misma consulta.
        SistemaArena mezcla = base(); Torneo m1 = inscribir(mezcla, "A", new FutbolSala(), 4); m1.cerrarInscripciones();
        Torneo m2 = mezcla.crearTorneo("B", "Relevos", new Relevos());
        for (int i = 5; i <= 7; i++) mezcla.inscribir("B", "P" + i); m2.cerrarInscripciones();
        mezcla.programar("A-E1", FECHA, 10, "Z"); mezcla.programar("A-E6", FECHA, 9, "Z"); mezcla.programar("B-C1", FECHA, 10, "A");
        comprobar("Calendario fecha-hora-sede mixto", "[A-E6, B-C1, A-E1]", () -> {
            List<String> codigos = new ArrayList<>(); for (Evento x : mezcla.calendarioGeneral()) codigos.add(x.getCodigo()); return codigos.toString();
        });
        mezcla.programar("B-C1", FECHA.minusDays(1), 23, "A");
        comprobar("Calendario prioriza fecha sobre hora", "B-C1", () -> mezcla.calendarioGeneral().iterator().next().getCodigo());
        comprobar("Calendario de torneo", 2, () -> mezcla.calendarioTorneo("A").size());
        comprobar("Calendario de participante", 1, () -> mezcla.calendarioParticipante("P1").size());
        comprobar("Recorrido no permite eliminar", true, () -> {
            Iterator<Evento> it = mezcla.calendarioGeneral().iterator(); it.next();
            try { it.remove(); return false; } catch (UnsupportedOperationException ex) { return true; }
        });
        comprobar("Entrada no numérica se recupera", 7, () -> new EntradaConsola(new Scanner("abc\n7\n")).entero(""));
        comprobar("Fecha mal formada se recupera", "2026-12-01", () -> new EntradaConsola(new Scanner("2026-02-30\n2026-12-01\n")).fecha("").toString());
        System.out.println("TOTAL: " + numero + " pruebas aprobadas; 0 fallos.");
    }
}
