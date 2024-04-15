import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Game_interface {
    private Board board;
    private final Player[] players;
    private final Scanner sc;
    private Player playerA;
    private Player playerB;
    private boolean gameIsFinished = false;
    private String username; // Ajoutez une variable de classe pour stocker le nom d'utilisateur



    /**
     * Sélectionner aléatoirement une valeur entière entre 0 et 1
     *
     * @return 0 ou 1
     */
    private int randomize() {

        return (int) (Math.random() * 2);
    }

    /**
     * Définir les conditions de fin de jeu
     *
     * @return True si le jeu est fini sinon False
     */
    private boolean isFinished() {

        if (playerA.hasWin() || playerB.hasWin())
            return true;

        return playerA.getNbreJetons() == 0 && playerB.getNbreJetons() == 0;
    }

    /**
     * Initialiser les couleurs, les symboles et les affecter aux joueurs aléatoirement
     *
     * @param nameA Nom du joueur A
     * @param nameB Nom du joueur B
     */
    private void initialize(String nameA, String nameB) {

        Symbol symbolA = randomize() == 0 ? Symbol.JAUNE : Symbol.ROUGE;
        Symbol symbolB = symbolA == Symbol.JAUNE ? Symbol.ROUGE : Symbol.JAUNE;

        int positionA = randomize();
        int positionB = positionA == 0 ? 1 : 0;

        players[positionA] = new Player(nameA, symbolA, this.board);
        players[positionB] = nameB.equals("Ordinateur") ? new Computer(nameB, symbolB, this.board) : new Player(nameB, symbolB, this.board);

        playerA = players[0];
        playerB = players[1];
    }

    /**
     * Démarrer la partie et saisir les choix des utilisateurs
     *
     * @return
     * @throws InterruptedException Du à l'utilisation de Thread.sleep
     */
    private void start() throws InterruptedException {
        JFrame fenetre = new JFrame();
        fenetre.setTitle("Puissance 4");
        fenetre.setSize(400, 300);
        fenetre.setLocationRelativeTo(null);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Utilisation de GridBagLayout pour contrôler la disposition
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espacement entre les composants

        // Titre
        JLabel titre = new JLabel("Login");
        titre.setForeground(Color.WHITE);
        titre.setFont(new Font("Serif", Font.BOLD, 35));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.PAGE_START; // Alignement en haut
        panel.add(titre, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END; // Alignement à droite
        panel.add(usernameLabel, gbc);

        JTextField t_username = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Alignement à gauche
        panel.add(t_username, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END; // Alignement à droite
        panel.add(passwordLabel, gbc);

        JPasswordField t_password = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Alignement à gauche
        panel.add(t_password, gbc);

        // Bouton OK
        JButton ok = new JButton("OK");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Alignement au centre
        panel.add(ok, gbc);
        // Déclarez DatabaseManager comme un champ de classe dans votre classe d'interface graphique
        DataManager dbManager = new DataManager();
        // Utilisez DatabaseManager pour gérer la connexion dans votre actionneur de bouton "OK"
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameInput = t_username.getText();
                String password = new String(t_password.getPassword());

                boolean loggedIn = dbManager.login(usernameInput, password);
                if (loggedIn) {
                    username = usernameInput; // Stockez le nom d'utilisateur dans la variable de classe
                    // Connexion réussie, lancez la page d'accueil
                    Game_interface jouer = new Game_interface();
                    try {
                        jouer.home(username);
                        fenetre.dispose();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    // Afficher un message d'erreur si le mot de passe est incorrect
                    JOptionPane.showMessageDialog(fenetre, "Mot de passe incorrect", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.setBackground(Color.BLACK);
        fenetre.setContentPane(panel);
        fenetre.setVisible(true);
    }

    private void home(String nameA) throws InterruptedException {
        JFrame welcomeFrame = new JFrame();
        welcomeFrame.setSize(400, 300);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLocationRelativeTo(null);

        // Panneau pour contenir les composants
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement entre les composants

        // Titre
        JLabel titleLabel = new JLabel("Bienvenue sur Puissance 4");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 35));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 0); // Espacement supplémentaire
        gbc.anchor = GridBagConstraints.CENTER; // Alignement au centre
        panel.add(titleLabel, gbc);

        // Bouton "Jouer à 2 joueurs"
        JButton play2PlayersButton = new JButton("Jouer à 2 joueurs");
        play2PlayersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Demander le nom du deuxième joueur
                String nameB = JOptionPane.showInputDialog(welcomeFrame, "Entrez le nom du deuxième joueur:", "Nom du joueur 2", JOptionPane.PLAIN_MESSAGE);
                if (nameB != null && !nameB.isEmpty()) {
                    // Continuer avec les noms des joueurs
                    initialize(nameA, nameB);
                    try {
                        jouerManche();
                        welcomeFrame.dispose();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    // Afficher un message d'erreur si aucun nom n'est saisi
                    JOptionPane.showMessageDialog(welcomeFrame, "Veuillez entrer un nom pour le deuxième joueur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Alignement au centre
        panel.add(play2PlayersButton, gbc);

        // Espacement entre les boutons
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 10, 0); // Espacement supplémentaire
        panel.add(Box.createVerticalStrut(10), gbc);

        // Bouton "Jouer contre l'ordinateur"
        JButton playAgainstComputerButton = new JButton("Jouer contre l'ordinateur");
        playAgainstComputerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameB = "Ordinateur";
                initialize(nameA,nameB);
                try {
                    jouerManche();
                    welcomeFrame.dispose();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER; // Alignement au centre
        panel.add(playAgainstComputerButton, gbc);

        // Ajoute le panneau à la fenêtre
        welcomeFrame.add(panel);
        panel.setBackground(Color.BLACK);

        // Affiche la fenêtre
        welcomeFrame.setVisible(true);

    }


    /**
     * Jouer le tour d'un joueur
     *
     * @param j         Le joueur qui doit jouer
     * @param gridPanel
     * @throws InterruptedException Du à l'utilisation de Thread.sleep
     */
    /**
     * Déroulement de la partie
     *
     * @throws InterruptedException Du à l'utilisation de Thread.sleep
     */


    private JLabel turnLabel; // Déclaration du JLabel pour afficher le tour du joueur
    private void jouerManche() throws InterruptedException {
        // Création du JLabel pour afficher le tour du joueur
        turnLabel = new JLabel();

        // Affichage des informations sur les joueurs
        String colorA = playerA.getSymbol() == Symbol.JAUNE ? "jaune" : "rouge";
        String colorB = colorA.equals("jaune") ? "rouge" : "jaune";

        JLabel infoLabel = new JLabel(playerA.getName() + " commencera avec la couleur " + colorA + " et le symbole " + playerA.getSymbol());
        JLabel infoLabel2 = new JLabel(playerB.getName() + " sera second avec la couleur " + colorB + " et le symbole " + playerB.getSymbol());

        JPanel infoPanel = new JPanel();
        infoPanel.add(infoLabel);
        infoPanel.add(infoLabel2);

        // Création de la grille de jeu
        JPanel gridPanel = new JPanel(new GridLayout(board.getGrid().length, board.getGrid()[0].length));
        for (int i = 0; i < board.getGrid().length; i++) {
            for (int j = 0; j < board.getGrid()[0].length; j++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(50, 50));
                cell.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Ajoute une marge à la cellule
                // Récupère le symbole de la position correspondante dans la grille
                Symbol symbol = board.getGrid()[i][j];
                // Personnalise l'apparence de la cellule en fonction du symbole
                if (symbol == Symbol.ROUGE) {
                    cell.setBackground(Color.RED); // Couleur de fond pour un jeton rouge
                } else if (symbol == Symbol.JAUNE) {
                    cell.setBackground(Color.YELLOW); // Couleur de fond pour un jeton jaune
                } else {
                    cell.setBackground(Color.WHITE); // Couleur de fond par défaut
                }
                cell.setOpaque(true); // Assurez-vous que le panneau est opaque pour afficher la couleur de fond
                gridPanel.add(cell);
            }
        }

        // Création d'un panneau pour les boutons de fin de partie
        JPanel endPanel = new JPanel();
        JButton replayButton = new JButton("Rejouer");
        JButton quitButton = new JButton("Quitter");
        endPanel.add(replayButton);
        endPanel.add(quitButton);

        // Affichage de l'interface graphique
        JFrame frame = new JFrame("Puissance 4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(infoPanel, BorderLayout.NORTH);
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(turnLabel, BorderLayout.SOUTH); // Ajout du turnLabel au bas du conteneur
        frame.pack();
        frame.setVisible(true);

        // Logique de jeu
        while (!gameIsFinished) {
            // Tour de joueur A
            jouerTourSolo(playerA, gridPanel);
            // Vérifier si le jeu est terminé après le tour de joueur A
            if (playerA.hasWin()) {
                gameIsFinished = true;
                break;
            }
            // Tour de joueur B
            jouerTourSolo(playerB, gridPanel);
            // Vérifier si le jeu est terminé après le tour de joueur B
            if (playerB.hasWin()) {
                gameIsFinished = true;
                break;
            }
        }

        // Affichage du message de fin de partie et des boutons de fin de partie
        String message = "";
        if (playerA.hasWin()) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/my_database", "root", "root")) {
                String query = "UPDATE users SET score = score + 10 WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, String.valueOf(playerA.getName()));
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            message = playerA.getName() + " est le gagnant !";
        } else if (playerB.hasWin()) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/my_database", "root", "root")) {
                String query = "UPDATE users SET score = score + 10 WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, String.valueOf(playerB.getName()));
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            message = playerB.getName() + " est le gagnant !";
        } else {
            message = "Match nul !";
        }
        JOptionPane.showMessageDialog(frame, message, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
        // Demander à l'utilisateur s'il veut rejouer
        int choice = JOptionPane.showConfirmDialog(frame, "Voulez-vous rejouer ?", "Rejouer", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            // Réinitialiser la grille et relancer une partie
            gameIsFinished = false;
            board = new Board();
            frame.dispose();
            home(username);
        } else {
            // Quitter le jeu
            frame.dispose();
        }
    }

    private void jouerTourSolo(Player player, JPanel gridPanel) throws InterruptedException {
        gameIsFinished = isFinished();
        if (!gameIsFinished) {
            // Afficher le tour du joueur
            turnLabel.setText("C'est le tour de " + player.getName());
            int column = -1;
            // Si ce n'est pas un ordinateur, demandez à l'utilisateur de choisir une colonne
            if (!(player instanceof Computer)) {
                // Demander à l'utilisateur de choisir une colonne valide
                do {
                    String input = JOptionPane.showInputDialog("Choisissez une colonne (0 à 6) : ");
                    try {
                        column = Integer.parseInt(input);
                        if (column < 0 || column > 6 || board.isColumnFull(column)) {
                            JOptionPane.showMessageDialog(gridPanel, "Veuillez entrer une colonne valide.");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(gridPanel, "Veuillez entrer un nombre.");
                    }
                } while (column < 0 || column > 6 || board.isColumnFull(column));
                // Jouez dans la colonne choisie
                player.playAtColumn(column);
            } else {
                // Si c'est un ordinateur, laissez-le choisir une colonne automatiquement
                ((Computer) player).play(board);
            }

            // Mettez à jour l'interface graphique avec le coup du joueur
            updateGridPanel(gridPanel, board);

        }
    }

    private void updateGridPanel(JPanel gridPanel, Board board) {
        gridPanel.removeAll();
        for (int i = 0; i < board.getGrid().length; i++) {
            for (int j = 0; j < board.getGrid()[0].length; j++) {
                int finalJ = j;
                int finalI = i;
                JPanel cell = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Symbol symbol = board.getGrid()[finalI][finalJ];
                        if (symbol == Symbol.ROUGE) {
                            g.setColor(Color.RED);
                        } else if (symbol == Symbol.JAUNE) {
                            g.setColor(Color.YELLOW);
                        } else {
                            g.setColor(Color.WHITE);
                        }
                        int diameter = Math.min(getWidth(), getHeight());
                        g.fillOval((getWidth() - diameter) / 2, (getHeight() - diameter) / 2, diameter, diameter);
                    }
                };
                cell.setPreferredSize(new Dimension(70, 70));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridPanel.add(cell);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }


    public Game_interface() {
        sc = new Scanner(System.in);
        board = new Board();
        players = new Player[2];
    }

    public static void main(String[] args) throws InterruptedException {
        Game_interface game = new Game_interface();
        game.start();

    }
}
