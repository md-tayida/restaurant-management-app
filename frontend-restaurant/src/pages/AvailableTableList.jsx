import { useEffect, useState } from "react";
import { getAvailableTables } from "../api/tableApi";

const AvailableTableList = () => {
    const [tables, setTables] = useState([]);

    useEffect(() => {
        const fetchTables = async () => {
            try {
                const data = await getAvailableTables();
                setTables(data);
            } catch (err) {
                console.error("Failed to fetch tables:", err);
            }
        };

        fetchTables();
    }, []);

    return (
        <div>
            <h2>Available Tables</h2>
            {tables.length === 0 ? (
                <p>No available tables.</p>
            ) : (
                <ul>
                    {tables.map((table) => (
                        <li key={table.id}>
                            Table ####{table.tableNumber}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default AvailableTableList;
