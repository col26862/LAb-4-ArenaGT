public record FilaEmpresa(Empresa empresa, int puntos) implements Comparable<FilaEmpresa> {
    @Override public int compareTo(FilaEmpresa otra) {
        int c = Integer.compare(otra.puntos, puntos);
        return c != 0 ? c : String.CASE_INSENSITIVE_ORDER.compare(empresa.getNombre(), otra.empresa.getNombre());
    }
    @Override public String toString() { return empresa.getNombre() + ": " + puntos + " puntos"; }
}
