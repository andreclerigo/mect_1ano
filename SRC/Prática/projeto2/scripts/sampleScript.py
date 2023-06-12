# pip install pygeoip
# pip install fastparquet 
# pip install dnspython
import pandas as pd
import numpy as np
import ipaddress
import dns.resolver
import dns.reversename
import pygeoip
import matplotlib.pyplot as plt 

datafile='data0.parquet'

### IP geolocalization
gi=pygeoip.GeoIP('./GeoIP.dat')
gi2=pygeoip.GeoIP('./GeoIPASNum.dat')
addr='193.136.73.21'
cc=gi.country_code_by_addr(addr)
org=gi2.org_by_addr(addr)
print(cc,org)

### DNS resolution
addr=dns.resolver.resolve("www.ua.pt", 'A')
for a in addr:
    print(a)
    
### Reverse DNS resolution    
name=dns.reversename.from_address("193.136.172.20")
addr=dns.resolver.resolve(name, 'PTR')
for a in addr:
    print(a)

### Read parquet data files
data=pd.read_parquet(datafile)
#print(data.to_string())

#Just the UDP flows
udpF=data.loc[data['proto']=='udp']

#Number of UDP flows for each source IP
nudpF=data.loc[data['proto']=='udp'].groupby(['src_ip'])['up_bytes'].count()

#Number of UDP flows to port 443, for each source IP
nudpF443=data.loc[(data['proto']=='udp')&(data['port']==443)].groupby(['src_ip'])['up_bytes'].count()

#Average number of downloaded bytes, per flow, for each source IP
avgUp=data.groupby(['src_ip'])['down_bytes'].mean()


#Total uploaded bytes to destination port 443, for each source IP, ordered from larger amount to lowest amount
upS=data.loc[((data['port']==443))].groupby(['src_ip'])['up_bytes'].sum().sort_values(ascending=False)

#Histogram of the total uploaded bytes to destination port 443, by source IP
upS=data.loc[((data['port']==443))].groupby(['src_ip'])['up_bytes'].sum().hist()
plt.show()

#Is destination IPv4 a public address?
NET=ipaddress.IPv4Network('192.168.100.0/24')
bpublic=data.apply(lambda x: ipaddress.IPv4Address(x['dst_ip']) not in NET,axis=1)

#Geolocalization of public destination adddress
cc=data[bpublic]['dst_ip'].apply(lambda y:gi.country_code_by_addr(y)).to_frame(name='cc')




######################### OTHER STUFF EXPLORED / TO EXPLORE ################################

# Anomalous behavior detection, description, and possible causes

# Define data file path
testfile='./../dataset1/test1.parquet'

# Read parquet data file
test=pd.read_parquet(testfile)

# Calculate IQR for uploaded and downloaded bytes
Q1_upload = test['up_bytes'].quantile(0.25)
Q3_upload = test['up_bytes'].quantile(0.75)
IQR_upload = Q3_upload - Q1_upload

Q1_download = test['down_bytes'].quantile(0.25)
Q3_download = test['down_bytes'].quantile(0.75)
IQR_download = Q3_download - Q1_download

# Define thresholds for outliers
threshold_upload_upper = Q3_upload + 1.5 * IQR_upload
threshold_upload_lower = Q1_upload - 1.5 * IQR_upload
threshold_download_upper = Q3_download + 1.5 * IQR_download
threshold_download_lower = Q1_download - 1.5 * IQR_download

# Identify anomalous behavior
anomalies_upload = test[(test['up_bytes'] > threshold_upload_upper) | (test['up_bytes'] < threshold_upload_lower)]
anomalies_download = test[(test['down_bytes'] > threshold_download_upper) | (test['down_bytes'] < threshold_download_lower)]

print("Anomalous upload activity:\n", anomalies_upload)
print("Anomalous download activity:\n", anomalies_download)

# Plot histograms for uploaded and downloaded bytes
fig, axs = plt.subplots(2, 1, figsize=(10, 10))

# Uploaded bytes histogram
test['up_bytes'].hist(bins=100, ax=axs[0])
axs[0].set_title('Uploaded Bytes Histogram')

# Downloaded bytes histogram
test['down_bytes'].hist(bins=100, ax=axs[1])
axs[1].set_title('Downloaded Bytes Histogram')

plt.tight_layout()
plt.show()

# Define a function to check for outliers based on the mean and standard deviation
def detect_outlier(series, multiplier = 3.0):
    mean = series.mean()
    std = series.std()
    
    # Outliers are defined as values that are 'multiplier' standard deviations away from the mean
    lower_threshold = mean - multiplier * std
    upper_threshold = mean + multiplier * std
    
    # Return a boolean series where True indicates an outlier
    return ((series < lower_threshold) | (series > upper_threshold))

# Apply the function to the uploaded and downloaded bytes
outliers_upload = detect_outlier(test['up_bytes'])
outliers_download = detect_outlier(test['down_bytes'])

print("Anomalous upload activity:\n", test[outliers_upload])
print("Anomalous download activity:\n", test[outliers_download])

# Import necessary libraries
import pandas as pd
import numpy as np
import ipaddress
import dns.resolver
import dns.reversename
import pygeoip
import matplotlib.pyplot as plt 

# Define data file path
datafile='./../dataset1/data1.parquet'

# IP geolocalization setup
gi=pygeoip.GeoIP('./../GeoIP_DBs/GeoIP.dat')
gi2=pygeoip.GeoIP('./../GeoIP_DBs/GeoIPASNum.dat')

# Function to get country from IP
def get_country(ip):
    try:
        return gi.country_name_by_addr(ip)
    except:
        return None

# Read parquet data file
data=pd.read_parquet(datafile)

# Add country information
data['src_country'] = data['src_ip'].apply(get_country)
data['dst_country'] = data['dst_ip'].apply(get_country)

# Make sure timestamp column is datetime
# Assuming 'timestamp' column represents 1/100 of seconds from 0h of the day
# Convert hundredths of a second to microseconds (1/100 sec = 10,000 µs)
data['timestamp'] = pd.to_timedelta(data['timestamp'] * 10000, unit='us')

# Convert timestamp to datetime, setting a start date
data['timestamp'] = pd.Timestamp("2023-01-01") + data['timestamp']

# Get general statistics of the data
general_stats = data.describe(include='all')
print("General Statistics:\n", general_stats)

# Analyze typical behavior of network devices
# ... (Rest of your code) ...

# Traffic by timestamp
hourly_data = data.groupby(data['timestamp'].dt.floor('H')).size()
half_hour_data = data.groupby(data['timestamp'].dt.floor('30T')).size()

# Print and visualize the findings
# ... (Rest of your code) ...

print("Hourly Traffic: \n", hourly_data)
print("Half-Hourly Traffic: \n", half_hour_data)

# Traffic over time
hourly_data.plot(kind='line', ax=axs[2, 1])
axs[2, 1].set_title('Connections over Hour')

half_hour_data.plot(kind='line', ax=axs[3, 1])
axs[3, 1].set_title('Connections over Half-Hour')

plt.tight_layout()
plt.show()

# SIEM rules --> define them


