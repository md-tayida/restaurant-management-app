// src/components/payment/RefundedDetail.jsx

import React from 'react';
import { Printer } from 'lucide-react';
import {
    PaymentDetailLayout,
    DetailHeader,
    DetailItemList,
    TotalSummary
} from './PaymentDetailShared';

// Helper function to extract data
const getDetailData = (item) => {
    const isOrder = false;
    const orderDetails = item.orderResponse || {};
    const paymentDetails = item;
    const itemsToDisplay = orderDetails.orderItems || [];
    const totalPrice = item.amount;
    return { isOrder, orderDetails, paymentDetails, itemsToDisplay, totalPrice };
};

export default function RefundedDetail({ item, onPrint }) {
    const { isOrder, orderDetails, paymentDetails, itemsToDisplay, totalPrice } = getDetailData(item);

    const actions = (
        <button onClick={onPrint} className="w-full btn-secondary"><Printer size={18} /> พิมพ์</button>
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