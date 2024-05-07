import java.io.*;
import java.util.*;

/** The MainMenu that controls the entire program using a menu-driven interface
 * @author Daniel Reid (u2143528)
 * @version 07/03/2024
 */

public class MainMenu {

    private static final String SLSFILE = "SLS2024.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // scanner for gathering user input
        int choice; // storing user input as choice

        ContestList contestList = new ContestList(); // creating empty lists for each data type that we can add to
        SkaterList skaterList = new SkaterList(); // within our application
        SkaterScore skaterScore = new SkaterScore();

        loadDataFromFile(contestList, skaterList, skaterScore); // loading data from the file if it exists, if not then it will create a new file


        do { // do while loop to print the menu and gather choice whilst choice is not 12 (exit)
            printMenu(); // print the menu
            try { // try catch for input mismatch, if the user inputs a character that is not an integer it will catch it and throw an error
                System.out.print("Please enter a number between 1-12: "); // prompting the user for a choice between 1-12
                choice = sc.nextInt(); // store the choice

                switch (choice) { // switch case based on the user choice
                    default:
                        System.out.println("Please insert a number between 1-12...");
                    case 1:
                        addContest(contestList);
                        break;
                    case 2:
                        addAthlete(skaterList);
                        break;
                    case 3:
                        addScores(contestList, skaterList);
                        break;
                    case 4:
                        viewContestStandings(contestList, skaterList, skaterScore);
                        break;
                    case 5:
                        viewOneContestStandings(contestList, skaterList, skaterScore);
                        break;
                    case 6:
                        viewAllContestInfo(contestList);
                        break;
                    case 7:
                        viewOneContestInfo(contestList);
                        break;
                    case 8:
                        viewAllAthleteInfo(skaterList);
                        break;
                    case 9:
                        viewOneAthleteInfo(skaterList);
                        break;
                    case 10:
                        deleteOneContest(contestList);
                        break;
                    case 11:
                        deleteOneSkater(skaterList);
                        break;
                    case 12:
                        System.out.println("Application closing...");
                }


            } catch (InputMismatchException e) { // throw the error if incorrect input
                System.out.println("Please insert an integer!");
                sc.nextLine(); // Get rid of the invalid input
                choice = -1; // Set choice to an invalid number, so we can continue the loop

            }
        } while (choice != 12); // if choice is 12 exit

        saveDataToFile(contestList, skaterList, skaterScore); // save any data to the file
    }


//    MENU PRINTING
    static void printMenu() {
        System.out.println();
        System.out.println("************************************");
        System.out.println("* Street League Skateboarding 2024 *");
        System.out.println("************************************");
        System.out.println();
        System.out.println("1: Add SLS contest location and date.");
        System.out.println("2: Add SLS competing skateboard athlete details.");
        System.out.println("3: Add/Update skateboard athlete scores.");
        System.out.println("4: View current standings across all contests.");
        System.out.println("5: View standings for a specific contest.");
        System.out.println("6: View data for all contests.");
        System.out.println("7: View information about a specific contest.");
        System.out.println("8: View data for all athletes.");
        System.out.println("9: View data for a specific athlete.");
        System.out.println("10: Remove an SLS contest stop.");
        System.out.println("11: Remove an athlete from the list.");
        System.out.println("12: Exit application and save changes.");
        System.out.println();

    }

