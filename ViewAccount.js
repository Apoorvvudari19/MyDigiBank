import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './ViewAccount.css'; // Make sure you import the CSS

function ViewAccount() {
  const [account, setAccount] = useState(null);
  const accountId = localStorage.getItem('userId');

  useEffect(() => {
    axios.get(`http://localhost:8080/accounts/${accountId}`)
      .then(res => {
        setAccount(res.data);
      })
      .catch(err => {
        console.error(err);
        alert('Failed to load account details.');
      });
  }, [accountId]);

  if (!account) return <div className="view-loading">Loading account details...</div>;

  return (
    <div className="view-account-container">
      <div className="account-info-box">
        <h2>Account Details</h2>
        <p><strong>Account Holder Name:</strong> {account.accountHolderName}</p>
        <p><strong>Account ID:</strong> {account.accountId}</p>
        <p><strong>Email:</strong> {account.email}</p>
        <p><strong>Phone Number:</strong> {account.phoneNumber}</p>
        <p><strong>Account Type:</strong> {account.accountType}</p>
        <p><strong>Balance:</strong> ₹ {account.balance.toFixed(2)}</p>
        <p><strong>Status:</strong> {account.status}</p>
        <p><strong>IFSC:</strong> {account.ifsc}</p>
      </div>
    </div>
  );
}

export default ViewAccount;
