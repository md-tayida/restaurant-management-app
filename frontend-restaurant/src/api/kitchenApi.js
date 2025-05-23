import axios from 'axios';

const API_BASE = 'http://localhost:8090/api/kitchen';

export const getReadyToServeItems = async () => {
    const res = await axios.get(`${API_BASE}/ready-to-serve`);
    return res.data;
};

export const markItemCompleted = async (itemId) => {
    await axios.put(`${API_BASE}/item/${itemId}/done`);
};