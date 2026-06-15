import React, { useState } from 'react';
import { createReport } from '../services/api';

function CreateReport() {
  const [form, setForm] = useState({
    title: '',
    description: '',
    disasterType: 'EARTHQUAKE',
    severity: 'MEDIUM',
    latitude: '',
    longitude: '',
    reporterName: '',
    reporterContact: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');
    try {
      const payload = {
        ...form,
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude)
      };
      await createReport(payload);
      setMessage('Report submitted successfully!');
      setForm({
        title: '', description: '', disasterType: 'EARTHQUAKE',
        severity: 'MEDIUM', latitude: '', longitude: '',
        reporterName: '', reporterContact: ''
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit report');
    }
  };

  const useMyLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setForm({
            ...form,
            latitude: pos.coords.latitude.toFixed(6),
            longitude: pos.coords.longitude.toFixed(6)
          });
        },
        () => setError('Unable to get your location')
      );
    }
  };

  return (
    <div>
      <h1 className="page-title">Report a Disaster</h1>

      {message && <div style={{padding: '1rem', background: '#e8f5e9', borderRadius: 8, marginBottom: '1rem', color: '#2e7d32'}}>{message}</div>}
      {error && <div style={{padding: '1rem', background: '#ffebee', borderRadius: 8, marginBottom: '1rem', color: '#c62828'}}>{error}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Title *</label>
            <input name="title" value={form.title} onChange={handleChange} required placeholder="Brief description of the incident" />
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea name="description" value={form.description} onChange={handleChange} placeholder="Detailed description of what happened..." />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Disaster Type *</label>
              <select name="disasterType" value={form.disasterType} onChange={handleChange}>
                {['EARTHQUAKE','FLOOD','FIRE','CYCLONE','LANDSLIDE','TSUNAMI','DROUGHT','EPIDEMIC','INDUSTRIAL_ACCIDENT','OTHER']
                  .map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Severity *</label>
              <select name="severity" value={form.severity} onChange={handleChange}>
                {['LOW','MEDIUM','HIGH','CRITICAL'].map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Latitude *</label>
              <input name="latitude" type="number" step="any" value={form.latitude} onChange={handleChange} required placeholder="e.g. 28.6139" />
            </div>
            <div className="form-group">
              <label>Longitude *</label>
              <input name="longitude" type="number" step="any" value={form.longitude} onChange={handleChange} required placeholder="e.g. 77.2090" />
            </div>
          </div>

          <button type="button" className="btn btn-secondary" onClick={useMyLocation} style={{marginBottom: '1rem'}}>
            Use My Location
          </button>

          <div className="form-row">
            <div className="form-group">
              <label>Your Name</label>
              <input name="reporterName" value={form.reporterName} onChange={handleChange} placeholder="Optional" />
            </div>
            <div className="form-group">
              <label>Contact</label>
              <input name="reporterContact" value={form.reporterContact} onChange={handleChange} placeholder="Phone or email (optional)" />
            </div>
          </div>

          <button type="submit" className="btn btn-primary" style={{marginTop: '1rem', width: '100%', padding: '0.8rem'}}>
            Submit Report
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreateReport;
