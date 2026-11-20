package utnfc.backend.parcial;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ParserArticulosTest {
    @Test
    void leeDataset() {
        var resultado = assertDoesNotThrow(() -> new ParserArticulos()
                .leer(Path.of("datos/articulos.csv")));

        assertEquals(60, resultado.getLeidas());
        assertEquals(54, resultado.getArticulos().size());
        assertEquals(5, resultado.getDescartadas());
        assertEquals(1, resultado.getInvalidas());
    }
}
