import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { CircularProgressbarWithChildren } from 'react-circular-progressbar';
import { ResponsiveContainer } from 'recharts';
import 'react-circular-progressbar/dist/styles.css';
import './Dashboard.css';

function Dashboard() {
  const [data, setData] = useState([]);
  const [ppm, setPpm] = useState(0);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('http://localhost:5000/dht11', {
          method: 'GET'
        });
        let jsonData = await response.json();
        // Limit number of decimal places in data
        jsonData = jsonData.map(item => ({
          ...item,
          temperature: parseFloat(item.temperature.toFixed(1)),
          humidity: parseFloat(item.humidity.toFixed(1)),
        }));

        setData(jsonData.reverse());
      } catch (error) {
        console.error('Error fetching dht11 data:', error);
      }

      try {
        const response = await fetch('http://localhost:5000/mq5', {
          method: 'GET'
        });

        let jsonData = await response.json();

        if (!isNaN(jsonData[0].ppm)) {
          // convert float ppm to int
          setPpm(Math.round(jsonData[0].ppm));
        } else {
          setPpm(0);
        }
      } catch (error) {
        console.error('Error fetching ppm data:', error);
      }
    };

    // Fetch data immediately and then every 3 seconds
    fetchData();
    const intervalId = setInterval(fetchData, 3000); // 3000 ms = 3 s

    // Clear interval on component unmount
    return () => clearInterval(intervalId);
  }, []);

  return (
    <div className="dashboard-panel">
      <div className="dashboard-left">
        <ResponsiveContainer width="100%" height="60%">
        <LineChart data={data} margin={{ top: 5, right: 35, left: -10, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis 
              dataKey="timestamp"
              stroke="#c2c0c0"
              tick={{ fill: '#c2c0c0' }}
              strokeWidth={3}
              tickFormatter={(unixTime) => {
                const date = new Date(unixTime);
                return date.toLocaleTimeString(undefined, { hour12: false });
              }}
            />
            <YAxis 
              stroke="#c2c0c0"
              tick={{ fill: '#c2c0c0' }} strokeWidth={3}
              tickFormatter={(value) => value.toFixed(1)}
            />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="temperature" stroke="#6fbd8c" strokeWidth={2}/>
            <Line type="monotone" dataKey="humidity" stroke="#aba6f5" strokeWidth={2}/>
          </LineChart>
        </ResponsiveContainer>
      </div>
      <div className="dashboard">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
          <CircularProgressbarWithChildren 
            value={(ppm/10000)*100}
            styles={{
              path: {
                stroke: ppm <= 1000 ? '#6fbd8c' : '#D9534F',
              },
            }}>
            <div style={{ fontSize: '32px' }}>
              {ppm} ppm
            </div>
          </CircularProgressbarWithChildren>
          <div style={{ marginTop: '20px', fontSize: '32px'}}>LPG Gas</div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
