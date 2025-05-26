import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import axios from 'axios';

export default function LoginPage() {
    const [form, setForm] = useState({ username: '', password: '' });
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post('http://localhost:8090/api/auth/login', form);
            const token = res.data.token;
            localStorage.setItem('token', token);
            navigate('/manager'); // ✅ เปลี่ยนหน้า
        } catch (err) {
            // แสดงข้อความจากฝั่ง back-end ถ้ามี
            if (err.response && err.response.data) {
                alert(err.response.data.message || err.response.data);
            } else {
                alert("เกิดข้อผิดพลาดบางอย่าง");
            }
        }
    };

    return (
        <form onSubmit={handleSubmit} className="p-8">
            <input
                type="text"
                placeholder="Username"
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                className="block mb-2 p-2 border"
            />
            <input
                type="password"
                placeholder="Password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className="block mb-2 p-2 border"
            />
            <button type="submit" className="bg-blue-500 text-white px-4 py-2">
                Login
            </button>
        </form>
    );
}
