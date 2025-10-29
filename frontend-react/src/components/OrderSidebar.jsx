const OrderSidebar = ({ selectedItems, onSubmit, orderSent, onIncrease, onDecrease, onDescriptionChange }) => {
    const subtotal = selectedItems?.reduce((sum, item) => sum + item.price * item.quantity, 0) || 0;

    return (
        <div className="w-full lg:w-1/3 p-4">
            <div className="bg-white rounded-xl shadow-lg sticky top-24">
                <div className="p-4 border-b">
                    <h3 className="text-xl font-bold text-[#212529]">รายการสั่งซื้อของคุณ</h3>
                </div>

                <div className="p-4 space-y-4 max-h-80 overflow-y-auto">
                    {selectedItems.length === 0 ? (
                        <p className="text-gray-500 text-center py-4">ยังไม่มีรายการอาหาร</p>
                    ) : (
                        selectedItems.map(item => (
                            <div key={item.id} className="border-b pb-2">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="font-semibold text-sm text-[#212529]">{item.name}</p>
                                        <p className="text-gray-600 text-xs">{item.price} บาท</p>
                                    </div>
                                    <div className="flex items-center gap-2 border rounded-lg px-2">
                                        <button onClick={() => onDecrease(item.id)} className="text-lg text-red-500 font-bold">-</button>
                                        <span className="font-semibold text-[#212529]">{item.quantity}</span>
                                        <button onClick={() => onIncrease(item.id)} className="text-lg text-green-500 font-bold">+</button>
                                    </div>
                                </div>

                                {/* ช่องใส่รายละเอียดเพิ่มเติม */}
                                <input
                                    type="text"
                                    placeholder="รายละเอียดเพิ่มเติม"
                                    value={item.description || ""}
                                    onChange={(e) => onDescriptionChange(item.id, e.target.value)}
                                    className="w-full mt-2 p-2 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-green-400"
                                />
                            </div>
                        ))
                    )}
                </div>

                {selectedItems.length > 0 && (
                    <div className="p-4 border-t space-y-2">
                        <div className="flex justify-between font-semibold text-[#212529]">
                            <span>ยอดรวม</span>
                            <span>{subtotal.toLocaleString()} บาท</span>
                        </div>
                        <button
                            onClick={onSubmit}
                            className="w-full bg-[#28A745] text-white font-bold py-3 rounded-lg hover:bg-green-600 transition-colors"
                        >
                            ยืนยันออเดอร์
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default OrderSidebar;
