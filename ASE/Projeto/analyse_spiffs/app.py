import numpy as np
import serial
import json
import datetime
import matplotlib.dates as mdates
import matplotlib.pyplot as plt
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.ticker import MaxNLocator
import tkinter as tk
from tkinter import ttk
from tkinter import messagebox


# Configure serial port
serial_port = '/dev/ttyUSB0'
baud_rate = 115200
timeout = 1

# Read data from the serial port
ser = serial.Serial(serial_port, baud_rate, timeout=timeout)

# Initialize data lists
data = {
    "temp": {},
    "humid": {},
    "gas": {}
}

content = False
current_timestamp = ""
metric = ""
last_value = 0


class App:
    def __init__(self, master):
        self.master = master
        self.master.title('SPIFFS Data Analysis')

        # Frame for the Combobox
        frame = tk.Frame(master)
        frame.pack(fill=tk.X)

        # Combo box for selecting timestamp
        self.combo = ttk.Combobox(frame, width=30)  # increase the width as needed
        self.combo['values'] = list(data['temp'].keys())  # assuming all have same timestamps
        self.combo.pack(side=tk.TOP)  # center the Combobox
        self.combo.bind("<<ComboboxSelected>>", self.update_plot)

        # Download button
        self.download_button = ttk.Button(frame, text="Download", command=self.download_data, state='disabled')
        self.download_button.pack(side=tk.TOP, padx=10, ipadx=20, ipady=10)  # pad x-axis to create some space between combobox and button, and increase the size of the button

        # Create a FigureCanvasTkAgg object which is a Tkinter widget with a figure
        self.fig = Figure(dpi=100)
        self.canvas = FigureCanvasTkAgg(self.fig, master=master)
        self.canvas.get_tk_widget().pack(side=tk.BOTTOM, fill=tk.BOTH, expand=True)


    def update_plot(self, event):
        timestamp = self.combo.get()
        # Convert the timestamp to a datetime object
        start_time = datetime.datetime.strptime(timestamp, '%a %b %d %H:%M:%S %Y')
        
        # Create a list of datetime objects for each timestamp
        x_values = [start_time + datetime.timedelta(seconds=i*3) for i in range(len(data['temp'][timestamp]))]
        
        # clear previous plots
        self.fig.clear()
        
        ax1 = self.fig.add_subplot(311)  # 311 means 3 rows, 1 column, plot number 1
        ax1.plot(x_values, [float(i) for i in data['temp'][timestamp]], label='Temperature', color='red')
        ax1.set_ylabel('Celcius (Cº)')
        ax1.legend()

        ax2 = self.fig.add_subplot(312)  # 312 means 3 rows, 1 column, plot number 2
        ax2.plot(x_values, [float(i) for i in data['humid'][timestamp]], label='Humidity', color='blue')
        ax2.set_ylabel('Humidity (%)')
        ax2.legend()

        ax3 = self.fig.add_subplot(313)  # 313 means 3 rows, 1 column, plot number 3
        ax3.plot(x_values, [float(i) for i in data['gas'][timestamp]], label='Gas', color='purple')
        ax3.set_xlabel('Time (s)')
        ax3.set_ylabel('Gas (ppm)')
        ax3.legend()
        self.download_button['state'] = 'normal'  # enable the download button when a timestamp is selected

        # Format the x-axis
        date_format = mdates.DateFormatter('%H:%M:%S')
        ax1.xaxis.set_major_formatter(date_format)
        ax2.xaxis.set_major_formatter(date_format)
        ax3.xaxis.set_major_formatter(date_format)

        self.canvas.draw()


    def download_data(self):
        timestamp = self.combo.get()
        # Fetch data for the selected timestamp
        data_to_download = { 
            'temp': data['temp'].get(timestamp),
            'humid': data['humid'].get(timestamp),
            'gas': data['gas'].get(timestamp)
        }

        # Write data to JSON file
        timestamp_string = timestamp.replace(' ', '_')
        with open(f'data_{timestamp_string}.json', 'w') as f:
            json.dump(data_to_download, f, indent=4)  # indent=4 for pretty printing

        # Show a popup message
        messagebox.showinfo("Download completed", f"Data for {timestamp} downloaded!")


try:
    while True:
        line = ser.readline().decode('utf-8').strip()

        if line:
            print(line)
            if "SPIFFS unmounted successfully" in line:
                break
            if "Reading file" in line:
                content = False
                if "temp" in line:
                    metric = "temp"
                elif "hum" in line:
                    metric = "humid"
                elif "gas" in line:
                    metric = "gas"
                continue

            # if the string as timestamp format (Tue Jun 27 22:24:25 2023)
            if line[0].isalpha() and "ANALYSE_SPIFFS" not in line:
                data[metric][line] = []
                current_timestamp = line
                content = True
                continue
            
            if content:
                if metric == "temp" or metric == "humid":
                    if float(line) <= 5 or float(line) >= 100:
                        data[metric][current_timestamp].append(last_value)
                    else:
                        data[metric][current_timestamp].append(line)
                        last_value = line
                elif metric == "gas":
                    if float(line) >= 10000:
                        data[metric][current_timestamp].append("10000")
                    else:
                        data[metric][current_timestamp].append(line)
                        last_value = 10000
                    
    
    # print(data)
    root = tk.Tk()
    root.geometry('1920x1080')  # Optional: sets initial size of the window
    app = App(root)
    root.mainloop()

except KeyboardInterrupt:
    # Close the serial port when user stops the script
    ser.close()
    print("\nSerial port closed.")
