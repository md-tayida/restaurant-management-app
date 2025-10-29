import apiClient from './apiClient';

export const changePassword = async (passwordData) => {
    await apiClient.patch('/users/change-password', passwordData);
};

export const deleteUser = async (id) => {
    await apiClient.delete(`/users/${id}`);
};
