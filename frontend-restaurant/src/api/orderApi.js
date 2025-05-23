import axios from 'axios';

const BASE_URL = 'http://localhost:8090/api';

export const postOrder = async (orderPayload) => {
    try {
        const response = await axios.post(`${BASE_URL}/orders`, orderPayload);
        return response.data;
    } catch (error) {
        console.error('postOrder error:', error);
        throw error;
    }
};
