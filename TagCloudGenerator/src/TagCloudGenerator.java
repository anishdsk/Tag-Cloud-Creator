import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Program to receive a text file of words and to output an html page with a tag
 * cloud of words.
 *
 * @author Manmeet Sandhu
 * @author Anish Senthilkumar
 *
 */
public final class TagCloudGenerator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    /**
     * The maximum font size.
     */
    private static final int MAX_FONT_SIZE = 48;
    /**
     * The maximum font size.
     */
    private static final int DEFAULT_FONT_SIZE = 19;
    /**
     * The maximum value.
     */
    private static int maxValue;
    /**
     * The minimum value.
     */
    private static int minValue;

    /**
     * Compare {@code Map}s in alphabetical order.
     */
    private static class Order
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {
            return o1.getKey().compareToIgnoreCase(o2.getKey());
        }
    }

    /**
     * Compare {@code Map}s in numerical order.
     */
    private static class Order2
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    /**
     * Generates the pairs of terms and definitions (Strings) in the given
     * {@code file} into the given {@code Map}. Also generates a Queue of all
     * the terms in no particular order into the given {@code Queue}.
     *
     * @param file
     *            the given {@code file}
     * @param strMap
     *            the {@code Map} to be replaced
     * @param separators
     *            the {@code Set} that contains the separators
     * @replaces {@code strSet}
     * @replaces {@code q}
     * @ensures <pre>
     * {@code strMap = elements(file)}
     * </pre>
     */
    private static void generateTerms(BufferedReader file,
            Map<String, Integer> strMap, Set<Character> separators) {

        String term = "";
        String nexTerm = "";
        int index = 0;
        try {
            term = file.readLine();
            while (term != null) {
                index = 0;
                while (index < term.length()) {
                    nexTerm = nextWordOrSeparator(term, index, separators);
                    index += nexTerm.length();
                    if (!separators.contains(nexTerm.charAt(0))) {
                        if (strMap.containsKey(nexTerm.toLowerCase())) {
                            int tempValue = strMap
                                    .remove(nexTerm.toLowerCase());
                            strMap.put(nexTerm.toLowerCase(), tempValue + 1);
                        } else {
                            strMap.put(nexTerm.toLowerCase(), 1);
                        }
                    }
                }
                term = file.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error reading from file");
        }
    }

    /**
     * Outputs the opening tags for index.html.
     *
     * @param foldername
     *            the name of where we want to store the value
     * @param fileName
     *            the name of the file
     * @param number
     *            the number of words user wants
     * @throws IOException
     *             throws IOException
     * @updates out.content
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(String foldername, String fileName,
            int number, PrintWriter out) throws IOException {

        //Printing out the header for the html file
        out.println("<html>");
        out.println("<head>");
        out.print("<title>");
        out.print("Top " + number + " Words in " + fileName);
        out.println("</title>");
        out.println("<link href=\"http://www.cse.ohio-state.edu/software/2231"
                + "/web-sw2/assignments/projects/tag-cloud-generator/data/tagc"
                + "loud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.print("<h2>");
        out.print("Top " + number + " Words in " + fileName + "");
        out.println("</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires <pre>
     * {@code 0 <= position < |text|}
     * </pre>
     * @ensures <pre>
     * {@code nextWordOrSeparator =
     *   text[ position .. position + |nextWordOrSeparator| )  and
     * if elements(text[ position .. position + 1 )) intersection separators = {}
     * then
     *   elements(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    elements(text[ position .. position + |nextWordOrSeparator| + 1 ))
     *      intersection separators /= {})
     * else
     *   elements(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    elements(text[ position .. position + |nextWordOrSeparator| + 1 ))
     *      is not subset of separators)}
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int count = 0;
        char returnedPiece = 'k';
        String returned = "";
        if (separators.contains(text.charAt(position))) {
            while (count < text.substring(position, text.length()).length()) {
                returnedPiece = text.charAt(position + count);
                if (separators.contains(text.charAt(position + count))) {
                    returned = returned + returnedPiece;
                    count++;
                } else {
                    count = text.substring(position, text.length()).length();
                }
            }
            count = 0;
        } else {
            while (count < text.substring(position, text.length()).length()) {
                returnedPiece = text.charAt(position + count);
                if (!separators.contains(text.charAt(position + count))) {
                    returned = returned + returnedPiece;
                    count++;
                } else {
                    count = text.substring(position, text.length()).length();
                }
            }
            count = 0;
        }
        return returned;
    }

    /**
     * Outputs the HTML file that has code for a generated Tag Cloud.
     *
     * @param filename
     *            the file name of the input folder
     * @param wordCounter
     *            the Map of terms and definitions
     * @param weight
     *            the Map of terms and assigned weights
     * @param foldername
     *            the name of the folder the html files are output to
     * @param keys
     *            the Sequence of key values
     * @param numterms
     *            the number of words in the generated Tagcloud
     * @throws IOException
     *             throws IOException
     * @updates {@code out.content}
     * @ensures <pre>
     * {@code out.content = #out.content * [the HTML tags]}
     * </pre>
     */
    private static void outputPage(Map<String, Integer> wordCounter,
            String foldername, String filename, Map<String, Integer> weight,
            ArrayList<String> keys, int numterms, PrintWriter out)
            throws IOException {

        for (int count = 0; count < keys.size(); count++) {
            out.println("<span style=\"cursor:default\" class=\"f"
                    + weight.get(keys.get(count)) + "\" title=\"count: "
                    + wordCounter.get(keys.get(count)) + "\">" + keys.get(count)
                    + "</span>");
        }
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) {

        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in));

        //Ask for input file
        System.out.println("Insert name of input file: ");
        String fileName = null;
        try {
            fileName = input.readLine();
        } catch (IOException e1) {
            System.err.println("Error reading from keyboard");
            return;
        }
        BufferedReader file;
        try {
            file = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e1) {
            System.err.println(
                    "Error producing reader of input file (file not found)");
            return;
        }

        //Ask for output file Name
        System.out.println("Insert name of output folder: ");
        String folder = null;
        try {
            folder = input.readLine();
        } catch (IOException e1) {
            System.err.println("Error reading from keyboard");
        }

        //Ask the user the number of words they would like printed out
        System.out
                .println("Insert number of words in the generated tag cloud: ");
        int numberWords = 0;
        try {
            numberWords = Integer.parseInt(input.readLine());
        } catch (IOException e1) {
            System.err.println("Error reading from keyboard");
        }
        /*
         * Create a Set of separators
         */
        PrintWriter out;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(folder)));
        } catch (IOException e2) {
            System.err.println("Error Creating the print writer");

            try {
                input.close();
            } catch (IOException e) {
                System.err.println("Error closing input");
            }
            try {
                file.close();
            } catch (IOException e) {
                System.err.println("Error closing file");
            }
            return;
        }
        Set<Character> separatorSet = new HashSet<Character>();
        String separatorStr = " _/@#$%^&*()+=`~><;:,-.!?[]'";
        separatorSet.add('\t');
        separatorSet.add('\n');
        separatorSet.add('\r');
        separatorSet.add('\"');
        int count = 0;
        while (count < separatorStr.length()) {
            separatorSet.add(separatorStr.charAt(count));
            count++;
        }
        /*
         * Creates a Map of associated terms and word counts.
         */
        Map<String, Integer> wordCounter = new HashMap<String, Integer>();
        generateTerms(file, wordCounter, separatorSet);
        /*
         * Declare comparators to sort Map entries
         */
        Comparator<Map.Entry<String, Integer>> alphabet = new Order();
        Comparator<Map.Entry<String, Integer>> numeric = new Order2();
        /*
         * Sort N Map entries by decreasing value (numerically), where N is the
         * number of words in tag cloud provided by user, into a List containing
         * N entries
         */
        List<Map.Entry<String, Integer>> numSorted = new LinkedList<Map.Entry<String, Integer>>(
                wordCounter.entrySet());
        Collections.sort(numSorted, numeric);

        //Creating a smaller list and storing the number of words user wants
        List<Map.Entry<String, Integer>> smaller = new LinkedList<Map.Entry<String, Integer>>();
        smaller = numSorted.subList(0, numberWords);

        /*
         * Sort top Map entries in alphabetical order
         */
        Collections.sort(smaller, alphabet);
        /*
         * Transfer Pairs in List to a map, and create a Sequence of key values
         * to reference for data manipulation of map
         */
        ArrayList<String> keys = new ArrayList<String>();
        count = 0;
        while (count < numberWords && smaller.size() > 0) {
            Entry<String, Integer> temp1 = smaller.remove(0);
            if (count == 0) {
                //Entry<String, Integer> temp1 = smaller.remove(0);
                maxValue = temp1.getValue();
                keys.add(count, temp1.getKey());
                wordCounter.put(temp1.getKey(), temp1.getValue());
                count++;
            } else if (count == numberWords) {
                //Entry<String, Integer> temp1 = smaller.remove(0);
                minValue = temp1.getValue();
                keys.add(count, temp1.getKey());
                wordCounter.put(temp1.getKey(), temp1.getValue());
                count++;
            } else {
                //Entry<String, Integer> temp1 = smaller.remove(0);
                keys.add(count, temp1.getKey());
                wordCounter.put(temp1.getKey(), temp1.getValue());
                count++;
            }
        }

        // Creating a second map that stores the weights of the font based on
        // the number of times it was used.
        count = 0;
        Map<String, Integer> weight = new HashMap<String, Integer>();
        for (Entry<String, Integer> value : wordCounter.entrySet()) {
            if (minValue == maxValue) {
                count = DEFAULT_FONT_SIZE;
            } else {
                count = (MAX_FONT_SIZE * (value.getValue() - minValue))
                        / (maxValue - minValue);
            }
            if (count > MAX_FONT_SIZE) {
                count = MAX_FONT_SIZE;
            }
            weight.put(value.getKey(), count);
        }
        //Outputs the html page
        try {
            outputHeader(folder, fileName, numberWords, out);
        } catch (IOException e1) {
            System.err.println("Error outputting header for the html page");
        }
        try {
            outputPage(wordCounter, folder, fileName, weight, keys, numberWords,
                    out);
        } catch (IOException e1) {
            System.err.println("Error outputting html page");
        }

        // close input and output streams
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Error closing keyboard input stream");
        }
        try {
            file.close();
        } catch (IOException e) {
            System.err.println("Error closing file output stream");
        }

        out.close();

    }
}
