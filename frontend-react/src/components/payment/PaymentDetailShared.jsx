// src/components/payment/PaymentDetailShared.jsx

import React from 'react';
import { motion } from 'framer-motion';
import { Loader2 } from 'lucide-react';


export const PaymentDetailLayout = ({ item, header, itemList, summary, actions }) => {
    return (
        <motion.div
            key={item.id}
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className="bg-white rounded-2xl shadow-xl h-full flex flex-col"
        >
            {/* 1. Header */}
            <div className="p-6 border-b border-gray-200">{header}</div>

            {/* 2. Item List */}
            <div className="flex-1 p-6 overflow-y-auto space-y-3 max-h-[50vh] md:max-h-none">
                {itemList}
            </div>

            {/* 3. Summary & Actions */}
            <div className="p-6 bg-gray-50 rounded-b-2xl border-t border-gray-200 mt-auto">

                {summary}
                {actions && <div className="mt-6">{actions}</div>}
            </div>
        </motion.div>
    );
};

// --- Header Component ---
export const DetailHeader = ({ orderDetails, paymentDetails, isOrder }) => {
    const orderId = orderDetails.id;
    const tableInfo = orderDetails.tableId;
    const createdAt = isOrder ? orderDetails.createdAt : (paymentDetails?.createdAt || orderDetails.createdAt);

    return (
        <>
            <h2 className="text-2xl font-bold text-blue-900">
                {orderDetails.orderType === 'DINE_IN' && tableInfo ? `โต๊ะ ${tableInfo}` : `Order #${orderId}`}
            </h2>
            <p className="text-sm text-gray-500">
                สร้างเมื่อ: {new Date(createdAt).toLocaleString('th-TH')}
            </p>
            {!isOrder && paymentDetails?.method && (
                <p className="text-sm text-gray-500">วิธีชำระ: {paymentDetails.method}</p>
            )}
        </>
    );
};

// --- Item List Component ---
export const DetailItemList = ({ items }) => {
    if (items.length === 0) {
        return <p className="text-gray-500 text-sm">ไม่พบรายการอาหาร</p>;
    }
    return items.map((orderItem, index) => (
        <div key={orderItem.id || index} className="flex justify-between items-center text-gray-800">
            <div>
                <p className="font-medium">{orderItem.menuName}</p>
                <p className="text-sm text-gray-500">{orderItem.quantity} × {orderItem.price} บาท</p>
            </div>
            <p className="font-semibold text-lg">{(orderItem.quantity * orderItem.price)} บาท</p>
        </div>
    ));
};


// --- Total Summary Component ---
export const TotalSummary = ({ total }) => (
    <div className="space-y-2 text-gray-900">
        <div className="flex justify-between text-2xl font-bold text-blue-900 pt-4 border-t border-dashed">
            <span>ยอดรวมสุทธิ</span>
            <span>{total} บาท</span>
        </div>
    </div>
);

// --- Placeholder Components ---
export const DetailPlaceholder = ({ message }) => (
    <motion.div
        key="placeholder"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="flex items-center justify-center h-full"
    >
        <p className="text-2xl text-gray-500 px-6 text-center">{message}</p>
    </motion.div>
);

export const DetailLoading = () => (
    <div className="flex items-center justify-center h-full">
        <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
    </div>
);

export const DetailProcessing = ({ item }) => (
    <motion.div
        key={`processing-${item.id}`}
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="bg-white rounded-2xl shadow-xl h-full flex flex-col items-center justify-center p-6 text-center"
    >
        <Loader2 className="w-16 h-16 text-blue-600 animate-spin mb-4" />
        <h3 className="text-xl font-bold text-gray-800">กำลังดำเนินการ...</h3>
        <p className="text-gray-500 mt-2">กรุณารอสักครู่</p>
    </motion.div>
);