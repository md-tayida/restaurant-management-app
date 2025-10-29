import apiClient from './apiClient';

export const fetchTables = async () => {
    const response = await apiClient.get('/tables');
    return response.data;
};

export const fetchTableById = async (id) => {
    const response = await apiClient.get(`/tables/${id}`);
    return response.data;
};

export const createTable = async (tableData) => {
    const response = await apiClient.post('/tables', tableData);
    return response.data;
};

export const updateTableStatus = async (id, tableData) => {
    const response = await apiClient.patch(`/tables/${id}`, tableData);
    return response.data;
};

export const deleteTable = async (id) => {
    await apiClient.delete(`/tables/${id}`);
};
