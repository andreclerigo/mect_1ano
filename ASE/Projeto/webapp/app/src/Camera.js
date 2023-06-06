import React from 'react';
import './Camera.css';

const Camera = ({camIpAddress}) => {
    return (
        <div className="camera-panel">
            <h2 style={{marginRight: '14%'}}>ESP32 Camera &#40;{`http://${camIpAddress}/`}&#41;</h2>
            <div className="camera">
                <iframe
                    src={`http://${camIpAddress}/`}
                    title="Camera Video"
                    width="100%"
                    height="100%"
                    frameBorder="0"
                ></iframe>
            </div>
        </div>
    );
};

export default Camera;
