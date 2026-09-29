package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.EstadoPedido;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

/**
 * Gestiona las operaciones de base de datos relacionadas
 * con los pedidos.
 */
public class PedidoDAO {

    /**
     * Guarda un pedido en la base de datos.
     *
     * @param pedido pedido que será almacenado
     * @return true si el pedido fue guardado correctamente
     */
    public boolean guardar(Pedido pedido) {

        String sql = "INSERT INTO pedido (id, direccion, distancia_km, tipo, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, pedido.getIdPedido());
            sentencia.setString(2, pedido.getDireccionEntrega());
            sentencia.setDouble(3, pedido.getDistanciaKm());
            sentencia.setString(4, pedido.getTipoPedido());
            sentencia.setString(5, pedido.getEstado().toString());

            sentencia.executeUpdate();

            return true;

        } catch (SQLException ex) {
            System.out.println("Error al guardar pedido.");
            System.out.println(ex.getMessage());

            return false;
        }
    }

    /**
     * Obtiene todos los pedidos registrados en la base de datos.
     *
     * @return lista de pedidos registrados
     */
    public List<Pedido> listarTodos() {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT id, direccion, distancia_km, tipo, estado FROM pedido ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String direccion = resultado.getString("direccion");
                double distanciaKm = resultado.getDouble("distancia_km");
                String tipo = resultado.getString("tipo");
                String estado = resultado.getString("estado");

                Pedido pedido;

                switch (tipo) {
                    case "COMIDA":
                        pedido = new PedidoComida(id, direccion, distanciaKm);
                        break;

                    case "ENCOMIENDA":
                        pedido = new PedidoEncomienda(id, direccion, distanciaKm);
                        break;

                    case "EXPRESS":
                        pedido = new PedidoExpress(id, direccion, distanciaKm);
                        break;

                    default:
                        System.out.println("Tipo de pedido desconocido: " + tipo);
                        continue;
                }

                pedido.setEstado(EstadoPedido.valueOf(estado));

                pedidos.add(pedido);
            }

        } catch (SQLException ex) {
            System.out.println("Error al listar pedidos.");
            System.out.println(ex.getMessage());
        }

        return pedidos;
    }

    /**
     * Actualiza el estado de un pedido registrado en la base de datos.
     *
     * @param pedido pedido cuyo estado será actualizado
     * @return true si el estado fue actualizado correctamente
     */
    public boolean actualizarEstado(Pedido pedido) {

        String sql = "UPDATE pedido SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, pedido.getEstado().toString());
            sentencia.setInt(2, pedido.getIdPedido());

            int filasActualizadas = sentencia.executeUpdate();

            return filasActualizadas > 0;

        } catch (SQLException ex) {
            System.out.println("Error al actualizar estado del pedido.");
            System.out.println(ex.getMessage());

            return false;
        }
    }
}