import apiClient from './apiClient';

export const login = async (credentials) => {
    const response = await apiClient.post('/auth/login', credentials);
    return response.data;
};

export const register = async (userData) => {
    userData = { name, username, password, confirmPassword }
    await apiClient.post('/auth/register', userData);
};
