package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import model.Entrega;

/**
 * Gestiona las operaciones de base de datos relacionadas
 * con las entregas.
 */
public class EntregaDAO {

    /**
     * Guarda una entrega en la base de datos.
     *
     * @param entrega entrega que será almacenada
     * @return true si la entrega fue guardada correctamente
     */
    public boolean guardar(Entrega entrega) {

        String sql = "INSERT INTO entrega (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, entrega.getIdPedido());
            sentencia.setInt(2, entrega.getIdRepartidor());
            sentencia.setDate(3, Date.valueOf(entrega.getFecha()));
            sentencia.setTime(4, Time.valueOf(entrega.getHora()));

            sentencia.executeUpdate();

            return true;

        } catch (SQLException ex) {
            System.out.println("Error al guardar entrega.");
            System.out.println(ex.getMessage());

            return false;
        }
    }
}