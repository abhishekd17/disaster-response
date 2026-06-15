import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet';
import { getRecentReports } from '../services/api';
import 'leaflet/dist/leaflet.css';

const SEVERITY_COLORS = {
  LOW: '#4caf50',
  MEDIUM: '#ff9800',
  HIGH: '#f44336',
  CRITICAL: '#9c27b0'
};

const SEVERITY_RADIUS = {
  LOW: 6,
  MEDIUM: 8,
  HIGH: 10,
  CRITICAL: 13
};

function MapView() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getRecentReports(168)
      .then(res => setReports(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Loading map data...</div>;

  return (
    <div className="map-container">
      <MapContainer
        center={[22.5, 78.9]}
        zoom={5}
        style={{ height: '100%', width: '100%' }}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {reports.map(report => (
          <CircleMarker
            key={report.id}
            center={[report.latitude, report.longitude]}
            radius={SEVERITY_RADIUS[report.severity]}
            pathOptions={{
              color: SEVERITY_COLORS[report.severity],
              fillColor: SEVERITY_COLORS[report.severity],
              fillOpacity: 0.6
            }}
          >
            <Popup>
              <div>
                <strong>{report.title}</strong>
                <br />
                <span className={`badge badge-${report.severity.toLowerCase()}`}>
                  {report.severity}
                </span>
                {' '}
                <span className={`badge badge-${report.status.toLowerCase()}`}>
                  {report.status}
                </span>
                <br />
                <small>{report.city}, {report.country}</small>
                <br />
                <small>{report.disasterType} | {new Date(report.createdAt).toLocaleString()}</small>
                <br />
                <small>Upvotes: {report.upvotes} {report.verified && '| Verified'}</small>
              </div>
            </Popup>
          </CircleMarker>
        ))}
      </MapContainer>
    </div>
  );
}

export default MapView;
