import React, { useEffect, useState } from 'react';
import axios from 'axios';

const ReadyToServePage = () => {
    const [orderItems, setOrderItems] = useState([]);

    useEffect(() => {
        fetchItems();
    }, []);

    const fetchItems = () => {
        axios.get('http://localhost:8090/api/orderItems-status/ready-to-serve')
            .then(res => setOrderItems(res.data))
            .catch(err => console.error(err));
    };

    const handleServe = async (itemId) => {
        try {
            await axios.patch(`http://localhost:8090/api/orderItems-status/ready-to-serve/${itemId}`);
            // ลบรายการที่เสิร์ฟแล้วออก
            setOrderItems(prev => prev.filter(item => item.id !== itemId));
        } catch (error) {
            alert('ไม่สามารถอัปเดตสถานะการเสิร์ฟได้');
            console.error(error);
        }
    };

    return (
        <div style={{ padding: '30px', fontFamily: 'sans-serif' }}>
            <h2>🍽️ รายการอาหารพร้อมเสิร์ฟ</h2>

            {orderItems.length === 0 ? (
                <p>ไม่มีรายการอาหารที่พร้อมเสิร์ฟ</p>
            ) : (
                orderItems.map(item => (
                    <div key={item.id} style={cardStyle}>
                        <div style={rowStyle}>
                            <div>
                                <p><strong>🧾 โต๊ะ:</strong> {item.tableNumber}</p>
                                <p><strong>🍛 เมนู:</strong> {item.menuName}</p>
                                <p><strong>จำนวน:</strong> {item.quantity}</p>
                                <p><small>📦 เวลาเตรียม: {new Date(item.createTime).toLocaleTimeString()}</small></p>
                            </div>
                            <button onClick={() => handleServe(item.id)} style={serveButtonStyle}>
                                ✅ เสิร์ฟเรียบร้อย
                            </button>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
};

// 🔧 Styles
const cardStyle = {
    border: '1px solid #ccc',
    borderRadius: '10px',
    padding: '15px',
    marginBottom: '15px',
    background: '#eafaf1'
};

const rowStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
};

const serveButtonStyle = {
    backgroundColor: '#2ecc71',
    color: 'white',
    padding: '10px 16px',
    borderRadius: '8px',
    border: 'none',
    cursor: 'pointer',
    fontWeight: 'bold'
};

export default ReadyToServePage;
