import React, { useEffect, useState, useMemo, useRef, useCallback } from "react"; // Added useCallback
import { motion, AnimatePresence } from "framer-motion";
import { Clock, Utensils, Truck, Coffee, XCircle, RefreshCw, ChefHat, Bell, Check, AlertTriangle, AlertCircle } from "lucide-react"; // Added AlertCircle



// --- (Import API จริง - Commented out for Preview) ---
import { fetchActiveOrders } from "../api/orderApi";
import { updateOrderItemStatus } from "../api/orderItemApi";


// --- Constants ---
const STATUS_LABELS = {
    PENDING: "รอทำ",
    IN_PROGRESS: "กำลังทำ",
    READY: "พร้อมเสิร์ฟ",
};

const ORDER_TYPE_ICONS = {
    DINE_IN: { icon: <Utensils className="w-4 h-4" />, label: "ทานที่ร้าน", color: "text-blue-600" },
    TAKEAWAY: { icon: <Coffee className="w-4 h-4" />, label: "กลับบ้าน", color: "text-purple-600" },
    DELIVERY: { icon: <Truck className="w-4 h-4" />, label: "เดลิเวอรี", color: "text-green-600" },
};

const themeColors = {
    primary: "#1E3A8A", secondary: "#2563EB", background: "#F3F4F6", text: "#111827",
    success: "#10B981", pending: "#F59E0B", error: "#EF4444", info: "#3B82F6", unavailable: "#6B7280"
};

// --- Helper Functions ---
const formatTimeAgo = (dateString, now) => {
    if (!dateString) return ""; const then = new Date(dateString); const seconds = Math.floor((now - then) / 1000); if (seconds < 60) return "เมื่อสักครู่"; const minutes = Math.floor(seconds / 60); if (minutes < 60) return `${minutes} นาที`; const hours = Math.floor(minutes / 60); if (hours < 24) return `${hours} ชม.`; const days = Math.floor(hours / 24); return `${days} วัน`;
};

// --- Sub-components ---

// CancelModal
const CancelModal = ({ isOpen, item, onClose, onConfirm }) => {
    if (!isOpen) return null;
    return (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4" onClick={onClose} >
            <motion.div initial={{ scale: 0.9, y: 20 }} animate={{ scale: 1, y: 0 }} exit={{ scale: 0.9, y: 20, opacity: 0 }} className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6 border-t-4 border-red-500" onClick={(e) => e.stopPropagation()} >
                <div className="flex justify-between items-center mb-4">
                    <h3 className="text-xl font-bold text-gray-800 flex items-center gap-2"><AlertTriangle className="text-red-500" /> ยืนยันการยกเลิก</h3>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600"> <XCircle size={24} /> </button>
                </div>
                <p className="text-gray-600 mb-2"> คุณต้องการยกเลิกรายการนี้ใช่หรือไม่? </p>
                <p className="text-lg font-semibold text-gray-900 bg-gray-50 rounded-lg p-3 mb-6 border border-gray-200">
                    {item?.menuName} <span className="text-red-600 font-bold">× {item?.quantity}</span>
                </p>
                <div className="flex justify-end gap-3">
                    <button onClick={onClose} className="px-4 py-2 rounded-lg bg-gray-200 text-gray-700 font-medium hover:bg-gray-300 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-offset-1"> ไม่ </button>
                    <button onClick={onConfirm} style={{ backgroundColor: themeColors.error }} className="px-4 py-2 rounded-lg text-white font-medium hover:opacity-90 transition-opacity focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-1"> ใช่, ยืนยันยกเลิก </button>
                </div>
            </motion.div>
        </motion.div>
    );
};


