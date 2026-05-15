import CallLog from '../models/CallLog.js';
import User from '../models/User.js';
import MetaCampaign from '../models/MetaCampaign.js';
import bcrypt from 'bcryptjs';

export const getDashboardStats = async (req, res) => {
    try {
        const totalEmployees = await User.countDocuments({ role: 'EMPLOYEE' });
        const activeEmployees = await User.countDocuments({ role: 'EMPLOYEE', isActive: true });
        const totalCallsSynced = await CallLog.countDocuments();
        
        const recentLogs = await CallLog.find().sort({ timestamp: -1 }).limit(5).populate('employeeId', 'email');
        const recentActivity = recentLogs.map(log => ({
            _id: log._id.toString(),
            type: 'Call Sync',
            description: `Synced call from ${log.employeeId?.email || 'Unknown'}`,
            timestamp: log.timestamp || new Date()
        }));

        res.json({
            success: true,
            message: "Dashboard stats fetched successfully",
            data: {
                totalEmployees,
                activeEmployees,
                totalCallsSynced,
                recentActivity
            }
        });
    } catch (error) {
        console.error('Dashboard Stats Error:', error);
        res.status(500).json({ success: false, message: 'Error aggregating dashboard stats' });
    }
};

export const getEmployees = async (req, res) => {
    try {
        const employees = await User.find({ role: 'EMPLOYEE' }).select('-passwordHash');
        res.json({
            success: true,
            message: "Employees fetched successfully",
            data: employees.map(emp => ({
                _id: emp._id.toString(),
                name: (emp.profile?.firstName || 'Unknown') + ' ' + (emp.profile?.lastName || ''),
                email: emp.email,
                phone: emp.profile?.phone,
                role: emp.role,
                isOnline: emp.isActive,
                lastActive: emp.lastLogin,
                department: emp.profile?.department
            }))
        });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to fetch employees' });
    }
};

export const addEmployee = async (req, res) => {
    try {
        const { name, email, phone, password, role, department } = req.body;
        
        const exists = await User.findOne({ email });
        if (exists) {
            return res.status(400).json({ success: false, message: 'Email already exists' });
        }

        const salt = await bcrypt.genSalt(10);
        const passwordHash = await bcrypt.hash(password, salt);

        const cleanName = name ? name.trim() : '';
        const nameParts = cleanName.split(' ').filter(part => part.length > 0);
        
        const newEmployee = await User.create({
            email,
            passwordHash,
            role: role || 'EMPLOYEE',
            isActive: true,
            profile: { 
                firstName: nameParts.length > 0 ? nameParts[0] : 'Employee', 
                lastName: nameParts.length > 1 ? nameParts.slice(1).join(' ') : 'Name', 
                phone, 
                department 
            }
        });

        res.status(201).json({
            success: true,
            message: "Employee created successfully",
            data: {
                _id: newEmployee._id.toString(),
                name: (newEmployee.profile?.firstName || '') + ' ' + (newEmployee.profile?.lastName || ''),
                email: newEmployee.email,
                phone: newEmployee.profile?.phone,
                role: newEmployee.role,
                isOnline: newEmployee.isActive,
                lastActive: newEmployee.lastLogin,
                department: newEmployee.profile?.department
            }
        });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to add employee: ' + error.message });
    }
};

export const getMetaCampaigns = async (req, res) => {
    try {
        const campaigns = await MetaCampaign.find().sort({ spend: -1 });
        res.json({
            success: true,
            message: "Meta campaigns fetched successfully",
            data: campaigns.map(c => ({
                _id: c._id.toString(),
                campaignName: c.name,
                status: c.status || 'ACTIVE',
                spend: c.dailyBudget || 0,
                reach: 0,
                clicks: 0,
                ctr: 0.0,
                cpc: 0.0,
                trend: null
            }))
        });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to fetch meta campaigns' });
    }
};
