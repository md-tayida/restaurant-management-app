import React, { useEffect, useState } from 'react';
import axios from 'axios';

const BASE_URL = 'http://localhost:8090/api';

const CounterPage = () => {
    const [tables, setTables] = useState([]);
    const [selectedTable, setSelectedTable] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState('CASH');

    useEffect(() => {
        fetchTables();
    }, []);

    const fetchTables = async () => {
        try {
            const res = await axios.get(`${BASE_URL}/tables/with-order-status`);
            setTables(res.data);
        } catch (err) {
            console.error('Error fetching tables', err);
        }
    };

    const getTableStatusColor = (table) => {
        if (table.tableStatus === 'AVAILABLE') return '#27ae60';

        // รวม items จาก orders ทั้งหมด
        const orders = table.orders || [];
        const items = orders.flatMap(order => order.items || []);

        // ถ้ามี PREPARING หรือ READY_TO_SERVE → สีส้ม
        const hasPreparingOrReady = items.some(item => {
            const status = (item.status || '').toUpperCase();
            return status === 'PREPARING' || status === 'READY_TO_SERVE';
        });

        if (hasPreparingOrReady) return '#f39c12';

        // ถ้า items ไม่ว่างและทุกอัน DONE → สีฟ้า
        const allDone = items.length > 0 && items.every(item => (item.status || '').toUpperCase() === 'DONE');
        if (allDone) return '#3498db';

        return '#3498db'; // fallback สีฟ้า
    };


    const handleClickTable = (table) => {
        console.log('orders of clicked table:', table.orders);
        const unpaidOrders = (table.orders || []).filter(order => order.status === 'ACTIVE');
        console.log('active orders:', unpaidOrders);

        const activeItems = unpaidOrders.flatMap(order => order.items || []);
        console.log('active items:', activeItems);

        if (unpaidOrders.length > 0) {
            setSelectedTable({ ...table, orders: unpaidOrders, items: activeItems });
            setShowModal(true);
        }
    };

    const calculateItemTotal = (item) => {
        const price = item.unitPrice ?? 0;
        const qty = item.quantity ?? 0;
        return price * qty;
    };

    const handlePayment = async () => {
        if (!selectedTable) return;
        try {
            await axios.post(`${BASE_URL}/payments`, {
                tableId: selectedTable.tableId,
                method: paymentMethod
            });
            alert('ชำระเงินเรียบร้อยแล้ว');
            setSelectedTable(null);
            setShowModal(false);
            fetchTables();
        } catch (err) {
            console.error('Payment error', err);
            alert('เกิดข้อผิดพลาดในการชำระเงิน');
        }
    };

    return (
        <div className="p-6 font-sans bg-gray-100 min-h-screen">
            <h2 className="text-2xl font-bold mb-4 text-gray-800">💳 รายการโต๊ะทั้งหมด</h2>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {tables.map((table) => (
                    <div
                        key={table.tableId}
                        onClick={() => handleClickTable(table)}
                        className="rounded-xl text-white p-4 cursor-pointer shadow-md transition-transform hover:scale-105"
                        style={{ backgroundColor: getTableStatusColor(table) }}
                    >
                        <div className="text-lg font-semibold">โต๊ะ {table.tableNumber}</div>
                    </div>
                ))}
            </div>

            {showModal && selectedTable && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
                    <div className="bg-white rounded-xl p-6 w-[90%] md:w-[600px] shadow-xl relative">
                        <button
                            onClick={() => setShowModal(false)}
                            className="absolute top-2 right-2 text-gray-500 hover:text-red-500 text-lg"
                        >
                            ✖
                        </button>

                        <h3 className="text-xl font-bold mb-4 text-gray-800">
                            💰 ชำระเงิน - โต๊ะ {selectedTable.tableNumber}
                        </h3>

                        {selectedTable.items.length === 0 ? (
                            <p className="text-gray-500">ไม่มีรายการอาหาร</p>
                        ) : (
                            <ul className="mb-4 list-disc list-inside space-y-1 text-gray-700">
                                {selectedTable.items.map((item, idx) => (
                                    <li key={idx}>
                                        {item.menuName} × {item.quantity}
                                        <span className="ml-2 font-semibold text-green-700">
                                            {calculateItemTotal(item)} บาท
                                        </span>
                                    </li>
                                ))}
                            </ul>
                        )}


                        <div className="text-right font-bold text-lg mb-4 text-gray-800">
                            ยอดรวมทั้งหมด: <span className="text-green-600">{selectedTable.totalPrice ?? 0} บาท</span>
                        </div>

                        <div className="mb-4">
                            <label className="block mb-1 font-medium text-gray-700">วิธีชำระเงิน:</label>
                            <select
                                value={paymentMethod}
                                onChange={(e) => setPaymentMethod(e.target.value)}
                                className="border border-gray-300 rounded p-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
                            >
                                <option value="CASH">เงินสด</option>
                                <option value="QR">QR Code</option>
                                <option value="CARD">บัตรเครดิต</option>
                            </select>
                        </div>

                        <div className="flex justify-end space-x-2">
                            <button
                                onClick={() => setShowModal(false)}
                                className="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400"
                            >
                                ยกเลิก
                            </button>
                            <button
                                onClick={handlePayment}
                                className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 font-semibold"
                            >
                                ✅ ยืนยันชำระเงิน
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CounterPage;
