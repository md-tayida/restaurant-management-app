// src/api/helloApi.js
import axios from "axios";

const BASE_URL = "http://localhost:8090/api";

export const getHelloMessage = async () => {
    const response = await axios.get(`${BASE_URL}/hello`);

    console.log(response.data);
    return response.data;
};
