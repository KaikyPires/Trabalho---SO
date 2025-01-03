package escalonamento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.List;
import java.util.Queue;

public class Escalonador {
    public static void main(String[] args) {
        // Configuração da interface gráfica
        SwingUtilities.invokeLater(() -> criarInterfaceGrafica());
    }
    
    private static void criarInterfaceGrafica() {
        JFrame frame = new JFrame("Simulador de Escalonamento de Processos - IFMG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
    
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xE7E7E7)); // Fundo cinza claro global
    // Adicionar a logo na parte superior


        // Painel de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        inputPanel.setBackground(new Color(0xE7E7E7)); // Fundo cinza claro
    
        inputPanel.setPreferredSize(new Dimension(700, 80));
    
        // Componentes de entrada
        JTextField numProcessadoresField = new JTextField(8);
        JTextField numProcessosField = new JTextField(8);
        JTextField quantumField = new JTextField(8);
    
        estilizarInput(numProcessadoresField);
        estilizarInput(numProcessosField);
        estilizarInput(quantumField);
    
        JLabel label1 = new JLabel("Número de Processadores:");
        JLabel label2 = new JLabel("Número de Processos na Fila de Prontos:");
        JLabel label3 = new JLabel("Quantum (para Round-Robin):");
    
        estilizarLabel(label1, false);
        estilizarLabel(label2, false);
        estilizarLabel(label3, false);
    
        inputPanel.add(label1);
        inputPanel.add(numProcessadoresField);
        inputPanel.add(label2);
        inputPanel.add(numProcessosField);
        inputPanel.add(label3);
        inputPanel.add(quantumField);
    
