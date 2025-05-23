import React, { useEffect, useState } from 'react';

const KitchenPage = ({ category }) => {
    const [items, setItems] = useState([]);

    const fetchItems = () => {
        fetch(`http://localhost:8090/api/kitchen/preparing/${category}`)
            .then(res => res.json())
            .then(data => setItems(data))
            .catch(err => console.error('เกิดข้อผิดพลาด:', err));
    };

    useEffect(() => {
        fetchItems();
    }, [category]);

    const markAsReady = (orderItemId) => {
        fetch(`http://localhost:8090/api/kitchen/item/${orderItemId}/ready-to-serve`, {
            method: 'PUT'
        })
            .then(res => {
                if (res.ok) {
                    fetchItems(); // รีโหลดใหม่หลังอัปเดต
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
            }}>👨‍🍳 คำสั่งอาหารแผนก: <span style={{ color: '#e67e22' }}>{category}</span></h2>

            {items.length === 0 ? (
                <p style={{ fontStyle: 'italic', color: '#888' }}>ไม่มีคำสั่ง ณ ขณะนี้</p>
            ) : (
                <div style={{ display: 'grid', gap: '15px' }}>
                    {items.map((item) => (
                        <div
                            key={item.orderItemId}
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

                            <div>
                                {item.status === 'PREPARING' && (
                                    <button
                                        onClick={() => markAsReady(item.id)}
                                        style={{
                                            backgroundColor: '#3498db',
                                            color: '#fff',
                                            border: 'none',
                                            borderRadius: '5px',
                                            padding: '6px 12px',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        ✅ เสร็จสิ้น
                                    </button>
                                )}

                                {item.status === 'READY_TO_SERVE' && (
                                    <span style={{ color: '#27ae60', fontWeight: 'bold' }}>พร้อมเสิร์ฟ</span>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default KitchenPage;
