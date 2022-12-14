#include <iostream>
#include "bitStream.h"

using namespace std;

int main (int argc, char *argv[]) {	
    //this program converts a binary file and converts it to a ".txt" file that contains only 1's and 0's using the bit_stream class
    //start a timer
    clock_t start = clock();

    if (argc < 3) {	
        cerr << "Usage: " << argv [0] << " <input file> <output file>\n" ;
        return 1 ;
    }

    //open the input file
    BitStream inputFile (argv [1], "r") ;
    
    //open the output file
    ofstream outputFile (argv [2], ios::out) ;
    if (! outputFile) {
        cerr << "Error: could not open output file " << argv [2] << ".\n" ;
        return 1 ;
    }

    //read the bits from the input file
    vector<int> bits;
    bits = inputFile.readBits(inputFile.getFileSize() * 8);
    inputFile.close();

    //write the bits to the output file
    for (int i = 0; i < bits.size(); i++){
        outputFile << bits[i];
    }
    outputFile.close();

    //end the timer
    clock_t end = clock();
    double elapsed_secs = double(end - start) / CLOCKS_PER_SEC;
    //convert the time to milliseconds
    elapsed_secs = elapsed_secs * 1000;
    cout << "Time: " << elapsed_secs << " ms" << endl;

    return 0;
}