import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main {
    public static void main(String[] args){
        //Créer une fenetre
        JFrame fenetre = new JFrame();
        //Definit un titre
        fenetre.setTitle("Puissance 4");
        //Definit sa taille
        fenetre.setSize(600, 600);
        //Definit sa position
        fenetre.setLocationRelativeTo(null);
        //Terminer le processus à la fermeture de notre fenetre
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Créer un panel pour contenir les composants
        JPanel panel = new JPanel();
        fenetre.setContentPane(panel);
        //Colorer le panel
        panel.setBackground(Color.gray);
        //Cree un label
        JLabel label = new JLabel("Hello world !");
        //Inserer le label au panel
        fenetre.getContentPane().add(label);
        //cree un champs de saisie
        JTextField t = new JTextField(17);
        fenetre.getContentPane().add(t);
        //cree un boutton
        JButton b=new JButton("OK");
        fenetre.getContentPane().add(b);
        //ajouter une action au boutton
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nom=t.getText();
                JLabel label_rep = new JLabel();
                fenetre.getContentPane().add(label_rep);
                label_rep.setText(nom);
            }
        });
        // Rendre la fenêtre visible
        fenetre.setVisible(true);
    }
}
