import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './TransactionHistory.css';

function TransactionHistory() {
  const [transactions, setTransactions] = useState([]);
  const [filtered, setFiltered] = useState([]);

  const [type, setType] = useState('');
  const [minAmount, setMinAmount] = useState('');
  const [maxAmount, setMaxAmount] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const userId = localStorage.getItem('userId');

  const currencySymbols = {
    INR: '₹',
    USD: '$',
    EUR: '€',
    GBP: '£',
    JPY: '¥',
    AUD: 'A$',
    CAD: 'C$'
  };

  const fetchTransactions = () => {
    if (!userId) return;

    const params = {
      type: type || undefined,
      minAmt: minAmount || undefined,
      maxAmt: maxAmount || undefined,
      fromDate: startDate || undefined,
      toDate: endDate || undefined
    };

    axios
      .get(`http://localhost:8080/accounts/${userId}/transactions`, { params })
      .then((res) => {
        setTransactions(res.data);
        setFiltered(res.data);
      })
      .catch((err) => {
        console.error('Failed to fetch transactions:', err);
        alert('Could not load transaction history.');
      });
  };

  useEffect(() => {
    fetchTransactions();
  }, [userId]);

  const applyFilter = () => {
    fetchTransactions();
  };

  const formatAmount = (amount, currency) => {
    const symbol = currencySymbols[currency] || currency;
    return `${symbol} ${amount.toFixed(2)}`;
  };

  return (
    <div className="transaction-history">
      <h2>Transaction History</h2>

      <div className="filters">
        <select value={type} onChange={(e) => setType(e.target.value)}>
          <option value="">All</option>
          <option value="Credit">Credit</option>
          <option value="Debit">Debit</option>
        </select>

        <input
          type="number"
          placeholder="Min Amount"
          value={minAmount}
          onChange={(e) => setMinAmount(e.target.value)}
        />
        <input
          type="number"
          placeholder="Max Amount"
          value={maxAmount}
          onChange={(e) => setMaxAmount(e.target.value)}
        />

        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
        />
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />

        <button onClick={applyFilter}>Apply Filters</button>
      </div>

      <table className="transaction-table">
        <thead>
          <tr>
            <th>Txn ID</th>
            <th>Date & Time</th>
            <th>Type</th>
            <th>Amount</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>
          {filtered.length > 0 ? (
            filtered.map((txn, idx) => (
              <tr key={idx}>
                <td>{txn.transactionId}</td>
                <td>
                  {new Date(txn.dateOfTransaction).toLocaleString('en-IN', {
                    timeZone: 'Asia/Kolkata',
                    dateStyle: 'medium',
                    timeStyle: 'medium',
                    hour12: true
                  })}
                </td>
                <td>{txn.transactionType}</td>
                <td>{formatAmount(txn.amount, txn.currencyCode)}</td>
                <td>{txn.description}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="5">No transactions found</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default TransactionHistory;
