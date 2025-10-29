// src/components/payment/PendingDetail.jsx (Updated)

import React from 'react';
import { Printer, QrCode, Banknote, CreditCard } from 'lucide-react';
import Swal from 'sweetalert2'; // <-- 1. Import SweetAlert2
// import 'sweetalert2/dist/sweetalert2.min.css'; // <-- 2. (Optional) Import CSS หากยังไม่ได้ทำที่ไฟล์หลัก
import {
    PaymentDetailLayout,
    DetailHeader,
    DetailItemList,
    TotalSummary
} from './PaymentDetailShared';

// Helper function to extract data (คงเดิม)
const getDetailData = (item) => {
    const isOrder = true;
    const orderDetails = item;
    const paymentDetails = null;
    const itemsToDisplay = orderDetails.orderItems || [];
    const totalPrice = item.totalPrice;
    return { isOrder, orderDetails, paymentDetails, itemsToDisplay, totalPrice };
};

export default function PendingDetail({ item, onPrint, onRequestPayment }) {
    const { isOrder, orderDetails, paymentDetails, itemsToDisplay, totalPrice } = getDetailData(item);
    const order = item; // 'item' คือ 'order' ใน context นี้

    // --- 3. สร้างฟังก์ชัน Handler สำหรับการคลิกปุ่มชำระเงิน ---
    const handlePaymentClick = (paymentMethod) => {
        // ตรวจสอบว่า orderItems มีอยู่จริง (ป้องกัน error)
        const orderItems = order.orderItems || [];

        // ลอจิกการตรวจสอบตามที่คุณให้มา
        const hasUnservedItems = orderItems.some(
            (item) => item.status !== "SERVED" && item.status !== "CANCELED"
        );

        if (hasUnservedItems) {
            // ถ้ารายการยังไม่พร้อม ให้แสดง Popup
            Swal.fire({
                icon: "warning",
                title: "ยังไม่สามารถชำระเงินได้",
                text: "บางรายการอาหารของคุณยังไม่ถูกเสิร์ฟ กรุณาเคลียร์รายการอาหารก่อนชำระเงิน",
                confirmButtonColor: "#1e3a8a", // (สี Tailwind blue-900)
                confirmButtonText: "ตกลง",
                background: "#fff",
                color: "#333",
            });
            return; // หยุดการทำงาน
        }

        // ถ้าตรวจสอบผ่าน (ไม่มีรายการที่ยังไม่เสิร์ฟ)
        // ให้เรียกฟังก์ชัน onRequestPayment ที่ส่งมาจาก Page หลัก
        onRequestPayment(paymentMethod);
    };
    // --- สิ้นสุดฟังก์ชัน Handler ---


    const actions = (
        <>
            <div className="flex items-center gap-3 mb-3">
                <button onClick={onPrint} className="flex-1 btn-secondary"><Printer size={18} /> พิมพ์</button>

                {/* 4. อัปเดต onClick ให้เรียกใช้ handlePaymentClick */}
                <button
                    onClick={() => handlePaymentClick('QR_CODE')}
                    className="flex-1 btn-primary bg-blue-600 hover:bg-blue-700"
                >
                    <QrCode size={18} /> สแกน QR
                </button>
            </div>
            <div className="grid grid-cols-2 gap-3">
                {/* 4. อัปเดต onClick ให้เรียกใช้ handlePaymentClick */}
                <button
                    onClick={() => handlePaymentClick('CASH')}
                    className="btn-primary bg-green-500 hover:bg-green-600"
                >
                    <Banknote size={18} /> เงินสด
                </button>
                {/* 4. อัปเดต onClick ให้เรียกใช้ handlePaymentClick */}
                <button
                    onClick={() => handlePaymentClick('CARD')}
                    className="btn-primary bg-indigo-600 hover:bg-indigo-700"
                >
                    <CreditCard size={18} /> บัตร
                </button>
            </div>
        </>
    );

    return (
        <PaymentDetailLayout
            item={item}
            header={<DetailHeader orderDetails={orderDetails} paymentDetails={paymentDetails} isOrder={isOrder} />}
            itemList={<DetailItemList items={itemsToDisplay} />}
            summary={<TotalSummary total={totalPrice} />}
            actions={actions}
        />
    );
}