//      LOAD FILE DATA
    static void loadDataFromFile(ContestList contestList, SkaterList skaterList, SkaterScore skaterScore) {
        File file = new File(SLSFILE); // This will always be the name of the file
        try {
            if (!file.exists()) { // if the file does not exist, create a new file
                file.createNewFile();
                System.out.println("File created: " + SLSFILE);
            }
            Scanner scanner = new Scanner(file); // We use the scanner to look through the text in the file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine(); // each line of the file
                String[] parts = line.split(":"); // splitting the data by the colon into two parts
                String type = parts[0].trim(); // this will be the switch case
                String data = parts[1].trim(); // this will be the data we use in the application

                switch (type) {
                    case "Destination":
                        String[] contestData = data.split(","); // splitting the data on the comma into two parts
                        String location = contestData[0].trim(); // first part will be the location
                        String date = contestData[1].trim(); // second part will be the date
                        contestList.addContest(new Contest(location, date)); // create a new contest with this information
                        break;
                    case "Skater":
                        String[] skaterData = data.split(","); // splitting the data on the comma into 4 parts
                        String name = skaterData[0].trim(); // first part is the name of the skater
                        String stance = skaterData[1].trim(); // this will be the stance
                        String nationality = skaterData[2].trim(); // this will be the nationality
                        String gender = skaterData[3].trim(); // this will be the gender
//                        SINCE WE CHECK THE DATA IS CORRECTLY FORMATTED INSIDE THE APPLICATION, NO CHECKS ARE REQUIRED HERE
                        skaterList.addSkater(new Skater(name, stance, nationality, gender)); // create a new skater with this information
                        break;
                    case "Scores":
                        String[] scoresData = data.split(",", 3); // Limit the split to 3 parts as the array also uses comma
                        if (scoresData.length < 3) { // if we have less than three parts of data throw an error
                            System.out.println("Invalid Scores data: " + data);
                            break; // this was mainly used for debugging
                        }
                        String skaterName = scoresData[0].trim(); // the name is the first part of the data
                        String contestLocation = scoresData[1].trim(); // the contest location is the second
                        String scores = scoresData[2].trim(); // and the scores are the third, but they are a String here

                        // Convert the scores string to an array of Double[]
                        String[] scoresStringArray = scores.substring(1, scores.length()-1).split(", "); // return a string that is a substring of our string and split it at the commas
                        Double[] scoresArray = new Double[scoresStringArray.length]; // create a new Double[] with the length of our string array
                        for (int i = 0; i < scoresStringArray.length; i++) { // parse each part of the string into a Double and add it to our score array
                            scoresArray[i] = Double.parseDouble(scoresStringArray[i]);
                        }

                        // Find the skater and contest
                        Skater skater = skaterList.getSkaterByName(skaterName); // since the skater and contest information has already been read
                        Contest contest = contestList.getContestByName(contestLocation); // we need to get this information by its name using a simple getXByName mehtod

                        // Add the scores to the skater
                        if (skater != null && contest != null) { // checking to see if the skater and contest exists
                            System.out.println("Adding scores to skater: " + skater.getName() + " for contest: " + contest.getLocation()); // Print the skater's name and contest location
                            skater.addScores(contest, scoresArray); // add the scores to the skater for that contest
                        } else { // if they are not found then print an error
                            System.out.println("Skater or contest not found: " + skaterName + ", " + contestLocation);
                        }
                        break;
                    default:
                        System.out.println("Unknown data type in file: " + type); // this should never occur due to the checks we have inplace in our application, but this is here just in case
                }
            }
        } catch (IOException e) { // If for some reason we cannot load the file
            System.out.println("An error occurred while loading data from file: " + e.getMessage());
        }
    }


    //      SAVE FILE DATA
    static void saveDataToFile(ContestList contestList, SkaterList skaterList, SkaterScore skaterScore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SLSFILE))) { // try and catch for the writing to the file
            // save contest details to the file
            for (int i = 1; i <= contestList.getTotal(); i++) { // looping for the length of the contest list
                writer.println("Destination: " + contestList.getContest(i).getLocation() + ", " // "Destination: " will be used to sort the data in the file loading method
                        + contestList.getContest(i).getDate()); // writing every entry to the file using the PrintWriter
            }
            // save skater details to the file
            for (int i = 1; i <= skaterList.getTotal(); i++) { // looping for the length of the skater list
                writer.println("Skater: " + skaterList.getSkater(i).getName() + ", " + skaterList.getSkater(i).getStance() + ", " // "Skater: " will be used to sort the data in the file loading method
                        + skaterList.getSkater(i).getNationality() + ", " + skaterList.getSkater(i).getGender()); // again using the PrintWriter to write the data to our file
            }
            // save scores to the file
            for (int i = 1; i <= contestList.getTotal(); i++) { // looping for the length of contest list (even if there are no scores we will still save the skater to the contest)
                Contest contest = contestList.getContest(i); // get the details of the contest at position i in the loop
                for (int j = 1; j <= skaterList.getTotal(); j++) { // looping for the length of the skater list
                    Skater skater = skaterList.getSkater(j); // get the skater's information
                    Double[] scores = skater.getScores(contest); // storing the skaters scores for the current iteration of contest into "scores"
                    if (scores != null && scores.length > 0) { // if the skater has scores then...
                        String scoresString = Arrays.toString(scores); // convert the scores array into a String
                        writer.println("Scores: " + skater.getName() + ", " + contest.getLocation() + ", " + scoresString);
                        // "Scores: ", skater.getName(), and contest.getLocation() will be used to sort the data when we load the file
                    }
                }
            }
        } catch (IOException e) { // if we cannot save to the file
            System.out.println("Error saving data to file.");
        }
    }

