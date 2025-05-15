
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

public class RobotManagementGUI {

    private JFrame mainFrame;
    private JFrame robotLivraisonFrame;
    private JFrame robotRecycleurFrame;
    private JFrame robotSolaireFrame;

    private JTextPane consoleOutput;
    private JScrollPane consoleScrollPane;

    private RobotLivraison robotLivraison;
    private RobotRecycleur robotRecycleur;
    private RobotSolaire robotSolaire;

    private JPanel mapPanel;

    private ImageIcon backgroundImage;
    private ImageIcon livraisonRobotIcon;
    private ImageIcon recycleurRobotIcon;
    private ImageIcon solaireRobotIcon;
    private ImageIcon stationIcon1;
    private ImageIcon stationIcon2;
    private ImageIcon stationIcon3;
    private ImageIcon packageIcon;
    private ImageIcon networkIcon;
    private List<DeliveryStation> deliveryStations;

    // For animation purposes
    private Timer deliveryAnimationTimer;
    private Point startPoint;
    private Point endPoint;
    private boolean isDelivering;
    private int animationStep;
    private static final int TOTAL_ANIMATION_STEPS = 30;

    // Inner class to represent a delivery station
    private static class DeliveryStation {

        String name;
        int x;
        int y;
        ImageIcon icon;

