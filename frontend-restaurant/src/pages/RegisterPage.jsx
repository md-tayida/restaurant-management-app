import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function RegisterPage() {
    const navigate = useNavigate();
    const [roles, setRoles] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        username: '',
        password: '',
        confirmPassword: '',
        role: '',
    });
    const [error, setError] = useState('');

    useEffect(() => {
        axios.get('http://localhost:8090/api/user/roles')
            .then(res => {
                setRoles(res.data);
                if (res.data.length > 0) {
                    setFormData(prev => ({ ...prev, role: res.data[0].name }));
                }
            })
            .catch(() => setError('ไม่สามารถโหลดข้อมูลสิทธิ์ได้'));
    }, []);

    const handleChange = (e) => {
        setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setError('');

        // เช็คว่ารหัสผ่านตรงกัน
        if (formData.password !== formData.confirmPassword) {
            setError('รหัสผ่านไม่ตรงกัน');
            return;
        }

        axios.post('http://localhost:8090/api/auth/register', formData, {
            headers: { 'Content-Type': 'application/json' }
        })
            .then(res => {
                alert('สมัครสมาชิกสำเร็จ!');
                navigate('/login'); // เด้งไปหน้า Login
            })
            .catch(err => {
                console.error('Register failed:', err.response?.data || err.message);
                setError(err.response?.data?.message || 'สมัครสมาชิกไม่สำเร็จ');
            });
    };

    return (
        <div className="p-8 max-w-md mx-auto">
            <h2 className="text-2xl font-bold mb-6">Register</h2>
            {error && <p className="text-red-500 mb-4">{error}</p>}
            <form onSubmit={handleSubmit}>
                <input
                    name="name"
                    placeholder="ชื่อ"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="border p-2 w-full mb-4"
                />

                <input
                    name="username"
                    placeholder="Username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                    className="border p-2 w-full mb-4"
                />

                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    className="border p-2 w-full mb-4"
                />

                <input
                    type="password"
                    name="confirmPassword"
                    placeholder="Confirm Password"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    required
                    className="border p-2 w-full mb-4"
                />

                <select
                    name="role"
                    value={formData.role}
                    onChange={handleChange}
                    className="border p-2 w-full mb-6"
                    required
                >
                    {roles.map(role => (
                        <option key={role.name} value={role.name}>
                            {role.name}
                        </option>
                    ))}
                </select>

                <button type="submit" className="bg-blue-600 text-white py-2 px-4 rounded w-full">
                    Register
                </button>
            </form>
        </div>
    );
}
