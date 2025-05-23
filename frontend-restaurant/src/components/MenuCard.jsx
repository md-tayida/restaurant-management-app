const MenuCard = ({ item, onAdd }) => (
    <div style={{
        border: '1px solid #ddd',
        borderRadius: '12px',
        padding: '15px',
        backgroundColor: '#fff',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        transition: 'transform 0.2s',
        cursor: 'pointer'
    }}>
        {item.imageUrl && (
            <img
                src={item.imageUrl}
                alt={item.name}
                style={{ width: '100%', height: '150px', objectFit: 'cover', borderRadius: '8px' }}
            />
        )}
        <h4 style={{ margin: '10px 0 5px', fontSize: '16px', color: '#34495e' }}>{item.name}</h4>
        <p style={{ fontSize: '14px', color: '#7f8c8d' }}>{item.description}</p>
        <p style={{ fontWeight: 'bold', margin: '5px 0' }}>{item.price} บาท</p>
        <button onClick={() => onAdd(item)} style={{
            padding: '8px 16px',
            backgroundColor: '#2980b9',
            color: '#fff',
            border: 'none',
            borderRadius: '6px',
            fontWeight: 'bold',
            width: '100%',
            marginTop: '8px',
            transition: 'background-color 0.3s',
            cursor: 'pointer'
        }}>➕ เพิ่ม

        </button>
    </div>
);

export default MenuCard;
