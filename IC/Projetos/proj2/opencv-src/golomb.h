// Golomb class
#include <fstream>
#include <vector>
#include <string>
#include <iostream>
#include <bitset>
#include <cmath>

// C++ can only write bytes not bits. So we need to write a byte at a time.
class Golomb {
    private:
        int min_bits;
        int max_bits;
        int n_values_with_min_bits;

        // min_bits and values with min_bits
        void calculateBits(int m) {
            if (m != 0){
                // calculate max_bits: log2(m) with ceiling to integer
                max_bits = ceil(log2(m));
                // calculate min_bits: max_bits - 1
                min_bits = max_bits - 1;
                // calculate n_values_with_min_bits: (2^max_bits) - m
                n_values_with_min_bits = pow(2, max_bits) - m;
            } else {
                // if m is 0, then the values are 0
                max_bits = 0;
                min_bits = 0;
                n_values_with_min_bits = 0;
            }
        }

        // function that converts integer to string of bits with n bits of representation
        std::string remaindersBitString(int num, int n_bits_representation){
            std::string result = "";
            for (int i = 0; i < n_bits_representation; i++) {
                result = std::to_string(num % 2) + result;
                num /= 2;
            }

            return result;
        }

        int bitStringToInt(std::string bit_string) {
            int result = 0;
            for (long unsigned int i = 0; i < bit_string.length(); i++)
                result = result * 2 + (bit_string[i] - '0');

            return result;
        }

    public:
        // constructor
        Golomb() { }

        //decode function for a fixed M value
        std::vector<int> decode(std::string encoded_string, int m) {
            calculateBits(m);
            std::vector<int> result;
            // bit position in the encoded string
            int i = 0;

            // loop through the encoded string
            while((long unsigned int) i < encoded_string.length()) {
                int quotient = 0;
                // count the number of 0s in the encoded string to get the quotient (written in unary code)
                while (encoded_string[i] == '0') {
                    quotient++;
                    // next bit
                    i++;
                }
                // skip a bit (the 1 in the unary code, that represents the end of the quotient)
                i++;
                // initialize the remainder 
                int remainder = 0;
                // counter for the number of bits read from the encoded string in the remainder part (binary code)
                int j = 0;
                // temporary string to store the remainder
                std::string tmp = "";
                
                // if m is 1, the remainder is 0
                // besides that if m is 0, it's forced as 1 (to avoid division by 0)
                if (m != 1){
                    // while the number of bits of the remainder doesn't reach min_bits
                    while (j < min_bits) {
                        // add the bit to the temporary string (to get the remainder)
                        tmp += encoded_string[i];
                        // next bit
                        i++;
                        // next bit of the remainder part (binary code)
                        j++;
                    }

                    // convert the temporary string to integer to get the remainder
                    int res1 = bitStringToInt(tmp);

                    // if the remainder has a value that is greater than the number of values with min_bits (which corresponds to the max value with min_bits)
                    // the next bit must be read as part of the remainder
                    // if the value is smaller, the remainder is the value read so far
                    if (res1 < n_values_with_min_bits) {
                        remainder = res1;
                    } else {
                        tmp += encoded_string[i];
                        i++;
                        // convert the temporary string to integer to get the remainder
                        remainder = bitStringToInt(tmp) - n_values_with_min_bits;
                    }
                } else {
                    remainder = 0;
                    i++;
                }

                // result value without sign
                int res = quotient * m + remainder;

                // if the encoded string has a 1 in the end of the remainder, the result is negative, otherwise it's positive
                if (encoded_string[i] == '1') {
                    result.push_back(-(res));
                } else {
                    result.push_back(res);
                }

                i++;
            }

            return result;
        }

        //decode function for dynamic Ms (changing Ms)
        std::vector<int> decodeMultiple(std::string encoded_string, std::vector<int> m_vector, int block_size) {
            std::vector<int> result;
            // bit position in the encoded string
            int i = 0;
            // m index on the vector for the current block
            int m_i = 0;
            // blocksize bits counter
            int count = 0;
            calculateBits(m_vector[m_i]);
            while((long unsigned int) i < encoded_string.length()) {
                    int quotient = 0;
                    // count the number of 0s in the encoded string to get the quotient (written in unary code)
                    while (encoded_string[i] == '0') {
                        quotient++;
                        // next bit
                        i++;
                    }
                    // skip a bit (the 1 in the unary code, that represents the end of the quotient)
                    i++;
                    int remainder = 0;
                    // counter for the number of bits read from the encoded string in the remainder part (binary code)
                    int j = 0;
                    std::string tmp = "";

                    // if m is 1, the remainder is 0;
                    // besides that if m is 0, it's forced as 1 (to avoid division by 0)
                    if (m_vector[m_i] != 1){
                        while (j < min_bits) {
                            tmp += encoded_string[i];
                            // next bit
                            i++;
                            // next bit of the remainder part (binary code)
                            j++;
                        }
                        
                        // convert the temporary string to integer to get the remainder
                        int res1 = bitStringToInt(tmp);

                        // if the remainder has a value that is greater than the number of values with min_bits (which corresponds to the max value with min_bits)
                        // the next bit must be read as part of the remainder
                        // if the value is smaller, the remainder is the value read so far
                        if (res1 < n_values_with_min_bits) {
                            remainder = res1;
                        } else {
                            tmp += encoded_string[i];
                            // next bit
                            i++;
                            remainder = bitStringToInt(tmp) - n_values_with_min_bits;
                        }
                    } else {
                        remainder = 0;
                        // next bit
                        i++;
                    }

                    // result value without sign
                    int res = quotient * m_vector[m_i] + remainder;

                    // if the encoded string has a 1 in the end of the remainder, the result is negative, otherwise it's positive
                    if (encoded_string[i] == '1') {
                        result.push_back(-(res));
                    } else {
                        result.push_back(res);
                    }

                    // next bit
                    i++;

                    count++;
                    // if the block size is reached, the m index is incremented
                    if (count == block_size) {
                        // next m index
                        m_i++;
                        // reset blocksize bits counter
                        count = 0;
                        calculateBits(m_vector[m_i]);
                    }  
                }
            return result;
        }

        std::string encode(int num, int m){
            calculateBits(m);
            // result string
            std::string result = "";
            int quotient = 0;
            int remainder = 0;
            // if m isn't 0, calculate the quotient and the remainder with golomb coding
            if (m != 0){
                quotient = abs(num) / m;
                remainder = abs(num) % m;
            }
            // concatenate the quotient in unary code 
            for (int i = 0; i < quotient; i++) {
                result += "0";
            }

            // use a bit (1) to represent the end of the quotient and the begginning of the remainder
            result += "1";

            // if m is 1, the remainder is 0, otherwise calculate the remainder in binary code
            if (m != 1){
                if (remainder < n_values_with_min_bits) {
                    result += remaindersBitString(remainder, min_bits);
                } else {
                    result += remaindersBitString(remainder + n_values_with_min_bits, max_bits);
                }
            }else{
                result += "0";
            }

            // if the number is negative, add a 1 at the end of the remainder to indicate the sign
            num < 0 ? result += "1" : result += "0";


            return result;
        }
};
