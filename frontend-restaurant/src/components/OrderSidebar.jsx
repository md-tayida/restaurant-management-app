const OrderSidebar = ({ selectedItems, onSubmit, orderSent, onIncrease, onDecrease }) => {
    const total = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

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
                    <ul style={{ paddingLeft: '0', listStyle: 'none' }}>
                        {selectedItems.map((item) => (
                            <li key={item.id} style={{
                                marginBottom: '15px',
                                paddingBottom: '10px',
                                borderBottom: '1px solid #b2bec3',
                            }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <div>
                                        <strong>{item.name}</strong><br />
                                        <span style={{ color: '#636e72' }}>
                                            {item.quantity} x {item.price} บาท = {item.price * item.quantity} บาท
                                        </span>
                                    </div>
                                    <div>
                                        <button onClick={() => onDecrease(item.id)} style={btnStyle}>−</button>
                                        <span style={{ margin: '0 10px' }}>{item.quantity}</span>
                                        <button onClick={() => onIncrease(item.id)} style={btnStyle}>+</button>
                                    </div>
                                </div>
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

const btnStyle = {
    padding: '4px 10px',
    fontSize: '16px',
    fontWeight: 'bold',
    borderRadius: '6px',
    border: '1px solid #ccc',
    backgroundColor: '#fff',
    cursor: 'pointer',
};

export default OrderSidebar;
