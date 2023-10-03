/*Language : Java
  Author   : Gene Choi, Marielle Koffi, Silvia Matthews
  Group    : Gene Choi, Marielle Koffi, Silvia Matthews, Josh Dazey
  Note     : Josh Dazey has not done any work including communicating with us, writting any code
  Date     : Sep 27th, 2023
  Course   : CMP SCI 4500
  Professor: Keith Miller, Ph.D

This program plays a game with two "Person" entities, placing them at the southwest and northwest corners 
of a square grid and seeing how many 1-unit moves it takes for them to meet (if they ever do meet). 
Two protocols are defined: Protocol 4 and Protocol 8. 
Protocol 4 moves the Persons one unit north, south, east, or west. If the Person's move would move them out of bounds of the grid,
it stays in place. Persons alternate turns. 
Protocol 8 moves the Persons one unit north, south, east, or west. It can also move them diagonally, northeast, northwest, 
southeast, or southwest. If the Person's move would move them out of bounds of the grid, another move is generated until 
the Person can make a valid move. Persons alternate turns.
*/
/**********************************************************************************************************************
The indata.txt includes both letters and integers:
    1st line       : independent,[D|M|R|P],x1,x2,...,xN           ----required x1<x2<...<xN
    next tree lines: fixed,[D|M|R|P],w1                           ----w1 gives the value of one of [D|M|R|P]
    5th line       : dependent,[L|H|A]
Note: The first four lines should include all four of variables D,M,R,P.
***********************************************************************************************************************/
/*Java regex matching logic was looked up on GeeksforGeeks:
 https://www.geeksforgeeks.org/how-to-check-if-string-contains-only-digits-in-java/

 Data structures used: 
 Objects, arrays, Lists, Scanners, BufferedReaders, InputStreams, Writers.*/

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class Person {
    int xCoordinate;
    int yCoordinate;

    public Person(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }
}

class ExperimentData {
    int Dimension;
    int maxMoves;
    int repetitions;
    int protocol;
    double highMoves;
    double lowMoves;
    double averageMoves;

    public ExperimentData(int dimension, int maxMoves, int repetitions, int protocol, double lowMoves, double highMoves,
            double averageMoves) {
        Dimension = dimension;
        this.maxMoves = maxMoves;
        this.repetitions = repetitions;
        this.protocol = protocol;
        this.highMoves = highMoves;
        this.lowMoves = lowMoves;
        this.averageMoves = averageMoves;
    }
}

public class Main {

    // sample xcoords/ycoords
    // these should be replaced with dynamically populated arrayLists

    private static ArrayList<Integer> xCoordinates = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    private static ArrayList<Integer> yCoordinates = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

    private static List<Double> resultsExp1 = new ArrayList<>();
    private static List<Double> resultsExp2 = new ArrayList<>();
    private static List<Double> resultsExp3 = new ArrayList<>();

    // Variables for experiment 1
    private static ArrayList<String> experiment1Independent = new ArrayList<String>();
    private static ArrayList<String> experiment1Fix1 = new ArrayList<String>(3);
    private static ArrayList<String> experiment1Fix2 = new ArrayList<String>(3);
    private static ArrayList<String> experiment1Fix3 = new ArrayList<String>(3);
    private static ArrayList<String> experiment1Dependent = new ArrayList<String>(2);

    private static ArrayList<Integer> experiment1D = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment1M = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment1R = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment1P = new ArrayList<Integer>();

    // Variables for experiment 2
    private static ArrayList<String> experiment2Independent = new ArrayList<String>();
    private static ArrayList<String> experiment2Fix1 = new ArrayList<String>(3);
    private static ArrayList<String> experiment2Fix2 = new ArrayList<String>(3);
    private static ArrayList<String> experiment2Fix3 = new ArrayList<String>(3);
    private static ArrayList<String> experiment2Dependent = new ArrayList<String>(2);

