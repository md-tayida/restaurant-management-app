import React, { useEffect, useState } from 'react';

const ReadyToServePage = () => {
    const [items, setItems] = useState([]);

    const fetchItems = () => {
        fetch('http://localhost:8090/api/kitchen/ready-to-serve')
            .then(res => res.json())
            .then(data => setItems(data))
            .catch(err => console.error('เกิดข้อผิดพลาด:', err));
    };

    useEffect(() => {
        fetchItems();
        const interval = setInterval(fetchItems, 5000); // รีเฟรชทุก 5 วินาที
        return () => clearInterval(interval);
    }, []);

    const markAsDone = (itemId) => {
        fetch(`http://localhost:8090/api/kitchen/item/${itemId}/done`, {
            method: 'PUT'
        })
            .then(res => {
                if (res.ok) {
                    fetchItems();
                } else {
                    alert('ไม่สามารถอัปเดตสถานะได้');
                }
            });
    };

    return (
        <div style={{
            padding: '30px',
            backgroundColor: '#f4f6f8',
            minHeight: '100vh',
            fontFamily: 'Arial, sans-serif'
        }}>
            <h2 style={{
                marginBottom: '20px',
                color: '#2c3e50'
            }}>🍽 รายการพร้อมเสิร์ฟ</h2>

            {items.length === 0 ? (
                <p style={{ fontStyle: 'italic', color: '#888' }}>ไม่มีรายการพร้อมเสิร์ฟ</p>
            ) : (
                <div style={{ display: 'grid', gap: '15px' }}>
                    {items.map((item) => (
                        <div
                            key={item.id}
                            style={{
                                backgroundColor: '#ffffff',
                                padding: '15px 20px',
                                borderRadius: '10px',
                                boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center'
                            }}
                        >
                            <div>
                                <strong>โต๊ะ {item.tableNumber}</strong>: {item.menuName} × {item.quantity}
                            </div>
                            <button
                                onClick={() => markAsDone(item.id)}
                                style={{
                                    backgroundColor: '#27ae60',
                                    color: '#fff',
                                    border: 'none',
                                    borderRadius: '5px',
                                    padding: '6px 12px',
                                    fontWeight: 'bold',
                                    cursor: 'pointer'
                                }}
                            >
                                ✅ เสิร์ฟแล้ว
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ReadyToServePage;
