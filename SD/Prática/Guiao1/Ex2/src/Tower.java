/**
 * General description:
 * the class will create a Tower with a name and a stack of disks.
 *
 * @author André Clérigo
 * @version 1.0 - 16/2/2023
 */
class Tower {
    private final String name;
    private final Stack disks;

    /**
     * General description:
     * the method will create a Tower with a name and a stack of disks.
     * @param name
     */
    public Tower(String name) {
        this.name = name;
        this.disks = new Stack();
    }

    /**
     * General description:
     * the method will return the name of the Tower.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * General description:
     * the method will return the size of the stack of disks.
     * @param disk
     */
    public void push(int disk) {
        disks.push(disk);
    }

    /**
     * General description:
     * the method will return the size of the stack of disks.
     * @return
     */
    public int pop() {
        return disks.pop();
    }

    /**
     * General description:
     * the method will return the graphical representation of the Tower.
     * @return
     */
    // Graphical representation of the tower
    public String toString() {
        if (disks.size() == 0) {
            return "Empty";
        } else {
            // Iterate through the disks in the tower
            StringBuilder str = new StringBuilder();
            str.append('\n');
            for (int i = 0; i < disks.size(); i++) {
                // Get the disk at the current index
                int disk = disks.get(i);

                // Add the number of empty spaces required
                str.append(" ".repeat(disks.size() - disk));

                // Add the disk to the string
                str.append("_".repeat(disk));

                // Add the tower
                str.append("|");

                // Add the disk to the string
                str.append("_".repeat(disk));

                str.append("\n");
            }
            // Remove the last newline
            str.deleteCharAt(str.length() - 1);

            return str.toString();
        }
    }
}
