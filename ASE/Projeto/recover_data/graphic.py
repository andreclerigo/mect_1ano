import serial
import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import time
from datetime import datetime


# Configure serial port
serial_port = '/dev/ttyUSB0'
baud_rate = 115200
timeout = 1

# Read data from the serial port
ser = serial.Serial(serial_port, baud_rate, timeout=timeout)

# Initialize data lists
temperature_data = []
humidity_data = []
gas_data = []

i = 1
dump = False

try:
    while True:
        line = ser.readline().decode('utf-8').strip()

        if line:
            print(line)
            if "Data read from memory" in line:
                line_data = line.split(" ")
                # print(line_data)

                if i % 3 != 0:
                    if int(line_data[-1]) <= 40 and int(line_data[-1]) > 10:
                        if i % 2 == 0:
                            temperature_data.append(int(line_data[-1]))
                        else:
                            humidity_data.append(int(line_data[-1]))
                else:
                    gas_data.append(int(line_data[-1]) * 40)
                i += 1
                dump = True
            else:
                if dump:
                    break

    # Plot the temperature_data values
    # the timestamp is equal to current time - 3 seconds * (number of samples - index of sample)
    current_time = time.time()
    # PROBLEM: THIS SHOULD BE THE INPUT OF THE LAST TIME THAT THE ROVER WAS ACTIVE (CAN WRITE OVER AND OVER ON EEPROM BECAUSE IT WILL WEAR IT OUT)

    # Calculate the timestamps for temperature data and format them
    temperature_timestamps = [datetime.fromtimestamp(current_time - 3 * (len(temperature_data) - i)).strftime('%H:%M:%S') for i in range(len(temperature_data))]

    # Create a figure with three subplots
    fig, (ax1, ax2, ax3) = plt.subplots(3, 1)

    # Plot the temperature_data values against the timestamps in the first subplot
    ax1.plot(temperature_timestamps, temperature_data)
    ax1.set_xlabel("Time")
    ax1.set_ylabel("Temperature (ºC)")
    ax1.set_title("Temperature over Time")  # Add a title to the subplot
    ax1.set_xticks(range(0, len(temperature_timestamps), 3))  # Set x-axis tick positions at every 3rd index
    ax1.set_xlim(0, len(temperature_timestamps))  # Adjust the x-axis limits
    ax1.tick_params(axis='x', rotation=45)

    # Calculate the timestamps for humidity data and format them
    humidity_timestamps = [datetime.fromtimestamp(current_time - 3 * (len(humidity_data) - i)).strftime('%H:%M:%S') for i in range(len(humidity_data))]

    # Plot the humidity_data values against the timestamps in the second subplot
    ax2.plot(humidity_timestamps, humidity_data)
    ax2.set_xlabel("Time")
    ax2.set_ylabel("Humidity (%)")
    ax2.set_title("Humidity over Time")  # Add a title to the subplot
    ax2.set_xticks(range(0, len(humidity_timestamps), 3))  # Set x-axis tick positions at every 3rd index
    ax2.set_xlim(0, len(humidity_timestamps))  # Adjust the x-axis limits
    ax2.tick_params(axis='x', rotation=45)

    gas_timestamps = [datetime.fromtimestamp(current_time - 3 * (len(gas_data) - i)).strftime('%H:%M:%S') for i in range(len(gas_data))]

    # Plot the gas values against the timestamps in the second subplot
    ax3.plot(gas_timestamps, gas_data)
    ax3.set_xlabel("Time")
    ax3.set_ylabel("Gas (ppm)")
    ax3.set_title("Gas over Time")  # Add a title to the subplot
    ax3.set_xticks(range(0, len(gas_timestamps), 3))  # Set x-axis tick positions at every 3rd index
    ax3.set_xlim(0, len(gas_timestamps))  # Adjust the x-axis limits
    ax3.tick_params(axis='x', rotation=45)

    # Adjust the spacing between subplots
    plt.tight_layout()

    plt.show()  # Display the plot with both subplots
except KeyboardInterrupt:
    # Close the serial port when user stops the script
    ser.close()
    print("\nSerial port closed.")
