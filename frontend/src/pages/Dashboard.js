import React, { useEffect, useState } from 'react';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { getStats } from '../services/api';

const COLORS = ['#e94560', '#f44336', '#ff9800', '#4caf50', '#2196f3', '#9c27b0', '#00bcd4', '#795548', '#607d8b', '#ffeb3b'];

function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getStats()
      .then(res => setStats(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Loading dashboard...</div>;
  if (!stats) return <div className="loading">Failed to load stats</div>;

  const typeData = (stats.byType || []).map(([name, value]) => ({ name, value }));
  const severityData = (stats.bySeverity || []).map(([name, value]) => ({ name, value }));

  return (
    <div>
      <h1 className="page-title">Dashboard</h1>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-value">{stats.totalReports}</div>
          <div className="stat-label">Total Reports</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{stats.reportsLast24h}</div>
          <div className="stat-label">Last 24 Hours</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{stats.activeIncidents}</div>
          <div className="stat-label">Active Incidents</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{stats.resolvedIncidents}</div>
          <div className="stat-label">Resolved</div>
        </div>
      </div>

      <div className="charts-grid">
        <div className="card">
          <h3 style={{marginBottom: '1rem'}}>Reports by Type</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie data={typeData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} label>
                {typeData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="card">
          <h3 style={{marginBottom: '1rem'}}>Active by Severity</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={severityData}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value" fill="#e94560" radius={[4, 4, 0, 0]}>
                {severityData.map((entry, i) => {
                  const colors = { LOW: '#4caf50', MEDIUM: '#ff9800', HIGH: '#f44336', CRITICAL: '#9c27b0' };
                  return <Cell key={i} fill={colors[entry.name] || '#e94560'} />;
                })}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
