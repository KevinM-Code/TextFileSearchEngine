package filesearchapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;

/**
 * <h1>File Search Engine</h1> This is an application that will allow you to
 * search local files added to the search engine's index. This application will
 * also allow an administrator to add, update, and remove files from the index.
 * Users will be able to enter search terms, and select between AND, OR, or
 * PHRASE search. The matching file names (if any) are then displayed in a list.
 *
 * @author Kevin Mock
 * @version 1.0
 */
public class FileSearchApp extends JFrame {

    private static final long serialVersionUID = 1L;
    public static JTextField txtEnterSearchTerms;
    final Includer aba = new Includer();
    private static JLabel numFilesLblMain = new JLabel();
    public static JTextArea textPane = new JTextArea();
    public static JRadioButton rdbtnAllTheSearch = new JRadioButton("All the Search Terms");
    public static JRadioButton rdbtnExactPhrase = new JRadioButton("Exact Phrase");
    public static JRadioButton rdbtnAnyOfThe = new JRadioButton("Any of the Search Terms");

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkOrCreatePersistenceFile();
            }
        });
    }

    /**
     * This checks to see if the persistence file has been created in the home
     * directory. If not, it creates the persistence file in the home directory
     * in a file name <i>searchenginefilenonelikeit</i>
     * then starts the program by loading the GUI.
     */
    public static void checkOrCreatePersistenceFile() {
        try {
            String testPathToTextFile = System.getProperty("user.home")
                    + File.separator + "searchenginefilenonelikeit" + File.separator + "persistencefile.txt";
            File isTextFile = new File(testPathToTextFile);

            String path = System.getProperty("user.home");
            path += File.separator + "searchenginefilenonelikeit";
            File mkDir = new File(path);

            if (isTextFile.exists()) {
                FileMaintenanceConsole.getFilePathsFromPersist();
                FileMaintenanceConsole.getCurrentInfoFromFiles();
                FileSearchApp fsa;
                fsa = new FileSearchApp();
                fsa.initSearchGUI();
                if (FileMaintenanceConsole.filePaths.size() == 0) {
                    fsa.initMaintGUI();
                }
            } else if (mkDir.mkdirs()) {
                String pathToTextFile = System.getProperty("user.home")
                        + File.separator + "searchenginefilenonelikeit" + File.separator + "persistencefile.txt";
                File toTextFile = new File(pathToTextFile);
                try {
                    toTextFile.createNewFile();
                    Path filePath = Paths.get(path);
                    //set hidden attribute
                    Files.setAttribute(filePath, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);

                    FileMaintenanceConsole.getFilePathsFromPersist();
                    FileMaintenanceConsole.getCurrentInfoFromFiles();
                    FileSearchApp fsa = new FileSearchApp();
                    fsa.initSearchGUI();
                    fsa.initMaintGUI();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane optionPane = new JOptionPane("Failure to create the persistance file!!", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog("Error Message");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the GUI
     */
    public void initSearchGUI() {
        // Main Console
        FileSearchApp frame = new FileSearchApp();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        // Set the icon in the upper-left corner for Main Window
        String iconPath = System.getProperty("user.dir") + "\\src\\filesearchapp\\pics\\icon.png";
        ImageIcon icon = new ImageIcon(iconPath);
        frame.setIconImage(icon.getImage());
    }

    public void initMaintGUI() {

        // Need some method to stop display if not authorized admin
        // GUI Maintenance Console
        FileMaintenanceConsole frameMaint = new FileMaintenanceConsole();
        frameMaint.setVisible(true);
        frameMaint.setLocationRelativeTo(null);
    }

    /**
     * Create the Main GUI Console.
     */
    public FileSearchApp() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("File Search Application");
        setBounds(100, 100, 769, 613);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Title label
        JLabel titleLbl = new JLabel("Text File Search Engine");
        titleLbl.setBounds(183, 10, 358, 42);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        titleLbl.setFont(new Font("Serif", Font.BOLD, 32));
        contentPane.add(titleLbl);

        // Search button
        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(552, 57, 106, 27);
        searchBtn.setEnabled(false);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SearchTool st = new SearchTool();
                st.selection();
            }
        });
        contentPane.add(searchBtn);

        // Text field to enter search terms
        txtEnterSearchTerms = new JTextField();
        txtEnterSearchTerms.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtEnterSearchTerms.getDocument().getLength() > 0) {
                    searchBtn.setEnabled(true);
                } else {
                    searchBtn.setEnabled(false);

                }
            }
        });
        txtEnterSearchTerms.setBounds(67, 57, 474, 27);
        txtEnterSearchTerms.setHorizontalAlignment(SwingConstants.CENTER);
        txtEnterSearchTerms.setFont(new Font("Serif", Font.BOLD, 14));
        contentPane.add(txtEnterSearchTerms);
        txtEnterSearchTerms.setColumns(10);

        //Radio buttons
        rdbtnAllTheSearch.setBounds(75, 90, 166, 23);
        contentPane.add(rdbtnAllTheSearch);

        rdbtnExactPhrase.setBounds(275, 90, 127, 23);
        contentPane.add(rdbtnExactPhrase);

        rdbtnAnyOfThe.setBounds(425, 90, 202, 23);
        contentPane.add(rdbtnAnyOfThe);

        // Maintenance and About buttons
        JButton btnMaintenance = new JButton("Maintenance");
        btnMaintenance.setBounds(85, 479, 114, 42);
        btnMaintenance.setToolTipText("Open the Maintenance Window");
        contentPane.add(btnMaintenance);
        btnMaintenance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileMaintenanceConsole frameMaint = new FileMaintenanceConsole();
                frameMaint.setVisible(true);
            }
        });

        JButton btnAbout = new JButton("About");
        btnAbout.setBounds(574, 479, 114, 42);
        btnAbout.setToolTipText("About the creators of the application");
        contentPane.add(btnAbout);
        btnAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame displayAbout = new JFrame("About");
                displayAbout.setVisible(true);
                displayAbout.setSize(600, 225);
                JLabel label1 = new JLabel("<HTML><center><br><br>The Java Search Engine was produced by Kevin Mock,<br>"
                        + " as a HCC Java II project. <br>It was created on 2/20/18 in Tampa,FL. </br></center></html>");
                label1.setFont(new Font("Tahoma", Font.BOLD, 18));
                JPanel panel = new JPanel();
                panel.add(label1);
                displayAbout.getContentPane().add(panel);

            }
        });

        // Button group which allows only one radio button to be selected at a time
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rdbtnAnyOfThe);
        buttonGroup.add(rdbtnExactPhrase);
        buttonGroup.add(rdbtnAllTheSearch);

        textPane.setEditable(false);
        textPane.setBounds(25, 114, 701, 342);
        contentPane.add(textPane);

        // Number of Files label
        numFilesLblMain.setText("Number of files indexed: " + FileMaintenanceConsole.list.size());
        numFilesLblMain.setBounds(183, 479, 358, 42);
        numFilesLblMain.setHorizontalAlignment(SwingConstants.CENTER);
        numFilesLblMain.setFont(new Font("Serif", Font.BOLD, 18));
        contentPane.add(numFilesLblMain);

    }

    public static void numFilesUpdate() {
        numFilesLblMain.setText("Number of files indexed: " + FileMaintenanceConsole.list.size());
    }

}