        // Painel para o botão
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0xE7E7E7)); // Fundo cinza claro
        JButton iniciarButton = new JButton("Iniciar Simulação");
        iniciarButton.setBackground(new Color(0xE30613)); // Vermelho IFMG
        iniciarButton.setForeground(Color.WHITE);
        iniciarButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(iniciarButton);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x009639)));
        // Adiciona os painéis de entrada e botão ao topo
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        topPanel.setBackground(new Color(0xE7E7E7)); // Fundo cinza claro
        panel.add(topPanel, BorderLayout.NORTH);
    
        // Área de saída
        JTextArea outputArea = new JTextArea(0, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        outputArea.setRows(7);
        
    
    
        // Painel inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel graficoPanel = new JPanel();
        graficoPanel.setPreferredSize(new Dimension(1000, 320));
        graficoPanel.setBackground(Color.WHITE);
        graficoPanel.setBorder(BorderFactory.createLineBorder(new Color(0x009639), 2));
        JScrollPane scrollPaneGrafico = new JScrollPane(graficoPanel);
        scrollPaneGrafico.setPreferredSize(new Dimension(1000, 320));
        bottomPanel.add(scrollPaneGrafico, BorderLayout.NORTH);
    
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Indicadores");
        tableModel.addColumn("Valores");
        tableModel.addColumn("Descrição");
    

        JTable tabelaIndicadores = new JTable(tableModel);
        tabelaIndicadores.setFillsViewportHeight(true);
        tabelaIndicadores.setFont(new Font("Arial", Font.BOLD, 14));
        tabelaIndicadores.setRowHeight(30);
      
        tabelaIndicadores.setBorder(BorderFactory.createLineBorder(new Color(0x009639), 1));
    
        JScrollPane scrollPaneTabela = new JScrollPane(tabelaIndicadores);
        scrollPaneTabela.setPreferredSize(new Dimension(800, 200));
        scrollPaneTabela.setBorder(BorderFactory.createLineBorder(new Color(0x009639), 2));
        bottomPanel.add(scrollPaneTabela, BorderLayout.CENTER);
        
        JTableHeader cabecalho = tabelaIndicadores.getTableHeader();
cabecalho.setBackground(new Color(0x009639));
cabecalho.setForeground(Color.WHITE);         // Texto branco
cabecalho.setFont(new Font("Arial", Font.BOLD, 16)); // Fonte negrito
        panel.add(bottomPanel, BorderLayout.SOUTH);
    
        iniciarButton.addActionListener(e -> {
            try {
                int numProcessadores = Integer.parseInt(numProcessadoresField.getText());
                int numProcessos = Integer.parseInt(numProcessosField.getText());
                int quantum = Integer.parseInt(quantumField.getText());
    
                Simulador simulador = new Simulador(numProcessadores, numProcessos, quantum);
                String resultado = simulador.executar();
                outputArea.setText(resultado);
    
                DefaultCategoryDataset dataset = simulador.getDataset();
                JFreeChart chart = ChartFactory.createBarChart(
                        "Desempenho dos Algoritmos",
                        "Indicadores",
                        "Tempo (ms)",
                        dataset);
    
                CategoryPlot plot = chart.getCategoryPlot();
                BarRenderer renderer = (BarRenderer) plot.getRenderer();
                renderer.setSeriesPaint(0, new Color(0xE30613));
                renderer.setSeriesPaint(1, new Color(0x009639));
    
                ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(1000, 300));
                graficoPanel.removeAll();
                graficoPanel.add(chartPanel);
                graficoPanel.revalidate();
    
                tableModel.setRowCount(0);
                tableModel.addRow(new Object[]{"Tempo médio de execução (Round-Robin)", simulador.getTurnaroundTimeRR(), "Tempo médio de execução no Round-Robin"});
                tableModel.addRow(new Object[]{"Tempo médio de espera (Round-Robin)", simulador.getWaitingTimeRR(), "Tempo médio de espera no Round-Robin"});
                tableModel.addRow(new Object[]{"Número de trocas de contexto (Round-Robin)", simulador.getContextSwitchesRR(), "Trocas de contexto no Round-Robin"});
                tableModel.addRow(new Object[]{"Tempo médio de execução (SJF)", simulador.getTurnaroundTimeSJF(), "Tempo médio de execução no SJF"});
                tableModel.addRow(new Object[]{"Tempo médio de espera (SJF)", simulador.getWaitingTimeSJF(), "Tempo médio de espera no SJF"});
    
            } catch (NumberFormatException ex) {
                outputArea.setText("Por favor, insira valores válidos para os campos.");
            }
        });
    
        frame.add(panel);
        frame.setVisible(true);
    }

    private static void estilizarLabel(JLabel label, boolean isSmall) {
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", isSmall ? Font.PLAIN : Font.BOLD, isSmall ? 12 : 14));
        label.setBorder(new EmptyBorder(isSmall ? 2 : 5, 0, isSmall ? 2 : 5, 0));
    }

    private static void estilizarInput(JTextField input) {
        input.setFont(new Font("Arial", Font.PLAIN, 14));
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE30613), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
}

class Simulador {
    private final int numProcessadores;
    private final int numProcessos;
    private final int quantum;
    private final List<Processo> processos;
    private double turnaroundTimeRR;
    private double waitingTimeRR;
    private int contextSwitchesRR;
    private double turnaroundTimeSJF;
    private double waitingTimeSJF;

    public Simulador(int numProcessadores, int numProcessos, int quantum) {
        this.numProcessadores = numProcessadores;
        this.numProcessos = numProcessos;
        this.quantum = quantum;
        this.processos = gerarProcessos(numProcessos);
    }

    private List<Processo> gerarProcessos(int quantidade) {
        List<Processo> lista = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < quantidade; i++) {
            int tempoChegada = random.nextInt(10);
            int duracao = random.nextInt(10) + 1;
            int prioridade = random.nextInt(5) + 1;
            lista.add(new Processo(i + 1, tempoChegada, duracao, prioridade));
        }

