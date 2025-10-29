

import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
// (Icons ที่ใช้ใน Page นี้: Header, List, Loading)
import { Receipt, Loader2, Utensils, Coffee, Truck, AlertCircle } from 'lucide-react';
import { createPayment, updatePayment, getPaymentsByStatus } from '../api/paymentApi';
import { fetchActiveOrders } from '../api/orderApi';

import PendingDetail from '../components/payment/PendingDetail';
import PaidDetail from '../components/payment/PaidDetail';
import RefundedDetail from '../components/payment/RefundedDetail';
import {
    DetailLoading,
    DetailProcessing,
    DetailPlaceholder
} from '../components/payment/PaymentDetailShared';


// --- Constants ---
const ORDER_TYPE_INFO = { DINE_IN: { icon: <Utensils className="w-4 h-4" />, label: "ทานที่ร้าน" }, TAKEAWAY: { icon: <Coffee className="w-4 h-4" />, label: "กลับบ้าน" }, DELIVERY: { icon: <Truck className="w-4 h-4" />, label: "เดลิเวอรี" }, };

// --- Modal ---
const ConfirmModal = ({ isOpen, onClose, config }) => {
    if (!isOpen) return null;
    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
            onClick={onClose}
        >
            <motion.div
                initial={{ scale: 0.9, y: 20 }}
                animate={{ scale: 1, y: 0 }}
                exit={{ scale: 0.9, y: 20 }}
                className="bg-white rounded-2xl shadow-xl w-full max-w-md"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="p-6">
                    <h3 className="text-xl font-bold text-gray-900">{config.title}</h3>
                    <div className="text-gray-600 mt-2 whitespace-pre-wrap">{config.message}</div>
                </div>
                <div className="px-6 py-4 bg-gray-50 rounded-b-2xl flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 rounded-lg bg-gray-200 text-gray-800 font-bold hover:bg-gray-300 transition-colors"
                    >
                        ยกเลิก
                    </button>
                    <button
                        onClick={() => { config.onConfirm(); onClose(); }}
                        className={`px-4 py-2 rounded-lg text-white font-bold transition-colors ${config.confirmColor} hover:opacity-90`}
                    >
                        {config.confirmText}
                    </button>
                </div>
            </motion.div>
        </motion.div>
    );
};

