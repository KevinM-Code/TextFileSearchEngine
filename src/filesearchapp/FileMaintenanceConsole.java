package filesearchapp;

import static filesearchapp.Includer.getWords_ToLower;
import static filesearchapp.Includer.readFromSelectedFile;
import static filesearchapp.Includer.removeFromListAndRewriteToPersist;
import static filesearchapp.Includer.updateListAndRewriteToPersist;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This Class creates the GUI maintenance console so the user can add, remove,\n
 * and update the index for the file search.
 */
public class FileMaintenanceConsole extends JFrame {

    private static final long serialVersionUID = 1L;

    public static String pathToTextFile = System.getProperty("user.home") + File.separator
            + "searchenginefilenonelikeit" + File.separator + "persistencefile.txt";
    private static File persistenceFile = new File(pathToTextFile);

    static ArrayList<String> list = new ArrayList<String>();
    static ArrayList<String> filePaths = new ArrayList<String>();
    private static ArrayList<String> currentFileContents = new ArrayList<String>();
    private static String[] columnNames = {"File Name", "Status"};
    private static DefaultTableModel model = new DefaultTableModel(columnNames,
            0);
    private static JTable fileTable = new JTable(model);
    private static JLabel numFilesLbl = new JLabel();
    private static String lines = "";
    static ArrayList<String> fileNameList = new ArrayList<String>();

