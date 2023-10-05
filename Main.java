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
    String currentIndepVar;
    String currentDepVar;
    Integer Dimension;
    Integer maxMoves;
    Integer repetitions;
    Integer protocol;
    double highMoves;
    double lowMoves;
    double averageMoves;

    public ExperimentData(String currentIndepVar, String currentDepVar, int dimension, int protocol, int maxMoves,
            int repetitions,
            double lowMoves, double highMoves,
            double averageMoves) {
        Dimension = dimension;
        this.currentIndepVar = currentIndepVar;
        this.currentDepVar = currentDepVar;
        this.maxMoves = maxMoves;
        this.repetitions = repetitions;
        this.protocol = protocol;
        this.highMoves = highMoves;
        this.lowMoves = lowMoves;
        this.averageMoves = averageMoves;
    }
}

public class Main {

    private static String currentIndepVar;
    private static String currentDepVar;

    // sample xcoords/ycoords
    // these should be replaced with dynamically populated arrayLists

    private static ArrayList<Integer> xCoordinates = new ArrayList<>();
    private static ArrayList<Double> yCoordinates = new ArrayList<>();

    private static List<Double> resultsExp1 = new ArrayList<>();
    private static List<Double> resultsExp2 = new ArrayList<>();
    private static List<Double> resultsExp3 = new ArrayList<>();

    // Variables for experiment 1
    private static ArrayList<String> indepExperiment1 = new ArrayList<String>();
    private static ArrayList<String> fixedExperiment1_1 = new ArrayList<String>(1);
    private static ArrayList<String> fixedExperiment1_2 = new ArrayList<String>(1);
    private static ArrayList<String> fixedExperiment1_3 = new ArrayList<String>(1);
    private static ArrayList<String> depExperiment1 = new ArrayList<String>(1);

    private static ArrayList<Integer> experiment1D = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment1P = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment1M = new ArrayList<Integer>();
    private static ArrayList<Integer> experiment1R = new ArrayList<Integer>();

    // Hold one letter abbreviations for experiment 1
    private static ArrayList<String> charArr = new ArrayList<String>();

    private static List<String> errors = new ArrayList<>();

