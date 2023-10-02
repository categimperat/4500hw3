/*Language : Java
  Author   : Gene Choi, Marielle Koffi, Silvia Matthews, Josh Dazey
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
import java.util.Collections;
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
    /*
     * private static List<Double> resultsExp1 = new ArrayList<>();
     * private static List<Double> resultsExp2 = new ArrayList<>();
     * private static List<Double> resultsExp3 = new ArrayList<>();
     * private static int[] experiment1Dimensions = new int[5];
     * private static int[] experiment1PMR = new int[3];
     * private static int[] experiment2Reps = new int[5];
     * private static int[] experiment2DPM = new int[3];
     * private static int[] experiment3Protocols = { 4, 4, 8, 8 };
     * private static int[] experiment3DMR = new int[3];
     */
    private static List<Double> resultsExp1 = new ArrayList<>();
    private static List<Double> resultsExp2 = new ArrayList<>();
    private static List<Double> resultsExp3 = new ArrayList<>();

    private static String[] experiment1Independent = new String[14];
    private static String[] experiment1Fix1 = new String[3];
    private static String[] experiment1Fix2 = new String[3];
    private static String[] experiment1Fix3 = new String[3];
    private static String[] experiment1Dependent = new String[2];

    private static int[] experiment1D = new int[12];
    private static int[] experiment1M = new int[12];
    private static int[] experiment1R = new int[12];
    private static int[] experiment1P = new int[12];

    private static String[] experiment2Independent = new String[14];
    private static String[] experiment2Fix1 = new String[3];
    private static String[] experiment2Fix2 = new String[3];
    private static String[] experiment2Fix3 = new String[3];
    private static String[] experiment2Dependent = new String[2];

    private static String[] experiment3Independent = new String[14];
    private static String[] experiment3Fix1 = new String[3];
    private static String[] experiment3Fix2 = new String[3];
    private static String[] experiment3Fix3 = new String[3];
    private static String[] experiment3Dependent = new String[2];

    private static void parseInput() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("indata.txt"));

        String line;
        List<String> errors = new ArrayList<>();

        try (Stream<String> fileStream = Files.lines(Paths.get("indata.txt"))) {
            int noOfLines = (int) fileStream.count();
            if (noOfLines > 15) {
                errors.add("Input file contains too many lines of data.");
            }else if (noOfLines < 5) {
                errors.add("Input file contains less five lines of data.");
            }
        }

        // Check and parse each line of input file
        for (int i = 1; i <= 15; i++) {
            line = br.readLine();
            String[] values;

            // check every line to see if it is null. if true, this indicates a file with 5<lines<15
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
                    if (values.length > 3 && values.length < 15) {
                         if ( values[0].equals("independent") && (values[1].equals("D") || values[1].equals("M") || values[1].equals("R") || values[1].equals("P")) ) {
                              for (int j = 2; j < values.length; j++) {
                                  if (values[j] < values[j+1]) {
                                       //Add data into its relative line
                                       //Data for experiment 1
                                       if (i == 1) {
                                           experiment1Independent[0] = values[0];
                                           experiment1Independent[1] = values[1];
                                           experiment1Independent[j] = values[j];
                                       }
                                       
                                       //Data for experiment 2
                                       if (i == 6) {
                                           experiment2Independent[0] = values[0];
                                           experiment2Independent[1] = values[1];
                                           experiment2Independent[j] = values[j];
                                       }
                                       
                                       //Data for experiment 3
                                       if (i == 11) {
                                           experiment3Independent[0] = values[0];
                                           experiment3Independent[1] = values[1];
                                           experiment3Independent[j] = values[j];
                                       }
                                       
                                  }else {
                                      errors.add("The number should be increase in line " + i);
                                  }
                              }
                              
                         }else {
                             errors.add("Line " + i + " contains incorrect data in the first two items.")
                         }
                     }else {
                         errors.add ("Length is incorrect in line " + i);
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
                    if ( values.length == 3 ) {
                          if (values[0].equals("fixed") && (values[1].equals("D") || values[1].equals("M") || values[1].equals("R") || values[1].equals("P"))) {
                              //Add data into its relative line
                              //Data for experiment 1
                              if (i == 2) {
                                  experiment1Fix1[0] = values[0];
                                  experiment1Fix1[1] = values[1];
                                  experiment1Fix1[2] = values[2];
                              }
                              if (i == 3) {
                                  experiment1Fix2[0] = values[0];
                                  experiment1Fix2[1] = values[1];
                                  experiment1Fix2[2] = values[2];
                              }
                              if (i == 4) {
                                  experiment1Fix3[0] = values[0];
                                  experiment1Fix3[1] = values[1];
                                  experiment1Fix3[2] = values[2];
                              }
                              
                              //Data for experiment 2
                              if (i == 7) {
                                  experiment2Fix1[0] = values[0];
                                  experiment2Fix1[1] = values[1];
                                  experiment2Fix1[2] = values[2];
                              }
                              if (i == 8) {
                                  experiment2Fix2[0] = values[0];
                                  experiment2Fix2[1] = values[1];
                                  experiment2Fix2[2] = values[2];
                              }
                              if (i == 9) {
                                  experiment2Fix3[0] = values[0];
                                  experiment2Fix3[1] = values[1];
                                  experiment2Fix3[2] = values[2];
                              }
                              
                              //Data for experiment 3
                              if (i == 12) {
                                  experiment3Fix1[0] = values[0];
                                  experiment3Fix1[1] = values[1];
                                  experiment3Fix1[2] = values[2];
                              }
                              if (i == 13) {
                                  experiment3Fix2[0] = values[0];
                                  experiment3Fix2[1] = values[1];
                                  experiment3Fix2[2] = values[2];
                              }
                              if (i == 14) {
                                  experiment3Fix3[0] = values[0];
                                  experiment3Fix3[1] = values[1];
                                  experiment3Fix3[2] = values[2];
                              }
                          }else {
                              errors.add("Line " + i + " contains incorrect data in the first two items.");
                          }
                              
                     }else {
                         errors.add("Length is incorrect in line " + i);
                     }
                     
                case 5:
                case 10:
                case 15:
                    if ( values.length == 2) {
                        if (values[0].equals("dependent") && (values[1].equals("L") || values[1].equals("H") || values[1].equals("A")) ){
                            //Add data into its relative line
                            //Data for experiment 1
                            if (i == 5) {
                                experiment1Dependent[0] = values[0];
                                experiment1Dependent[1] = values[1];
                            }
                            
                            //Data for experiment 2
                            if (i == 10) {
                                experiment2Dependent[0] = values[0];
                                experiment2Dependent[1] = values[1];
                            }
                            
                            //Data for experiment 3
                            if (i == 15) {
                                experiment3Dependent[0] = values[0];
                                experiment3Dependent[1] = values[1];
                            }
                        }else {
                            errors.add("Line " + i + " contains incorrect data in the first item.")
                        }
                    }else {
                        errors.add("Length is incorrect in line " + i);
                    }
            }    
        }
            
        br.close();
        
        //Find which line is the exact D, P, M, R
        //First, check if the second element of the first four lines each experiment is not the same.
        if ( !experiment1Independent[1].equals(experiment1Fix1[1]) 
             && !experiment1Independent[1].equals(experiment1Fix2[1]) 
             && !experiment1Independent[1].equals(experiment1Fix3[1]) 
             && !experiment1Fix1[1].equals(experiment1Fix2[1])
             && !experiment1Fix1[1].equals(experiment1Fix3[1])
             && !experiment1Fix2[1].equals(experiment1Fix3[1]) ) {
                 //Assign experiment 1D value
                 if (experiment1Independent[1].equals("D")) {
                     for (int j = 0; j < (experiment1Independent.length - 2); j++) {
                         experiment1D[j] = Integer.parseInt(experiment1Independent[j+2]);
                     }
                 }else if (experiment1Fix1[1].equals("D")) {
                     experiment1D[0] = Integer.parseInt(experiment1Fix1[2]);
                 }else if (experiment1Fix2[1].equals("D")) {
                     experiment1D[0] = Integer.parseInt(experiment1Fix2[2]);
                 }else if (experiment1Fix3[1].equals("D")) {
                     experiment1D[0] = Integer.parseInt(experiment1Fix3[2]);
                 }
                 
                 //Assign experiment 1M value 
                 if (experiment1Independent[1].equals("M")) {
                     for (int j = 0; j < (experiment1Independent.length - 2); j++) {
                         experiment1M[j] = Integer.parseInt(experiment1Independent[j+2]);
                     }
                 }else if (experiment1Fix1[1].equals("M")) {
                     experiment1M[0] = Integer.parseInt(experiment1Fix1[2]);
                 }else if (experiment1Fix2[2].equals("M")) {
                     experiment1M[0] = Integer.parseInt(experiment1Fix2[2]);
                 }else if (experiment1Fix3[2].equals("M")) {
                     experiment1M[0] = Integer.parseInt(experiment1Fix3[2]);
                 }
                 
                 //Assign experiment 1R value 
                 if (experiment1Independent[1].equals("R")) {
                     for (int j = 0; j < (experiment1Independent.length - 2); j++) {
                         experiment1R[j] = Integer.parseInt(experiment1Independent[j+2]);
                     }
                 }else if (experiment1Fix1[1].equals("R")) {
                     experiment1R[0] = Integer.parseInt(experiment1Fix1[2]);
                 }else if (experiment1Fix2[2].equals("R")) {
                     experiment1R[0] = Integer.parseInt(experiment1Fix2[2]);
                 }else if (experiment1Fix3[2].equals("R")) {
                     experiment1R[0] = Integer.parseInt(experiment1Fix3[2]);
                 }
                 
                 
                 //Assign experiment 1P value 
                 if (experiment1Independent[1].equals("P")) {
                     for (int j = 0; j < (experiment1Independent.length - 2); j++) {
                         experiment1P[j] = Integer.parseInt(experiment1Independent[j+2]);
                     }
                 }else if (experiment1Fix1[1].equals("P")) {
                     experiment1P[0] = Integer.parseInt(experiment1Fix1[2]);
                 }else if (experiment1Fix2[2].equals("P")) {
                     experiment1P[0] = Integer.parseInt(experiment1Fix2[2]);
                 }else if (experiment1Fix3[2].equals("P")) {
                     experiment1P[0] = Integer.parseInt(experiment1Fix3[2]);
                 }
                 
                 //Check the range for D, M, R, or P??? Not sure if it is required
                 
                 //continue........
                 
        } else{
            errors.add("The first four lines for experiment 1 should include all four of different variables i.");
        }
        

        // print any problematic lines, if there are any.
        if (!errors.isEmpty()) {
            System.out.println(errors.size() + " error(s) found:");
            for (String error : errors) {
                System.out.println(error);
            }
            System.exit(1);
        } else {
            System.out.println("No errors found in input file.");
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
    private static void outputGenerator() throws IOException {

    }

    public static void generateBarGraph(List<Integer> xCoordinates, List<Integer> yCoordinates, String outputFile)
            throws IOException {
        // Check if the number of x and y coordinates match
        if (xCoordinates.size() != yCoordinates.size()) {
            System.err.println("Error: xCoordinates and yCoordinates must have the same number of items.");
            System.exit(1);
        }

        // Check if xCoordinates are in ascending order
        for (int i = 1; i < xCoordinates.size(); i++) {
            if (xCoordinates.get(i) <= xCoordinates.get(i - 1)) {
                System.err.println("Error: xCoordinates must be in ascending order.");
                System.exit(1);
            }
        }

        // Determine the maximum value in yCoordinates
        int yMax = yCoordinates.stream().max(Integer::compare).orElse(0);

        // Open the output file
        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));

        // Generate the bar graph
        for (int i = 0; i < xCoordinates.size(); i++) {
            int x = xCoordinates.get(i);
            int y = yCoordinates.get(i);
            int yGraph = Math.round((100 * (float) y / yMax));

            // Print x, separator, and stars based on yGraph value
            writer.printf("%d, |%s%n", x, "*".repeat(yGraph));
        }

        // Close the output file
        writer.close();
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
        double low;
        double high;
        double average;
        parseInput();
        List<Integer> data = new ArrayList<>();

        // Running experiment 1
        for (int i = 0; i < 5; i++) {
            data = experiment(experiment1PMR[2], experiment1Dimensions[i], experiment1PMR[1], experiment1PMR[0]);
            low = Collections.min(data);
            high = Collections.max(data);
            average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            resultsExp1.add(low);
            resultsExp1.add(high);
            resultsExp1.add(average);
        }

        // Running Experiment 2
        for (int i = 0; i < 5; i++) {
            data = experiment(experiment2Reps[i], experiment2DPM[0], experiment2DPM[2], experiment2DPM[1]);
            low = Collections.min(data);
            high = Collections.max(data);
            average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            resultsExp2.add(low);
            resultsExp2.add(high);
            resultsExp2.add(average);
        }

        // Running Experiment 3
        for (int i = 0; i < 4; i++) {
            data = experiment(experiment3DMR[2], experiment3DMR[0], experiment3DMR[1], experiment3Protocols[i]);
            low = Collections.min(data);
            high = Collections.max(data);
            average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            resultsExp3.add(low);
            resultsExp3.add(high);
            resultsExp3.add(average);
        }

        outputGenerator();
    }
}
