public record Aporte(Participante participante, int puntos, int diferencia, int victorias) {
    public Aporte {
        Validacion.exigir(participante != null && puntos >= 0 && victorias >= 0 && victorias <= 1, "Aporte inválido.");
    }
}
