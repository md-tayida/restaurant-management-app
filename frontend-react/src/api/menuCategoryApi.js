import apiClient from './apiClient';

export const fetchMenuCategories = async () => {
    const response = await apiClient.get('/menu-categories');
    return response.data;
};

export const createMenuCategory = async (categoryData) => {
    const response = await apiClient.post('/menu-categories', categoryData);
    return response.data;
};

export const deleteMenuCategory = async (categoryId) => {
    await apiClient.delete(`/menu-categories/${categoryId}`);
};