    /**
     * Create the GUI Maintenance Console
     */
    public FileMaintenanceConsole() {

        // Set the icon in the upper-left corner for Maintenance Window
        String iconPath = System.getProperty("user.dir") + "\\src\\filesearchapp\\pics\\icon.png";
        ImageIcon iconMaint = new ImageIcon(iconPath);
        setIconImage(iconMaint.getImage());

        // Set the Window
        setBounds(400, 150, 752, 600);
        JPanel contentPane = new JPanel();
        setTitle("Search Engine Maintenance Console");

        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Longer panel across the top
        JPanel panel = new JPanel();
        panel.setBounds(12, 12, 714, 47);
        panel.setBorder(
                new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel.setBackground(Color.LIGHT_GRAY);
        contentPane.add(panel);

        // Title of the Console Window
        JLabel lblNewLabel_2 = new JLabel("Search Engine - File Maintenance");
        lblNewLabel_2.setFont(new Font("Serif", Font.BOLD, 27));
        panel.add(lblNewLabel_2);

        // Number of Files label
        numFilesLbl.setText("Number of files indexed: " + list.size());
        numFilesLbl.setBounds(183, 479, 358, 42);
        numFilesLbl.setHorizontalAlignment(SwingConstants.CENTER);
        numFilesLbl.setFont(new Font("Serif", Font.BOLD, 18));
        contentPane.add(numFilesLbl);

        // Add file button
        JButton addBtn = new JButton("Add File");
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Need to launch MaintGUI First  initMaintGui
                Includer af = new Includer();
                af.addFile();
            }
        });
        addBtn.setToolTipText("Add a file");
        addBtn.setBounds(22, 424, 148, 35);
        addBtn.setFont(new Font("Serif", Font.PLAIN, 13));
        contentPane.add(addBtn);

        // Rebuild out-of-date button
        JButton btnRebuildOutofdate = new JButton("Rebuild Out-of-date");
        btnRebuildOutofdate.setToolTipText("Re-index any file");
        btnRebuildOutofdate.setBounds(192, 422, 148, 37);
        btnRebuildOutofdate.setFont(new Font("Serif", Font.PLAIN, 13));
        contentPane.add(btnRebuildOutofdate);
        File persistenceFile = new File(Includer.pathToTextFile);
        String selectedFileName = Includer.selectedFileName;

        /**
         * The contents of the files changed, rebuild all the files initialized
         * in the search list to the persistent file.
         */
        btnRebuildOutofdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Scanner sc = null;
                try {
                    sc = new Scanner(persistenceFile);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileMaintenanceConsole.class.getName()).log(Level.SEVERE, null, ex);
                }

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();

                    // Split the line into segments for analysis
                    String array[] = line.split(":::");
                    // Second segment is the absolute path
                    String indexedFile = array[0].concat("").trim();

                    File f = new File(indexedFile);
                    // Check if the specified file 
                    // Exists or not 
                    if (f.exists()) // make sure it is a path to a file
                    {
                        removeFromListAndRewriteToPersist(persistenceFile, indexedFile);
                        try {
                            updateListAndRewriteToPersist(persistenceFile, getWords_ToLower(readFromSelectedFile(indexedFile)), indexedFile);
                        } catch (IOException ex) {
                            Logger.getLogger(FileMaintenanceConsole.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        );

        // Delete File Button
        JButton deleteBtn = new JButton("Delete File");

        deleteBtn.setToolTipText(
                "Deletes a file");
        deleteBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int selectedRow = fileTable.getSelectedRow();
                model.removeRow(selectedRow);

                Includer.removeAndRewrite(persistenceFile, selectedRow);
                numFilesLbl.setText("Number of files indexed: " + list.size());
                FileSearchApp.numFilesUpdate();
            }
        });
        deleteBtn.setFont(new Font("Serif", Font.PLAIN, 13));
        deleteBtn.setBounds(559, 425, 148, 32);
        contentPane.add(deleteBtn);

        // Reset Window Button
        JButton resetBtn = new JButton("Reset Windows");
        resetBtn.setActionCommand("RESET");
        resetBtn.setToolTipText(
                "Resets window positions to the default location");
        contentPane.add(resetBtn);
        resetBtn.setFont(new Font("Serif", Font.PLAIN, 13));
        resetBtn.setBounds(375, 422, 148, 35);
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Moves window to center of screen
                setLocationRelativeTo(null);
            }
        });

        // JScroll Pane that surrounds the Jtable
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 55, 714, 346);
        contentPane.add(scrollPane);

        /*
         * Table where the path to file and status of the file being indexed
		 * will populate
         */
        fileTable.setShowGrid(false);
        fileTable.setSurrendersFocusOnKeystroke(true);
        scrollPane.setViewportView(fileTable);
        fileTable.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK,
                null, null, null));

        fileTable.setRowSelectionAllowed(true);
        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        fileTable.setFillsViewportHeight(true);

        if (fileTable.getRowCount() == 0) {
            initListFilesAndStatus();
        }
    }

    /**
     * This <i>method</i> reads from the file(s) that were indexed in the
     * persistence file and stores the results to use later to find out if
     * anything has changed.
     */
    public static void getCurrentInfoFromFiles() throws Exception {
        for (String filePath : filePaths) {
            File file = new File(filePath);
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                lines += line + " ";
            }
            currentFileContents.add(lines);
            lines = "";
            input.close();
        }
    }

    /**
     * This <i>method</i> reads from the persistence file, extracts the file
     * paths from each file(s) already indexed, and saves each file path to the
     * filePaths ArrayList
     */
    public static void getFilePathsFromPersist() {
        /*
        * Scan the file to see if there are already files indexed, if so store
        * each line as a String in an ArrayList for analysis.
         */
        Scanner sc = null;
        try {
            sc = new Scanner(FileMaintenanceConsole.persistenceFile);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }

        /*
        * Get all the information from the persistence file ( to use against
        * the current file status
         */
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            FileMaintenanceConsole.list.add(line);

            // Split the line into segments for analysis
            String lineSections[] = line.split(":::");
            // First segment is the absolute path
            String checkIndexedFile = lineSections[0];
            FileMaintenanceConsole.filePaths.add(checkIndexedFile.replace(" ", ""));
        }
    }

    /**
     * In this <i>method</i>, in this for loop we are analyzing each line that
     * was stored in the ArrayList (signifying the number of files indexed). We
     * are comparing what was stored in the persistence file with the contents
     * that are actually in the file at time of program initialization then
     * posting the results to the DefaultTableModel.
     */
    public static void initListFilesAndStatus() {

        for (int i = 0; i < list.size(); i++) {
            String line = list.get(i);

            /*
            * We start by using the information from each file that we
            * COLLECTED as they are currently in the computer to find out if
            * the contents have changed
             */
            String plainText = currentFileContents.get(i);

            /*
            * extract the words from the file selected with a regex storing
            * each word in an element
             */
            String[] wordsToBeIndexed = plainText.split("[^a-zA-Z0-9-]+");

            // Set all letters to lower case and replace any spaces
            for (int x = 0; x < wordsToBeIndexed.length; x++) {
                wordsToBeIndexed[x] = wordsToBeIndexed[x].toLowerCase()
                        .replaceAll(" ", "");
            }

            // Split the line into segments for analysis
            String array[] = line.split(":::");
            // Third segment of the line are the words in the file
            String wordsInFile = array[1];

            String wordsInPersistenceFileArray[] = wordsInFile.split(",");

            /*
            * Get rid of spaces in each array so we are just comparing the word
            * and nothing else
             */
            for (int y = 0; y < wordsInPersistenceFileArray.length; y++) {
                wordsInPersistenceFileArray[y] = wordsInPersistenceFileArray[y]
                        .replaceAll(" ", "");
            }

            /*
            * Are we dealing with he same file? Comparing the absolute path of
            * the file being indexed and other absolute paths from the
            * persistence file
             */
            if (Arrays.equals(wordsInPersistenceFileArray,
                    wordsToBeIndexed)) {
                model.insertRow(i,
                        new Object[]{filePaths.get(i), "Initialized"});

            } else {
                model.insertRow(i, new Object[]{filePaths.get(i),
                    "The file has changed since last indexed"});
            }
        }
    }

    /**
     * In this <i>method</i>, using the same instance of the
     * <code>DefaultTableModel</code> the row in the model is deleted and the
     * number of files is updated.
     *
     * @param rowIndex The index of the DefaultTableModel to be removed
     */
    public static void removeFileJustRemovedFromIndex(int rowIndex) {
        model.removeRow(rowIndex);
        numFilesLbl.setText("Number of files indexed: " + list.size());
    }

    /**
     * In this <i>method</i>, the file was just indexed, show the file as
     * "Indexed", and update the jLabel with new number of files.
     *
     * @param fileName
     */
    public static void addFileJustIndexedToTableModel(String fileName) {
        model.addRow(new Object[]{fileName, "Initialized"});
        numFilesLbl.setText("Number of files indexed: " + list.size());
    }

}
