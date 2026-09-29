package UI;

import dao.RepartidorDAO;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.ControladorDeEnvios;
import model.Pedido;
import model.Repartidor;
import model.ZonaDeCarga;

/**
 * Panel utilizado para iniciar y supervisar las entregas de SpeedFast.
 *
 * Permite seleccionar la cantidad de repartidores que participarán
 * en la simulación y utiliza los repartidores registrados en la
 * base de datos.
 */
public class PanelEntrega extends JPanel {

    private final ControladorDeEnvios controlador;
    private final ZonaDeCarga zonaDeCarga;
    private final RepartidorDAO repartidorDAO;

    private JComboBox<Integer> cmbRepartidores;
    private JButton btnIniciar;
    private JTextArea areaResultados;

    /**
     * Constructor que inicializa el panel de entregas.
     *
     * @param controlador controlador general de pedidos
     * @param zonaDeCarga zona de carga compartida
     */
    public PanelEntrega(ControladorDeEnvios controlador, ZonaDeCarga zonaDeCarga) {
        this.controlador = controlador;
        this.zonaDeCarga = zonaDeCarga;
        this.repartidorDAO = new RepartidorDAO();

        configurarPanel();
        crearComponentes();
        configurarEventos();
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
        JLabel lblTitulo = new JLabel("Gestión de entregas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblRepartidores = new JLabel("Cantidad de repartidores:");

        cmbRepartidores = new JComboBox<>(new Integer[]{
            1, 2, 3, 4, 5
        });

        cmbRepartidores.setSelectedItem(3);

        btnIniciar = new JButton("Iniciar entregas");

        panelControles.add(lblRepartidores);
        panelControles.add(cmbRepartidores);
        panelControles.add(btnIniciar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        panelSuperior.add(panelControles, BorderLayout.CENTER);

        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);

        JScrollPane scrollResultados = new JScrollPane(areaResultados);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollResultados, BorderLayout.CENTER);
    }

    /**
     * Configura la acción del botón para iniciar las entregas.
     */
    private void configurarEventos() {
        btnIniciar.addActionListener(e -> iniciarEntregas());
    }

    /**
     * Inicia la simulación concurrente utilizando la cantidad
     * de repartidores seleccionada por el usuario.
     */
    private void iniciarEntregas() {

        if (zonaDeCarga.getCantidadPedidosPendientes() == 0) {
            JOptionPane.showMessageDialog(this, "No existen pedidos pendientes para entregar.", "Zona de carga vacía", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int cantidadRepartidores = (Integer) cmbRepartidores.getSelectedItem();

        List<Repartidor> repartidoresDisponibles = repartidorDAO.listarTodos();

        if (repartidoresDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen repartidores registrados en la base de datos.", "Sin repartidores", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cantidadRepartidores > repartidoresDisponibles.size()) {
            JOptionPane.showMessageDialog(this, "Solo existen " + repartidoresDisponibles.size() + " repartidores registrados.", "Cantidad inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnIniciar.setEnabled(false);
        areaResultados.setText("");

        Thread simulacion = new Thread(() -> ejecutarSimulacion(cantidadRepartidores, repartidoresDisponibles));
        simulacion.start();
    }

    /**
     * Ejecuta los repartidores de forma concurrente y espera
     * hasta que todos hayan finalizado.
     *
     * @param cantidadRepartidores cantidad de repartidores a utilizar
     * @param repartidoresDisponibles repartidores recuperados desde MySQL
     */
    private void ejecutarSimulacion(int cantidadRepartidores, List<Repartidor> repartidoresDisponibles) {

        ExecutorService executor = Executors.newFixedThreadPool(cantidadRepartidores);

        for (int i = 0; i < cantidadRepartidores; i++) {

            Repartidor repartidorBD = repartidoresDisponibles.get(i);

            Repartidor repartidor = new Repartidor(
                    repartidorBD.getId(),
                    repartidorBD.getNombre(),
                    zonaDeCarga,
                    controlador
            );

            executor.submit(repartidor);
        }

        executor.shutdown();

        while (!executor.isTerminated()) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        SwingUtilities.invokeLater(() -> {
            mostrarResultadoFinal();
            btnIniciar.setEnabled(true);
        });
    }

    /**
     * Muestra el estado final de todos los pedidos registrados.
     */
    private void mostrarResultadoFinal() {

        List<Pedido> pedidos = controlador.getPedidosRegistrados();

        StringBuilder resultado = new StringBuilder();

        resultado.append("RESULTADO FINAL\n");
        resultado.append("============================\n\n");

        for (Pedido pedido : pedidos) {
            resultado.append("Pedido #")
                    .append(pedido.getIdPedido())
                    .append(" | ")
                    .append(pedido.getTipoPedido())
                    .append(" | Estado: ")
                    .append(pedido.getEstado());

            if (pedido.getRepartidorAsignado() != null) {
                resultado.append(" | Repartidor: ").append(pedido.getRepartidorAsignado());
            }

            resultado.append("\n");
        }

        resultado.append("\nPedidos entregados: ").append(controlador.getTotalEntregados());
        resultado.append("\nPedidos pendientes: ").append(zonaDeCarga.getCantidadPedidosPendientes());

        areaResultados.setText(resultado.toString());
        areaResultados.setCaretPosition(0);
    }
}