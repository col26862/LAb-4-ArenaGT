public record Posicion(Participante participante, int puntos, long diferencia, int victorias) implements Comparable<Posicion> {
    @Override public int compareTo(Posicion otra) {
        int c = Integer.compare(otra.puntos, puntos);
        if (c == 0) c = Long.compare(otra.diferencia, diferencia);
        if (c == 0) c = Integer.compare(otra.victorias, victorias);
        if (c == 0) c = String.CASE_INSENSITIVE_ORDER.compare(participante.getNombre(), otra.participante.getNombre());
        if (c == 0) c = participante.getCodigo().compareTo(otra.participante.getCodigo());
        return c;
    }
    @Override public String toString() { return String.format("%-6s %-20s Pts: %3d  Dif: %+4d  Vic: %2d", participante.getCodigo(), participante.getNombre(), puntos, diferencia, victorias); }
}
