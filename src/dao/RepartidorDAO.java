package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Repartidor;

/**
 * Gestiona las operaciones de base de datos relacionadas
 * con los repartidores.
 */
public class RepartidorDAO {

    /**
     * Obtiene todos los repartidores registrados en la base de datos.
     *
     * @return lista de repartidores registrados
     */
    public List<Repartidor> listarTodos() {

        List<Repartidor> repartidores = new ArrayList<>();

        String sql = "SELECT id, nombre FROM repartidor ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");

                Repartidor repartidor = new Repartidor(id, nombre);

                repartidores.add(repartidor);
            }

        } catch (SQLException ex) {
            System.out.println("Error al listar repartidores.");
            System.out.println(ex.getMessage());
        }

        return repartidores;
    }
}