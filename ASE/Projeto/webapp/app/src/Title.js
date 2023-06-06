import './Title.css';

function Title({ ipAddress, setIpAddress, camIpAddress, setCamIpAddress, isConnected, isTrying, pingServer }) {
  const handleInputChange = (e) => {
    setIpAddress(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    pingServer();
  };

  const handleCamIpChange = (e) => {
    setCamIpAddress(e.target.value);
  };

  return (
    <div className="title">
      <h1>ASE Rover</h1>

      <form onSubmit={handleSubmit} className="ip-input-row">
          <input
            type="text"
            placeholder='ESP32 IP'
            value={ipAddress}
            onChange={handleInputChange}
          />
        <button type="submit">Connect</button>
        <div className={`connection-status ${isTrying ? 'trying' : isConnected ? 'connected' : 'disconnected'}`}>
          {isTrying ? <div className="dot orange" /> : isConnected ? <div className="dot green" /> : <div className="dot red" />}
          {isTrying ? 'Trying' : isConnected ? 'Connected' : 'Disconnected'}
        </div>
      </form>

      <div className="ip-input-row">
        <input
          type="text"
          placeholder='ESP32 Cam IP'
          value={camIpAddress}
          onChange={handleCamIpChange}
        />
      </div>
    </div>
  );
}

export default Title;
