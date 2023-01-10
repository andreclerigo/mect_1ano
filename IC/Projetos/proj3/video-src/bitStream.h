#include <fstream>
#include <vector>
#include <string>
#include <iostream>
#include <bitset>

// C++ can only write bytes not bits. So we need to write a byte at a time.
class BitStream {
    private:
        std::fstream file;
        std::string fileMode;
        std::string fileName;
        int fileSize;
        //create a variable that holds the current array of 8 bits
        std::vector<int> bitArray;
        //create a variable that holds the current bit in the current array
        int currentBitPos;
        //create a variable that holds the current array in the byteArray
        int currentArrayPos;

    public:
        BitStream(std::string name, std::string mode) {
            fileName = name;
            fileMode = mode;

            if (mode == "r"){
                file.open(fileName, std::ios::in | std::ios::binary);
                currentBitPos = 0;
                fileSize = getFileSize();
            } else if (mode == "w") {
                //clear the file if it already exists, 
                //otherwise create a new file
                //append mode
                file.open(fileName, std::ios::out | std::ios::binary | std::ios::trunc);
                currentBitPos = 0;
                currentArrayPos = 0;
            } else {
                std::cout << "Invalid mode" << std::endl;
            }
        }

        int getFileSize() {
            //create a duplicate file stream
            std::fstream file2;
            file2.open(fileName, std::ios::in | std::ios::binary);
            file2.seekg(0, std::ios::end);
            int size = file2.tellg();
            file2.seekg(0, std::ios::beg);
            file2.close();

            return size;
        }

        std::vector<int> byteToBitArray(char byte){
            if (fileMode == "r") {
                //read the byte and convert it to a bit array 
                std::vector<int> bitArray;
                for (int i = 0; i < 8; i++) {
                    bitArray.push_back((byte >> i) & 1);
                }

                //reverse the bit array
                std::vector<int> reversedBitArray;
                for (int i = 7; i >= 0; i--) {
                    reversedBitArray.push_back(bitArray[i]);
                }

                return reversedBitArray;
            }

            std::vector<int> bitArray;
            for (int i = 0; i < 8; i++) {
                bitArray.push_back(byte & 1);
                byte >>= 1;
            }

            return bitArray;
        }

        char bitArrayToByte(std::vector<int> bitArray){
            char byte = 0;
            //invert the bit array
            std::vector<int> invertedBitArray;
            for (int i = 7; i >= 0; i--) {
                invertedBitArray.push_back(bitArray[i]);
            }

            for (int i = 0; i < 8; i++) {
                byte |= invertedBitArray[i] << i;
            }

            return byte;
        }

        char bitArrayToByte2(std::vector<int> bitArray){
            char byte = 0;
            for (int i = 0; i < 8; i++) {
                byte += bitArray[i] << (7-i);
            } 
            return byte;
        }

        std::vector<int> readBits(int n) {
            if (fileMode != "r") {
                std::cout << "File not open for reading" << std::endl;
                return std::vector<int>();
            }
            
            std::vector <int> outBits;

            char byte;
            int bitCount = 0;
            while (bitCount < n) {
                if (currentBitPos == 0) {
                    file.read(&byte, 1);
                    bitArray = byteToBitArray(byte);
                }

                outBits.push_back(bitArray[currentBitPos]);
                currentBitPos++;
                bitCount++;

                if (currentBitPos == 8) {
                    currentBitPos = 0;
                }
            }

            return outBits;
        }

        std::vector<int> readBits2(int n) {
            if (fileMode != "r") {
                std::cout << "File not open for reading" << std::endl;
                return std::vector<int>();
            }

            std::vector<int> outBits;

            char byte;
            int bitCount = 0;
            while (bitCount < n) {
                if (currentBitPos == 0) {
                    file.read(&byte, 1);

                    // Use bitwise operators to reverse the order of the bits within the byte
                    byte = ((byte & 0xF0) >> 4) | ((byte & 0x0F) << 4);
                    byte = ((byte & 0xCC) >> 2) | ((byte & 0x33) << 2);
                    byte = ((byte & 0xAA) >> 1) | ((byte & 0x55) << 1);
                }

                // Use a bitwise operator to extract the current bit from the byte
                int bit = (byte >> currentBitPos) & 1;
                outBits.push_back(bit);
                currentBitPos++;
                bitCount++;

                if (currentBitPos == 8) {
                    currentBitPos = 0;
                }
            }

            return outBits;
        }

        int readBit() {
            if (fileMode != "r") {
                std::cout << "File not open for reading" << std::endl;
                return -1;
            }

            if (currentBitPos == 0) {
                char byte;
                file.read(&byte, 1);
                bitArray = byteToBitArray(byte);
            }

            int bit = bitArray[currentBitPos];
            currentBitPos = (currentBitPos + 1) % 8;

            return bit;
        }

        void writeBits(std::vector<int> bits) {
            if (fileMode != "w"){
                std::cout << "File is not open for writing" << std::endl;
                return;
            }
            int n = bits.size();
            int bitCount = 0;
            while (n > 0) {
                if (currentBitPos == 8) {
                    char byte = bitArrayToByte2(bitArray);
                    file.write(&byte, 1);
                    currentBitPos = 0;
                }

                //if the current bit position is 0, then we need to create a new array
                if (currentBitPos == 0) {
                    bitArray = std::vector<int>(8);
                }
                //write the next bit to the current array
                bitArray[currentBitPos] = bits[bitCount];
                currentBitPos++;
                bitCount++;
                n--;
            }
        }

        void writeBit(int bit) {
            if (fileMode != "w") {
                std::cout << "File is not open for writing" << std::endl;
                return;
            }

            //if the current bit position is 8, then we need to change it to 0
            if (currentBitPos == 8) {
                //reset the current bit position
                char byte = bitArrayToByte(bitArray);
                //write the bitArray to the end of the file
                file.write(&byte, 1);
                currentBitPos = 0;
            }

            //if the current bit position is 0, then we need to create a new array
            if (currentBitPos == 0) {
                bitArray = std::vector<int>(8);
            }
            //write the next bit to the current array
            bitArray[currentBitPos] = bit;
            currentBitPos++;
        }

        void close() {
            //write the bitArray to the file
            char byte = bitArrayToByte(bitArray);
            file.write(&byte, 1);
            file.close();
        }
};
