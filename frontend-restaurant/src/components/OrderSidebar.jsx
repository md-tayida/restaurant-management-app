const OrderSidebar = ({ selectedItems, onSubmit, orderSent }) => {
    const total = selectedItems.reduce((sum, item) => sum + item.price, 0);

    return (
        <div style={{
            flex: 1,
            padding: '30px',
            backgroundColor: '#dff9fb',
            borderLeft: '2px solid #dcdde1',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
        }}>
            <div>
                <h2>🛒 รายการออเดอร์</h2>
                {selectedItems.length === 0 ? (
                    <p>ยังไม่มีรายการที่เลือก</p>
                ) : (
                    <ul style={{ paddingLeft: '20px' }}>
                        {selectedItems.map((item, index) => (
                            <li key={index} style={{ marginBottom: '6px', color: '#2d3436' }}>
                                {item.name} - {item.price} บาท
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            {selectedItems.length > 0 && (
                <div style={{ marginTop: '20px' }}>
                    <div style={{
                        fontSize: '18px',
                        fontWeight: 'bold',
                        marginBottom: '10px',
                        color: '#2ecc71'
                    }}>
                        รวม: {total} บาท
                    </div>

                    <button
                        onClick={onSubmit}
                        style={{
                            width: '100%',
                            padding: '12px',
                            backgroundColor: '#27ae60',
                            color: 'white',
                            fontWeight: 'bold',
                            border: 'none',
                            borderRadius: '8px',
                            fontSize: '16px',
                            cursor: 'pointer'
                        }}
                    >
                        ✅ สั่งอาหาร
                    </button>
                </div>
            )}

            {orderSent && (
                <p style={{
                    marginTop: '20px',
                    color: '#16a085',
                    fontWeight: 'bold',
                    fontSize: '16px'
                }}>
                    ✔️ สั่งอาหารเรียบร้อยแล้ว! กำลังส่งไปยังครัว...
                </p>
            )}
        </div>
    );
};

export default OrderSidebar;