    private static ArrayList<Integer> experiment2D = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment2M = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment2R = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment2P = new ArrayList<Integer>();

    // Variables for experiment 3
    private static ArrayList<String> experiment3Independent = new ArrayList<String>();
    private static ArrayList<String> experiment3Fix1 = new ArrayList<String>(3);
    private static ArrayList<String> experiment3Fix2 = new ArrayList<String>(3);
    private static ArrayList<String> experiment3Fix3 = new ArrayList<String>(3);
    private static ArrayList<String> experiment3Dependent = new ArrayList<String>(2);

    private static ArrayList<Integer> experiment3D = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment3M = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment3R = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment3P = new ArrayList<Integer>();

    private static List<String> errors = new ArrayList<>();

    private static void parseInput() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("indata.txt"));

        String line;

        try (Stream<String> fileStream = Files.lines(Paths.get("indata.txt"))) {
            int noOfLines = (int) fileStream.count();
            if (noOfLines > 15) {
                errors.add("Input file contains too many lines of data.");
            }
            /*
             * else if (noOfLines < 5) {
             * errors.add("Input file contains less five lines of data.");
             * }
             */
        }

        // Check and parse each line of input file
        for (int i = 1; i <= 15; i++) {
            line = br.readLine();
            String[] values;

            String regex = "[0-9]+";
            Pattern p = Pattern.compile(regex);
            // check every line to see if it is null. if true, this indicates a file with
            // 5<lines<15
            // lines. if false, split line between commas.
            if (line == null) {
                errors.add("Incomplete input file.");
                break;
            } else {
                // if line contains whitespace, add that to errors. then, eliminate all
                // whitespace and keep analyzing file for errors.
                if (line.contains(" ")) {
                    errors.add("Line " + i + " contains whitespace.");
                    line = line.replaceAll(" ", "");
                }
                values = line.split(",");
            }

            switch (i) {
                case 1:
                case 6:
                case 11:
                    if (values.length >= 4 && values.length <= 15) {
                        if (values[0].equals("independent")) {
                            if (values[1].equals("D") || values[1].equals("M")
                                    || values[1].equals("R") || values[1].equals("P")) {
                                for (int j = 2; j < values.length - 1; j++) {
                                    if (Integer.parseInt(values[j]) < Integer.parseInt(values[j + 1])) {
                                        // Add data into its relative line
                                        // Data for experiment 1
                                        if (i == 1) {
                                            experiment1Independent.add(values[0]);
                                            experiment1Independent.add(values[1]);
                                            experiment1Independent.add(values[j]);
                                        }

                                        // Data for experiment 2
                                        if (i == 6) {
                                            experiment2Independent.add(values[0]);
                                            experiment2Independent.add(values[1]);
                                            experiment2Independent.add(values[j]);
                                        }

                                        // Data for experiment 3
                                        if (i == 11) {
                                            experiment3Independent.add(values[0]);
                                            experiment3Independent.add(values[1]);
                                            experiment3Independent.add(values[j]);
                                        }

                                    } else {
                                        errors.add("The number should be increase in line " + i);
                                    }
                                }

                            } else {
                                errors.add("Line " + i + " contains incorrect letter in the second item.");
                            }
                        } else {
                            errors.add("Line " + i + " contains incorrect letters in the first item.");
                        }
                    } else {
                        errors.add("Length is incorrect in line " + i);
                    }

                case 2:
                case 3:
                case 4:
                case 7:
                case 8:
                case 9:
                case 12:
                case 13:
                case 14:
                    if (values.length == 3) {
                        if (values[0].equals("fixed") && (values[1].equals("D") || values[1].equals("M")
                                || values[1].equals("R") || values[1].equals("P"))) {
                            // Add data into its relative line
                            // Data for experiment 1
                            if (i == 2) {
                                experiment1Fix1.add(values[0]);
                                experiment1Fix1.add(values[1]);
                                experiment1Fix1.add(values[2]);
                            }
                            if (i == 3) {
                                experiment1Fix2.add(values[0]);
                                experiment1Fix2.add(values[1]);
                                experiment1Fix2.add(values[2]);
                            }
                            if (i == 4) {
                                experiment1Fix3.add(values[0]);
                                experiment1Fix3.add(values[1]);
                                experiment1Fix3.add(values[2]);
                            }

                            // Data for experiment 2
                            if (i == 7) {
                                experiment2Fix1.add(values[0]);
                                experiment2Fix1.add(values[1]);
                                experiment2Fix1.add(values[2]);
                            }
                            if (i == 8) {
                                experiment2Fix2.add(values[0]);
                                experiment2Fix2.add(values[1]);
                                experiment2Fix2.add(values[2]);
                            }
                            if (i == 9) {
                                experiment2Fix3.add(values[0]);
                                experiment2Fix3.add(values[1]);
                                experiment2Fix3.add(values[2]);
                            }

                            // Data for experiment 3
                            if (i == 12) {
                                experiment3Fix1.add(values[0]);
                                experiment3Fix1.add(values[1]);
                                experiment3Fix1.add(values[2]);
                            }
                            if (i == 13) {
                                experiment3Fix2.add(values[0]);
                                experiment3Fix2.add(values[1]);
                                experiment3Fix2.add(values[2]);
                            }
                            if (i == 14) {
                                experiment3Fix3.add(values[0]);
                                experiment3Fix3.add(values[1]);
                                experiment3Fix3.add(values[2]);
                            }
                        } else {
                            errors.add("Line " + i + " contains incorrect data in the first two items.");
                        }

                    } else {
                        errors.add("Length is incorrect in line " + i);
                    }

                case 5:
                case 10:
                case 15:
                    if (values.length == 2) {
                        if (values[0].equals("dependent")
                                && (values[1].equals("L") || values[1].equals("H") || values[1].equals("A"))) {
                            // Add data into its relative line
                            // Data for experiment 1
                            if (i == 5) {
                                experiment1Dependent.add(values[0]);
                                experiment1Dependent.add(values[1]);
                            }

                            // Data for experiment 2
                            if (i == 10) {
                                experiment2Dependent.add(values[0]);
                                experiment2Dependent.add(values[1]);
                            }

                            // Data for experiment 3
                            if (i == 15) {
                                experiment3Dependent.add(values[0]);
                                experiment3Dependent.add(values[1]);
                            }
                        } else {
                            errors.add("Line " + i + " contains incorrect data in the first item.");
                        }
                    } else {
                        errors.add("Length is incorrect in line " + i);
                    }
            }
        }

        br.close();

        // Find which line is the exact D, P, M, R
        // First, check if the second element of the first four lines each experiment is
        // not the same.
        if (!experiment1Independent.get(1).equals(experiment1Fix1.get(1))
                && !experiment1Independent.get(1).equals(experiment1Fix2.get(1))
                && !experiment1Independent.get(1).equals(experiment1Fix3.get(1))
                && !experiment1Fix1.get(1).equals(experiment1Fix2.get(1))
                && !experiment1Fix1.get(1).equals(experiment1Fix3.get(1))
                && !experiment1Fix2.get(1).equals(experiment1Fix3.get(1))) {
            // Assign experiment 1D value
            if (experiment1Independent.get(1).equals("D")) {
                for (int j = 0; j < (experiment1Independent.size() - 2); j++) {
                    System.out.println(experiment1Independent.get(j + 2));
                    experiment1D.add(Integer.parseInt(experiment1Independent.get(j + 2)));
                }
            } else if (experiment1Fix1.get(1).equals("D")) {
                experiment1D.add(Integer.parseInt(experiment1Fix1.get(2)));
            } else if (experiment1Fix2.get(1).equals("D")) {
                experiment1D.add(Integer.parseInt(experiment1Fix2.get(2)));
            } else if (experiment1Fix3.get(1).equals("D")) {
                experiment1D.add(Integer.parseInt(experiment1Fix3.get(2)));
            }

            // Assign experiment 1M value
            if (experiment1Independent.get(1).equals("M")) {
                for (int j = 0; j < (experiment1Independent.size() - 2); j++) {
                    experiment1M.add(Integer.parseInt(experiment1Independent.get(j + 2)));
                }
            } else if (experiment1Fix1.get(1).equals("M")) {
                experiment1M.add(Integer.parseInt(experiment1Fix1.get(2)));
            } else if (experiment1Fix2.get(1).equals("M")) {
                experiment1M.add(Integer.parseInt(experiment1Fix2.get(2)));
            } else if (experiment1Fix3.get(1).equals("M")) {
                experiment1M.add(Integer.parseInt(experiment1Fix3.get(2)));
            }

            // Assign experiment 1R value
            if (experiment1Independent.get(1).equals("R")) {
                for (int j = 0; j < (experiment1Independent.size() - 2); j++) {
                    experiment1R.add(Integer.parseInt(experiment1Independent.get(j + 2)));
                }
            } else if (experiment1Fix1.get(1).equals("R")) {
                experiment1R.add(Integer.parseInt(experiment1Fix1.get(2)));
            } else if (experiment1Fix2.get(1).equals("R")) {
                experiment1R.add(Integer.parseInt(experiment1Fix2.get(2)));
            } else if (experiment1Fix3.get(1).equals("R")) {
                experiment1R.add(Integer.parseInt(experiment1Fix3.get(2)));
            }

            // Assign experiment 1P value
            if (experiment1Independent.get(1).equals("P")) {
                for (int j = 0; j < (experiment1Independent.size() - 2); j++) {
                    experiment1P.add(Integer.parseInt(experiment1Independent.get(j + 2)));
                }
            } else if (experiment1Fix1.get(1).equals("P")) {
                experiment1P.add(Integer.parseInt(experiment1Fix1.get(2)));
            } else if (experiment1Fix2.get(1).equals("P")) {
                experiment1P.add(Integer.parseInt(experiment1Fix2.get(2)));
            } else if (experiment1Fix3.get(1).equals("P")) {
                experiment1P.add(Integer.parseInt(experiment1Fix3.get(2)));
            }

            // Check the range for D, M, R, or P??? Not sure if it is required

            // continue........

        } else {
            errors.add("The first four lines for each experiment should include all four of different variables.");
        }
        checkForErrors();

    }

    private static void checkForErrors() {
        // print any problematic lines, if there are any.
        if (!errors.isEmpty()) {
            System.out.println(errors.size() + " error(s) found:");
            for (String error : errors) {
                System.out.println(error);
            }
            System.exit(1);
        } else {
            System.out.println("No errors found.");
        }
    }

    // Function to move the person, protocol 4 or 8
    private static void move(Person person, int protocol, int Dimension) {
        Person temp = new Person(person.xCoordinate, person.yCoordinate);
        Random rand = new Random();
        int direction = 0;
        if (protocol == 4)
            direction = rand.nextInt(4);
        if (protocol == 8)
            direction = rand.nextInt(8);

        switch (direction) {
            case 0: // north
                if (person.yCoordinate < Dimension)
                    person.yCoordinate++;
                break;
            case 1: // east
                if (person.xCoordinate < Dimension)
                    person.xCoordinate++;
                break;
            case 2: // south
                if (person.yCoordinate > 0)
                    person.yCoordinate--;
                break;
            case 3: // west
                if (person.xCoordinate > 0)
                    person.xCoordinate--;
                break;
            case 4: // northeast
                if (person.yCoordinate < Dimension && person.xCoordinate < Dimension) {
                    person.yCoordinate++;
                    person.xCoordinate++;
                }
                break;
            case 5: // northwest
                if (person.yCoordinate < Dimension && person.xCoordinate > 0) {
                    person.yCoordinate++;
                    person.xCoordinate--;
                }
                break;
            case 6: // southeast
                if (person.yCoordinate > 0 && person.xCoordinate < Dimension) {
                    person.yCoordinate--;
                    person.xCoordinate++;
                }
                break;
            case 7: // southwest
                if (person.yCoordinate > 0 && person.xCoordinate > 0) {
                    person.yCoordinate--;
                    person.xCoordinate--;
                }
                break;
        }
        if (protocol == 8 && person.xCoordinate == temp.xCoordinate && person.yCoordinate == temp.yCoordinate) {
            move(person, protocol, Dimension);
        }
    }

    // This function plays the game.
    // This function actually executes the moves and gives the output.
    private static int playGame(Person person1, Person person2, int Dimension, int maxMoves, int protocol) {

        int counter = 0;

        while (counter < maxMoves) {
            if (counter % 2 == 0) {
                move(person1, protocol, Dimension);
                // It uses an if statement and take a modulus 2 of the move counter to rotate
                // between each of the players so they alternate turns.
            } else {
                move(person2, protocol, Dimension);
            }
            if (person1.xCoordinate == person2.xCoordinate && person1.yCoordinate == person2.yCoordinate)
                // Then after the player has moved it checks if there has been a meeting, if
                // there has, it breaks out of the loop.

                break;
            counter++; // If there isn't a meeting then it adds to the counter and repeats the loop.
        }

        // This section checks if the meeting occurred or not and prints the appropriate
        // message.

        if (counter < maxMoves)
            return ++counter;
        else
            return counter;
    }

    // This function runs the experiment and logs the outcome into a dad list.
    private static List<Integer> experiment(int repetitions, int Dimension, int maxMoves, int protocol) {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < repetitions; i++) {
            Person person1 = new Person(0, 0);
            Person person2 = new Person(Dimension, Dimension);
            int result = playGame(person1, person2, Dimension, maxMoves, protocol);
            data.add(result);
        }

        return data;
    }

    // This function takes the results of the experiments run in main() and
    // writes them to outputfile.txt.
    private static void outputGenerator(ArrayList<Integer> xCoordinates, ArrayList<Integer> yCoordinates)
            throws IOException {
        // sample xcoords/ycoords

        // Check if the number of x and y coordinates match
        if (xCoordinates.size() != yCoordinates.size()) {
            errors.add("Error: xCoordinates and yCoordinates must have the same number of items.");
        }
        // Check if xCoordinates are in ascending order
        for (int i = 1; i < xCoordinates.size(); i++) {
            if (xCoordinates.get(i) <= xCoordinates.get(i - 1)) {
                errors.add("Error: xCoordinates must be in ascending order.");
            }
        }
        // Determine the maximum value in yCoordinates
        int yMax = yCoordinates.get(0);
        for (int i = 0; i < yCoordinates.size(); i++) {
            if (yCoordinates.get(i) > yMax)
                yMax = yCoordinates.get(i);
        }

        // Open the output file
        PrintWriter outWriter = new PrintWriter(new FileWriter("outdata.txt"));

        // Generate the bar graph
        for (int i = 0; i < xCoordinates.size(); i++) {
            int x = xCoordinates.get(i);
            int y = yCoordinates.get(i);
            int yGraph = Math.round((100 * (float) y / yMax));

            // Print x, separator, and stars based on yGraph value
            outWriter.printf("%d| %s%n", x, "*".repeat(yGraph));
        }

        // Close the output file
        outWriter.close();
        checkForErrors();
    }

    public static void main(String[] args) throws IOException {
        System.out
                .println("The program takes an input file which describes the parameters of 3 different experiments.\n"
                        +
                        "The program takes the input file and parses each of the parameters for the experiments, then it runs\n"
                        +
                        "each experiment according to those parameters.  It will take all the results, calculate the high, low,\n"
                        +
                        " and average values of each experiment, then it will log those results in an output file.\n");
        parseInput();

        // the actual experiment neds to be run

        outputGenerator(xCoordinates, yCoordinates);
    }
}
