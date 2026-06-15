import React, { useState, useRef } from 'react';
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import { createReport } from '../services/api';
import 'leaflet/dist/leaflet.css';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

function LocationPicker({ position, setPosition }) {
  useMapEvents({
    click(e) {
      setPosition([e.latlng.lat, e.latlng.lng]);
    },
  });
  return position ? <Marker position={position} /> : null;
}

function CreateReport() {
  const [form, setForm] = useState({
    title: '',
    description: '',
    disasterType: 'EARTHQUAKE',
    severity: 'MEDIUM',
    reporterName: '',
    reporterContact: ''
  });
  const [position, setPosition] = useState(null);
  const [locationQuery, setLocationQuery] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [searching, setSearching] = useState(false);
  const mapRef = useRef(null);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const searchLocation = async () => {
    if (!locationQuery.trim()) return;
    setSearching(true);
    setError('');
    try {
      const res = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(locationQuery)}&limit=1`
      );
      const data = await res.json();
      if (data.length > 0) {
        const lat = parseFloat(data[0].lat);
        const lon = parseFloat(data[0].lon);
        setPosition([lat, lon]);
        if (mapRef.current) {
          mapRef.current.flyTo([lat, lon], 13);
        }
      } else {
        setError('Location not found. Try a different search term.');
      }
    } catch {
      setError('Failed to search location');
    }
    setSearching(false);
  };

  const useMyLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const lat = pos.coords.latitude;
          const lng = pos.coords.longitude;
          setPosition([lat, lng]);
          if (mapRef.current) {
            mapRef.current.flyTo([lat, lng], 13);
          }
        },
        () => setError('Unable to get your location. Please allow location access.')
      );
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    if (!position) {
      setError('Please select a location on the map, search for a place, or use "My Location"');
      return;
    }

    try {
      const payload = {
        ...form,
        latitude: position[0],
        longitude: position[1]
      };
      await createReport(payload);
      setMessage('Report submitted successfully!');
      setForm({
        title: '', description: '', disasterType: 'EARTHQUAKE',
        severity: 'MEDIUM', reporterName: '', reporterContact: ''
      });
      setPosition(null);
      setLocationQuery('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit report');
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

          <div className="form-group">
            <label>Location * (search or click on map)</label>
            <div style={{display: 'flex', gap: '0.5rem'}}>
              <input
                value={locationQuery}
                onChange={e => setLocationQuery(e.target.value)}
                placeholder="Search: e.g. Patna, Bihar or Koramangala, Bangalore"
                onKeyDown={e => e.key === 'Enter' && (e.preventDefault(), searchLocation())}
                style={{flex: 1}}
              />
              <button type="button" className="btn btn-secondary" onClick={searchLocation} disabled={searching}>
                {searching ? '...' : 'Search'}
              </button>
              <button type="button" className="btn btn-primary" onClick={useMyLocation}>
                My Location
              </button>
            </div>
            {position && (
              <small style={{color: '#666', marginTop: '0.3rem', display: 'block'}}>
                Selected: {position[0].toFixed(4)}, {position[1].toFixed(4)}
              </small>
            )}
          </div>

          <div style={{height: '300px', borderRadius: '8px', overflow: 'hidden', marginBottom: '1.2rem'}}>
            <MapContainer
              center={[22.5, 78.9]}
              zoom={5}
              style={{ height: '100%', width: '100%' }}
              ref={mapRef}
            >
              <TileLayer
                attribution='&copy; OpenStreetMap'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
              <LocationPicker position={position} setPosition={setPosition} />
            </MapContainer>
          </div>

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
