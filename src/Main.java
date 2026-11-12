import java.util.*;
public final class Main {
    private final SistemaArena sistema;
    private final EntradaConsola entrada;
    private Main(SistemaArena sistema, EntradaConsola entrada) { this.sistema = sistema; this.entrada = entrada; }
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--pruebas")) { Pruebas.main(new String[0]); return; }
        new Main(DatosIniciales.crear(), new EntradaConsola(new Scanner(System.in))).ejecutar();
    }
    private void ejecutar() {
        System.out.println("ArenaGT | César Colorado | Carné 26862");
        System.out.println("T1-T5 y P1-P6: datos de demostración, sin ingresos ni premios iniciales.");
        while (true) {
            System.out.println("\n1. Registrar participante    2. Crear torneo    3. Cotizar    4. Inscribir");
            System.out.println("5. Cerrar inscripciones    6. Programar/reprogramar    7. Registrar resultado");
            System.out.println("8. Torneos/tablas/eventos    9. Participantes    10. Calendarios    11. Reportes    0. Salir");
            try {
                int opcion = entrada.entero("Opción: ");
                switch (opcion) {
                    case 0: return;
                    case 1: registrarParticipante(); break;
                    case 2: sistema.crearTorneo(entrada.texto("Código: "), entrada.texto("Nombre: "), elegirDisciplina()); System.out.println("Torneo creado."); break;
                    case 3:
                        System.out.println("Cotización para " + Validacion.texto(entrada.texto("Empresa: "), "Empresa"));
                        System.out.printf("Total: Q%.2f%n", elegirDisciplina().cotizar(entrada.entero("Cantidad: "))); break;
                    case 4: sistema.inscribir(entrada.texto("Código de torneo: "), entrada.texto("Código de participante: ")); System.out.println("Inscripción cobrada."); break;
                    case 5: sistema.torneo(entrada.texto("Código de torneo: ")).cerrarInscripciones(); System.out.println("Inscripciones cerradas; eventos generados."); break;
                    case 6: sistema.programar(entrada.texto("Código de evento: "), entrada.fecha("Fecha AAAA-MM-DD: "), entrada.entero("Hora (0-23): "), entrada.texto("Sede: ")); System.out.println("Programación guardada."); break;
                    case 7:
                        Evento evento = sistema.evento(entrada.texto("Código de evento: "));
                        evento.registrarResultado(evento.getTorneo().getDisciplina().leerResultado(entrada, evento));
                        System.out.println("Resultado registrado.");
                        if (evento.getTorneo().getEstado() == EstadoTorneo.FINALIZADO) mostrarTorneo(evento.getTorneo()); break;
                    case 8: sistema.getTorneos().forEach(this::mostrarTorneo); break;
                    case 9: sistema.getParticipantes().forEach(System.out::println); break;
                    case 10: mostrarCalendario(); break;
                    case 11: reportes(); break;
                    default: System.out.println("Opción fuera de rango.");
                }
            } catch (EntradaConsola.FinEntrada ex) { System.out.println("Fin de entrada."); return; }
            catch (IllegalArgumentException ex) { System.out.println("Operación rechazada: " + ex.getMessage()); }
        }
    }
    private void registrarParticipante() {
        sistema.registrarParticipante(entrada.texto("Código: "), entrada.texto("Nombre del participante: "), entrada.texto("Empresa: "));
        System.out.println("Participante registrado.");
    }
    private Disciplina elegirDisciplina() {
        List<Disciplina> disciplinas = sistema.getDisciplinas();
        for (int i = 0; i < disciplinas.size(); i++) System.out.printf("%d. %s | Q%.2f | %d-%d participantes%n", i + 1, disciplinas.get(i), disciplinas.get(i).getCuota(), disciplinas.get(i).getMinimo(), disciplinas.get(i).getMaximo());
        int n = entrada.entero("Disciplina: ");
        Validacion.exigir(n >= 1 && n <= disciplinas.size(), "Disciplina fuera de rango."); return disciplinas.get(n - 1);
    }
    private void mostrarTorneo(Torneo t) {
        System.out.println("\n" + t);
        t.tabla().forEach(System.out::println);
        for (Evento e : sistema.calendarioTorneo(t.getCodigo())) mostrarEvento(e);
        for (Evento e : t.getEventos()) if (e.getProgramacion() == null) mostrarEvento(e);
        t.campeon().ifPresent(p -> System.out.printf("Campeón: %s | Premio entregado: Q%.2f%n", p.getNombre(), t.getPremioEntregado()));
    }
    private void mostrarEvento(Evento e) {
        System.out.println(e);
        e.getResultado().ifPresent(d -> System.out.println("  " + d));
        if (e.getEstado() == EstadoEvento.FINALIZADO) e.getAportes().forEach(a -> System.out.printf("  %s: %d puntos, diferencia %+d, victorias %d%n", a.participante().getCodigo(), a.puntos(), a.diferencia(), a.victorias()));
    }
    private void mostrarCalendario() {
        int opcion = entrada.entero("1. General  2. Torneo  3. Participante: ");
        Calendario c;
        switch (opcion) {
            case 1: c = sistema.calendarioGeneral(); break;
            case 2: c = sistema.calendarioTorneo(entrada.texto("Torneo: ")); break;
            case 3: c = sistema.calendarioParticipante(entrada.texto("Participante: ")); break;
            default: throw new IllegalArgumentException("Calendario fuera de rango.");
        }
        for (Evento e : c) mostrarEvento(e);
        if (c.size() == 0) System.out.println("No hay eventos programados.");
    }
    private void reportes() {
        System.out.printf("Ingresos totales: Q%.2f%n", sistema.ingresosTotales());
        for (Disciplina d : sistema.getDisciplinas()) System.out.printf("  %s: Q%.2f%n", d, sistema.ingresosDisciplina(d));
        System.out.printf("Premios entregados: Q%.2f%n", sistema.premiosTotales());
        for (Torneo t : sistema.getTorneos()) if (t.getEstado() == EstadoTorneo.FINALIZADO) System.out.printf("  %s | Campeón: %s | Q%.2f%n", t.getCodigo(), t.campeon().orElseThrow().getNombre(), t.getPremioEntregado());
        System.out.println("Ranking de empresas:"); sistema.rankingEmpresas().forEach(System.out::println);
    }
}
