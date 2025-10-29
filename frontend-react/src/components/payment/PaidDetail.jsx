// src/components/payment/PaidDetail.jsx

import React from 'react';
import { Printer, Undo2 } from 'lucide-react';
import {
    PaymentDetailLayout,
    DetailHeader,
    DetailItemList,
    TotalSummary
} from './PaymentDetailShared';

const getDetailData = (item) => {
    const isOrder = false;
    const orderDetails = item.orderResponse || {};
    const paymentDetails = item;
    const itemsToDisplay = orderDetails.orderItems || [];
    const totalPrice = item.amount;
    return { isOrder, orderDetails, paymentDetails, itemsToDisplay, totalPrice };
};

export default function PaidDetail({ item, onPrint, onRequestRefund }) {
    const { isOrder, orderDetails, paymentDetails, itemsToDisplay, totalPrice } = getDetailData(item);

    const actions = (
        <div className="grid grid-cols-2 gap-3">
            <button onClick={onPrint} className="btn-secondary"><Printer size={18} /> พิมพ์</button>
            <button onClick={onRequestRefund} className="btn-primary bg-red-500 hover:bg-red-600"><Undo2 size={18} /> คืนเงิน</button>
        </div>
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