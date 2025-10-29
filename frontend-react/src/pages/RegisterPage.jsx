import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
import { FaUserPlus } from 'react-icons/fa'

export default function RegisterPage() {
    const navigate = useNavigate()
    const [roles, setRoles] = useState([])
    const [formData, setFormData] = useState({
        name: '',
        username: '',
        password: '',
        confirmPassword: '',
        role: '',
    })
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        axios.get('http://localhost:8090/api/user/roles')
            .then(res => {
                console.log('✅ API response (roles):', res.data)
                setRoles(res.data)
                if (res.data.length > 0) {
                    setFormData(prev => ({ ...prev, role: res.data[0].role }))
                    console.log('✅ ตั้งค่า role เริ่มต้นเป็น:', res.data[0].role)

                }
            })
            .catch(() => setError('ไม่สามารถโหลดข้อมูลสิทธิ์ได้'))
    }, [])

    const handleChange = (e) => {
        setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }))
    }


    const handleSubmit = (e) => {
        e.preventDefault()
        setError('')

        if (formData.password !== formData.confirmPassword) {
            setError('รหัสผ่านไม่ตรงกัน')
            return
        }

        setLoading(true)
        axios.post('http://localhost:8090/api/auth/register', formData, {
            headers: { 'Content-Type': 'application/json' }
        })
            .then(() => {
                alert('สมัครสมาชิกสำเร็จ!')
                navigate('/login')
            })
            .catch(err => {
                console.error('Register failed:', err.response?.data || err.message)
                setError(err.response?.data?.message || 'สมัครสมาชิกไม่สำเร็จ')
            })
            .finally(() => setLoading(false))
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-900 via-blue-800 to-gray-800">
            <div className="bg-white/95 backdrop-blur-md shadow-2xl rounded-2xl w-full max-w-md p-10">
                <div className="flex flex-col items-center mb-8">
                    <FaUserPlus className="text-blue-700 text-4xl mb-3" />
                    <h1 className="text-3xl font-bold text-blue-900">Create Staff Account</h1>
                    <p className="text-gray-500 text-sm mt-1">สมัครบัญชีพนักงานร้านอาหาร</p>
                </div>

                {error && (
                    <div className="mb-4 p-3 text-sm text-red-700 bg-red-100 border border-red-300 rounded-lg">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-gray-700 font-medium mb-2">ชื่อพนักงาน</label>
                        <input
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            placeholder="ชื่อ-นามสกุล"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none"
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Username</label>
                        <input
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            placeholder="ชื่อผู้ใช้"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none"
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Password</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            placeholder="รหัสผ่าน"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none"
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 font-medium mb-2">Confirm Password</label>
                        <input
                            type="password"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                            placeholder="ยืนยันรหัสผ่าน"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none"
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 font-medium mb-2">
                            สิทธิ์การใช้งาน (Role)
                        </label>
                        <select
                            name="role"
                            value={formData.role}
                            onChange={handleChange}
                            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none text-gray-800 bg-white"
                            required
                        >

                            {roles.length > 0 ? (
                                roles.map((r, index) => (
                                    <option key={index} value={r.role}>
                                        {r.role}
                                    </option>
                                ))
                            ) : (
                                <option disabled>กำลังโหลดสิทธิ์...</option>
                            )}
                        </select>
                    </div>


                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full py-3 rounded-lg text-white font-semibold shadow-md transition-all duration-200 ${loading
                            ? 'bg-blue-300 cursor-not-allowed'
                            : 'bg-gradient-to-r from-blue-700 to-blue-500 hover:scale-[1.02]'
                            }`}
                    >
                        {loading ? 'กำลังสมัครสมาชิก...' : 'สมัครสมาชิก'}
                    </button>

                    <p className="text-center text-gray-600 text-sm mt-4">
                        มีบัญชีอยู่แล้ว?{' '}
                        <span
                            className="text-blue-600 hover:underline cursor-pointer"
                            onClick={() => navigate('/login')}
                        >
                            เข้าสู่ระบบ
                        </span>
                    </p>
                </form>
            </div>
        </div>
    )
}
