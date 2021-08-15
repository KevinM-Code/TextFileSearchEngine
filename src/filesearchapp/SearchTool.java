package filesearchapp;

import java.io.PrintStream;
import java.util.*;

public class SearchTool {

    private boolean isAllTheSearchSelected = FileSearchApp.rdbtnAllTheSearch.isSelected();
    private boolean isExactPhraseSelected = FileSearchApp.rdbtnExactPhrase.isSelected();
    private boolean isAnyOfTheSelected = FileSearchApp.rdbtnAnyOfThe.isSelected();
    private PrintStream standardOut;

    public SearchTool() {
        standardOut = System.out;
        PrintStream printStream = new PrintStream(new CustomOutputStream(FileSearchApp.textPane));
        System.setOut(printStream);
    }

    public void selection() {
        if (isAllTheSearchSelected) {
            allOfTheSearchTerms();
        }

        if (isAnyOfTheSelected) {
            anyOfTheSearchTerms();
        }

        if (isExactPhraseSelected) {
            exactPhrase();
        }
    }

    /**
     * Once the search term is selected this <i>method</i> retrieves the
     * information from the the ArrayList. This is the structure in each element
     * (File Name ::: some, words). The method breaks up each string into two
     * parts <strong>path to the file</strong> and the <strong>words in the
     * file</strong>. The method stores the absolute path to the file
     * temporarily in memory while iterating through the file's words to see if
     * there is a match. If there is a match the absolute path to the file is
     * stored in a HashSet where there are no duplicates. When done the results
     * are printed, the search text field is cleared, and focus is brought back
     * to the search text area for the next search.
     */
    public static void exactPhrase() {
        Set<String> fileList = new HashSet<>();

        // What I am searching for and creates comma separated list to look for.
        String valuesToSearchFor = FileSearchApp.txtEnterSearchTerms.getText();
        String searchWordArray[] = valuesToSearchFor.split(" ");
        String result = String.join(", ", searchWordArray);

        /* iterate through each file stored in the ArrayList */
        for (int x = 0; x < FileMaintenanceConsole.list.size(); x++) {
            // Split the line into segments for analysis
            String array[] = FileMaintenanceConsole.list.get(x).split(":::");
            // First segment is the absolute path
            String indexedFile = array[0].replaceAll(" ", "");
            // Second segment of the line are the words in the file
            String wordsInFile = array[1];

            /*
			 * Make an array of words from the line of words that have a comma between each
			 * word from the ArrayList (already in memory) not the persistence file...
			 * little quicker
             */
            String wordsInPersistenceFileArray[] = wordsInFile.split(",");

            /*
			 * Locates string from "result" that is "equal" to the terms that were entered
             */
            for (int y = 0; y < wordsInPersistenceFileArray.length; y++) {
                wordsInPersistenceFileArray[y] = wordsInPersistenceFileArray[y].replaceAll(" ", "");
                if (wordsInFile.contains(result)) {
                    /* you will not get duplicates here */
                    fileList.add(indexedFile);
                }
            }
        }

        FileSearchApp.textPane.setText("");
        System.out.println("");
        System.out.println("Matching Files for: " + valuesToSearchFor);
        System.out.println("");
        for (String s : fileList) {
            System.out.println(s);
        }

        /* Clear the search textbox and returns focus to textbox */
        FileSearchApp.txtEnterSearchTerms.setText("");
        FileSearchApp.txtEnterSearchTerms.requestFocus();
    }

