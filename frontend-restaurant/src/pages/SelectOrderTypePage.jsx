// src/pages/OrderTypePage.jsx
import { useNavigate } from 'react-router-dom';

const SelectOrderTypePage = () => {
    const navigate = useNavigate();

    const handleSelect = (type) => {
        if (type === 'DINE_IN') {
            navigate('/menu', { state: { orderType: type } });
        } else {
            navigate('/menu', { state: { orderType: type } });
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h2 className="text-2xl font-bold mb-6">เลือกรูปแบบการสั่ง</h2>
            <div className="space-x-4">
                <button
                    onClick={() => handleSelect('DINE_IN')}
                    className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded"
                >
                    Dine-in
                </button>
                <button
                    onClick={() => handleSelect('TAKEAWAY')}
                    className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded"
                >
                    Takeaway
                </button>
            </div>
        </div>
    );
};

export default SelectOrderTypePage;