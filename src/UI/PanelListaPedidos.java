package UI;

import dao.PedidoDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import model.Pedido;

/**
 * Panel utilizado para mostrar los pedidos registrados en SpeedFast.
 *
 * Presenta los pedidos almacenados en la base de datos mediante
 * una tabla que puede actualizarse para reflejar la información actual.
 */
public class PanelListaPedidos extends JPanel {

    private final PedidoDAO pedidoDAO;

    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;

    /**
     * Constructor que inicializa el panel de listado de pedidos.
     */
    public PanelListaPedidos() {
        this.pedidoDAO = new PedidoDAO();

        configurarPanel();
        crearComponentes();
        refrescarTabla();
    }

    /**
     * Configura la distribución general del panel.
     */
    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    }

    /**
     * Crea y organiza los componentes visuales del panel.
     */
    private void crearComponentes() {
        JLabel lblTitulo = new JLabel("Listado de pedidos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        String[] columnas = {
            "ID",
            "Dirección",
            "Distancia (km)",
            "Tipo",
            "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setFillsViewportHeight(true);

        JScrollPane scrollTabla = new JScrollPane(tablaPedidos);

        add(lblTitulo, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
    }

    /**
     * Consulta los pedidos almacenados en la base de datos
     * y actualiza el contenido de la tabla.
     */
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);

        List<Pedido> pedidos = pedidoDAO.listarTodos();

        for (Pedido pedido : pedidos) {

            Object[] fila = {
                pedido.getIdPedido(),
                pedido.getDireccionEntrega(),
                pedido.getDistanciaKm(),
                pedido.getTipoPedido(),
                pedido.getEstado()
            };

            modeloTabla.addRow(fila);
        }
    }
}