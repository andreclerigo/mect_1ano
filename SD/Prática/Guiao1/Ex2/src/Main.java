import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Get the number of disks from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of disks: ");
        int numDisks = scanner.nextInt();

        // Create the three towers as stacks
        Tower towerA = new Tower("A");
        Tower towerB = new Tower("B");
        Tower towerC = new Tower("C");

        // Add the disks to the first tower
        for (int i = numDisks; i > 0; i--) {
            towerA.push(i);
        }

        // Print the initial state of the towers
        printTowers(towerA, towerB, towerC);

        // Move the disks from tower A to tower C
        moveDisks(numDisks, towerA, towerB, towerC);

        // Print the final state of the towers
        printTowers(towerA, towerB, towerC);
    }

    // Recursively move the disks from the source tower to the destination tower
    private static void moveDisks(int numDisks, Tower src, Tower aux, Tower dst) {
        if (numDisks == 1) {
            // Move the top disk from the source tower to the destination tower
            int disk = src.pop();
            dst.push(disk);

            // Print the movement of the disk
            System.out.println("Move disk " + disk + " from Tower " + src.getName() + " to Tower " + dst.getName());
        } else {
            // Move the top n-1 disks from the source tower to the auxiliary tower
            moveDisks(numDisks - 1, src, dst, aux);

            // Move the bottom disk from the source tower to the destination tower
            int disk = src.pop();
            dst.push(disk);

            // Print the movement of the disk
            System.out.println("Move disk " + disk + " from Tower " + src.getName() + " to Tower " + dst.getName());

            // Move the n-1 disks from the auxiliary tower to the destination tower
            moveDisks(numDisks - 1, aux, src, dst);
        }
    }

    // Print the state of the three towers
    private static void printTowers(Tower towerA, Tower towerB, Tower towerC) {
        System.out.println();
        System.out.println("Tower A: " + towerA.toString());
        System.out.println("Tower B: " + towerB.toString());
        System.out.println("Tower C: " + towerC.toString());
        System.out.println();
    }
}
