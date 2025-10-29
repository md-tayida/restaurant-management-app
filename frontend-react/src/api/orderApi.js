import apiClient from './apiClient';

export const fetchOrders = async () => {
    const response = await apiClient.get('/orders');
    return response.data;
};

export const fetchOrderById = async (id) => {
    const response = await apiClient.get(`/orders/${id}`);
    return response.data;
};

export const fetchActiveOrderByTableId = async (id) => {
    const response = await apiClient.get(`/orders/table/${id}`);
    return response.data;
};

export const createOrder = async (orderData) => {
    const response = await apiClient.post('/orders', orderData);
    return response.data;
};

export const updateOrderStatus = async (id, statusData) => {
    const response = await apiClient.patch(`/orders/${id}`, statusData);
    return response.data;
};


export const fetchActiveOrders = async () => {
    const response = await apiClient.get(`/kitchen-management`);
    return response.data;
};
