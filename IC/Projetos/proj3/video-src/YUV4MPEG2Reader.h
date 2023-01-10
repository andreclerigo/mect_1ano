#include <string>
#include <fstream>
#include <regex>
#include <iostream>

class YUV4MPEG2Reader {
public:
  // Constructs a YUV4MPEG2Reader that reads from the given file.
  // Throws an exception if the file cannot be opened.
  YUV4MPEG2Reader(const std::string& file_name)
    : file_(file_name, std::ios::in | std::ios::binary) {
        if (!file_.is_open()) {
            throw std::runtime_error("Cannot open file: " + file_name);
        }

        // Parse the header of the file to extract the width, height, and color space.
        std::string line;
        // read first line
        std::getline(file_, line);

        // extract width and height
        sscanf(line.c_str(), "YUV4MPEG2 W%d H%d F%d:%d", &width_, &height_, &frame_rate_1_, &frame_rate_2_);
        // check if 'C' is in the line
        if (strchr(line.c_str(), 'C') == NULL) {
            color_space_ = "420";
        } else {
            // extract only the color space started by 'C'
            color_space_ = line.substr(line.find('C') + 1);
            std::cout << "here" << std::endl;
        }
        // check if 'I' is in the line
        if (strchr(line.c_str(), 'I') == NULL) {
            interlace_ = "p";
        } else {
            // extract only the interlace started by 'I' with the next character
            interlace_ = line.substr(line.find('I') + 1, 1);
        }
        // check if 'A' is in the line
        if (strchr(line.c_str(), 'A') == NULL) {
            aspect_ratio_1_ = 1;
            aspect_ratio_2_ = 1;
        } else {
            // extract only the aspect ratio started by 'A'
            std::string aspect_ratio = line.substr(line.find('A') + 1);
            // extract the first number
            aspect_ratio_1_ = std::stoi(aspect_ratio.substr(0, aspect_ratio.find(':')));
            // extract the second number
            aspect_ratio_2_ = std::stoi(aspect_ratio.substr(aspect_ratio.find(':') + 1));
        }

        // count the number of frames
        num_frames_ = 0;
        while (std::getline(file_, line)) {
            if (line.find("FRAME") != std::string::npos) {
                num_frames_++;
            }
        }
    }

    // Returns the number of frames in the file.
    int numFrames() const {
        return num_frames_;
    }

    // Returns the width of the frame.
    int width() const {
        return width_;
    }

    // Returns the height of the frame.
    int height() const {
        return height_;
    }

    // Returns the color space defined in the header (parameter 'C').
    std::string colorSpace() const {
        return color_space_;
    }

    // Returns the aspect ratio
    int aspectRatio1() const {
        return aspect_ratio_1_;
    }

    int aspectRatio2() const {
        return aspect_ratio_2_;
    }


    // Returns the interpolation method defined in the header (parameter 'I').
    std::string interlacing() const {
        return interlace_;
    }

    //returns thre frame rate
    int frameRate1() const {
        return frame_rate_1_;
    }

    int frameRate2() const {
        return frame_rate_2_;
    }

private:
    std::ifstream file_;
    int width_;
    int height_;
    std::string interlace_;
    int aspect_ratio_1_;
    int aspect_ratio_2_;
    int frame_rate_1_;
    int frame_rate_2_;
    int num_frames_;
    std::string color_space_;
};