//      OPTION 1
    static void addContest(ContestList listIn) { // method for adding a contest using the users input
        Scanner sc = new Scanner(System.in); // scanner to gather input
        String dateIn; // empty strings for the date and location of the contest
        String locIn;

        do { // do while loop that will check the information entered is in the correct format
            System.out.print("Please insert the contest's location (city): ");
            locIn = sc.next(); // grabs input

            if (locIn.matches(".*\\d+.*")) { // this expression is used to check if a string has one or more digits within
                System.out.println("Invalid input. Location can only contain letters...");
            }
        } while (locIn.matches(".*\\d+.*")); // this loop will continue while the string has any digits

        do { // do while loop to ensure the correct format of date is entered and the date is between 2020-2030
            System.out.print("Please insert the date of the contest (DD/MM/YYYY): ");
            dateIn = sc.next(); // grabs input

            if (!dateIn.matches("\\b(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/(202[0-9]|2030)\\b")) { // Regex for DD/MM/YYYY format with a valid year between 2020-2030
                System.out.println("Invalid date format. Please use DD/MM/YYYY format and a valid year between 2020-2030.");
            }
        } while (!dateIn.matches("\\b(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/(202[0-9]|2030)\\b"));

        listIn.addContest(new Contest(locIn, dateIn)); // add the contest to the list

        System.out.println();
        System.out.println("Contest added successfully!");
    }

//      OPTION 2
    static void addAthlete(SkaterList listIn) {
        Scanner sc = new Scanner(System.in); // scanner for receiving user input

        String nameIn;
        String stanceIn;
        String nationIn;
        String genderIn;

        if (!listIn.isFull()) { // The maximum contestants allowed is 8

            do { // do while loop that checks if the name contains only letters
                System.out.print("Please insert the athlete's last name: ");
                nameIn = sc.next();

                if (nameIn.matches(".*\\d+.*")) {
                    System.out.println("Name can only contain letters...");
                }
            } while (nameIn.matches(".*\\d+.*"));

            do { // do while loop that checks if the stance is regular or goofy
                System.out.print("Please insert the athlete's stance: ");
                stanceIn = sc.next();

                if (!stanceIn.equalsIgnoreCase("regular") && !stanceIn.equalsIgnoreCase("goofy")) {
                    System.out.println("Please insert 'regular' or 'goofy'...");
                }

            } while (!stanceIn.equalsIgnoreCase("regular") && !stanceIn.equalsIgnoreCase("goofy"));

            do { // do while loop to check that the nationality is in a 3 letter format
                System.out.print("Please insert the athlete's nationality (3 Letters): ");
                nationIn = sc.next();

                if (nationIn.length() != 3 || !nationIn.matches("^[a-zA-Z]+$")) {
                    System.out.println("Nationality must be three letters long (i.e. USA, ENG)...");
                }
            } while (nationIn.length() != 3 || !nationIn.matches("^[a-zA-Z]+$"));

            do { // do while loop to check if the skaters gender is male or female (simplicity's sake, sorry)
                System.out.print("Please insert the athlete's gender: ");
                genderIn = sc.next();

                if (!genderIn.equalsIgnoreCase("male") && !genderIn.equalsIgnoreCase("female")) {
                    System.out.println("Please insert 'Male' or 'Female'...");
                }

            } while (!genderIn.equalsIgnoreCase("male") && !genderIn.equalsIgnoreCase("female"));

            listIn.addSkater(new Skater(nameIn, stanceIn, nationIn.toUpperCase(), genderIn)); // adding the skater to the list and capitalising their nationality
            System.out.println("Skater's information added successfully!");
            System.out.println();
        } else { // if list is full then...
            System.out.println("8 skaters are already competing, list is full...");
        }
    }

