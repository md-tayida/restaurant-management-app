import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import MenuCard from '../components/MenuCard';
import OrderSidebar from '../components/OrderSidebar';
import { fetchMenus } from '../api/menuApi';
import { postOrder } from '../api/orderApi';

const MenuPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const orderType = location.state?.orderType;

    const [tableId, setTableId] = useState('');
    const [menuByCategory, setMenuByCategory] = useState({});
    const [selectedItems, setSelectedItems] = useState([]);
    const [orderSent, setOrderSent] = useState(false);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!orderType) {
            navigate('/order-type');
            return;
        }

        const loadMenus = async () => {
            try {
                setLoading(true);
                const data = await fetchMenus();
                const grouped = data.reduce((acc, item) => {
                    const category = item.category?.name || 'อื่นๆ';
                    if (!acc[category]) acc[category] = [];
                    acc[category].push(item);
                    return acc;
                }, {});
                setMenuByCategory(grouped);
                setError(null);
            } catch (err) {
                setError('ไม่สามารถโหลดเมนูได้ กรุณาลองใหม่อีกครั้ง');
            } finally {
                setLoading(false);
            }
        };

        loadMenus();
    }, [orderType, navigate]);

    const handleAddItem = (menu) => {
        setSelectedItems([...selectedItems, menu]);
    };

    const handleSubmitOrder = async () => {
        if (orderType === 'DINE_IN' && !tableId) {
            alert('กรุณาเลือกโต๊ะก่อนสั่งอาหาร');
            return;
        }

        const orderPayload = {
            orderType,
            tableId: orderType === 'DINE_IN' ? tableId : null,
            items: selectedItems.map(item => ({
                menuId: item.id,
                name: item.name,
                price: item.price,
                quantity: 1,
            })),
        };

        try {
            await postOrder(orderPayload);
            setOrderSent(true);
            setSelectedItems([]);
        } catch (err) {
            alert('ส่งออเดอร์ไม่สำเร็จ');
        }
    };

    return (
        <div style={{ fontFamily: 'Arial, sans-serif' }}>
            <Navbar orderType={orderType} tableId={tableId} setTableId={setTableId} />

            <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#f1f2f6' }}>
                {/* เมนูอาหาร */}
                <div style={{ flex: 2, padding: '30px', overflowY: 'auto' }}>
                    <h2 style={{ marginBottom: '20px' }}>📋 เมนูอาหาร</h2>

                    {loading && <p>กำลังโหลดเมนู...</p>}
                    {error && <p style={{ color: 'red' }}>{error}</p>}
                    {!loading && !error && Object.keys(menuByCategory).length === 0 && <p>ยังไม่มีเมนูอาหารในขณะนี้</p>}

                    {!loading && !error && Object.keys(menuByCategory).map(categoryName => (
                        <div key={categoryName} style={{ marginBottom: '40px' }}>
                            <h3 style={{ color: '#2c3e50', marginBottom: '15px' }}>{categoryName}</h3>
                            <div style={{
                                display: 'grid',
                                gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
                                gap: '20px'
                            }}>
                                {menuByCategory[categoryName].map(item => (
                                    <MenuCard key={item.id} item={item} onAdd={handleAddItem} />
                                ))}
                            </div>
                        </div>
                    ))}
                </div>

                {/* รายการออเดอร์ */}
                <OrderSidebar
                    selectedItems={selectedItems}
                    onSubmit={handleSubmitOrder}
                    orderSent={orderSent}
                />
            </div>
        </div>
    );
};

export default MenuPage;