// KitchenItemCard
const KitchenItemCard = ({ item, now, onStatusChange, onCancelClick }) => {
    const orderTypeInfo = ORDER_TYPE_ICONS[item.orderType] || {};
    const timeAgo = formatTimeAgo(item.createdAt, now);
    const minutesAgo = Math.floor((now - new Date(item.createdAt)) / 60000);
    let timeColorClass = "text-gray-500";
    if (minutesAgo >= 10) timeColorClass = "text-red-600 font-bold";
    else if (minutesAgo >= 5) timeColorClass = "text-yellow-600 font-semibold";
    const isPending = item.status === 'PENDING';
    const isInProgress = item.status === 'IN_PROGRESS';
    const isReady = item.status === 'READY';
    const statusBorderColor = isPending ? 'border-yellow-500' : isInProgress ? 'border-blue-500' : 'border-green-500';

    return (
        <motion.div layout initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, x: -50 }} transition={{ duration: 0.3 }} className="bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden mb-4" >
            <div className={`p-4 border-l-8 ${statusBorderColor}`}>
                <div className="flex justify-between items-start mb-2">
                    <div className="flex-1 mr-2">
                        <p className="text-xs font-semibold text-gray-500"> Order #{item.orderId} {item.tableId ? `(โต๊ะ ${item.tableId})` : ''} </p>
                        <p className="text-xl font-bold text-gray-900 truncate" title={item.menuName}> {item.menuName} </p>
                    </div>
                    <span style={{ color: themeColors.secondary }} className="text-2xl font-bold ml-2 whitespace-nowrap"> ×{item.quantity} </span>
                </div>
                {item.description && (<p className="text-sm text-red-700 bg-red-50 rounded-md p-2 mt-2 border border-red-200"> <strong><AlertTriangle size={14} className="inline mr-1 mb-0.5" />หมายเหตุ:</strong> {item.description} </p>)}
                <div className="flex justify-between items-center mt-3 text-sm">
                    <span className={`flex items-center gap-1.5 font-medium ${orderTypeInfo.color}`}> {orderTypeInfo.icon} {orderTypeInfo.label} </span>
                    <span className={`flex items-center gap-1 ${timeColorClass}`}> <Clock size={14} /> {timeAgo} </span>
                </div>
            </div>
            <div className="grid grid-cols-2 gap-px bg-gray-100">
                {isPending && (<button onClick={() => onStatusChange(item.id, "IN_PROGRESS")} style={{ backgroundColor: themeColors.pending }} className="col-span-2 btn-action"> <ChefHat size={18} /> เริ่มทำ </button>)}
                {isInProgress && (<button onClick={() => onStatusChange(item.id, "READY")} style={{ backgroundColor: themeColors.info }} className="col-span-2 btn-action"> <Bell size={18} /> เสร็จแล้ว </button>)}
                {isReady && (<button onClick={() => onStatusChange(item.id, "SERVED")} style={{ backgroundColor: themeColors.success }} className="col-span-2 btn-action"> <Check size={18} /> เสิร์ฟแล้ว </button>)}
            </div>
            {!isReady && (<button onClick={() => onCancelClick(item)} className="btn-cancel"> <XCircle size={14} /> ยกเลิก </button>)}
        </motion.div>
    );
};


// KitchenColumn
const KitchenColumn = ({ title, count, bgColorClass, headerColorClass, children }) => {
    return (
        <div className={`flex-1 min-w-[350px] max-w-md h-full flex flex-col rounded-xl shadow-inner overflow-hidden ${bgColorClass}`}>
            <h2 className={`text-xl font-bold p-4 sticky top-0 z-10 flex justify-between items-center ${headerColorClass}`}>
                <span>{title}</span>
                <span className="text-base font-semibold text-gray-700 bg-white/70 rounded-full px-3 py-0.5">{count}</span>
            </h2>
            <div className="flex-1 p-4 pt-0 overflow-y-auto custom-scrollbar">
                <AnimatePresence>
                    {children}
                </AnimatePresence>
                {count === 0 && (<p className="text-center text-gray-500 mt-16 text-lg">ไม่มีรายการ</p>)}
            </div>
        </div>
    );
};


