package utnfc.backend.parcial;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Inventario {
    private final List<Articulo> articulos;

    public Inventario(List<Articulo> articulos) {
        this.articulos = List.copyOf(articulos);
    }

    public double valorTotal() {
        return articulos.stream().mapToDouble(Articulo::importe).sum();
    }

    public long contar(Predicate<Articulo> criterio) {
        return articulos.stream().filter(criterio).count();
    }

    public Map<String, Long> porCategoria() {
        return articulos.stream().collect(Collectors.groupingBy(
                Articulo::getCategoria, TreeMap::new, Collectors.counting()));
    }
}
