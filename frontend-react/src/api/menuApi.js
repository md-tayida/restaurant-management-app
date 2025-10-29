import apiClient from './apiClient';

export const fetchMenus = async () => {
    const response = await apiClient.get('/menus');
    return response.data;
};

export const fetchMenusByCategory = async (categoryId) => {
    const response = await apiClient.get(`/menus/category/${categoryId}`);
    return response.data;
};

export const fetchMenuById = async (id) => {
    const response = await apiClient.get(`/menus/${id}`);
    return response.data;
};

export const createMenu = async (menuData) => {
    const response = await apiClient.post('/menus', menuData);
    return response.data;
};

export const updateMenuStatus = async (id, menuData) => {
    const response = await apiClient.patch(`/menus/${id}`, menuData);
    return response.data;
};

export const deleteMenu = async (id) => {
    await apiClient.delete(`/menus/${id}`);
};
