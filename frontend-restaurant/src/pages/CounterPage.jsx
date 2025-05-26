import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function CounterPage() {
    const [tables, setTables] = useState([]);
    const [orders, setOrders] = useState([]);
    const [selectedTableOrders, setSelectedTableOrders] = useState(null); // สำหรับ popup
    const [showModal, setShowModal] = useState(false);

    useEffect(() => {
        fetchTables();
        fetchOrders();
    }, []);

    const fetchTables = async () => {
        try {
            const res = await axios.get('http://localhost:8090/api/tables');
            setTables(res.data);
        } catch (err) {
            console.error('Error fetching tables', err);
        }
    };

    const fetchOrders = async () => {
        try {
            const res = await axios.get('http://localhost:8090/api/orders');
            setOrders(res.data);
        } catch (err) {
            console.error('Error fetching orders', err);
        }
    };

    const handlePay = async (tableId) => {
        try {
            await axios.post(`http://localhost:8090/api/orders/${tableId}/pay`);
            alert('ชำระเงินเรียบร้อย');
            fetchTables();
            fetchOrders();
        } catch (err) {
            console.error('Error paying order', err);
            alert('ชำระเงินไม่สำเร็จ');
        }
    };

    const handleViewOrder = (tableId) => {
        const tableOrders = orders.filter(order => order.tableId === tableId);
        setSelectedTableOrders(tableOrders);
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setSelectedTableOrders(null);
    };

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold mb-6">Counter Page</h1>
            <div className="grid grid-cols-4 gap-4">
                {tables.map(table => {
                    const isAvailable = table.status === 'AVAILABLE';
                    const tableOrders = orders.filter(order => order.tableId === table.id);
                    return (
                        <div
                            key={table.id}
                            className={`p-4 rounded shadow cursor-pointer ${isAvailable ? 'bg-green-300' : 'bg-orange-300'
                                }`}
                            style={{ width: '300px', height: '180px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
                        >
                            <h2 className="text-lg font-semibold mb-2">{table.tableNumber}</h2>
                            <p>Status: {table.status}</p>

                            {!isAvailable && (
                                <div>
                                    <button
                                        className="mt-2 mr-2 px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
                                        onClick={() => handleViewOrder(table.id)}
                                    >
                                        ดูรายการอาหาร
                                    </button>

                                    <button
                                        className="mt-2 px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
                                        onClick={() => handlePay(table.id)}
                                    >
                                        ชำระเงิน
                                    </button>
                                </div>
                            )}
                        </div>
                    );
                })}

            </div>

            {/* Modal popup */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                    <div className="bg-white p-6 rounded shadow-lg w-96 max-h-[80vh] overflow-y-auto">
                        <h2 className="text-xl font-bold mb-4">รายการอาหารโต๊ะ</h2>
                        {selectedTableOrders.length === 0 ? (
                            <p>ไม่มีรายการอาหารสำหรับโต๊ะนี้</p>
                        ) : (
                            selectedTableOrders.map(order => (
                                <div key={order.id} className="mb-4 border-b pb-2">
                                    <p><strong>Order ID:</strong> {order.id}</p>
                                    <p><strong>สถานะ:</strong> {order.status}</p>
                                    <p><strong>เวลาสั่ง:</strong> {new Date(order.createdAt).toLocaleString()}</p>
                                    <p><strong>รวมราคา:</strong> {order.totalPrice.toFixed(2)}</p>
                                    <div className="mt-2">
                                        <p className="font-semibold">รายการสินค้า:</p>
                                        <ul className="list-disc pl-5">
                                            {order.items.map(item => (
                                                <li key={item.id}>
                                                    {item.menuName} x {item.quantity} ({item.status})
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                </div>
                            ))
                        )}
                        <button
                            onClick={closeModal}
                            className="mt-4 px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
                        >
                            ปิด
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