//      OPTION 3
    static void addScores(ContestList contestList, SkaterList skaterList) {
        Scanner sc = new Scanner(System.in); // scanner to grab user input
        int skaterSelection;
        int contestSelection;

        if (!skaterList.isEmpty()) { // if the list is empty then we cannot add any scores
            System.out.println("For which skater would you like to add scores?");

            for (int i = 1; i <= skaterList.getTotal(); i++) { // looping to find the skater that we would like to add scores for
                System.out.println(i + ": " + skaterList.getSkater(i).getName().toUpperCase());
            }

            do { // getting the users choice for the skater
                System.out.print("Please select a number between 1-" + skaterList.getTotal() + ": ");
                skaterSelection = sc.nextInt();
            } while (skaterSelection > skaterList.getTotal() || skaterSelection < 1);

            int skaterIn = skaterSelection; // storing the selection

            if (!contestList.isEmpty()) { // if the list is empty then we cannot add scores
                System.out.println("For which contest would you like to add " // printing choices
                        + skaterList.getSkater(skaterSelection).getName() + "'s scores?");

                for (int i = 1; i <= contestList.getTotal(); i++) { // gathering user input
                    System.out.println(i + ": " + contestList.getContest(i).getLocation().toUpperCase());
                }

                do { // getting the users choice, will loop until a valid number is chosen
                    System.out.print("Please select a number between 1-" + contestList.getTotal() + ": ");
                    contestSelection = sc.nextInt();
                } while (contestSelection > contestList.getTotal() || contestSelection < 1);

                int contestIn = contestSelection; // storing the selection

                Double[] scores = new Double[7]; // creating an array that can accept 7 doubles
                System.out.println("Enter the 7 scores (2 run scores and 5 best trick scores) for: " + skaterList.getSkater(skaterIn).getName() + " in contest " + contestList.getContest(contestIn).getLocation() + ":");
                double score; // creating an empty double to save a score
                for (int i = 0; i < 7; i++) { // looping for the length of the array (always 7)
                    do { // do while to check the score entered is between 0 and 100
                        System.out.print("Enter score " + (i + 1) + ": ");
                        score = sc.nextDouble();
                    } while (score < 0 || score > 100);
                    
                    scores[i] = score; // add each score to the array
                }

                skaterList.getSkater(skaterIn).addScores(contestList.getContest(contestIn), scores); // add the scores the skater for the contest

            } else { // if there are no contests in the list
                System.out.println("No contests currently in list!");
            }

        } else { // if there are no skaters in the list
            System.out.println("No skaters currently in list!");
        }


    }

