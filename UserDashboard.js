import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './UserDashboard.css';

function UserDashboard() {
  const [user, setUser] = useState(null);
  const userId = localStorage.getItem('userId');
  const navigate = useNavigate();

  useEffect(() => {
    if (userId) {
      axios.get(`http://localhost:8080/accounts/${userId}`)
        .then(res => setUser(res.data))
        .catch(err => {
          console.error("User fetch error:", err);
          alert("Failed to load user dashboard.");
        });
    }
  }, [userId]);

  const handleLogout = () => {
    localStorage.removeItem('userId');
    navigate('/login');
  };

  const navigateTo = (path) => {
    navigate(path);
  };

  if (!user) return <div className="loading">Loading dashboard...</div>;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2 className="title">User Dashboard</h2>
        <div className="header-buttons">
          <button onClick={() => navigateTo('/user/viewaccount')}>View Account</button>
          <button onClick={() => navigateTo('/user/viewbalance')}>View Balance</button>
          <button onClick={() => navigateTo('/user/transfer')}>Transfer Money</button>
          <button onClick={() => navigateTo('/user/transactions')}>Transaction History</button>
          <button className="logout-btn" onClick={handleLogout}>Logout</button>
        </div>
      </div>

      <div className="dashboard-content">
        <p><strong>Name:</strong> {user.accountHolderName}</p>
        <p><strong>Account ID:</strong> {user.accountId}</p>
        <p><strong>Account Type:</strong> {user.accountType}</p>
        
      </div>
    </div>
  );
}

export default UserDashboard;
