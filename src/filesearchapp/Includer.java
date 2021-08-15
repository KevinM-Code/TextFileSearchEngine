package filesearchapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Includer {

    private static int indexNum;
    public static String pathToTextFile = System.getProperty("user.home") + File.separator
            + "searchenginefilenonelikeit" + File.separator + "persistencefile.txt";
    public File persistenceFile = new File(pathToTextFile);
    private static String fileName = null;
    public static String selectedFileName = null;

    /**
     * This <i>method</i> opens up the File Dialog file selection GUI using the
     * program icon in the upper left corner. <br>
     * It then allows the user to select a file and return the absolute path of
     * the file as a String.
     *
     * @return <code>theFileName</code> - the absolute path of the file
     */
    public String getSelectedFilePath() throws NullPointerException {
        // Use same image for the file dialog window and make the file dialog object
        String iconPath = System.getProperty("user.dir") + "\\src\\filesearchapp\\pics\\icon.png";
        Image img = new ImageIcon(iconPath).getImage();
        JFrame jf = new JFrame();
        FileDialog fd = new FileDialog(jf, "Choose a file", FileDialog.LOAD);
        // Set image to file dialog window, set to visible, and get file name to index
        fd.getOwner().setIconImage(img);
        fd.setVisible(true);

        // Is there a file and can it be read
        File[] selectedFiles = fd.getFiles();
        jf.dispose();

        selectedFileName = filePath(isFile(selectedFiles));
        // Quick change from type File to type String
        return selectedFileName;
    }

    /**
     * This <i>method</i> is called when the Add button is clicked
     */
    public void addFile() {
        try {
            fileName = getSelectedFilePath();
        } catch (NullPointerException e) {
            System.err.println("No File was selected... carry on...");
        }

        String plainText = null;
        try {
            plainText = readFromSelectedFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] wordsToBeIndexed = getWords_ToLower(plainText);

        try {
            if (isFileWordsSameAsAnyAlreadyIndexed(wordsToBeIndexed, fileName)) {
                updateListAndRewriteToPersist(persistenceFile, wordsToBeIndexed, fileName);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This <i>method</i> returns <code>boolean</code> whether the words from
     * the file selected are the same as the words already indexed, iterating
     * through each indexed file in the persistence file. Returning true if the
     * words in the file is not the same and false if the path to the file and
     * the file words are the same as those that have already been indexed
     *
     * @param wordsToBeIndexed the indexed words
     * @param fileName the absolute path of the file selected
     * @return <code>boolean</code> - whether the file selected matches the file
     * recorded in the persistence file
     * @throws FileNotFoundException
     */
    public boolean isFileWordsSameAsAnyAlreadyIndexed(String[] wordsToBeIndexed, String fileName) throws FileNotFoundException {
        /*
		 * Check to see if the path of the file selected is the same as file paths
		 * already indexed and see if the words in those files have changed
         */
        Scanner sc = new Scanner(persistenceFile);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            // Split the line into segments for analysis
            String array[] = line.split(":::");
            // Second segment is the absolute path
            String indexedFile = array[0].concat("A");
            // Third segment of the line are the words in the file
            String wordsInFile = array[1];

            /*
			 * Modifications so that the same absolute path from the persistence and the
			 * file to be index are the same when read
             */
            String fileToBeIndexed = fileName.concat(" A");
            String wordsInPersistenceFileArray[] = wordsInFile.split(",");

            /*
			 * Get rid of spaces in each array so we are just comparing the word and nothing
			 * else
             */
            for (int y = 0; y < wordsInPersistenceFileArray.length; y++) {
                wordsInPersistenceFileArray[y] = wordsInPersistenceFileArray[y].replaceAll(" ", "");

            }

            /*
			 * Are we dealing with the same file? Comparing the absolute path of the file
			 * being indexed and other absolute paths from the persistence file
             */
            if ((indexedFile).equals(fileToBeIndexed)) {
                /*
                 * To this point we have found a absolute file path that is the same now has it
                 * changed?
                 */
                if (Arrays.equals(wordsInPersistenceFileArray, wordsToBeIndexed)) {
                    JOptionPane optionPane = new JOptionPane("This File is Already Indexed!!!",
                            JOptionPane.ERROR_MESSAGE);
                    JDialog dialog = optionPane.createDialog("Error Message");
                    dialog.setAlwaysOnTop(true);
                    dialog.setVisible(true);
                    return false;
                } else {
                    FileSearchApp fmc = new FileSearchApp();
                    int decision = JOptionPane.showConfirmDialog(fmc,
                            /* message */
                            "Would you like to rebuild the file?",
                            /* title */
                            "File Update", JOptionPane.YES_NO_OPTION);
                    if (decision == JOptionPane.NO_OPTION) {
                        return false;
                    }
                    if (decision == JOptionPane.YES_OPTION) {
                        // TODO Method to rebuild
                        removeFromListAndRewriteToPersist(persistenceFile, fileName);
                        updateListAndRewriteToPersist(persistenceFile, wordsToBeIndexed, fileName);
                        return false;
                    }
                }
            }
        }
        sc.close();
        return true;
    }

    /**
     * This <i>method</i> takes a String of words and splits each word into an
     * array, then converts each word to lowercase.
     *
     * @param plainText a String of words
     * @return <code>wordsToBeIndexed</code> - the words from the String
     */
    public static String[] getWords_ToLower(String plainText) {
        // extract the words from the file selected with a regex storing each word in an
        // element
        String[] wordsToBeIndexed = plainText.split("[^a-zA-Z0-9-]+");

        // Set all letters to lower case and replace any spaces
        for (int i = 0; i < wordsToBeIndexed.length; i++) {
            wordsToBeIndexed[i] = wordsToBeIndexed[i].toLowerCase().replaceAll(" ", "");
        }

        return wordsToBeIndexed;
    }

    /**
     * This <i>method</i> reads from a file and returns a <code>String</code> of
     * text from the file.
     *
     * @param fileName the absolute path of the file
     * @return <code>plainText</code> - the stream of text from the file
     */
    public static String readFromSelectedFile(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        String lines = "";
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {

                lines += line + " ";
            }
        }
        return lines;
    }

    /**
     * This <i>method</i> is part of the rebuild process, it takes the words
     * extracted from the file selected, the absolute path of the file selected
     * to update the ArrayList, and reprints the file with the correct words
     * into the persistence file.
     *
     * @param persistenceFile
     * @param wordsToBeIndexed an array of words that are being indexed
     * @param fileName the absolute path to the file
     */
    public static void updateListAndRewriteToPersist(File persistenceFile, String[] wordsToBeIndexed, String fileName) {
        /* Print to the file */
        PrintWriter file = null;

        try {
            file = new PrintWriter(new FileWriter(persistenceFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String words = String.join(", ", wordsToBeIndexed);

        FileMaintenanceConsole.list
                .add(fileName + " ::: " + words);

        for (int n = 0; n < FileMaintenanceConsole.list.size(); n++) {
            file.println(FileMaintenanceConsole.list.get(n));
        }

        file.flush();
        file.close();

        FileMaintenanceConsole.addFileJustIndexedToTableModel(fileName);
        FileSearchApp.numFilesUpdate();

    }

    /**
     * This <i>method</i> takes the persistence file as type <code>File</code>
     * and the index number of the <code>ArrayList</code> as parameters, removes
     * the selected index number from the ArrayList, and reprints the ArrayList
     * to the persistence file.
     *
     * @param persistenceFile Persistence file used
     * @param indexNumber The index number of the <code>ArrayList</code> you
     * want removed
     */
    public static void removeAndRewrite(File persistenceFile, int indexNumber) {

        FileMaintenanceConsole.list.remove(indexNumber);

        /* Print to the file */
        PrintWriter file = null;

        try {
            file = new PrintWriter(new FileWriter(persistenceFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < FileMaintenanceConsole.list.size(); i++) {
            file.println(FileMaintenanceConsole.list.get(i));
        }

        file.flush();
        file.close();

    }

    /**
     * This <i>method</i> is part of the rebuild process, it looks for the
     * absolute path as a <code>String</code> in the populated
     * <strong>list</strong> <code>ArrayList</code>. Once found, it get the
     * index number of place where it found the absolute path, and removes that
     * index from the <code>ArrayList</code>. Once the index is removed (because
     * the file was removed) the new list is rewritten to the persistence file
     * immediately.
     *
     * @param persistenceFile The persistence file used for the application
     * @param fileName the file that was just selected
     */
    public static void removeFromListAndRewriteToPersist(File persistenceFile, String fileName) {

        for (int n = 0; n < FileMaintenanceConsole.list.size(); n++) {

            if (FileMaintenanceConsole.list.get(n).contains(fileName)) {
                int indexToDelete = FileMaintenanceConsole.list.indexOf(n);
                indexNum = indexToDelete + n + 1;
                FileMaintenanceConsole.list.remove(indexNum);
            }
        }
        /* Print to the file */
        PrintWriter file = null;

        try {
            file = new PrintWriter(new FileWriter(persistenceFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < FileMaintenanceConsole.list.size(); i++) {
            file.println(FileMaintenanceConsole.list.get(i));
        }
        FileMaintenanceConsole.removeFileJustRemovedFromIndex(indexNum);

        file.flush();
        file.close();
    }

    /**
     * This <i>method</i> checks to see if there is a file there and can be
     * read. If it does not return null, then the file is there and it is
     * readable.
     *
     * @param selectedFiles array of selectedFiles
     * @return <code>file</code> - a single file
     */
    public File isFile(File[] selectedFiles) {

        if (selectedFiles.length == 0) {
            return null;
        }
        File file = selectedFiles[0];
        if (!(file.exists() && file.isFile() && file.canRead())) {
            JOptionPane.showMessageDialog(null, "File \"" + file.getName() + "\" can't be indexed!");
            return null;
        } else {
            return file;
        }
    }

    /**
     * Change the type from <code>File</code> to <code>String</code>
     *
     * @param filename
     * @return <code>fileName</code> - Absolute path of the file as a
     * <i>String</i>
     */
    public String filePath(File filename) {
        String fileName = null;
        try {
            fileName = filename.getCanonicalPath();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileName;
    }
}