// --- Main Component ---
export default function PaymentPage() {
    // State Variables
    const [currentTab, setCurrentTab] = useState("PENDING"); // Default tab
    const [listData, setListData] = useState([]);
    const [selectedItem, setSelectedItem] = useState(null);
    const [loading, setLoading] = useState(false); // Loading list data
    const [isProcessing, setIsProcessing] = useState(false); // Processing payment/refund
    const [error, setError] = useState(null);
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [modalConfig, setModalConfig] = useState({ title: '', message: '', onConfirm: () => { }, confirmText: 'ตกลง', confirmColor: 'bg-blue-600' });

    // --- Data Loading ---
    const loadDataForTab = useCallback(async (tab) => {
        setLoading(true); setError(null); setSelectedItem(null);
        try {
            let data = [];
            if (tab === "PENDING") { data = await fetchActiveOrders(); } // Fetch ACTIVE Orders
            else { data = await getPaymentsByStatus(tab); } // Fetch PAID/REFUNDED Payments
            setListData(data);
            if (data.length > 0) setSelectedItem(data[0]); // Auto-select first
        } catch (err) { console.error(`Failed loading tab ${tab}:`, err); setError(`โหลดรายการไม่สำเร็จ`); setListData([]); }
        finally { setLoading(false); }
    }, []);
    useEffect(() => { loadDataForTab(currentTab); }, [currentTab, loadDataForTab]); // Reload on tab change

    // --- Payment Flow ---
    const requestPayment = (paymentMethod) => {
        if (!selectedItem || currentTab !== 'PENDING' || isProcessing) return;
        const order = selectedItem; // In PENDING tab, item is an Order
        const methods = { 'QR_CODE': 'สแกน QR', 'CASH': 'เงินสด', 'CARD': 'บัตรเครดิต' }; // Map CARD for display
        setModalConfig({ title: 'ยืนยันชำระ', message: `ยืนยันชำระ Order #${order.id} (${methods[paymentMethod]})\nยอด: ${order.totalPrice.toFixed(2)} บาท?`, onConfirm: () => processPaymentFlow(paymentMethod, order), confirmText: 'ยืนยัน', confirmColor: 'bg-green-500' });
        setShowConfirmModal(true);
    };

    const processPaymentFlow = async (paymentMethod, order) => {
        setIsProcessing(true); setError(null);
        const originalOrderId = order.id;
        try {
            // Step 1: Create Payment
            console.log("Creating payment for order:", order.id, "with method:", paymentMethod);
            const createResponse = await createPayment({ orderId: order.id, paymentMethod: paymentMethod, amount: order.totalPrice });
            if (!createResponse || !createResponse.id) throw new Error('Create payment failed.');
            const paymentId = createResponse.id;
            // Step 2: Simulate Delay
            await new Promise((res) => setTimeout(res, 1500));
            // Step 3: Update Payment to PAID
            const updateResponse = await updatePayment(paymentId, { status: "PAID" });
            if (!updateResponse || updateResponse.status !== 'PAID') throw new Error('Update payment to PAID failed.');
            // Step 4: Reload PENDING (ACTIVE Orders) list
            await loadDataForTab("PENDING");
        } catch (err) {
            console.error("Payment flow failed:", err); setError(err.message || "ชำระเงินไม่สำเร็จ");
            await loadDataForTab("PENDING"); // Reload on error
            // Try re-selecting original order if still present
            const stillExists = listData.find(item => item.id === originalOrderId);
            if (stillExists) setSelectedItem(stillExists); else setSelectedItem(null);
        } finally { setIsProcessing(false); }
    };

    // --- Refund Flow ---
    const requestRefund = () => {
        if (!selectedItem || currentTab !== 'PAID' || isProcessing) return;
        const payment = selectedItem; // In PAID tab, item is a Payment
        setModalConfig({ title: 'ยืนยันคืนเงิน', message: `คืนเงิน Payment #${payment.id} (Order #${payment.orderResponse?.id})\nยอด ${payment.amount.toFixed(2)} บาท?`, onConfirm: () => processRefundFlow(payment), confirmText: 'ยืนยัน', confirmColor: 'bg-red-500' });
        setShowConfirmModal(true);
    };

    const processRefundFlow = async (payment) => {
        setIsProcessing(true); setError(null);
        const originalPaymentId = payment.id;
        try {
            // Step 1: Update Payment to REFUNDED
            const result = await updatePayment(payment.id, { status: "REFUNDED" });
            if (!result || result.status !== 'REFUNDED') throw new Error('Refund failed.');
            // Step 2: Reload PAID list
            await loadDataForTab("PAID");
        } catch (err) {
            console.error("Refund flow failed:", err); setError(err.message || "คืนเงินไม่สำเร็จ");
            await loadDataForTab("PAID"); // Reload on error
            const stillExists = listData.find(item => item.id === originalPaymentId);
            if (stillExists) setSelectedItem(stillExists); else setSelectedItem(null);
        } finally { setIsProcessing(false); }
    };

    // --- UI Components ---
    const TabButton = ({ status, label }) => (<button onClick={() => !loading && !isProcessing && setCurrentTab(status)} disabled={loading || isProcessing} className={`flex-1 py-3 px-2 text-sm md:text-base font-bold border-b-4 transition-all whitespace-nowrap ${currentTab === status ? "border-blue-600 text-blue-600" : "border-transparent text-gray-500 hover:text-gray-800 disabled:text-gray-300 disabled:cursor-not-allowed"}`} >{label}</button>);

    const ListItemCard = ({ item, onSelect, isSelected, type }) => {
        const isOrder = type === 'PENDING';
        const orderDetails = isOrder ? item : item.orderResponse || {};
        const displayId = isOrder ? `Order #${item.id}` : `Pay #${item.id}`;
        const tableInfo = orderDetails.tableId;
        const orderType = orderDetails.orderType || 'UNKNOWN';
        const totalPrice = isOrder ? item.totalPrice : item.amount;
        const info = ORDER_TYPE_INFO[orderType] || {};
        const isCurrentlyProcessing = isProcessing && selectedItem?.id === item.id && type === 'PENDING'; // Processing shown only on PENDING item
        return (
            <motion.div
                layout
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                onClick={() => !isProcessing && onSelect(item)}
                className={`p-4 mb-3 rounded-xl border-l-4 transition-all relative ${isSelected ? 'bg-blue-100 border-blue-600 shadow-md' : 'bg-white border-gray-300 hover:bg-gray-50 hover:shadow-sm'} ${isProcessing ? 'opacity-60 cursor-not-allowed' : 'cursor-pointer'}`}
            >
                {isCurrentlyProcessing && <div className="absolute inset-0 bg-blue-100/70 flex items-center justify-center rounded-xl z-10"><Loader2 className="w-6 h-6 text-blue-600 animate-spin" /></div>}
                <div className="flex justify-between items-center mb-1">
                    <span className="text-sm font-semibold text-gray-500">{displayId}</span>
                    <span className="text-sm font-medium text-gray-600 flex items-center gap-1.5">{info.icon} {info.label}</span>
                </div>
                <p className="text-lg font-bold text-gray-900 truncate">
                    {orderType === 'DINE_IN' && tableInfo ? `โต๊ะ ${tableInfo}` : info.label || 'Unknown Order'}
                </p>
                <p className="text-lg font-semibold text-blue-900 mt-1">{totalPrice} บาท</p>
                {!isOrder && item.method && <p className="text-xs text-gray-500 mt-1">วิธีชำระ: {item.method}</p>}
            </motion.div>
        );
    };

    // --- Helper function for rendering detail view ---
    const renderDetailView = () => {
        // 1. Loading state (list is loading)
        if (loading && !selectedItem) return <DetailLoading />;

        // 2. Processing state (payment/refund in progress)
        // (เฉพาะ PENDING tab ที่จะแสดงผล processing แบบเต็มจอ)
        const isCurrentlyProcessing = isProcessing && selectedItem?.id === selectedItem?.id && currentTab === 'PENDING';
        if (isCurrentlyProcessing) return <DetailProcessing item={selectedItem} />;

        // 3. No item selected (or list is empty)
        if (!selectedItem) {
            const message = listData.length > 0 ? 'กรุณาเลือกรายการ' : 'ไม่มีรายการในแท็บนี้';
            return <DetailPlaceholder message={message} />;
        }

        // 4. Render based on tab
        if (currentTab === 'PENDING') {
            return (
                <PendingDetail
                    key={selectedItem.id}
                    item={selectedItem}
                    onPrint={() => window.print()} // (ส่ง function เข้าไป)
                    onRequestPayment={requestPayment}
                />
            );
        }

        if (currentTab === 'PAID') {
            return (
                <PaidDetail
                    key={selectedItem.id}
                    item={selectedItem}
                    onPrint={() => window.print()}
                    onRequestRefund={requestRefund}
                />
            );
        }

        if (currentTab === 'REFUNDED') {
            return (
                <RefundedDetail
                    key={selectedItem.id}
                    item={selectedItem}
                    onPrint={() => window.print()}
                />
            );
        }

        return null; // Fallback
    };


    // --- Main Render ---
    return (
        <div className="flex flex-col h-screen bg-gray-100 font-inter">
            {/* ... Modal and Processing Overlay ... */}
            <AnimatePresence>
                {showConfirmModal && <ConfirmModal isOpen={showConfirmModal} onClose={() => !isProcessing && setShowConfirmModal(false)} config={modalConfig} />}
            </AnimatePresence>
            {isProcessing && <div className="fixed inset-0 bg-black/60 z-[100] flex flex-col items-center justify-center p-4">
                <Loader2 className="w-16 h-16 text-white animate-spin" />
                <p className="text-white text-xl font-semibold mt-4">กำลังประมวลผล...</p>
            </div>}

            {/* ... Header ... */}
            <header className="flex items-center justify-between p-4 bg-blue-900 shadow-md z-30">
                <h1 className="text-2xl font-bold text-white flex items-center gap-2"><Receipt /> Payment / Billing</h1>
                {error && <div className="flex items-center gap-2 bg-red-500/80 text-white text-sm px-3 py-1 rounded-md">
                    <AlertCircle size={16} />
                    <span>{error}</span>
                    <button onClick={() => setError(null)} className="ml-2 opacity-70 hover:opacity-100">&times;</button>
                </div>}
            </header>

            {/* ... Main Content ... */}
            <div className="flex-1 flex flex-col md:flex-row overflow-hidden">
                <aside className="w-full md:w-[40%] lg:w-1/3 flex flex-col h-auto max-h-[50vh] md:max-h-none md:h-full bg-white border-r border-gray-200">
                    <div className="flex flex-row border-b border-gray-200 bg-white sticky top-0 z-20"> {/* Tabs */}
                        <TabButton status="PENDING" label="รอชำระ" />
                        <TabButton status="PAID" label="ชำระแล้ว" />
                        <TabButton status="REFUNDED" label="คืนเงินแล้ว" />
                    </div>
                    <div className="p-4 overflow-y-auto flex-1 relative"> {/* List Area */}
                        {loading && <div className="absolute inset-0 bg-white/80 flex items-center justify-center z-10"><Loader2 className="w-8 h-8 text-blue-600 animate-spin" /></div>}
                        <AnimatePresence>
                            {!loading && listData.map(item => <ListItemCard key={item.id} item={item} onSelect={setSelectedItem} isSelected={selectedItem?.id === item.id} type={currentTab} />)}
                        </AnimatePresence>
                        {!loading && listData.length === 0 && <p className="text-center text-gray-500 mt-10 px-4">ไม่มีรายการ{currentTab === 'PENDING' ? 'ออเดอร์รอชำระ (ACTIVE)' : (currentTab === 'PAID' ? 'ที่ชำระแล้ว' : 'ที่คืนเงินแล้ว')}</p>}
                    </div>
                </aside>

                <main className="flex-1 p-4 md:p-6 lg:p-8 overflow-y-auto bg-gray-100 relative"> {/* Detail Area */}
                    <AnimatePresence mode="wait">
                        {renderDetailView()}
                    </AnimatePresence>
                </main>
            </div>

            {/* ... Styles ... */}
            <style>{`
                .btn-primary { 
                    padding: 0.75rem 1rem; 
                    border-radius: 0.5rem; 
                    color: white; 
                    font-weight: 600; 
                    display: flex; 
                    align-items: center; 
                    justify-content: center; 
                    gap: 0.5rem; 
                    transition: background-color 0.2s; 
                } 
                .btn-secondary { 
                    padding: 0.75rem 1rem; 
                    border-radius: 0.5rem; 
                    background-color: #e5e7eb; 
                    color: #1f2937; 
                    font-weight: 600; 
                    display: flex; 
                    align-items: center; 
                    justify-content: center; 
                    gap: 0.5rem; 
                    transition: background-color 0.2s; 
                } 
                .btn-secondary:hover { 
                    background-color: #d1d5db; 
                }
            `}</style>
        </div>
    );
}