import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || '';

const api = axios.create({
  baseURL: `${API_BASE}/api/v1`,
  headers: { 'Content-Type': 'application/json' }
});

export const getReports = (page = 0, size = 20, type, severity) => {
  const params = { page, size };
  if (type) params.type = type;
  if (severity) params.severity = severity;
  return api.get('/reports', { params });
};

export const getReportById = (id) => api.get(`/reports/${id}`);

export const createReport = (report) => api.post('/reports', report);

export const searchNearby = (lat, lng, radiusKm, disasterType, minSeverity) => {
  const body = { latitude: lat, longitude: lng, radiusKm };
  if (disasterType) body.disasterType = disasterType;
  if (minSeverity) body.minSeverity = minSeverity;
  return api.post('/reports/search/nearby', body);
};

export const getRecentReports = (hours = 24) => api.get(`/reports/recent?hours=${hours}`);

export const updateStatus = (id, status) => api.patch(`/reports/${id}/status?status=${status}`);

export const upvoteReport = (id) => api.post(`/reports/${id}/upvote`);

export const getStats = () => api.get('/reports/stats');

export default api;
