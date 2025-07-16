import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './UserLogin.css';

function UserLogin() {
  const [accountId, setAccountId] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    if (!accountId || !password) {
      alert('Please enter both Account ID and Password');
      return;
    }

    if (accountId === 'admin' && password === 'admin') {
      localStorage.setItem('userId', 'admin');
      navigate('/admin/dashboard');
    } else if (password === accountId) {
      localStorage.setItem('userId', accountId);
      navigate('/user/userdashboard');
    } else {
      alert('Invalid credentials. Password must match Account ID for users.');
    }
  };

  const handleForgotPassword = () => {
    alert('Please contact customer support to reset your password.');
  };

  return (
    <div>
      {/* 🔷 Top Header */}
      <header className="login-header">
        <h1>My DigiBank</h1>
      </header>

      <div className="login-container">
        <h2>Login</h2>
        <form onSubmit={handleLogin}>
          <label>
            Account ID:
            <input
              type="text"
              value={accountId}
              onChange={(e) => setAccountId(e.target.value)}
              required
            />
          </label>
          <br />
          <label>
            Password:
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>
          <br />
          <button type="submit">Login</button>
          <div className="forgot-password" onClick={handleForgotPassword}>
            Forgot Password?
          </div>
        </form>
      </div>
    </div>
  );
}

export default UserLogin;
