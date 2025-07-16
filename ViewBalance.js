import React, { useEffect, useState } from 'react';
import axios from 'axios';

function ViewBalance() {
  const [balance, setBalance] = useState(null);
  const [accountInfo, setAccountInfo] = useState({});
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    if (userId) {
      axios.get(`http://localhost:8080/accounts/${userId}`)
        .then(res => {
          setAccountInfo(res.data);
          setBalance(res.data.balance);
        })
        .catch(err => {
          console.error(err);
          alert("Account not found or server error");
        });
    }
  }, [userId]);

  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <h2>Account Balance</h2>
      {balance !== null ? (
        <div>
          <p><strong>Account ID:</strong> {accountInfo.accountId}</p>
          <p><strong>IFSC:</strong> {accountInfo.ifsc}</p>
          <p><strong>Account Type:</strong> {accountInfo.accountType}</p>
          <h3>₹ {balance.toFixed(2)}</h3>
        </div>
      ) : (
        <p>Loading balance...</p>
      )}
    </div>
  );
}

export default ViewBalance;
