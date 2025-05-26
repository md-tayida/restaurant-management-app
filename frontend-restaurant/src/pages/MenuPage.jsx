import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import MenuCard from '../components/MenuCard';
import OrderSidebar from '../components/OrderSidebar';
import { fetchMenus } from '../api/menuApi';
import { postOrder } from '../api/orderApi';
import axios from 'axios';

const MenuPage = () => {
    const navigate = useNavigate();

    const [orderType, setOrderType] = useState('DINE_IN');
    const [tableId, setTableId] = useState('');
    const [tables, setTables] = useState([]);
    const [menuByCategory, setMenuByCategory] = useState({});
    const [selectedItems, setSelectedItems] = useState([]);
    const [orderSent, setOrderSent] = useState(false);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
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

        const loadTables = async () => {
            try {
                const res = await axios.get('http://localhost:8090/api/tables');
                setTables(res.data);
            } catch (err) {
                console.error('โหลดโต๊ะล้มเหลว');
            }
        };

        loadMenus();
        loadTables();
    }, [navigate]);

    const handleAddItem = (menu) => {
        const index = selectedItems.findIndex(item => item.id === menu.id);
        if (index !== -1) {
            const updatedItems = [...selectedItems];
            updatedItems[index].quantity += 1;
            setSelectedItems(updatedItems);
        } else {
            setSelectedItems([...selectedItems, { ...menu, quantity: 1 }]);
        }
    };

    const handleIncrease = (menuId) => {
        setSelectedItems(prev =>
            prev.map(item => item.id === menuId ? { ...item, quantity: item.quantity + 1 } : item)
        );
    };

    const handleDecrease = (menuId) => {
        setSelectedItems(prev =>
            prev
                .map(item => item.id === menuId ? { ...item, quantity: item.quantity - 1 } : item)
                .filter(item => item.quantity > 0)
        );
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
                quantity: item.quantity,
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

            <div style={{ padding: '20px' }}>
                <label>
                    ประเภทการสั่ง:
                    <select value={orderType} onChange={e => setOrderType(e.target.value)}>
                        <option value="DINE_IN">ทานที่ร้าน</option>
                        <option value="TAKEAWAY">สั่งกลับบ้าน</option>
                    </select>
                </label>

                {orderType === 'DINE_IN' && (
                    <div style={{ marginTop: '10px' }}>
                        <label>เลือกหมายเลขโต๊ะ: </label>
                        <select value={tableId} onChange={(e) => setTableId(e.target.value)}>
                            <option value="">-- กรุณาเลือก --</option>
                            {tables.map((table) => (
                                <option key={table.id} value={table.id}>
                                    {table.tableNumber} ({table.status})
                                </option>
                            ))}
                        </select>
                    </div>
                )}
            </div>

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
                    onIncrease={handleIncrease}
                    onDecrease={handleDecrease}
                />
            </div>
        </div>
    );
};

export default MenuPage;
