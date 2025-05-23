import axios from "axios";

const API_BASE = "http://localhost:8090/api/tables";

export const getAllTables = async () => {
    const res = await axios.get(`${API_BASE}`);
    return res.data;
};

export const getAvailableTables = async () => {
    const res = await axios.get(`${API_BASE}/available`);
    return res.data;
};


export const occupyTable = async (tableId) => {
    const res = await axios.put(`${API_BASE}/${tableId}/occupy`);
    return res.data;
};