    private static void parseInput() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("indata.txt"));

        String strLine;

        try (Stream<String> fileStream = Files.lines(Paths.get("indata.txt"))) {
            int noOfLines = (int) fileStream.count();
            if (noOfLines > 15) {
                errors.add("Input file contains too many lines.");
            } else if (noOfLines < 5) {
                errors.add("Input file contains too less lines.");
            } else if (noOfLines % 5 != 0) {
                errors.add("The number of lines should be a multiple of 5.");
            }
        }

        // Check if all data are correct in file
        for (int i = 1; i <= 5; i++) {
            strLine = br.readLine();
            String[] lineValues;

            String regex = "^[a-z]+,[A-Z],*([0-9]+,)*[0-9]*$";
            Pattern p = Pattern.compile(regex, Pattern.MULTILINE);

            if (strLine == null) {
                errors.add("Incomplete input file.");
                break;
            } else {
                if (strLine.contains(" ")) {
                    errors.add("Line " + i + " contains whitespace.");
                    strLine = strLine.replaceAll(" ", "");
                }
                lineValues = strLine.split(",");
            }

            Matcher matcher = p.matcher(strLine);

            if (!matcher.find()) {

                System.out.println("Not match: " + "Line " + i + " contains incorrect data.");
                errors.add("Line " + i + " contains incorrect data.");
            }

            switch (i) {
                case 1:
                    if (lineValues.length >= 4 && lineValues.length <= 14) {
                        if (lineValues[0].equals("independent")) {
                            if (lineValues[1].equals("D") || lineValues[1].equals("M")
                                    || lineValues[1].equals("R") || lineValues[1].equals("P")) {

                                charArr.add(lineValues[1]);
                                indepExperiment1.add(lineValues[1]);
                                System.out.println("lineValues.length = " + lineValues.length);
                                // Now compare if the values are increasing from left to right
                                for (int j = 2; j < lineValues.length; j++) {

                                    if (j == (lineValues.length - 1)) {
                                        indepExperiment1.add(lineValues[lineValues.length - 1]);

                                    } else if (Integer.parseInt(lineValues[j]) < Integer.parseInt(lineValues[j + 1])) {

                                        indepExperiment1.add(lineValues[j]);

                                    } else {
                                        errors.add("The data should be increasing on line " + i);
                                    }

                                    System.out.println("indepExperiment1: " + indepExperiment1);
                                }

                            } else {
                                errors.add("The second value/capital is incorrect on line " + i);
                            }
                        } else {
                            errors.add("The first word is not independent on line " + i);
                        }
                    } else {
                        System.out.println("lineValues.length = " + lineValues.length);
                        errors.add("Length is incorrect on line " + i);
                    }
                    break;
                case 2:
                case 3:
                case 4:
                    if (lineValues.length == 3) {
                        if (lineValues[0].equals("fixed")) {
                            if (lineValues[1].equals("D") || lineValues[1].equals("M")
                                    || lineValues[1].equals("R") || lineValues[1].equals("P")) {

                                if (i == 2) {
                                    charArr.add(lineValues[1]);
                                    fixedExperiment1_1.add(lineValues[1]);

                                    // Should check if it is a digit...
                                    fixedExperiment1_1.add(lineValues[2]);
                                    System.out.println("fixedExperiment1_1: " + fixedExperiment1_1);
                                }

                                if (i == 3) {
                                    charArr.add(lineValues[1]);
                                    fixedExperiment1_2.add(lineValues[1]);

                                    // Should check if it is a digit...
                                    fixedExperiment1_2.add(lineValues[2]);
                                    System.out.println("fixedExperiment1_2: " + fixedExperiment1_2);

                                }

                                if (i == 4) {
                                    charArr.add(lineValues[1]);
                                    fixedExperiment1_3.add(lineValues[1]);

                                    // Should check if it is a digit...
                                    fixedExperiment1_3.add(lineValues[2]);
                                    System.out.println("fixedExperiment1_3: " + fixedExperiment1_3);

                                }

                            } else {
                                errors.add("The second value/capital is incorrect on line " + i);
                            }

                        } else {
                            errors.add("The first word is not fixed on line " + i);
                        }
                    } else {
                        errors.add("Length is incorrect on line " + i);
                    }
                    break;

                case 5:
                    if (lineValues.length == 2) {
                        if (lineValues[0].equals("dependent")) {
                            if (lineValues[1].equals("L") || lineValues[1].equals("H")
                                    || lineValues[1].equals("A")) {

                                depExperiment1.add(lineValues[1]);
                                System.out.println("depExperiment1 = " + depExperiment1);

                            } else {
                                errors.add("The second value/capital is incorrect on line " + i);
                            }
                        } else {
                            errors.add("The first word is not dependent on line " + i);
                        }
                    } else {
                        errors.add("Length is incorrect on line " + i);
                    }

                    break;

            }// end of switch

        } // end of for

        br.close();

        // Check if the second value capital letter is not identical in the first four
        // lines
        if ((charArr.get(0) != charArr.get(1)) && (charArr.get(0) != charArr.get(2))
                && (charArr.get(0) != charArr.get(3)) && (charArr.get(1) != charArr.get(2))
                && (charArr.get(1) != charArr.get(3)) && (charArr.get(2) != charArr.get(3))) {

            if (charArr.indexOf("D") == -1 || charArr.indexOf("M") == -1
                    || charArr.indexOf("P") == -1 || charArr.indexOf("R") == -1) {

                errors.add("One of variables(D/M/P/R) is missing.");
            }

        } else {
            errors.add("The second value-capital letters are not identical in the first four lines.");
        }

        currentIndepVar = indepExperiment1.get(0);
        currentDepVar = depExperiment1.get(0);

        switch (fixedExperiment1_1.get(0)) {
            case "D":
                experiment1D.add(Integer.parseInt(fixedExperiment1_1.get(1)));

            case "P":
                experiment1P.add(Integer.parseInt(fixedExperiment1_1.get(1)));

            case "M":
                experiment1M.add(Integer.parseInt(fixedExperiment1_1.get(1)));

            case "R":
                experiment1R.add(Integer.parseInt(fixedExperiment1_1.get(1)));
        }
        switch (fixedExperiment1_2.get(0)) {
            case "D":
                experiment1D.add(Integer.parseInt(fixedExperiment1_2.get(1)));

            case "P":
                experiment1P.add(Integer.parseInt(fixedExperiment1_2.get(1)));

            case "M":
                experiment1M.add(Integer.parseInt(fixedExperiment1_2.get(1)));

            case "R":
                experiment1R.add(Integer.parseInt(fixedExperiment1_2.get(1)));
        }
        switch (fixedExperiment1_3.get(0)) {
            case "D":
                experiment1D.add(Integer.parseInt(fixedExperiment1_3.get(1)));

            case "P":
                experiment1P.add(Integer.parseInt(fixedExperiment1_3.get(1)));

            case "M":
                experiment1M.add(Integer.parseInt(fixedExperiment1_3.get(1)));

            case "R":
                experiment1R.add(Integer.parseInt(fixedExperiment1_3.get(1)));
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
    private static List<Integer> experiment(int Dimension, int protocol, int maxMoves, int repetitions) {
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
    private static void outputGenerator(String currentIndepVar, String currentDepVar, ArrayList<Integer> xCoordinates,
            ArrayList<Double> yCoordinates)
            throws IOException {

        double low;
        double high;
        double average;
        List<Integer> data = new ArrayList<>();

        // packaging data into arrays of objects
        // run experiment however many times as necessary
        ExperimentData[] experiment1 = new ExperimentData[indepExperiment1.size() - 1];
        if (indepExperiment1.get(0).equals("D")) {

            for (int i = 1; i < indepExperiment1.size(); i++) {
                // System.out.println(Integer.parseInt(indepExperiment1.get(i)));
                experiment1D.add(Integer.parseInt(indepExperiment1.get(i)));

                xCoordinates.add(Integer.parseInt(indepExperiment1.get(i)));

                experiment1[i - 1] = new ExperimentData(
                        currentIndepVar,
                        currentDepVar,
                        experiment1D.get(i - 1),
                        experiment1P.get(0),
                        experiment1M.get(0),
                        experiment1R.get(0),
                        1.0,
                        2.0,
                        3.0);
                data = experiment(experiment1[i - 1].Dimension, experiment1[i - 1].protocol,
                        experiment1[i - 1].maxMoves, experiment1[i - 1].repetitions);
                low = Collections.min(data);
                high = Collections.max(data);
                average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
                resultsExp1.add(low);
                resultsExp1.add(high);
                resultsExp1.add(average);

            }
        }
        if (indepExperiment1.get(0).equals("P")) {
            for (int i = 1; i < indepExperiment1.size(); i++) {
                experiment1P.add(Integer.parseInt(indepExperiment1.get(i)));
                xCoordinates.add(Integer.parseInt(indepExperiment1.get(i)));
                experiment1[i - 1] = new ExperimentData(
                        currentIndepVar,
                        currentDepVar,
                        experiment1D.get(0),
                        experiment1P.get(i - 1),
                        experiment1M.get(0),
                        experiment1R.get(0),
                        1.0,
                        2.0,
                        3.0);
                data = experiment(experiment1[i - 1].Dimension, experiment1[i - 1].protocol,
                        experiment1[i - 1].maxMoves, experiment1[i - 1].repetitions);
                low = Collections.min(data);
                high = Collections.max(data);
                average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
                resultsExp1.add(low);
                resultsExp1.add(high);
                resultsExp1.add(average);
            }
        }
        if (indepExperiment1.get(0).equals("M")) {
            for (int i = 1; i < indepExperiment1.size(); i++) {
                experiment1M.add(Integer.parseInt(indepExperiment1.get(i)));
                xCoordinates.add(Integer.parseInt(indepExperiment1.get(i)));
                experiment1[i - 1] = new ExperimentData(
                        currentIndepVar,
                        currentDepVar,
                        experiment1D.get(0),
                        experiment1P.get(0),
                        experiment1M.get(i - 1),
                        experiment1R.get(0),
                        1.0,
                        2.0,
                        3.0);
                data = experiment(experiment1[i - 1].Dimension, experiment1[i - 1].protocol,
                        experiment1[i - 1].maxMoves, experiment1[i - 1].repetitions);
                low = Collections.min(data);
                high = Collections.max(data);
                average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
                resultsExp1.add(low);
                resultsExp1.add(high);
                resultsExp1.add(average);
            }
        }
        if (indepExperiment1.get(0).equals("R")) {
            for (int i = 1; i < indepExperiment1.size(); i++) {
                experiment1R.add(Integer.parseInt(indepExperiment1.get(i)));
                xCoordinates.add(Integer.parseInt(indepExperiment1.get(i)));
                experiment1[i - 1] = new ExperimentData(
                        currentIndepVar,
                        currentDepVar,
                        experiment1D.get(0),
                        experiment1P.get(0),
                        experiment1M.get(0),
                        experiment1R.get(i - 1),
                        1.0,
                        2.0,
                        3.0);
                data = experiment(experiment1[i - 1].Dimension, experiment1[i - 1].protocol,
                        experiment1[i - 1].maxMoves, experiment1[i - 1].repetitions);
                low = Collections.min(data);
                high = Collections.max(data);
                average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
                resultsExp1.add(low);
                resultsExp1.add(high);
                resultsExp1.add(average);
            }
        }

        // populate ycoords
        for (int i = 0; i < resultsExp1.size() / 3; i++) {
            if (currentDepVar.equals("L")) {
                yCoordinates.add(resultsExp1.get((3 * i)));
            }
            if (currentDepVar.equals("H")) {
                yCoordinates.add(resultsExp1.get((3 * i) + 1));
            }
            if (currentDepVar.equals("A")) {
                yCoordinates.add(resultsExp1.get((3 * i) + 2));
            }
        }

        // Check if the number of x and y coordinates match
        if (xCoordinates.size() != yCoordinates.size()) {
            errors.add("Error: xCoordinates and yCoordinates must have the same number of items.");
        }

        // Check if xCoordinates are in ascending order
        for (int i = 0; i < xCoordinates.size() - 1; i++) {
            if (xCoordinates.get(i) > xCoordinates.get(i + 1)) {
                errors.add("Error: xCoordinates must be in ascending order.");
            }
        }

        // Determine the maximum value in yCoordinates
        double yMax = yCoordinates.get(0);
        for (int i = 0; i < yCoordinates.size(); i++) {
            if (yCoordinates.get(i) > yMax)
                yMax = yCoordinates.get(i);
        }

        // Open the output file

        PrintWriter outWriter = new PrintWriter(new FileWriter("outdata.txt"));

        // generate the table
        outWriter.println(
                "*--------------*--------------*--------------*------------*--------------*");
        outWriter.printf(
                "|  Dimensions  |  Max Moves   |  Repetitions |  Protocol  | %-12s |\n", currentDepVar);
        outWriter.println(
                "*--------------*--------------*--------------*------------*--------------*");
        for (int i = 0; i < xCoordinates.size(); i++) {
            if (currentDepVar.equals("L")) {
                outWriter.printf("| %-12d | %-12d | %-12d | %-10d | %-12.0f |\n",
                        experiment1[i].Dimension,
                        experiment1[i].maxMoves, experiment1[i].repetitions, experiment1[i].protocol,
                        resultsExp1.get((3 * i)));
            }
            if (currentDepVar.equals("H")) {
                outWriter.printf("| %-12d | %-12d | %-12d | %-10d | %-12.0f |\n",
                        experiment1[i].Dimension,
                        experiment1[i].maxMoves, experiment1[i].repetitions, experiment1[i].protocol,
                        resultsExp1.get((3 * i + 1)));
            }
            if (currentDepVar.equals("A")) {
                outWriter.printf("| %-12d | %-12d | %-12d | %-10d | %-12.0f |\n",
                        experiment1[i].Dimension,
                        experiment1[i].maxMoves, experiment1[i].repetitions, experiment1[i].protocol,
                        resultsExp1.get((3 * i + 2)));
            }
            outWriter.println(
                    "*--------------*--------------*--------------*-------------*-------------*");
        }
        outWriter.println();

        // Generate the bar graph
        if (xCoordinates.size() == yCoordinates.size()) {
            for (int i = 0; i < xCoordinates.size(); i++) {
                int x = xCoordinates.get(i);
                double y = yCoordinates.get(i);
                int yGraph = Math.round((100 * (float) y / (float) yMax));
                System.out.println(yGraph);

                // Print x, separator, and stars based on yGraph value
                outWriter.printf("%d| %s%n", x, "*".repeat(yGraph));
            }
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

        outputGenerator(currentIndepVar, currentDepVar, xCoordinates, yCoordinates);
    }
}
