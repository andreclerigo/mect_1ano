import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { ResponsiveContainer } from 'recharts';
import 'react-circular-progressbar/dist/styles.css';
import './Dashboard.css';

function Dashboard() {
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('http://localhost:5000/batman', {
          method: 'GET'
        });
        let jsonData = await response.json();

        setData(jsonData.reverse());
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    // Fetch data immediately and then every 500ms
    fetchData();
    const intervalId = setInterval(fetchData, 500); // 500 ms

    // Clear interval on component unmount
    return () => clearInterval(intervalId);
  }, []);

  return (
    <div className="dashboard-panel">
      <div className="dashboard">
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
              tickFormatter={(value) => value.toFixed(2)}
            />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="rtt_max" stroke="#F06543" strokeWidth={2}/>
            <Line type="monotone" dataKey="rtt_avg" stroke="#F49F0A" strokeWidth={2}/>
            <Line type="monotone" dataKey="rtt_min" stroke="#6fbd8c" strokeWidth={2}/>
          </LineChart>
        </ResponsiveContainer>
      </div>
      <div className="dashboard">
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
              tickFormatter={(value) => value.toFixed(2)}
            />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="jitter" stroke="#aba6f5" strokeWidth={2}/>
          </LineChart>
        </ResponsiveContainer>
      </div>
      <div className="dashboard">
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
              tickFormatter={(value) => value.toFixed(2)}
            />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="pdr" stroke="#aba6f5" strokeWidth={2}/>
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

export default Dashboard;