        return lista;
    }

    public String executar() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("Simulando com ").append(numProcessadores).append(" processadores e ")
                .append(numProcessos).append(" processos.\n");
        resultado.append("Quantum para Round-Robin: ").append(quantum).append("\n\n");

        resultado.append("=== Processos Gerados ===\n");
        for (Processo p : processos) {
            resultado.append(p).append("\n");
        }

        resultado.append("\n=== Execução Round-Robin ===\n");
        executarRoundRobin(resultado);

        resultado.append("\n=== Execução Shortest Job First (SJF) ===\n");
        executarSJF(resultado);

        return resultado.toString();
    }

    private void executarRoundRobin(StringBuilder resultado) {
        Queue<Processo> fila = new LinkedList<>(processos);
        int tempoAtual = 0;
        int totalWaitTime = 0;
        int totalTurnaroundTime = 0;
        contextSwitchesRR = 0;

        while (!fila.isEmpty()) {
            Processo processo = fila.poll();

            if (processo.getDuracao() > quantum) {
                processo.reduzirDuracao(quantum);
                tempoAtual += quantum;
                resultado.append("Tempo ").append(tempoAtual).append(": Processo ").append(processo.getId())
                        .append(" em execução.\n");
                fila.add(processo);
                contextSwitchesRR++;
            } else {
                tempoAtual += processo.getDuracao();
                int turnaround = tempoAtual - processo.getTempoChegada();
                int waiting = turnaround - processo.getDuracao();

                // Corrigir valores negativos
                if (waiting < 0)
                    waiting = 0;

                totalTurnaroundTime += turnaround;
                totalWaitTime += waiting;

                resultado.append("Tempo ").append(tempoAtual).append(": Processo ").append(processo.getId())
                        .append(" finalizado.\n");
            }
        }

        turnaroundTimeRR = (double) totalTurnaroundTime / processos.size();
        waitingTimeRR = (double) totalWaitTime / processos.size();
    }

    private void executarSJF(StringBuilder resultado) {
        List<Processo> fila = new ArrayList<>(processos);
        fila.sort(Comparator.comparingInt(Processo::getDuracao));
        int tempoAtual = 0;
        int totalWaitTime = 0;
        int totalTurnaroundTime = 0;

        for (Processo processo : fila) {
            tempoAtual += processo.getDuracao();
            int turnaround = tempoAtual - processo.getTempoChegada();
            int waiting = turnaround - processo.getDuracao();

            // Corrigir valores negativos
            if (waiting < 0)
                waiting = 0;

            totalTurnaroundTime += turnaround;
            totalWaitTime += waiting;

            resultado.append("Tempo ").append(tempoAtual).append(": Processo ").append(processo.getId())
                    .append(" finalizado.\n");
        }

        turnaroundTimeSJF = (double) totalTurnaroundTime / processos.size();
        waitingTimeSJF = (double) totalWaitTime / processos.size();
    }

    public DefaultCategoryDataset getDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Math.max(0, turnaroundTimeRR), "Round-Robin", "Turnaround Time");
        dataset.addValue(Math.max(0, waitingTimeRR), "Round-Robin", "Waiting Time");
        dataset.addValue(contextSwitchesRR, "Round-Robin", "Context Switches");
        dataset.addValue(Math.max(0, turnaroundTimeSJF), "SJF", "Turnaround Time");
        dataset.addValue(Math.max(0, waitingTimeSJF), "SJF", "Waiting Time");
        return dataset;
    }

    public double getTurnaroundTimeRR() {
        return turnaroundTimeRR;
    }

    public double getWaitingTimeRR() {
        return waitingTimeRR;
    }

    public int getContextSwitchesRR() {
        return contextSwitchesRR;
    }

    public double getTurnaroundTimeSJF() {
        return turnaroundTimeSJF;
    }

    public double getWaitingTimeSJF() {
        return waitingTimeSJF;
    }
}

class Processo {
    private final int id;
    private final int tempoChegada;
    private int duracao;
    private final int prioridade;

    public Processo(int id, int tempoChegada, int duracao, int prioridade) {
        this.id = id;
        this.tempoChegada = tempoChegada;
        this.duracao = duracao;
        this.prioridade = prioridade;
    }

    public int getId() {
        return id;
    }

    public int getDuracao() {
        return duracao;
    }

    public void reduzirDuracao(int quantum) {
        this.duracao -= quantum;
    }

    public int getTempoChegada() {
        return tempoChegada;
    }

    @Override
    public String toString() {
        return "Processo " + id + " - Chegada: " + tempoChegada + " - Duração: " + duracao + " - Prioridade: "
                + prioridade;
    }
}