//      OPTION 4
    static void viewContestStandings(ContestList contestList, SkaterList skaterList, SkaterScore skaterScore) {

        if (!contestList.isEmpty()) { // if the list is not empty
            System.out.println("Current SLS tour standings:");

            for (int i = 1; i <= contestList.getTotal(); i++) { // for loop to print the current standings for each contest
                System.out.println();
                System.out.println("Contest information: " + contestList.getContest(i).getLocation().toUpperCase() + ": " + contestList.getContest(i).getDate());

                double highScore = 0; // creating an empty double to store the highest score

                if (!skaterScore.hasScores()) { // if the skater has scores

                    for (int j = 1; j <= skaterList.getTotal(); j++) { // looping through for the total number of skaters in the list
                        double skaterContestScore = skaterList.getSkater(j).getTotalScore(contestList.getContest(i)); // getting the total score for each skater in the list
                        System.out.println(skaterList.getSkater(j).getName().toUpperCase() + ": " + skaterList.getSkater(j).getTotalScore((contestList.getContest(i)))); // printing their score

                        if (skaterContestScore > highScore) { // if the score is higher than the current high score, update highscore
                            highScore = skaterContestScore;
                        }
                    }
                }

                System.out.println("The high score of this contest was: " + highScore); // printing the highest score in the contest
            }
        } else { // if there are no contests in the list then
            System.out.println("No contests currently in list!");
        }
    }

//      OPTION 5
    static void viewOneContestStandings(ContestList contestList, SkaterList skaterList, SkaterScore skaterScore) {
        Scanner sc = new Scanner(System.in); // similar to the previous but will prompt the user to choose one specific contest they would like to see the scores of
        // useful for clarity if there are a lot of contests in the list
        int choice;

        if (!contestList.isEmpty()) { // if the list is not empty
            System.out.println("Which contest would you like to view the standings for?");

            for (int i = 1; i <= contestList.getTotal(); i++) { // prints a numbered list of the contests
                System.out.println(i + ": " + contestList.getContest(i).getLocation().toUpperCase());
            }

            do { // do while to get a selection for the contest the user would like to see the scores for
                System.out.print("Please insert a number between 1-" + contestList.getTotal() + ": ");
                choice = sc.nextInt();
            } while (choice > contestList.getTotal() || choice < 1); // can only be a choice between 1 and the length of the list

            System.out.println("Contest information: " + contestList.getContest(choice).getLocation().toUpperCase()); // prints the information for that contest
            if (!skaterScore.hasScores()) { // if the skater has scores
                for (int i = 1; i <= skaterList.getTotal(); i++) {
                    System.out.println(skaterList.getSkater(i).getName().toUpperCase() + ": " + Arrays.toString(skaterList.getSkater(i).getScores(contestList.getContest(choice))));
                }
            } else { // if there are no scores for this contest
                System.out.println("No skaters currently have scores for this contest!");
            }

        } else { // if there are no contests in the list
            System.out.println("No contests currently in list!");
        }
    }

//      OPTION 6
    static void viewAllContestInfo(ContestList contestList) { // view the info of the contest (only location and date atm) would like to add a 'winner' functionality later on
        if (!contestList.isEmpty()) { // if the contest list is not empty continue
            System.out.println("Current SLS tour contest information:");

            for (int i = 1; i <= contestList.getTotal(); i++) { // printing all the contests
                System.out.println("Stop " + i + ": " + contestList.getContest(i));
            }
        } else { // if there are no contests in the list
            System.out.println("No contests currently in list!");
        }
    }

//      OPTION 7
    static void viewOneContestInfo(ContestList contestList) { // similar to the last but only shows the information for one contest chosen by the user
        Scanner sc = new Scanner(System.in);
        int choice;

        if (!contestList.isEmpty()) { // if the list is not empty continue
            System.out.println("Which contest would you like to view the information of?");

            for (int i = 1; i <= contestList.getTotal(); i++) { // printing all the contests in the list
                System.out.println(i + ": " + contestList.getContest(i).getLocation().toUpperCase());
            }

            do { // gathering the choice from the user with input validation
                System.out.print("Please choose a number between 1-" + contestList.getTotal() + ": ");
                choice = sc.nextInt();
            } while (choice > contestList.getTotal() || choice < 1);

            System.out.println(contestList.getContest(choice)); // printing the contest of their choosing

        } else { // if there are no contests in the list then...
            System.out.println("No contests currently in list!");
        }
    }

