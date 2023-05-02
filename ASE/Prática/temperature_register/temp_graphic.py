# import serial

# # Configure serial port
# serial_port = '/dev/ttyUSB0'
# baud_rate = 115200
# timeout = 1

# # Read data from the serial port
# ser = serial.Serial(serial_port, baud_rate, timeout=timeout)

# try:
#     while True:
#         line = ser.readline().decode('utf-8').strip()
#         if line:
#             print(line)
#             if "Data read from memory" in line:
#                 data = line.split(" ")
#                 print(data[5] + " ºC")
# except KeyboardInterrupt:
#     # Close the serial port when user stops the script
#     ser.close()
#     print("\nSerial port closed.")
import serial
import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import time

# Configure serial port
serial_port = '/dev/ttyUSB0'
baud_rate = 115200
timeout = 1

# Read data from the serial port
ser = serial.Serial(serial_port, baud_rate, timeout=timeout)

# Initialize data lists
temperatures = []
dump = False

try:
    while True:
        line = ser.readline().decode('utf-8').strip()

        if line:
            print(line)
            if "Data read from memory" in line:
                data = line.split(" ")
                temperature = float(data[5])
                # print(f"{temperature} ºC")

                # Append temperature to the list
                temperatures.append(temperature)
                dump = True
            else:
                if dump:
                    break

    # Plot the temperature values
    plt.plot(range(0, len(temperatures) * 5, 5), temperatures)
    plt.xlabel("Time (s)")
    plt.ylabel("Temperature (ºC)")
    plt.show()

except KeyboardInterrupt:
    # Close the serial port when user stops the script
    ser.close()
    print("\nSerial port closed.")
