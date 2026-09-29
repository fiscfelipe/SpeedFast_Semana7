package model;

import java.time.*;

/**
 * Representa una entrega realizada dentro del sistema SpeedFast.
 *
 * Relaciona un pedido con un repartidor e incorpora
 * la fecha y hora en que se realiza la entrega.
 */
public class Entrega {

    private final int idPedido;
    private final int idRepartidor;
    private final LocalDate fecha;
    private final LocalTime hora;

    /**
     * Constructor que inicializa los datos de una entrega.
     *
     * @param idPedido identificador del pedido entregado
     * @param idRepartidor identificador del repartidor
     * @param fecha fecha de la entrega
     * @param hora hora de la entrega
     */
    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }
}