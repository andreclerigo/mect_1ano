import { Joystick } from 'react-joystick-component';
import React, { useState, useEffect } from 'react';
import './ControlPanel.css';

function ControlPanel({ isConnected, ipAddress }) {
  const [joystickSize, setJoystickSize] = useState(400);
  const [direction, setDirection] = useState(null);

  useEffect(() => {
    const handleResize = () => {
      // Adjust the size based on the screen width
      if (window.innerWidth <= 768) {
        setJoystickSize(300); // Adjust the size to your desired size for mobile
      } else {
        setJoystickSize(420); // Adjust the size to your desired size for desktop
      }
    };

    // Add event listener for window resize
    window.addEventListener('resize', handleResize);

    // Call handleResize initially
    handleResize();

    // Cleanup the event listener on component unmount
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  const [lastCommandTime, setLastCommandTime] = useState(0);

  const sendCommand = async (command) => {
    const currentTime = Date.now();
    // Only send the command if at least 500ms have passed since the last command
    if (isConnected && currentTime - lastCommandTime >= 500) {
      if (isConnected) {
        try {
          await fetch(`http://${ipAddress}/control?`, {
            method: 'POST',
            headers: {
              'Content-Type': 'text/plain'
            },
            body: command  // Send the direction as a key in the body
          });
          console.log(`Sent ${command} command to ${ipAddress}`);
          setLastCommandTime(currentTime);
        } catch (error) {
          console.log(`Could not send command to ${ipAddress}`);
        }
      }
    }
  };
  
  const handleMove = (joystick) => {
    let newDirection = '';
    if (joystick.type === 'move') {
      newDirection = (joystick.direction).toLowerCase();
    } 

    if (newDirection !== direction) {
      setDirection(newDirection);
      sendCommand(newDirection);
    }
  };

  const handleStop = () => {
    if (direction !== 'stop') {
      setDirection('stop');
      sendCommand('stop');
    }

    console.log('Stopped moving joystick');
  };

  return (
    <div className="control-panel">
      <div className="joystick-container">
        <Joystick
          size={joystickSize}
          baseColor="lightgray"
          stickColor="darkgray"
          throttle={100}
          move={handleMove}
          stop={handleStop}
          disabled={!isConnected}
        />
      </div>
    </div>
  );
}

export default ControlPanel;