        DeliveryStation(String name, int x, int y, ImageIcon icon) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.icon = icon;
        }
    }

    public RobotManagementGUI() {
        // Initialize robots
        robotLivraison = new RobotLivraison("Livraison-R2D2", 0, 0);
        robotRecycleur = new RobotRecycleur("Recycleur-WALL-E", 0, 0);
        robotSolaire = new RobotSolaire("Solaire-SOL1", 0, 0);

        // Initialize delivery stations
        deliveryStations = new ArrayList<>();

        // Redirect System.out to our console
        redirectSystemOut();

        // Load images
        loadImages();

        // Create and configure main frame
        createMainFrame();
    }

    private void loadImages() {
        // Load images with error handling
        try {
            // You would need to create these image files in your project
            backgroundImage = new ImageIcon(getClass().getResource("background1.jpg"));
            livraisonRobotIcon = new ImageIcon(getClass().getResource("delivry.png"));
            recycleurRobotIcon = new ImageIcon(getClass().getResource("Recycleur.png"));
            solaireRobotIcon = new ImageIcon(getClass().getResource("Solar.png"));

            // New icons for delivery stations (placeholders - replace with actual company logos)
            stationIcon1 = new ImageIcon(getClass().getResource("Immeuble.png"));
            stationIcon2 = new ImageIcon(getClass().getResource("Industry.png"));
            stationIcon3 = new ImageIcon(getClass().getResource("mall.png"));
            packageIcon = new ImageIcon(getClass().getResource("package.png"));
            try {
                networkIcon = new ImageIcon(getClass().getResource("/icon.png"));
                // Si l'icône est trop grande, vous pouvez la redimensionner
                Image image = networkIcon.getImage();
                Image resizedImage = image.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
                networkIcon = new ImageIcon(resizedImage);
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'icône réseau: " + e.getMessage());
            }
            // Create fallback icons if images not available
            if (stationIcon1 == null) {
                stationIcon1 = createTextIcon("Entreprise", new Color(0, 100, 200));
            }
            if (stationIcon2 == null) {
                stationIcon2 = createTextIcon("Industry", new Color(200, 50, 50));
            }
            if (stationIcon3 == null) {
                stationIcon3 = createTextIcon("Mall", new Color(50, 180, 50));
            }

            // Scale images if needed
            Image img = backgroundImage.getImage().getScaledInstance(800, 600, Image.SCALE_SMOOTH);
            backgroundImage = new ImageIcon(img);

            // Initialize delivery stations with their positions
            deliveryStations.add(new DeliveryStation("Entreprise", 100, 200, stationIcon1));
            deliveryStations.add(new DeliveryStation("Industry", 400, 150, stationIcon2));
            deliveryStations.add(new DeliveryStation("Mall", 200, 350, stationIcon3));

        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
            // Use default images or placeholders
            backgroundImage = null;
            livraisonRobotIcon = null;
            recycleurRobotIcon = null;
            solaireRobotIcon = null;

            // Create text-based icons for stations
            stationIcon1 = createTextIcon("Entreprise", new Color(0, 100, 200));
            stationIcon2 = createTextIcon("Industry", new Color(200, 50, 50));
            stationIcon3 = createTextIcon("Mall", new Color(50, 180, 50));

            // Initialize delivery stations with their positions
            deliveryStations.add(new DeliveryStation("Entreprise", 100, 200, stationIcon1));
            deliveryStations.add(new DeliveryStation("Industry", 400, 150, stationIcon2));
            deliveryStations.add(new DeliveryStation("Mall", 200, 350, stationIcon3));
        }

    }

    // Create a text-based icon when image resources aren't available
    private ImageIcon createTextIcon(String text, Color bgColor) {
        // Create a small image with text
        BufferedImage img = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Draw background
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, 60, 60);

        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 59, 59);

        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.drawString(text, (60 - textWidth) / 2, 30 + textHeight / 4);

        g2d.dispose();
        return new ImageIcon(img);
    }

    private void showHistoryDialog(List<String> history) {
        JDialog historyDialog = new JDialog();
        historyDialog.setTitle("Historique des Actions");
        historyDialog.setSize(500, 400);
        historyDialog.setLayout(new BorderLayout());

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Join all history entries with newlines
        String historyText = String.join("\n", history);
        historyArea.setText(historyText);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> historyDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        historyDialog.add(buttonPanel, BorderLayout.SOUTH);

        historyDialog.setLocationRelativeTo(null);
        historyDialog.setVisible(true);
    }

    private void createMainFrame() {
        mainFrame = new JFrame("Robot Management System");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // Background panel with image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Améliorer la qualité du rendu
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(200, 230, 200)); // Light green fallback
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Robot Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Robot selection panel
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(3, 1, 10, 10));
        selectionPanel.setOpaque(false);
        selectionPanel.setBorder(new EmptyBorder(20, 100, 20, 100));
        JPanel rechargePanel = new JPanel();
        //boutton charge li nsitha
        rechargePanel.setOpaque(false);
        JButton rechargeAllButton = createStyledButton("Recharger tous les robots", Color.GREEN);
        rechargeAllButton.addActionListener(e -> {
            robotLivraison.recharger(100);
            robotRecycleur.recharger(100);
            robotSolaire.recharger(100);
            appendToConsole("Tous les robots ont été rechargés à 100%\n", false);
        });
        rechargePanel.add(rechargeAllButton);
        // Create robot selection buttons
        JButton livraisonButton = createStyledButton("Robot de Livraison", Color.BLUE);
        JButton recycleurButton = createStyledButton("Robot Recycleur", new Color(0, 150, 0));
        JButton solaireButton = createStyledButton("Robot Solaire", new Color(255, 165, 0));

        // Add action listeners
        livraisonButton.addActionListener(e -> openRobotLivraisonFrame());
        recycleurButton.addActionListener(e -> openRobotRecycleurFrame());
        solaireButton.addActionListener(e -> openRobotSolaireFrame());

        // Add buttons to panel
        selectionPanel.add(livraisonButton);
        selectionPanel.add(recycleurButton);
        selectionPanel.add(solaireButton);

        // Console output panel
        createConsolePanel();

        // Add panels to main frame
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);
        backgroundPanel.add(selectionPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(rechargePanel, BorderLayout.NORTH);
        southPanel.add(consoleScrollPane, BorderLayout.CENTER);

        backgroundPanel.add(southPanel, BorderLayout.SOUTH);

        mainFrame.add(backgroundPanel);
        mainFrame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    private void createConsolePanel() {
        consoleOutput = new JTextPane();
        consoleOutput.setEditable(false);
        consoleOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));

        consoleScrollPane = new JScrollPane(consoleOutput);
        consoleScrollPane.setPreferredSize(new Dimension(800, 150));

        // Set up document styles for colored text
        StyledDocument doc = consoleOutput.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", defaultStyle);
        StyleConstants.setForeground(regular, Color.BLACK);

        Style error = doc.addStyle("error", regular);
        StyleConstants.setForeground(error, Color.RED);
    }

    // Method to redirect System.out to our console
    private void redirectSystemOut() {
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                char c = (char) b;
                buffer.append(c);
                if (c == '\n') {
                    final String text = buffer.toString();
                    SwingUtilities.invokeLater(() -> {
                        appendToConsole(text, false);
                    });
                    buffer = new StringBuilder();
                }
            }
        }));
    }

    // Method to append text to console with color
    public void appendToConsole(String text, boolean isError) {
        if (consoleOutput == null) {
            return;
        }

        StyledDocument doc = consoleOutput.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), text,
                    doc.getStyle(isError ? "error" : "regular"));
            consoleOutput.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /* Robot de Livraison Frame */
    private void openRobotLivraisonFrame() {
        if (robotLivraisonFrame != null) {
            robotLivraisonFrame.dispose();
        }

        robotLivraisonFrame = new JFrame("Robot de Livraison - " + robotLivraison.id);
        robotLivraisonFrame.setSize(800, 600);
        robotLivraisonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        robotLivraisonFrame.setLayout(new BorderLayout());

        // Create a path visualization panel
        JPanel pathPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw a delivery path background
                g.setColor(new Color(240, 240, 240));
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw grid lines
                g.setColor(new Color(200, 200, 200));
                for (int i = 0; i < getWidth(); i += 50) {
                    g.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 50) {
                    g.drawLine(0, i, getWidth(), i);
                }

                // Draw delivery stations
                for (DeliveryStation station : deliveryStations) {
                    if (station.icon != null) {
                        g.drawImage(station.icon.getImage(),
                                station.x, station.y, 60, 60, this);
                        g.setColor(Color.BLACK);
                        g.drawString(station.name, station.x, station.y + 75);
                    } else {
                        g.setColor(Color.ORANGE);
                        g.fillRect(station.x, station.y, 60, 60);
                        g.setColor(Color.BLACK);
                        g.drawString(station.name, station.x, station.y + 70);
                    }
                }

                // If animation is in progress, draw path line
                if (isDelivering && startPoint != null && endPoint != null) {
                    g.setColor(new Color(0, 100, 255));
                    ((Graphics2D) g).setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            0, new float[]{5.0f, 5.0f}, 0));
                    g.drawLine(startPoint.x + 25, startPoint.y + 25,
                            endPoint.x + 25, endPoint.y + 25);
                    ((Graphics2D) g).setStroke(new BasicStroke()); // Reset stroke
                }

                // Draw robot position
                int robotX, robotY;

                if (isDelivering) {
                    // Calculate animation position
                    double progress = (double) animationStep / TOTAL_ANIMATION_STEPS;
                    robotX = startPoint.x + (int) ((endPoint.x - startPoint.x) * progress);
                    robotY = startPoint.y + (int) ((endPoint.y - startPoint.y) * progress);
                } else {
                    robotX = (robotLivraison.x * 5) % (getWidth() - 100) + 50;
                    robotY = (robotLivraison.y * 5) % (getHeight() - 200) + 100;
                }

                if (livraisonRobotIcon != null) {
                    g.drawImage(livraisonRobotIcon.getImage(),
                            robotX, robotY, 50, 50, this);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillOval(robotX, robotY, 20, 20);
                }

                // Draw package icon if robot has a package
                if (robotLivraison.colisActuel > 0) {
                    if (packageIcon != null) {
                        g.drawImage(packageIcon.getImage(), robotX + 30, robotY - 15, 25, 25, this);
                    } else {
                        g.setColor(Color.ORANGE);
                        g.fillRect(robotX + 30, robotY - 10, 15, 15);
                    }
                }
                if (robotLivraison.connecte && networkIcon != null) {
                    g.drawImage(networkIcon.getImage(), robotX + 12, robotY - 30, 25, 25, this);
                }
            }
        };

        JButton rechargeButton = createStyledButton("Recharger", Color.GREEN);
        rechargeButton.addActionListener(e -> {
            robotLivraison.recharger(100);
            appendToConsole("Robot rechargé à 100%\n", false);
            pathPanel.repaint();
        });

        JButton historyButton = createStyledButton("Historique", new Color(150, 0, 150));
        historyButton.addActionListener(e -> showHistoryDialog(robotLivraison.historiqueActions));

        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons
        JButton startButton = createStyledButton("Démarrer", new Color(0, 150, 0));
        JButton stopButton = createStyledButton("Arrêter", Color.RED);
        JButton chargeButton = createStyledButton("Charger Colis", Color.ORANGE);
        JButton moveButton = createStyledButton("Déplacer", Color.BLUE);
        JButton disconnectButton = createStyledButton("Déconnecter", new Color(150, 150, 150));
        JButton sendDataButton = new JButton("Envoyer données");

        JPanel networkPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        networkPanel.setBorder(BorderFactory.createTitledBorder("Options réseau"));
        // Bouton pour connecter/déconnecter au réseau
        JButton connectButton = new JButton("Connecter au réseau");
        networkPanel.add(connectButton);
        networkPanel.add(sendDataButton);
        // Champ pour spécifier le nom du réseau
        JTextField networkField = new JTextField("Réseau_Principal", 15);
        networkPanel.add(new JLabel("Nom du réseau:"));
        networkPanel.add(networkField);

        // Add action listeners
        JTextField dataField = new JTextField("Données de livraison", 15);
        networkPanel.add(new JLabel("Données:"));
        networkPanel.add(dataField);

        sendDataButton.addActionListener(e -> {
            try {
                String donnees = dataField.getText().trim();
                if (donnees.isEmpty()) {
                    appendToConsole("Erreur: Veuillez spécifier des données à envoyer\n", true);
                    return;
                }

                robotLivraison.envoyerDonnees(donnees);
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        startButton.addActionListener(e -> {
            try {
                robotLivraison.démarrer();
                robotLivraison.energie = 100; // Ensure enough energy for demo
                pathPanel.repaint();
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        stopButton.addActionListener(e -> {
            robotLivraison.arreter();
            pathPanel.repaint();
        });
        chargeButton.addActionListener(e -> {
            if (!robotLivraison.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            // Convert delivery stations to array for JOptionPane
            String[] destinations = new String[deliveryStations.size()];
            for (int i = 0; i < deliveryStations.size(); i++) {
                destinations[i] = deliveryStations.get(i).name;
            }

            // Show selection dialog
            String selectedDestination = (String) JOptionPane.showInputDialog(
                    robotLivraisonFrame,
                    "Choisissez une destination pour le colis:",
                    "Charger Colis",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    destinations,
                    destinations[0]
            );

            if (selectedDestination != null) {
                try {
                    // Find selected station
                    DeliveryStation targetStation = null;
                    for (DeliveryStation station : deliveryStations) {
                        if (station.name.equals(selectedDestination)) {
                            targetStation = station;
                            break;
                        }
                    }

                    if (targetStation != null) {
                        // Current robot position
                        int robotX = (robotLivraison.x * 5) % (pathPanel.getWidth() - 100) + 50;
                        int robotY = (robotLivraison.y * 5) % (pathPanel.getHeight() - 200) + 100;

                        // Load the package
                        robotLivraison.chargerColis(selectedDestination);
                        robotLivraison.destination = selectedDestination;

                        // Start delivery animation
                        startDeliveryAnimation(
                                new Point(robotX, robotY),
                                new Point(targetStation.x, targetStation.y),
                                pathPanel
                        );
                    }
                } catch (EnergieInsuffisanteException ex) {
                    appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
                }
            }
        });
        moveButton.addActionListener(e -> {
            if (!robotLivraison.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            try {
                String xStr = JOptionPane.showInputDialog(robotLivraisonFrame, "Entrez X:");
                String yStr = JOptionPane.showInputDialog(robotLivraisonFrame, "Entrez Y:");

                if (xStr != null && yStr != null) {
                    int x = Integer.parseInt(xStr);
                    int y = Integer.parseInt(yStr);

                    // Check if this is a delivery point
                    if (robotLivraison.colisActuel > 0
                            && robotLivraison.destination != null) {

                        // Find if there's a delivery station at these coordinates
                        DeliveryStation targetStation = null;
                        for (DeliveryStation station : deliveryStations) {
                            // Check if x,y is close to a station
                            if (Math.abs(station.x / 5 - x) < 10 && Math.abs(station.y / 5 - y) < 10) {
                                targetStation = station;
                                break;
                            }
                        }

                        if (targetStation != null
                                && targetStation.name.equals(robotLivraison.destination)) {

                            // Current robot position
                            int robotX = (robotLivraison.x * 5) % (pathPanel.getWidth() - 100) + 50;
                            int robotY = (robotLivraison.y * 5) % (pathPanel.getHeight() - 200) + 100;

                            // Start delivery animation
                            startDeliveryAnimation(
                                    new Point(robotX, robotY),
                                    new Point(targetStation.x, targetStation.y),
                                    pathPanel
                            );

                            robotLivraison.FaireLivraison(x, y);
                        } else {
                            robotLivraison.deplacer(x, y);
                        }
                    } else {
                        robotLivraison.deplacer(x, y);
                    }

                    pathPanel.repaint();
                }
            } catch (NumberFormatException ex) {
                appendToConsole("Erreur: Coordonnées invalides\n", true);
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        connectButton.addActionListener(e -> {
            try {
                if (!robotLivraison.connecte) {
                    // Connecter au réseau
                    String reseau = networkField.getText().trim();
                    if (reseau.isEmpty()) {
                        appendToConsole("Erreur: Veuillez spécifier un nom de réseau\n", true);
                        return;
                    }

                    robotLivraison.connecter(reseau);
                    connectButton.setText("Déconnecter du réseau");
                } else {
                    // Déconnecter du réseau
                    robotLivraison.deconnecter();
                    connectButton.setText("Connecter au réseau");
                }
                // Forcer le repaint pour mettre à jour l'affichage de l'icône
                pathPanel.repaint();

            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        disconnectButton.addActionListener(e -> {
            robotLivraison.deconnecter();
        });

        // Add buttons to control panel
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(chargeButton);
        controlPanel.add(moveButton);
        controlPanel.add(rechargeButton);
        controlPanel.add(historyButton);

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("Status: " + robotLivraison.toString());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshButton = new JButton("Rafraîchir le statut");
        refreshButton.addActionListener(e -> {
            statusLabel.setText("Status: " + robotLivraison.toString());
        });

        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(refreshButton, BorderLayout.EAST);

        // Add panels to frame
        robotLivraisonFrame.add(pathPanel, BorderLayout.CENTER);
        robotLivraisonFrame.add(controlPanel, BorderLayout.SOUTH);
        robotLivraisonFrame.add(statusPanel, BorderLayout.NORTH);
        robotLivraisonFrame.add(networkPanel, BorderLayout.EAST);

        robotLivraisonFrame.setVisible(true);
    }

    // Method to handle delivery animation
    private void startDeliveryAnimation(Point start, Point end, JPanel panel) {
        // Cancel any existing animation
        if (deliveryAnimationTimer != null && deliveryAnimationTimer.isRunning()) {
            deliveryAnimationTimer.stop();
        }

        // Set up animation parameters
        startPoint = start;
        endPoint = end;
        isDelivering = true;
        animationStep = 0;

        // Create timer for animation
        deliveryAnimationTimer = new Timer(50, e -> {
            animationStep++;
            panel.repaint();

            if (animationStep >= TOTAL_ANIMATION_STEPS) {
                // Animation complete
                isDelivering = false;
                deliveryAnimationTimer.stop();

                // Complete delivery
                if (robotLivraison.colisActuel > 0
                        && robotLivraison.destination != null) {
                    robotLivraison.colisActuel = 0;
                    appendToConsole("Colis livré à " + robotLivraison.destination + "\n", false);
                    robotLivraison.destination = null;
                }

                panel.repaint();
            }
        });

        deliveryAnimationTimer.start();
    }

    /* Robot Recycleur Frame */
    private void openRobotRecycleurFrame() {
        if (robotRecycleurFrame != null) {
            robotRecycleurFrame.dispose();
        }

        robotRecycleurFrame = new JFrame("Robot Recycleur - " + robotRecycleur.id);
        robotRecycleurFrame.setSize(800, 600);
        robotRecycleurFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        robotRecycleurFrame.setLayout(new BorderLayout());

        // Create recyclage visualization panel
        JPanel recyclePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw environment background
                g.setColor(new Color(230, 255, 230));
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw recycling bins
                drawRecyclingBin(g, 50, 100, Color.BLUE, "Papier");
                drawRecyclingBin(g, 150, 100, Color.GREEN, "Verre");
                drawRecyclingBin(g, 250, 100, Color.YELLOW, "Plastique");
                drawRecyclingBin(g, 350, 100, Color.GRAY, "Metal");

                // Draw waste level indicator
                int wasteLevel = robotRecycleur.currentWasteLevel;
                int capacity = robotRecycleur.wasteCapacity;
                int barHeight = 150;
                int filledHeight = (int) (barHeight * ((double) wasteLevel / capacity));

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(600, 100, 30, barHeight);
                g.setColor(new Color(100, 200, 100));
                g.fillRect(600, 100 + (barHeight - filledHeight), 30, filledHeight);
                g.setColor(Color.BLACK);
                g.drawRect(600, 100, 30, barHeight);

                g.drawString("Niveau de déchets: " + wasteLevel + "/" + capacity, 550, 270);

                // Draw robot
                int robotX = (robotRecycleur.x * 5) % (getWidth() - 100) + 50;
                int robotY = (robotRecycleur.y * 5) % (getHeight() - 200) + 150;

                if (recycleurRobotIcon != null) {
                    g.drawImage(recycleurRobotIcon.getImage(),
                            robotX, robotY, 60, 60, this);
                } else {
                    g.setColor(Color.GREEN);
                    g.fillRect(robotX, robotY, 30, 30);
                }
            }

            private void drawRecyclingBin(Graphics g, int x, int y, Color color, String label) {
                g.setColor(color);
                g.fillRect(x, y, 60, 80);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, 60, 80);
                g.drawString(label, x + 10, y + 100);
            }
        };
        JButton historyButton = createStyledButton("Historique", new Color(150, 0, 150));
        historyButton.addActionListener(e -> showHistoryDialog(robotRecycleur.historiqueActions));
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 3, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.add(historyButton);

        JButton rechargeButton = createStyledButton("Recharger", Color.CYAN);
        rechargeButton.addActionListener(e -> {
            robotRecycleur.recharger(100);  // Changed from robotLivraison to robotRecycleur
            appendToConsole("Robot rechargé à 100%\n", false);
            recyclePanel.repaint();
        });

        // Create buttons
        JButton startButton = createStyledButton("Démarrer", new Color(0, 150, 0));
        JButton stopButton = createStyledButton("Arrêter", Color.RED);
        JButton moveButton = createStyledButton("Déplacer", Color.BLUE);
        JButton recycleButton = createStyledButton("Recycler", new Color(0, 200, 0));
        JButton monitorButton = createStyledButton("Surveiller environnement", new Color(100, 100, 255));
        JButton calculateFootprintButton = createStyledButton("Calculer empreinte", new Color(150, 100, 150));

        // Add action listeners
        startButton.addActionListener(e -> {
            try {
                robotRecycleur.démarrer();
                robotRecycleur.energie = 100; // Ensure enough energy for demo
                recyclePanel.repaint();
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        stopButton.addActionListener(e -> {
            robotRecycleur.arreter();
            recyclePanel.repaint();
        });

        moveButton.addActionListener(e -> {
            if (!robotRecycleur.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            try {
                String xStr = JOptionPane.showInputDialog(robotRecycleurFrame, "Entrez X:");
                String yStr = JOptionPane.showInputDialog(robotRecycleurFrame, "Entrez Y:");

                if (xStr != null && yStr != null) {
                    int x = Integer.parseInt(xStr);
                    int y = Integer.parseInt(yStr);

                    robotRecycleur.deplacer(x, y);
                    recyclePanel.repaint();
                }
            } catch (NumberFormatException ex) {
                appendToConsole("Erreur: Coordonnées invalides\n", true);
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        recycleButton.addActionListener(e -> {
            if (!robotRecycleur.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            String[] materials = {"plastique", "verre", "papier", "metal"};
            String material = (String) JOptionPane.showInputDialog(
                    robotRecycleurFrame, "Choisissez un matériau à recycler:",
                    "Recyclage", JOptionPane.QUESTION_MESSAGE, null,
                    materials, materials[0]);

            if (material != null) {
                String amountStr = JOptionPane.showInputDialog(
                        robotRecycleurFrame, "Quelle quantité?");

                if (amountStr != null) {
                    try {
                        int amount = Integer.parseInt(amountStr);
                        robotRecycleur.recyclerMateriau(material, amount);
                        recyclePanel.repaint();
                    } catch (NumberFormatException ex) {
                        appendToConsole("Erreur: Quantité invalide\n", true);
                    } catch (RobotException ex) {
                        appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
                    }
                }
            }
        });

        monitorButton.addActionListener(e -> {
            if (!robotRecycleur.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            Map<String, Double> readings = robotRecycleur.monitorEnvironment();
            StringBuilder sb = new StringBuilder("Lecture environnementale:\n");

            for (Map.Entry<String, Double> entry : readings.entrySet()) {
                sb.append("- ").append(entry.getKey()).append(": ")
                        .append(String.format("%.2f", entry.getValue())).append("\n");
            }

            JOptionPane.showMessageDialog(robotRecycleurFrame, sb.toString());
        });

        calculateFootprintButton.addActionListener(e -> {
            double footprint = robotRecycleur.calculateCarbonFootprint();
            JOptionPane.showMessageDialog(robotRecycleurFrame,
                    "Empreinte carbone: " + String.format("%.2f", footprint));
        });

        // Add buttons to control panel
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(moveButton);
        controlPanel.add(recycleButton);
        controlPanel.add(monitorButton);
        controlPanel.add(calculateFootprintButton);
        controlPanel.add(rechargeButton);

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("Status: " + robotRecycleur.toString());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshButton = new JButton("Rafraîchir le statut");
        refreshButton.addActionListener(e -> {
            statusLabel.setText("Status: " + robotRecycleur.toString());
            recyclePanel.repaint();
        });

        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(refreshButton, BorderLayout.EAST);

        // Add panels to frame
        robotRecycleurFrame.add(recyclePanel, BorderLayout.CENTER);
        robotRecycleurFrame.add(controlPanel, BorderLayout.SOUTH);
        robotRecycleurFrame.add(statusPanel, BorderLayout.NORTH);

        robotRecycleurFrame.setVisible(true);
    }

    /* Robot Solaire Frame */
    private void openRobotSolaireFrame() {
        if (robotSolaireFrame != null) {
            robotSolaireFrame.dispose();
        }

        robotSolaireFrame = new JFrame("Robot Solaire - " + robotSolaire.id);
        robotSolaireFrame.setSize(800, 600);
        robotSolaireFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        robotSolaireFrame.setLayout(new BorderLayout());

        // Create solar robot visualization panel
        JPanel solarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw sunny background
                g.setColor(new Color(135, 206, 250)); // Sky blue
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw sun
                g.setColor(Color.YELLOW);
                g.fillOval(getWidth() - 100, 50, 60, 60);

                // Draw ground
                g.setColor(new Color(200, 180, 120)); // Sand color
                g.fillRect(0, getHeight() - 100, getWidth(), 100);

                // Draw robot
                int robotX = (robotSolaire.x * 5) % (getWidth() - 100) + 50;
                int robotY = (robotSolaire.y * 5) % (getHeight() - 200) + 150;

                if (solaireRobotIcon != null) {
                    g.drawImage(solaireRobotIcon.getImage(),
                            robotX, robotY, 80, 80, this);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(robotX, robotY, 40, 30);
                }

                // Draw solar panel if deployed
                if (robotSolaire.panneauDeplié) {
                    g.setColor(Color.BLUE);
                    g.fillRect(robotX - 20, robotY - 20, 80, 10);

                    // Draw solar rays
                    g.setColor(Color.YELLOW);
                    drawDashedLine(g, getWidth() - 70, 80, robotX + 20, robotY - 15);
                }

                // Draw energy bar
                int energy = robotSolaire.energie;
                int barWidth = 150;
                int filledWidth = (int) (barWidth * (energy / 100.0));

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(50, 50, barWidth, 20);
                g.setColor(new Color(255, 165, 0));
                g.fillRect(50, 50, filledWidth, 20);
                g.setColor(Color.BLACK);
                g.drawRect(50, 50, barWidth, 20);

                g.drawString("Énergie: " + energy + "%", 50, 90);
            }

            private void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2) {
                Graphics2D g2d = (Graphics2D) g;
                float[] dash = {5.0f, 5.0f};
                g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2d.drawLine(x1, y1, x2, y2);
                g2d.setStroke(new BasicStroke()); // Reset stroke
            }
        };
        JButton historyButton = createStyledButton("Historique", new Color(150, 0, 150));
        historyButton.addActionListener(e -> showHistoryDialog(robotSolaire.historiqueActions));
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 3, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.add(historyButton);

        // Create buttons
        JButton startButton = createStyledButton("Démarrer", new Color(0, 150, 0));
        JButton stopButton = createStyledButton("Arrêter", Color.RED);
        JButton moveButton = createStyledButton("Déplacer", Color.BLUE);
        JButton deployPanelButton = createStyledButton("Déplier Panneau", new Color(255, 165, 0));
        JButton foldPanelButton = createStyledButton("Replier Panneau", new Color(150, 100, 0));
        JButton chargeButton = createStyledButton("Charger via solaire", new Color(255, 215, 0));

        // Add action listeners
        startButton.addActionListener(e -> {
            try {
                robotSolaire.démarrer();
                robotSolaire.energie = 50; // Start with some energy
                solarPanel.repaint();
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        stopButton.addActionListener(e -> {
            robotSolaire.arreter();
            solarPanel.repaint();
        });

        moveButton.addActionListener(e -> {
            if (!robotSolaire.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }
            // dans la méthode openRobotSolaireFrame()

            try {
                String xStr = JOptionPane.showInputDialog(robotSolaireFrame, "Entrez X:");
                String yStr = JOptionPane.showInputDialog(robotSolaireFrame, "Entrez Y:");

                if (xStr != null && yStr != null) {
                    int x = Integer.parseInt(xStr);
                    int y = Integer.parseInt(yStr);

                    robotSolaire.deplacer(x, y);
                    solarPanel.repaint();
                }
            } catch (NumberFormatException ex) {
                appendToConsole("Erreur: Coordonnées invalides\n", true);
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        deployPanelButton.addActionListener(e -> {
            if (!robotSolaire.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            try {
                robotSolaire.déplierPanneau();
                appendToConsole("Panneau solaire déplié\n", false);
                solarPanel.repaint();
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        foldPanelButton.addActionListener(e -> {
            if (!robotSolaire.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            try {
                robotSolaire.replierPanneau();
                appendToConsole("Panneau solaire replié\n", false);
                solarPanel.repaint();
            } catch (RobotException ex) {
                appendToConsole("Erreur: " + ex.getMessage() + "\n", true);
            }
        });

        chargeButton.addActionListener(e -> {
            if (!robotSolaire.enMarche) {
                appendToConsole("Erreur: Le robot doit être démarré\n", true);
                return;
            }

            if (!robotSolaire.panneauDeplié) {
                appendToConsole("Erreur: Le panneau solaire doit être déplié\n", true);
                return;
            }

            // Ask for hours input
            String hoursStr = JOptionPane.showInputDialog(robotSolaireFrame,
                    "Combien d'heures de charge?");

            if (hoursStr != null && !hoursStr.isEmpty()) {
                try {
                    int hours = Integer.parseInt(hoursStr);
                    robotSolaire.chargerViaSolaire(hours);
                    appendToConsole("Énergie chargée pendant " + hours + " heures\n", false);
                    solarPanel.repaint();
                } catch (NumberFormatException ex) {
                    appendToConsole("Erreur: Nombre d'heures invalide\n", true);
                }
            }
        });

        // Add buttons to control panel
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(moveButton);
        controlPanel.add(deployPanelButton);
        controlPanel.add(foldPanelButton);
        controlPanel.add(chargeButton);

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("Status: " + robotSolaire.toString());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshButton = new JButton("Rafraîchir le statut");
        refreshButton.addActionListener(e -> {
            statusLabel.setText("Status: " + robotSolaire.toString());
            solarPanel.repaint();
        });

        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(refreshButton, BorderLayout.EAST);

        // Add panels to frame
        robotSolaireFrame.add(solarPanel, BorderLayout.CENTER);
        robotSolaireFrame.add(controlPanel, BorderLayout.SOUTH);
        robotSolaireFrame.add(statusPanel, BorderLayout.NORTH);

        robotSolaireFrame.setVisible(true);
    }

    // Main method to start the application
    public static void main(String[] args) {
        // For thread safety, create GUI on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel to match OS
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RobotManagementGUI();
        });
    }
}
