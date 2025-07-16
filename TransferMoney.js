import React, { useState } from 'react';
import axios from 'axios';
import './TransferMoney.css';

function TransferMoney() {
  const [fromAccountId, setFromAccountId] = useState(localStorage.getItem('userId') || '');
  const [toAccountId, setToAccountId] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [fromCurrency, setFromCurrency] = useState('INR');
  const [toCurrency, setToCurrency] = useState('INR');
  const [message, setMessage] = useState('');

  const handleTransfer = async (e) => {
    e.preventDefault();

    if (fromAccountId === toAccountId) {
      setMessage("❌ Cannot transfer to the same account.");
      return;
    }

    if (!fromAccountId || !toAccountId || !amount || !fromCurrency || !toCurrency) {
      setMessage("❌ Please fill all fields.");
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/accounts/transfer", {
        fromAccountId,
        toAccountId,
        amount: parseFloat(amount),
        description,
        fromCurrency,
        toCurrency
      });

      setMessage("✅ Transfer successful!");
      setToAccountId('');
      setAmount('');
      setDescription('');
    } catch (error) {
      console.error("Transfer failed:", error);

      const errorMsg =
        error.response?.data?.message || // From ResponseStatusException
        error.response?.data ||          // Fallback to raw string
        error.message ||                 // General error
        "❌ Unknown error occurred";

      setMessage("❌ " + errorMsg);
    }
  };

  return (
    <div className="transfer-container">
      <h2>Transfer Money</h2>
      <form onSubmit={handleTransfer}>
        <div>
          <label>From Account ID</label>
          <input type="text" value={fromAccountId} readOnly />
        </div>
        <div>
          <label>To Account ID</label>
          <input
            type="text"
            value={toAccountId}
            onChange={e => setToAccountId(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Amount</label>
          <input
            type="number"
            step="0.01"
            value={amount}
            onChange={e => setAmount(e.target.value)}
            required
          />
        </div>
        <div>
          <label>From Currency</label>
          <select value={fromCurrency} onChange={e => setFromCurrency(e.target.value)} required>
            <option value="INR">INR</option>
            
          </select>
        </div>
        <div>
          <label>To Currency</label>
          <select value={toCurrency} onChange={e => setToCurrency(e.target.value)} required>
            <option value="INR">INR</option>
            <option value="USD">USD</option>
            <option value="EUR">EUR</option>
          </select>
        </div>
        <div>
          <label>Description (Required)</label>
          <input
            type="text"
            value={description}
            onChange={e => setDescription(e.target.value)}
          />
        </div>
        <button type="submit">Transfer</button>
      </form>
      {message && <p className="message">{message}</p>}
    </div>
  );
}

export default TransferMoney;
