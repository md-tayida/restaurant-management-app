import apiClient from './apiClient';

export const createPayment = async (paymentData) => {
    const response = await apiClient.post('/payments', paymentData);
    return response.data;
};

export const updatePayment = async (id, paymentData) => {
    const response = await apiClient.patch(`/payments/${id}`, paymentData);
    return response.data;
};

export const getPaymentsByStatus = async (status) => {
    const response = await apiClient.get(`/payments`, {
        params: { status },
    });
    return response.data;

};
