import React from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import MapView from './pages/MapView';
import ReportsList from './pages/ReportsList';
import CreateReport from './pages/CreateReport';
import Dashboard from './pages/Dashboard';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <nav className="navbar">
          <div className="nav-brand">Disaster Response Platform</div>
          <div className="nav-links">
            <NavLink to="/" end>Map</NavLink>
            <NavLink to="/reports">Reports</NavLink>
            <NavLink to="/create">Report Incident</NavLink>
            <NavLink to="/dashboard">Dashboard</NavLink>
          </div>
        </nav>
        <main className="main-content">
          <Routes>
            <Route path="/" element={<MapView />} />
            <Route path="/reports" element={<ReportsList />} />
            <Route path="/create" element={<CreateReport />} />
            <Route path="/dashboard" element={<Dashboard />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
