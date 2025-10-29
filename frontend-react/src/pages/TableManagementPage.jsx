import React, { useState, useEffect } from "react";
// Import framer-motion components
import { motion, AnimatePresence } from 'framer-motion';
// --- (API Imports - ไม่เปลี่ยนแปลง) ---
import { fetchTables, updateTableStatus, createTable } from "../api/tableApi";
import { fetchActiveOrderByTableId } from "../api/orderApi";
// --- (End API Imports) ---
import { useNavigate } from "react-router-dom";
// Import Swal from 'sweetalert2' - Make sure it's installed or imported correctly in your project
// If not using Swal, replace with a custom modal component
import Swal from 'sweetalert2';
import { Loader2, Plus, X, AlertCircle } from 'lucide-react'; // Added AlertCircle

const VALID_STATUSES = ["AVAILABLE", "RESERVED", "UNAVAILABLE"];

export default function TableManagement() {
    // --- (State Variables - ไม่เปลี่ยนแปลง) ---
    const [tables, setTables] = useState([]);
    const [selectedTable, setSelectedTable] = useState(null);
    const [loading, setLoading] = useState(true);
    const [order, setOrder] = useState(null);
    const [orderLoading, setOrderLoading] = useState(false);
    const [error, setError] = useState(null); // Added error state
    const navigate = useNavigate();
    // --- (End State Variables) ---

    // --- Theme Colors (ตามที่กำหนด) ---
    const themeColors = {
        primary: "#1E3A8A",      // bg-blue-900
        secondary: "#2563EB",    // bg-blue-600
        background: "#F3F4F6",  // bg-gray-100
        text: "#111827",        // text-gray-900
        success: "#10B981",      // bg-green-500
        pending: "#F59E0B",      // bg-yellow-500 (RESERVED)
        error: "#EF4444",        // bg-red-500 (OCCUPIED)
        info: "#3B82F6",         // bg-blue-500
        unavailable: "#6B7280"   // bg-gray-500 (UNAVAILABLE)
    };
    // --- (End Theme Colors) ---

    // --- (useEffect for Loading Tables - ไม่เปลี่ยนแปลง Logic) ---
    useEffect(() => {
        const loadTables = async () => {
            try {
                setLoading(true); setError(null); // Clear error on load
                const data = await fetchTables();
                // Sort tables numerically by tableNumber
                const sortedData = data.sort((a, b) => {
                    // *** FIX: Ensure tableNumber is treated as a string ***
                    const tableNumA = String(a.tableNumber || ''); // Convert to string, fallback to empty
                    const tableNumB = String(b.tableNumber || ''); // Convert to string, fallback to empty

                    const numA = parseInt(tableNumA.replace(/[^0-9]/g, ''), 10) || 0;
                    const numB = parseInt(tableNumB.replace(/[^0-9]/g, ''), 10) || 0;
                    const prefixA = tableNumA.replace(/[0-9]/g, '');
                    const prefixB = tableNumB.replace(/[0-9]/g, '');
                    if (prefixA !== prefixB) return prefixA.localeCompare(prefixB);
                    return numA - numB;
                });
                setTables(sortedData);
            } catch (err) {
                console.error("โหลดโต๊ะล้มเหลว:", err);
                setError("ไม่สามารถโหลดข้อมูลโต๊ะได้");
            } finally {
                setLoading(false);
            }
        };
        loadTables();
    }, []);
    // --- (End useEffect) ---

    // --- (API Logic Handlers - ไม่เปลี่ยนแปลง Logic) ---
    const handleStatusChange = async (id, newStatus) => {
        const upperStatus = newStatus.toUpperCase();
        if (!VALID_STATUSES.includes(upperStatus)) return;
        const originalTables = [...tables];
        const originalSelectedTable = selectedTable ? { ...selectedTable } : null;
        setTables(prev => prev.map(t => t.id === id ? { ...t, status: upperStatus } : t));
        if (selectedTable?.id === id) setSelectedTable(prev => ({ ...prev, status: upperStatus }));
        try {
            await updateTableStatus(id, { status: upperStatus });
        } catch (err) {
            console.error("Update failed:", err);
            setTables(originalTables);
            setSelectedTable(originalSelectedTable);
            Swal.fire({ icon: "error", title: "เกิดข้อผิดพลาด", text: "อัปเดตสถานะไม่สำเร็จ", confirmButtonColor: themeColors.primary });
        }
    };

    const handleTableClick = async (table) => {
        setError(null); setOrder(null); setSelectedTable(table);
        if (table.status === "OCCUPIED") {
            setOrderLoading(true);
            try {
                const orderData = await fetchActiveOrderByTableId(table.id);
                if (orderData && Object.keys(orderData).length > 0) { setOrder(orderData); }
                else { console.warn(`No active order for table ${table.id}`); }
            } catch (err) {
                console.error("Load order failed:", err);
                setError("โหลดออเดอร์ไม่สำเร็จ");
                setOrder(null);
            } finally { setOrderLoading(false); }
        }
    };

    const handleGoToPayment = () => {
        if (!selectedTable || !order || !order.orderItems) return;
        const hasUnservedItems = order.orderItems.some(item => item.status !== "SERVED" && item.status !== "CANCELED");
        if (hasUnservedItems) {
            Swal.fire({ icon: "warning", title: "ยังไม่สามารถชำระเงิน", text: "มีบางรายการยังไม่ได้เสิร์ฟ/ยกเลิก", confirmButtonColor: themeColors.primary, confirmButtonText: "ตกลง" });
            return;
        }
        navigate("/payment", { state: { tableNumber: selectedTable.tableNumber, orderId: order.id, totalPrice: order.totalPrice } });
    };

    const handleAddTable = async () => {
        const { value: tableNumber } = await Swal.fire({ title: "เพิ่มโต๊ะใหม่", input: "text", inputLabel: "หมายเลขโต๊ะ", inputPlaceholder: "กรอกหมายเลขโต๊ะ (เช่น A1)", confirmButtonText: 'เพิ่ม', cancelButtonText: 'ยกเลิก', confirmButtonColor: themeColors.primary, showCancelButton: true, inputValidator: (value) => { if (!value) return "กรอกหมายเลข!"; if (tables.some(t => String(t.tableNumber || '').toLowerCase() === value.toLowerCase())) return "เลขโต๊ะซ้ำ!"; } }); // Convert t.tableNumber to string
        if (!tableNumber) return;
        try {
            const newTable = await createTable({ tableNumber: tableNumber.toUpperCase(), status: "AVAILABLE" });
            setTables(prev => [...prev, newTable].sort((a, b) => {
                const tableNumA = String(a.tableNumber || ''); const tableNumB = String(b.tableNumber || ''); // Ensure string conversion
                const numA = parseInt(tableNumA.replace(/[^0-9]/g, ''), 10) || 0; const numB = parseInt(tableNumB.replace(/[^0-9]/g, ''), 10) || 0; const prefixA = tableNumA.replace(/[0-9]/g, ''); const prefixB = tableNumB.replace(/[0-9]/g, ''); if (prefixA !== prefixB) return prefixA.localeCompare(prefixB); return numA - numB;
            }));
            Swal.fire({ icon: "success", title: "สำเร็จ", text: `เพิ่มโต๊ะ ${newTable.tableNumber} แล้ว`, confirmButtonColor: themeColors.primary, timer: 1500, showConfirmButton: false });
        } catch (err) {
            console.error("Create failed:", err);
            Swal.fire({ icon: "error", title: "ผิดพลาด", text: "สร้างโต๊ะไม่สำเร็จ", confirmButtonColor: themeColors.primary });
        }
    };
    // --- (End API Logic Handlers) ---

    // --- Helper Functions ---
    const getStatusColorClasses = (status) => {
        switch (status) {
            case "OCCUPIED": return "bg-red-100 text-red-700 border-red-300";
            case "RESERVED": return "bg-yellow-100 text-yellow-700 border-yellow-300";
            case "UNAVAILABLE": return "bg-gray-200 text-gray-600 border-gray-300";
            case "AVAILABLE": default: return "bg-green-100 text-green-700 border-green-300";
        }
    };
    const formatStatus = (status) => {
        if (!status) return '';
        return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase();
    };
    // --- (End Helper Functions) ---

    // --- Render Logic ---
    return (
        // Use theme background and text color, ensure font is applied
        <div className={`min-h-screen p-6 font-inter bg-gray-100 text-gray-900`}>
            <div className="max-w-7xl mx-auto"> {/* Use larger max-width */}
                {/* Header Section */}
                <header className="flex flex-col sm:flex-row items-center justify-between mb-8 pb-4 border-b border-gray-300">
                    <div>
                        {/* Use theme primary color for heading */}
                        <h1 className="text-3xl font-bold text-blue-900 mb-1 flex items-center gap-2"> {/* Added icon */}
                            <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-armchair"><path d="M19 9V6a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2v3" /><path d="M3 16a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-5a2 2 0 0 0-4 0v2H7v-2a2 2 0 0 0-4 0Z" /><path d="M5 18v2" /><path d="M19 18v2" /></svg>
                            จัดการโต๊ะ
                        </h1>
                        <p className="text-gray-600">
                            ดูสถานะ จอง หรือเคลียร์โต๊ะ และดูรายการออเดอร์
                        </p>
                    </div>
                    {/* Styled "Add Table" Button */}
                    <button
                        onClick={handleAddTable}
                        className="mt-4 sm:mt-0 px-5 py-2.5 bg-blue-900 hover:bg-blue-700 text-white rounded-lg shadow-sm transition flex items-center gap-2 font-medium focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2" // Added focus styles
                    >
                        <Plus size={18} /> เพิ่มโต๊ะ
                    </button>
                </header>

                {/* Main Content Area: Loading, Error, or Table Grid */}
                {loading ? (
                    // Loading Indicator
                    <div className="flex justify-center items-center py-20">
                        <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
                        <span className="ml-4 text-lg text-gray-600">กำลังโหลดข้อมูลโต๊ะ...</span>
                    </div>
                ) : error ? (
                    // Error Display
                    <div className="text-center py-10 bg-red-50 border border-red-200 rounded-lg shadow-sm">
                        <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-3" />
                        <p className="text-red-700 font-semibold">{error}</p>
                        <button
                            onClick={() => window.location.reload()} // Simple reload action
                            className="mt-4 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2" // Added focus styles
                        >
                            ลองใหม่อีกครั้ง
                        </button>
                    </div>
                ) : (
                    // Table Grid Display
                    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-4 gap-5"> {/* Responsive grid */}
                        {tables.map((table) => (
                            // Table Card with motion
                            <motion.div
                                key={table.id} // Use stable ID for key
                                layout // Animate layout changes
                                initial={{ opacity: 0, scale: 0.95 }} // Initial animation state
                                animate={{ opacity: 1, scale: 1 }}   // Animate to final state
                                exit={{ opacity: 0, scale: 0.9 }}     // Animate on exit
                                transition={{ duration: 0.2, ease: "easeOut" }} // Smoother transition
                                onClick={() => handleTableClick(table)}
                                // Enhanced Card Styling
                                className={`
                                    bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition cursor-pointer border-t-4 transform hover:-translate-y-1 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2
                                    ${table.status === "OCCUPIED" ? 'border-red-500' :
                                        table.status === "RESERVED" ? 'border-yellow-500' :
                                            table.status === "UNAVAILABLE" ? 'border-gray-500' :
                                                'border-green-500'}
                                    ${selectedTable?.id === table.id ? 'ring-2 ring-blue-600 ring-offset-2' : ''} // Highlight selected
                                `}
                                tabIndex={0} // Make it focusable
                            >
                                <div className="flex justify-between items-center mb-3">
                                    {/* Table Number - Use theme primary color */}
                                    <h2 className="text-xl font-bold text-blue-900 truncate" title={String(table.tableNumber)}> {/* Add truncate and title */}
                                        {table.tableNumber}
                                    </h2>
                                    {/* Status Badge - Use helper function for colors */}
                                    <span
                                        className={`px-3 py-1 text-xs font-semibold rounded-full border ${getStatusColorClasses(table.status)}`} // Added font-semibold
                                    >
                                        {formatStatus(table.status)}
                                    </span>
                                </div>
                                {/* Example: Add Capacity (if available) with subtle styling */}
                                {/* {table.capacity && <p className="text-sm text-gray-500 mt-2">Capacity: {table.capacity}</p>} */}
                            </motion.div>
                        ))}
                    </div>
                )}

                {/* Table Detail Modal (using framer-motion AnimatePresence) */}
                <AnimatePresence>
                    {selectedTable && (
                        // Modal Overlay
                        <motion.div
                            key="modal-backdrop" // Add key for AnimatePresence
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm" // Darker overlay with blur
                            onClick={() => { setSelectedTable(null); setOrder(null); setError(null); }} // Close on backdrop click
                        >
                            {/* Modal Content */}
                            <motion.div
                                key="modal-content" // Add key
                                initial={{ scale: 0.9, y: 30, opacity: 0 }} // Start slightly lower and smaller, fade in
                                animate={{ scale: 1, y: 0, opacity: 1 }}
                                exit={{ scale: 0.9, y: 30, opacity: 0 }} // Exit animation
                                transition={{ type: "spring", stiffness: 300, damping: 25 }} // Spring animation
                                className="bg-white rounded-2xl shadow-xl max-w-md w-full overflow-hidden border-t-8 border-blue-900" // Top border accent
                                onClick={(e) => e.stopPropagation()} // Prevent closing when clicking inside modal
                            >
                                <div className="p-6 relative">
                                    {/* Close Button */}
                                    <button
                                        title="Close"
                                        className="absolute top-4 right-4 text-gray-400 hover:text-red-600 transition-colors rounded-full p-1 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-1" // Improved focus style
                                        onClick={() => { setSelectedTable(null); setOrder(null); setError(null); }}
                                    >
                                        <X size={24} />
                                    </button>

                                    {/* Modal Title */}
                                    <h3 className="text-2xl font-bold mb-5 text-blue-900 pr-8"> {/* Add padding for close button */}
                                        โต๊ะ {selectedTable.tableNumber}
                                    </h3>

                                    {/* Conditional Content: Order Details or Status Change */}
                                    {selectedTable.status === "OCCUPIED" ? (
                                        orderLoading ? (
                                            // Order Loading State
                                            <div className="flex items-center justify-center py-10 min-h-[200px]"> {/* Add min-height */}
                                                <Loader2 className="w-8 h-8 text-blue-600 animate-spin" />
                                                <span className="ml-3 text-gray-600">กำลังโหลดรายการออเดอร์...</span>
                                            </div>
                                        ) : error ? (
                                            // Order Loading Error
                                            <div className="text-center py-5 text-red-600 bg-red-50 p-3 rounded-md border border-red-200 min-h-[200px] flex flex-col justify-center">
                                                <AlertCircle className="inline-block w-6 h-6 mr-1 mx-auto mb-2" /> {error}
                                            </div>
                                        ) : order && order.orderItems && order.orderItems.length > 0 ? (
                                            // Order Details View
                                            <div>
                                                <h4 className="font-semibold text-gray-700 mb-2 border-b pb-1">🧾 รายการ</h4>
                                                {/* Scrollable Order Items List - Improved Styling */}
                                                <ul className="text-sm text-gray-800 mb-4 max-h-48 overflow-y-auto space-y-2 pr-2 border rounded-md p-3 bg-gray-50/50 custom-scrollbar">
                                                    {order.orderItems.map((item) => (
                                                        <li key={item.id} className="flex justify-between items-center odd:bg-white/60 p-1.5 rounded-md">
                                                            <span className="flex-1 mr-2 truncate" title={item.menuName}>{item.menuName} <span className="text-gray-500 font-medium">x{item.quantity}</span></span>
                                                            <span className="font-semibold text-gray-900 whitespace-nowrap">฿{item.price.toFixed(2)}</span>
                                                        </li>
                                                    ))}
                                                </ul>
                                                {/* Total Price - Enhanced Styling */}
                                                <div className="font-bold text-xl text-blue-900 mb-6 pt-3 border-t-2 border-dashed border-gray-300 flex justify-between">
                                                    <span>รวมทั้งหมด:</span>
                                                    <span>฿{order.totalPrice.toFixed(2)}</span>
                                                </div>

                                                {/* Buttons */}
                                                <div className="flex flex-col gap-3">
                                                    {/* Payment Button - Enhanced Styling */}
                                                    <button
                                                        onClick={handleGoToPayment}
                                                        className="w-full bg-blue-900 hover:bg-blue-700 text-white py-3 px-4 rounded-lg transition font-semibold text-center text-base shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 flex items-center justify-center gap-2" // Added focus, flex, gap
                                                    >
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect width="20" height="14" x="2" y="5" rx="2" /><line x1="2" x2="22" y1="10" y2="10" /></svg> {/* Credit Card Icon */}
                                                        ชำระเงิน
                                                    </button>
                                                    {/* Optional: Add to Order Button - Styled */}
                                                    {/* <button className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 py-2.5 px-4 rounded-lg transition font-medium focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-offset-2">สั่งเพิ่ม</button> */}
                                                </div>
                                            </div>
                                        ) : (
                                            // No active order found
                                            <p className="text-center text-gray-500 py-8 min-h-[200px] flex items-center justify-center">ไม่พบรายการออเดอร์สำหรับโต๊ะนี้</p>
                                        )
                                    ) : (
                                        // Status Change View
                                        <div className="mt-4">
                                            <label htmlFor="status-select" className="block text-sm font-medium text-gray-700 mb-1">เปลี่ยนสถานะโต๊ะ:</label>
                                            <select
                                                id="status-select"
                                                value={selectedTable.status}
                                                onChange={(e) => handleStatusChange(selectedTable.id, e.target.value)}
                                                // Styled Select Dropdown
                                                className="w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-600 focus:border-blue-600 transition appearance-none bg-white pr-8 bg-no-repeat bg-right"
                                                style={{ backgroundImage: `url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e")`, backgroundPosition: 'right 0.5rem center', backgroundSize: '1.5em 1.5em' }}
                                            >
                                                {VALID_STATUSES.map((status) => (
                                                    <option key={status} value={status}>
                                                        {formatStatus(status)}
                                                    </option>
                                                ))}
                                            </select>
                                            {/* Save Button - Enhanced Styling */}
                                            <button
                                                className="mt-6 w-full bg-blue-900 hover:bg-blue-700 text-white rounded-lg py-2.5 transition font-medium shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2" // Added focus styles
                                                onClick={() => { setSelectedTable(null); setOrder(null); setError(null); }} // Close modal after saving (state already updated optimistically)
                                            >
                                                บันทึกและปิด
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </motion.div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>
            {/* Custom Scrollbar Style (Optional) */}
            <style>{`
                .custom-scrollbar::-webkit-scrollbar { width: 6px; }
                .custom-scrollbar::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 3px; }
                .custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }
                .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
            `}</style>
        </div>
    );
}