    /**
     * Once the search term is selected this <i>method</i> retrieves the
     * information from the the ArrayList. This is the structure in each element
     * (File Name ::: some, words). The method breaks up each string into two
     * parts <strong>path to the file</strong> and the <strong>words in the
     * file</strong>. The method stores the absolute path to the file
     * temporarily in memory while iterating through the file's words to see if
     * there is an <i><b>EXACT</b></i> match. If there is a match the absolute
     * path to the file is stored in a HashSet where there are no duplicates.
     * When done the results are printed, the search text field is cleared, and
     * focus is brought back to the search text area for the next search.
     */
    public static void anyOfTheSearchTerms() {

        Set<String> fileList = new HashSet<>();
        String textFieldValue = FileSearchApp.txtEnterSearchTerms.getText();
        String searchWordArray[] = textFieldValue.split(" ");

        /*
		 * Make an array of words, that were entered for the search, and search through
		 * the files for each word
         */
        for (String aSearchWord : searchWordArray) {
            /* iterate through each file stored in the ArrayList */
            for (int x = 0; x < FileMaintenanceConsole.list.size(); x++) {
                // Split the line into segments for analysis
                String array[] = FileMaintenanceConsole.list.get(x).split(":::");
                // First segment is the absolute path
                String indexedFile = array[0].replaceAll(" ", "");
                // Second segment of the line are the words in the file
                String wordsInFile = array[1];

                /*
				 * Make an array of words from the line of words that have a comma between each
				 * word from the ArrayList (already in memory) not the persistence file...
				 * little quicker
                 */
                String wordsInPersistenceFileArray[] = wordsInFile.split(",");

                /*
				 * Look to see if there is a word that is indexed, is "equal" to the word that
				 * was entered
                 */
                for (int y = 0; y < wordsInPersistenceFileArray.length; y++) {
                    wordsInPersistenceFileArray[y] = wordsInPersistenceFileArray[y].replaceAll(" ", "");
                    if (aSearchWord.equals(wordsInPersistenceFileArray[y])) {
                        /* you will not get duplicates here */
                        fileList.add(indexedFile);
                    }
                }
            }
        }

        FileSearchApp.textPane.setText("");
        System.out.println("");
        System.out.println("Matching Files: ");
        System.out.println("");
        for (String s : fileList) {
            System.out.println(s);
        }

        /* Clear the search textbox */
        FileSearchApp.txtEnterSearchTerms.setText("");
        /* Bring focus back to the search textbox */
        FileSearchApp.txtEnterSearchTerms.requestFocus();
    }

    /**
     * Once the search term is selected this <i>method</i> retrieves the
     * information from the the ArrayList. This is the structure in each element
     * (File Name ::: some, words). The method breaks up each string into two
     * parts <strong>path to the file</strong> and the <strong>words in the
     * file</strong>. iterates through the words finds if all search words are
     * in the file.
     */
    public static void allOfTheSearchTerms() {

        Set<String> fileList = new HashSet<>();
        String textFieldValue = FileSearchApp.txtEnterSearchTerms.getText();
        String searchWordArray[] = textFieldValue.split(" ");
        Set<String> words = new HashSet<>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        String indexedFile = "";

        /*
		 * Make an array of words, that were entered for the search, and search through
		 * the files for each word
         */
        for (String aSearchWord : searchWordArray) {

            for (int x = 0; x < FileMaintenanceConsole.list.size(); x++) {
                // Split the line into segments for analysis
                String array[] = FileMaintenanceConsole.list.get(x).split(":::");
                // First segment is the absolute path
                indexedFile = array[0].replaceAll(" ", "");
                // Second segment of the line are the words in the file
                String wordsInFile = array[1];
                /*
				 * Make an array of words from the line of words that have a comma between each
				 * word from the ArrayList (already in memory) not the persistence file...
				 * little quicker
                 */
                String wordsInPersistenceFileArray[] = wordsInFile.split(",");
                /*
				 * Look to see if there is a word that is indexed, is "equal" to the word that
				 * was entered
                 */
                for (int y = 0; y < wordsInPersistenceFileArray.length; y++) {
                    wordsInPersistenceFileArray[y] = wordsInPersistenceFileArray[y].replaceAll(" ", "");

                    if (aSearchWord.equals(wordsInPersistenceFileArray[y])) {
                        /* you will not get duplicates here */
                        words.add(wordsInPersistenceFileArray[y]);
                    }
                }
                /* Through each search word iterate and count if the word is there at least once in the file.
				 * Integer::sum add to the count for each file if the number of search words equals the count
				 * you have All the search terms satisfied*/
                map.merge(indexedFile, words.size(), Integer::sum);
                words.clear();
            }
        }
        // System.out.println(words.size());
        FileSearchApp.textPane.setText("");
        System.out.println("");
        System.out.println("Matching Files: ");
        System.out.println("");

        Iterator<Map.Entry<String, Integer>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Integer> entry = entries.next();
            if (entry.getValue() == searchWordArray.length) {
                System.out.println(entry.getKey());
            }
        }

        /* Clear the search textbox */
        FileSearchApp.txtEnterSearchTerms.setText("");
        /* Bring focus back to the search textbox */
        FileSearchApp.txtEnterSearchTerms.requestFocus();
    }
}
