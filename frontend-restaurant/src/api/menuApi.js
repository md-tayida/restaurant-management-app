import axios from 'axios';

const BASE_URL = 'http://localhost:8090/api';

export const fetchMenus = async () => {
    try {
        const response = await axios.get(`${BASE_URL}/menus`);
        return response.data;  // ข้อมูลเมนู
    } catch (error) {
        console.error('fetchMenus error:', error);
        throw error;
    }
};
