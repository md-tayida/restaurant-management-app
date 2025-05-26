import React, { useEffect, useState } from 'react';
import axios from 'axios';

const KitchenDashboard = () => {
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('all');
    const [orderItems, setOrderItems] = useState([]);

    useEffect(() => {
        // โหลดหมวดหมู่เมนู
        axios.get('http://localhost:8090/api/menu-categories')
            .then(res => setCategories(res.data))
            .catch(err => console.error(err));
    }, []);

    useEffect(() => {
        // โหลด order items ตามหมวดหมู่ที่เลือก
        axios.get(`http://localhost:8090/api/orderItems-status/preparing/${selectedCategory}`)
            .then(res => setOrderItems(res.data))
            .catch(err => console.error(err));
    }, [selectedCategory]);

    const handleMarkReady = async (itemId) => {
        try {
            // PATCH ไปยัง backend ตาม format /preparing/{category}/{id}
            await axios.patch(`http://localhost:8090/api/orderItems-status/preparing/${selectedCategory}/${itemId}`);
            // ลบรายการที่ถูก mark ออกจากรายการใน UI
            setOrderItems(prev => prev.filter(item => item.id !== itemId));
        } catch (error) {
            alert('ไม่สามารถอัปเดตสถานะได้');
            console.error(error.response?.data || error.message);
        }
    };

    return (
        <div style={{ padding: '30px', fontFamily: 'sans-serif' }}>
            <h2>🍽️ รายการอาหารที่กำลังเตรียม</h2>

            {/* Menu bar */}
            <div style={{ display: 'flex', gap: '15px', marginBottom: '25px' }}>
                <button
                    onClick={() => setSelectedCategory('all')}
                    style={{ ...btnStyle, backgroundColor: selectedCategory === 'all' ? '#0984e3' : btnStyle.backgroundColor }}
                >
                    All
                </button>
                {categories.map(cat => (
                    <button
                        key={cat.id}
                        onClick={() => setSelectedCategory(cat.name)}
                        style={{ ...btnStyle, backgroundColor: selectedCategory === cat.name ? '#0984e3' : btnStyle.backgroundColor }}
                    >
                        {cat.name}
                    </button>
                ))}
            </div>

            {/* Order Items */}
            {orderItems.length === 0 ? (
                <p>ไม่มีรายการอาหารในขณะนี้</p>
            ) : (
                orderItems.map(item => (
                    <div key={item.id} style={cardStyle}>
                        <div style={rowStyle}>
                            <div>
                                <p><strong>🧾 โต๊ะ:</strong> {item.tableNumber}</p>
                                <p><strong>🍽️ เมนู:</strong> {item.menuName}</p>
                                <p><strong>จำนวน:</strong> {item.quantity}</p>
                                <p><small>⏱️ เวลา: {new Date(item.createTime).toLocaleTimeString()}</small></p>
                            </div>

                            <button onClick={() => handleMarkReady(item.id)} style={doneButtonStyle}>
                                ✅ เสร็จสิ้น
                            </button>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
};

// 🔧 Styles
const btnStyle = {
    padding: '8px 14px',
    backgroundColor: '#74b9ff',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
};

const cardStyle = {
    border: '1px solid #ccc',
    borderRadius: '10px',
    padding: '15px',
    marginBottom: '15px',
    background: '#f7f7f7',
};

const rowStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
};

const doneButtonStyle = {
    backgroundColor: '#27ae60',
    color: 'white',
    padding: '10px 16px',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    height: 'fit-content',
};

export default KitchenDashboard;