//      OPTION 8
    static void viewAllAthleteInfo(SkaterList skaterList) { // display all the athletes information
        if (!skaterList.isEmpty()) { // if the list is not empty continue
            System.out.println("Current SLS tour athlete information:");

            for (int i = 1; i <= skaterList.getTotal(); i++) { // looping through for the length of the skaterList and printing their information
                System.out.println("Name: " + skaterList.getSkater(i).getName() + ", Stance: " + skaterList.getSkater(i).getStance() +
                        ", Nationality: " + skaterList.getSkater(i).getNationality() + ", Gender: " + skaterList.getSkater(i).getGender());
            }
        } else { // if there are no skaters in the list
            System.out.println("No athletes currently in list!");
        }
    }

//      OPTION 9
    static void viewOneAthleteInfo(SkaterList skaterList) { // similar to the previous but the user can choose which skater they would like to view the info of
        Scanner sc = new Scanner(System.in);
        int choice;

        if (!skaterList.isEmpty()) { // if the list is not empty continue
            System.out.println("Which skater would you like to view the information of?");

            for (int i = 1; i <= skaterList.getTotal(); i++) { // printing all the skaters names
                System.out.println(i + ": " + skaterList.getSkater(i).getName().toUpperCase());
            }

            do { // gathering the users input with some input validation
                System.out.print("Please choose a number between 1-" + skaterList.getTotal() + ": ");
                choice = sc.nextInt();
            } while (choice > skaterList.getTotal() || choice < 1);

            // printing the skater of their choice
            System.out.println("Name: " + skaterList.getSkater(choice).getName() + ", Stance: " + skaterList.getSkater(choice).getStance() +
                    ", Nationality: " + skaterList.getSkater(choice).getNationality() + ", Gender: " + skaterList.getSkater(choice).getGender());

        } else { // if there are no skaters in the list
            System.out.println("No skater's currently in list!");
        }
    }

//      OPTION 10
    static void deleteOneContest(ContestList contestList) { // removing a chosen contest from the list
        Scanner sc = new Scanner(System.in);
        int choice;

        if (!contestList.isEmpty()) { // if the list is not empty continue
            System.out.println("Which contest would you like to delete?");

            System.out.println("1: Return to main menu"); // in case the user does not want to delete a contest

            for (int i = 1; i <= contestList.getTotal(); i++) { // looping for the length of the contestList and printing the names of each
                System.out.println((i + 1) + ": " + contestList.getContest(i).getLocation().toUpperCase() + ", "
                        + contestList.getContest(i).getDate());
            }

            do { // gathering the users choice with some input validation
                System.out.print("Please choose a number between 1-" + (contestList.getTotal() + 1) + ": ");
                choice = sc.nextInt();
            } while (choice > (contestList.getTotal() + 1) || choice < 1);

            if (choice != 1) { // if they want to remove a contest
                contestList.removeContest(choice - 2);
            }

            // selecting 1 will do nothing except continue back to the main menu

        } else { // if there are no contests in the list
            System.out.println("No contests currently in list!");
        }
    }

//      OPTION 11
    static void deleteOneSkater(SkaterList skaterList) { // same functionality as the deleteOneContest method but for skaters
        Scanner sc = new Scanner(System.in);
        int choice;

        if (!skaterList.isEmpty()) {
            System.out.println("Which skater would you like to delete?");

            System.out.println("1: Return to main menu");

            for (int i = 1; i <= skaterList.getTotal(); i++) {
                System.out.println((i + 1) + ": " + skaterList.getSkater(i).getName().toUpperCase());
            }

            do {
                System.out.print("Please choose a number between 1-" + (skaterList.getTotal() + 1) + ": ");
                choice = sc.nextInt();
            } while (choice > (skaterList.getTotal() + 1) || choice < 1);

            if (choice != 1) {
                skaterList.removeSkater(choice - 2);
            }

        } else {
            System.out.println("No skaters currently in list!");
        }
    }
}
