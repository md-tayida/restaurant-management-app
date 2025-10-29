import apiClient from './apiClient';

export const fetchActiveOrders = async () => {
    const response = await apiClient.get(`/kitchen-management`);
    return response.data;
};