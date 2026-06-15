import React, { useEffect, useState } from 'react';
import { getReports, upvoteReport } from '../services/api';

function ReportsList() {
  const [reports, setReports] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [typeFilter, setTypeFilter] = useState('');
  const [severityFilter, setSeverityFilter] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getReports(page, 15, typeFilter || undefined, severityFilter || undefined)
      .then(res => {
        setReports(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page, typeFilter, severityFilter]);

  const handleUpvote = async (id) => {
    const res = await upvoteReport(id);
    setReports(prev => prev.map(r => r.id === id ? res.data : r));
  };

  return (
    <div>
      <h1 className="page-title">Disaster Reports</h1>

      <div className="filters">
        <select value={typeFilter} onChange={e => { setTypeFilter(e.target.value); setPage(0); }}>
          <option value="">All Types</option>
          {['EARTHQUAKE','FLOOD','FIRE','CYCLONE','LANDSLIDE','TSUNAMI','DROUGHT','EPIDEMIC','INDUSTRIAL_ACCIDENT','OTHER']
            .map(t => <option key={t} value={t}>{t}</option>)}
        </select>
        <select value={severityFilter} onChange={e => { setSeverityFilter(e.target.value); setPage(0); }}>
          <option value="">All Severities</option>
          {['LOW','MEDIUM','HIGH','CRITICAL'].map(s => <option key={s} value={s}>{s}</option>)}
        </select>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading">Loading...</div>
        ) : (
          <table className="reports-table">
            <thead>
              <tr>
                <th>Title</th>
                <th>Type</th>
                <th>Severity</th>
                <th>Location</th>
                <th>Status</th>
                <th>Upvotes</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              {reports.map(r => (
                <tr key={r.id}>
                  <td><strong>{r.title}</strong></td>
                  <td>{r.disasterType}</td>
                  <td><span className={`badge badge-${r.severity.toLowerCase()}`}>{r.severity}</span></td>
                  <td>{r.city}</td>
                  <td><span className={`badge badge-${r.status.toLowerCase()}`}>{r.status}</span></td>
                  <td>
                    <button className="btn btn-secondary" onClick={() => handleUpvote(r.id)}>
                      {r.upvotes}
                    </button>
                  </td>
                  <td>{new Date(r.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="pagination">
        <button className="btn btn-secondary" disabled={page === 0} onClick={() => setPage(p => p - 1)}>
          Previous
        </button>
        <span style={{padding: '0.6rem 1rem'}}>Page {page + 1} of {totalPages}</span>
        <button className="btn btn-secondary" disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>
          Next
        </button>
      </div>
    </div>
  );
}

export default ReportsList;
