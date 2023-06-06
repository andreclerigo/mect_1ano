import React, { useState, useEffect, useCallback } from 'react';
import ControlPanel from './ControlPanel';
import Dashboard from './Dashboard';
import Title from './Title';
import Camera from './Camera';
import './App.css';

function App() {
  const [ipAddress, setIpAddress] = useState('');
  const [camIpAddress, setCamIpAddress] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const [isTrying, setIsTrying] = useState(false);

  const pingServer = useCallback(async () => {
    setIsTrying(true);
    try {
      await fetch(`http://${ipAddress}/ping`, {
        method: 'HEAD',
        mode: 'no-cors',
      });

      console.log(`Ping to ${ipAddress}`);
      setIsConnected(true);
    } catch (error) {
      console.log(`Could not reach ${ipAddress}`);
      setIsConnected(false);
    } finally {
      setIsTrying(false);
    }
  }, [ipAddress]);


  useEffect(() => {
    const pingInterval = setInterval(pingServer, 30000);

    return () => {
      clearInterval(pingInterval);
    };
  }, [pingServer]);

  return (
    <div className="App">
      <Title 
        ipAddress={ipAddress} 
        setIpAddress={setIpAddress}
        camIpAddress={camIpAddress}
        setCamIpAddress={setCamIpAddress}
        isConnected={isConnected} 
        isTrying={isTrying} 
        pingServer={pingServer}
      />
      <div className="layout">
        <ControlPanel 
          isConnected={isConnected}
          ipAddress={ipAddress}
        />
        <Camera camIpAddress={camIpAddress}/>
      </div>
      <Dashboard />
    </div>
  );
}

export default App;
