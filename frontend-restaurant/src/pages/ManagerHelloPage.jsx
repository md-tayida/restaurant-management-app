import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function ManagerHelloPage() {
    const [message, setMessage] = useState('');

    useEffect(() => {
        axios.get('http://localhost:8090/api/manager/hello')
            .then(res => {
                setMessage(res.data);
            })
            .catch(err => {
                setMessage('Error fetching data');
            });
    }, []);

    return (
        <div className="p-8">
            <h1 className="text-xl font-bold mb-4">Manager Hello Page</h1>
            <p>{message}</p>
        </div>
    );
}