// --- Main App Component ---
export default function App() {
    const [allOrders, setAllOrders] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [now, setNow] = useState(new Date());
    const [modalState, setModalState] = useState({ isOpen: false, item: null });

    // --- API Call Functions ---
    // Now uses the MOCK functions again for preview
    const loadOrders = useCallback(async () => {
        setLoading(true); setError(null);
        try {
            const data = await fetchActiveOrders(); // Uses MOCK API
            setAllOrders(data || []);
        } catch (err) { console.error("Failed to fetch orders:", err); setError("ไม่สามารถโหลดออเดอร์ได้"); setAllOrders([]); }
        finally { setLoading(false); }
    }, []);

    const handleStatusChange = async (itemId, newStatus) => {
        setAllOrders(prevOrders => { return prevOrders.map(order => ({ ...order, orderItems: (order.orderItems || []).map(item => item.id === itemId ? { ...item, status: newStatus } : item) })); });
        try {
            await updateOrderItemStatus(itemId, { orderItemStatus: newStatus }); // Uses MOCK API
            // No immediate reload needed with mock, rely on interval or manual refresh
        } catch (err) { setError("อัปเดตสถานะไม่สำเร็จ"); console.error("Failed to update status:", err); loadOrders(); /* Revert on error */ }
    };

    const handleCancelClick = (item) => setModalState({ isOpen: true, item: item });
    const closeModal = () => setModalState({ isOpen: false, item: null });
    const confirmCancel = async () => {
        if (!modalState.item) return; const itemIdToCancel = modalState.item.id; closeModal();
        try {
            await updateOrderItemStatus(itemIdToCancel, { orderItemStatus: "CANCELED" }); // Uses MOCK API
            loadOrders(); // Reload data after successful cancellation
        } catch (err) { setError("ยกเลิกรายการไม่สำเร็จ"); console.error("Failed to cancel item:", err); loadOrders(); /* Reload on error */ }
    };

    // --- useEffect for Initial Load and Intervals ---
    useEffect(() => {
        loadOrders();
        const loadInterval = setInterval(loadOrders, 30000);
        const timeInterval = setInterval(() => setNow(new Date()), 15000);
        return () => { clearInterval(loadInterval); clearInterval(timeInterval); };
    }, [loadOrders]);

    // --- Data Transformation with useMemo ---
    const { pendingItems, inProgressItems, readyItems } = useMemo(() => {
        const allItems = allOrders.flatMap(order => (order.orderItems || []).map(item => ({ ...item, orderId: order.id, orderType: order.orderType, tableId: order.tableId, }))).filter(item => item.status === "PENDING" || item.status === "IN_PROGRESS" || item.status === "READY").sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
        return { pendingItems: allItems.filter(item => item.status === 'PENDING'), inProgressItems: allItems.filter(item => item.status === 'IN_PROGRESS'), readyItems: allItems.filter(item => item.status === 'READY'), };
    }, [allOrders]);

    // --- Render Logic ---
    return (
        <div className={`flex flex-col h-screen bg-gray-100 font-inter text-gray-900`}>
            {/* Header */}
            <header className="flex items-center justify-between p-4 bg-white text-gray-800 shadow-sm z-20 border-b border-gray-200">
                <h1 className="text-2xl font-bold flex items-center gap-2"> 🍜 Dashboard ครัว </h1>
                <div className="flex items-center gap-4">
                    {error && (<div className="flex items-center gap-2 bg-red-100 text-red-700 text-sm px-3 py-1 rounded-md"> <AlertCircle size={16} /> <span>{error}</span> <button onClick={() => setError(null)} className="ml-1 opacity-70 hover:opacity-100">&times;</button> </div>)}
                    <button onClick={loadOrders} disabled={loading} style={{ backgroundColor: loading ? themeColors.unavailable : themeColors.secondary, color: 'white' }} className="flex items-center gap-2 px-4 py-2 rounded-lg font-medium hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1" > <RefreshCw size={16} className={loading ? "animate-spin" : ""} /> {loading ? "กำลังโหลด..." : "รีเฟรช"} </button>
                </div>
            </header>

            {/* Kanban Columns */}
            <main className="flex-1 flex gap-4 p-4 overflow-x-auto overflow-y-hidden">
                {/* Pending Column */}
                <KitchenColumn title="📝 รอทำ" count={pendingItems.length} bgColorClass="bg-yellow-100" headerColorClass="bg-yellow-200 text-yellow-800" >
                    {pendingItems.map(item => (<KitchenItemCard key={item.id} item={item} now={now} onStatusChange={handleStatusChange} onCancelClick={handleCancelClick} />))}
                </KitchenColumn>
                {/* In Progress Column */}
                <KitchenColumn title="🧑‍🍳 กำลังทำ" count={inProgressItems.length} bgColorClass="bg-blue-100" headerColorClass="bg-blue-200 text-blue-800" >
                    {inProgressItems.map(item => (<KitchenItemCard key={item.id} item={item} now={now} onStatusChange={handleStatusChange} onCancelClick={handleCancelClick} />))}
                </KitchenColumn>
                {/* Ready Column */}
                <KitchenColumn title="✅ พร้อมเสิร์ฟ" count={readyItems.length} bgColorClass="bg-green-100" headerColorClass="bg-green-200 text-green-800" >
                    {readyItems.map(item => (<KitchenItemCard key={item.id} item={item} now={now} onStatusChange={handleStatusChange} onCancelClick={handleCancelClick} />))}
                </KitchenColumn>
            </main>

            {/* Cancel Modal */}
            <AnimatePresence> {modalState.isOpen && (<CancelModal isOpen={modalState.isOpen} item={modalState.item} onClose={closeModal} onConfirm={confirmCancel} />)} </AnimatePresence>

            {/* Custom Scrollbar & Button Styles */}
            <style>{`
                .custom-scrollbar::-webkit-scrollbar { height: 8px; width: 6px; }
                .custom-scrollbar::-webkit-scrollbar-track { background: rgba(0,0,0,0.05); border-radius: 4px; }
                .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.2); border-radius: 4px; }
                .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(0,0,0,0.3); }
                .btn-action { /* Base styles for action buttons */
                    display: flex; align-items: center; justify-content: center; gap: 0.5rem;
                    width: 100%; padding: 0.75rem; color: white; font-weight: 700;
                    transition: opacity 0.2s;
                }
                .btn-action:hover { opacity: 0.9; }
                .btn-cancel { /* Base styles for cancel button */
                    display: flex; align-items: center; justify-content: center; gap: 0.375rem; /* 1.5 */
                    width: 100%; padding: 0.5rem; background-color: #f3f4f6; /* bg-gray-100 */
                    color: #6b7280; /* text-gray-500 */ font-size: 0.75rem; /* text-xs */ font-weight: 500; /* font-medium */
                    transition: background-color 0.2s, color 0.2s; border-top: 1px solid #e5e7eb; /* border-gray-200 */
                }
                .btn-cancel:hover { background-color: #fee2e2; color: #dc2626; } /* bg-red-100 text-red-600 */
             `}</style>
        </div>
    );
}

