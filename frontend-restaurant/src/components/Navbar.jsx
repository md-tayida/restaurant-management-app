import React, { useEffect, useState } from 'react';
import { getAllTables } from '../api/tableApi';

const Navbar = ({ orderType, tableId, setTableId }) => {
    const [tables, setTables] = useState([]);

    useEffect(() => {
        if (orderType === 'DINE_IN') {
            getAllTables().then(setTables).catch(console.error);
        }
    }, [orderType]);

    return (
        <nav className="flex justify-between items-center px-4 py-3 bg-blue-600 text-white">
            <h1 className="text-lg font-bold">Order Menu</h1>
            {orderType === 'DINE_IN' && (
                <select
                    value={tableId}
                    onChange={(e) => setTableId(e.target.value)}
                    className="text-black px-2 py-1 rounded"
                >
                    <option value="">-- เลือกโต๊ะ --</option>
                    {tables.map((table) => (
                        <option key={table.id} value={table.id}>
                            โต๊ะ {table.tableNumber}
                        </option>
                    ))}
                </select>
            )}
        </nav>
    );
};

export default Navbar;