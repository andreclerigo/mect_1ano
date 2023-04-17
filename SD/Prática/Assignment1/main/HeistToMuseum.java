package main;

import entities.*;
import sharedRegions.*;
import java.util.Scanner;
import java.io.File;

/**
 *   Simulation of the Heist to the museum problem.
 *   Static solution based on a posteriori reasoning to terminate the threads.
 */
public class HeistToMuseum
{
    /**
     *   Main method.
     *
     *     @param args runtime arguments
     */
    public static void main (String [] args)
    {
        OrdinaryThief [] ordinaryThief = new OrdinaryThief [SimulPar.ORDINARY_THIEVES];         // array of ordinary thieves threads
        MasterThief masterThief;                                                                // master thief thread
        AssaultParty [] assaultParties = new AssaultParty [SimulPar.ASSAULT_PARTIES_NUMBER];    // array of assault parties threads
        int [] paintingPerRoom = new int [SimulPar.ROOMS_NUMBER];                               // number of paintings in each room
        int [] distancePerRoom = new int [SimulPar.ROOMS_NUMBER];                               // distance between the room to the concentration site
        int [] agilityPerThief = new int [SimulPar.ORDINARY_THIEVES];                           // agility of each ordinary thief

        GeneralRepos repos;                                                                     // general repository of information
        Museum museum;                                                                          // museum
        ControlSite controlSite;                                                                // control site
        ConcetSite concetSite;                                                                  // concentration site

        String fileName;                                                                        // logging file name
        char opt;                                                                               // selected option
        boolean success;                                                                        // end of operation flag

        /* problem initialization */
        Scanner scanner = new Scanner(System.in);
        do { 
            System.out.println("Logging file name? ");
            fileName = scanner.nextLine();
            if (new File(fileName).exists()) {
                do {
                    System.out.println("There is already a file with this name. Delete it (y - yes; n - no)? ");
                    opt = scanner.nextLine().charAt(0);
                } while ((opt != 'y') && (opt != 'n'));

                success = (opt == 'y');
            } else {
                success = true;
            }
        } while (!success);

        // Setting the rooms attributes
        for (int i = 0; i < SimulPar.ROOMS_NUMBER; i++) {
            paintingPerRoom[i] = (int) (Math.random() * (SimulPar.MAX_PAINTINGS - SimulPar.MIN_PAINTINGS + 1)) + SimulPar.MIN_PAINTINGS;
            distancePerRoom[i] = (int) (Math.random() * (SimulPar.MAX_ROOM_DISTANCE - SimulPar.MIN_ROOM_DISTANCE + 1)) + SimulPar.MIN_ROOM_DISTANCE;
        }

        // Setting ordinary thieves agility
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            agilityPerThief[i] = (int) (Math.random() * (SimulPar.MAX_THIEVES_DISPLACEMENT - SimulPar.MIN_THIEVES_DISPLACEMENT + 1)) + SimulPar.MIN_THIEVES_DISPLACEMENT;
        }

        repos = new GeneralRepos (fileName, distancePerRoom, paintingPerRoom, agilityPerThief);

        for (int i = 0; i < SimulPar.ASSAULT_PARTIES_NUMBER; i++)
            assaultParties[i] = new AssaultParty(i, -1, repos);

        museum = new Museum(paintingPerRoom, distancePerRoom, assaultParties, repos);
        controlSite = new ControlSite(assaultParties, repos);
        concetSite = new ConcetSite(assaultParties, museum, repos);

        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            ordinaryThief[i] = new OrdinaryThief("Thief_" + (i + 1), i, agilityPerThief[i], museum, controlSite, concetSite, assaultParties);
        }

        masterThief = new MasterThief("MasterThief", controlSite, concetSite, assaultParties);

        /* start of the simulation */
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++)
            ordinaryThief[i].start ();
        
        masterThief.start ();

        /* waiting for the end of the simulation */
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            try {
                ordinaryThief[i].join ();
            } catch (InterruptedException ignored) {}

            System.out.println("The ordinary thief " + (i+1) + " has terminated.");
        }

        try {
            masterThief.join ();
        }
        catch (InterruptedException ignored) {}
        System.out.println("The master thief has terminated.");
    }
}
