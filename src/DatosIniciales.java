import java.time.LocalDate;
import java.util.*;
public final class DatosIniciales {
    private DatosIniciales() { }
    public static SistemaArena crear() {
        SistemaArena s = new SistemaArena();
        String[] nombres = {"Aurora", "Bravos", "Cóndores", "Dragones", "Estrellas", "Fénix"};
        for (int i = 0; i < nombres.length; i++) s.registrarParticipante("P" + (i + 1), nombres[i], new String[]{"TecnoGT", "Comercial Maya", "Servicios Quetzal"}[i / 2]);
        List<Participante> p = s.getParticipantes();
        for (int i = 0; i < s.getDisciplinas().size(); i++) {
            Torneo t = Torneo.precargado("T" + (i + 1), "Copa " + s.getDisciplinas().get(i).getNombre(), s.getDisciplinas().get(i), p.subList(0, 4));
            s.agregarPrecargado(t);
            if (i < 4) {
                t.cerrarInscripciones();
                int j = 0;
                for (Evento e : t.getEventos()) s.programar(e.getCodigo(), LocalDate.of(2026, 11, 20).plusDays(i * 10L + j++), 10, "Arena Central");
            }
        }
        // Fútbol: P1 y P3 quedan con 4 puntos y diferencias +3 y +1.
        resultado(s, "T1-E1", 3, 0); resultado(s, "T1-E2", 2, 2); resultado(s, "T1-E6", 1, 0);
        Evento penales = s.evento("T2-E1"); penales.registrarResultado(DatosResultado.penales(penales.getParticipantes(), 2, 2, 4, 3));
        resultado(s, "T2-E2", 3, 1); resultado(s, "T2-E3", 0, 2);
        // Baloncesto: P1 y P2 tienen 3 puntos y diferencia -3.
        // P2 queda antes porque suma una victoria y P1 ninguna.
        resultado(s, "T3-E1", 0, 1); resultado(s, "T3-E2", 0, 1);
        resultado(s, "T3-E3", 0, 1); resultado(s, "T3-E4", 0, 4);
        resultado(s, "T4-E1", 3, 0); resultado(s, "T4-E2", 3, 1); resultado(s, "T4-E3", 2, 3);
        Torneo relevos = s.torneo("T5"); relevos.cerrarInscripciones();
        Evento carrera = relevos.getEventos().get(0);
        s.programar(carrera.getCodigo(), LocalDate.of(2027, 1, 10), 9, "Pista Quetzal");
        carrera.registrarResultado(new DatosResultado(java.util.Map.of("P1", new java.math.BigDecimal("50.1"), "P2", new java.math.BigDecimal("51.9"), "P3", new java.math.BigDecimal("52.4")), java.util.Map.of(), java.util.Set.of("P4")));
        // A partir de aquí, cualquier finalización futura paga el premio normalmente.
        s.getTorneos().forEach(Torneo::terminarPrecarga);
        return s;
    }
    private static void resultado(SistemaArena s, String codigo, int a, int b) { Evento e = s.evento(codigo); e.registrarResultado(DatosResultado.marcador(e.getParticipantes(), a, b)); }
}
