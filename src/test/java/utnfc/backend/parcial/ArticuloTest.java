package utnfc.backend.parcial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ArticuloTest {
    @Test
    void calculaElMargenDelStock() {
        var articulo = Articulo.desdeCampos(new String[] {
                "A1", "Leche", "Lacteos", "Proveedor Sur", "100", "5", "ACTIVO", "60"
        });

        assertEquals(200, articulo.importe());
    }

    @Test
    void protegeLasInvariantes() {
        assertThrows(IllegalArgumentException.class, () -> Articulo.desdeCampos(new String[] {
                "A1", "Leche", "Lacteos", "Proveedor Sur", "0", "5", "ACTIVO", "60"
        }));
        assertThrows(IllegalArgumentException.class, () -> Articulo.desdeCampos(new String[] {
                "A1", "Leche", "Lacteos", "Proveedor Sur", "100", "-1", "ACTIVO", "60"
        }));
        assertThrows(IllegalArgumentException.class, () -> Articulo.desdeCampos(new String[] {
                "A1", "Leche", "Lacteos", "Proveedor Sur", "100", "5", "ACTIVO", "120"
        }));
    }
}
