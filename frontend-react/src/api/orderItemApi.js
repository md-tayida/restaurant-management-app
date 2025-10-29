import apiClient from './apiClient';

export const updateOrderItemStatus = async (id, statusData) => {
    const response = await apiClient.patch(`/order-items/${id}/flow`, statusData);
    return response.data;
};
