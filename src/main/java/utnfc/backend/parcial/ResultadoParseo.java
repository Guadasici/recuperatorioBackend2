package utnfc.backend.parcial;

import java.util.List;

public class ResultadoParseo {
    private final List<Articulo> articulos;
    private final int leidas;
    private final int descartadas;
    private final List<String> errores;

    public ResultadoParseo(List<Articulo> articulos, int leidas, int descartadas,
            List<String> errores) {
        this.articulos = List.copyOf(articulos);
        this.leidas = leidas;
        this.descartadas = descartadas;
        this.errores = List.copyOf(errores);
    }

    public List<Articulo> getArticulos() {
        return articulos;
    }

    public int getLeidas() {
        return leidas;
    }

    public int getDescartadas() {
        return descartadas;
    }

    public int getInvalidas() {
        return errores.size();
    }

    public List<String> getErrores() {
        return errores;
    }
}
