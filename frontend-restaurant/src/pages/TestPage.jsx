// src/pages/TestPage.jsx
import { useEffect, useState } from "react";
import { getHelloMessage } from "../api/testApi";

const TestPage = () => {
    const [message, setMessage] = useState("");

    useEffect(() => {
        getHelloMessage()
            .then(setMessage)
            .catch(err => console.error("Error fetching message:", err));
    }, []);

    return <h1>{message}</h1>;
};

export default TestPage;
