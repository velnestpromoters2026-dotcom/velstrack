import CallLog from '../models/CallLog.js';
import User from '../models/User.js';
import MetaCampaign from '../models/MetaCampaign.js';
import Target from '../models/Target.js';
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

export const getMetaStatus = async (req, res) => {
    try {
        // Mock state for connection verification system
        // STATE 1: Not connected
        // STATE 2: Connected but no campaigns
        // STATE 3: Campaigns exist but inactive
        // STATE 4: Campaigns actively delivering
        
        const campaignsCount = await MetaCampaign.countDocuments();
        let state = "NOT_CONNECTED";
        let message = "Meta account not connected";
        
        // Simulating the logic: assuming it's connected if we have a token (mocked as true for now)
        const isConnected = true; 
        
        if (isConnected) {
            if (campaignsCount === 0) {
                state = "CONNECTED_NO_CAMPAIGNS";
                message = "Meta account connected successfully. Create your first campaign in Meta Ads Manager to begin receiving analytics.";
            } else {
                const activeCount = await MetaCampaign.countDocuments({ status: 'ACTIVE' });
                if (activeCount > 0) {
                    state = "ACTIVE_DELIVERY";
                    message = "Campaigns are actively delivering";
                } else {
                    state = "CAMPAIGNS_INACTIVE";
                    message = "Campaigns exist but are inactive";
                }
            }
        }

        res.json({ success: true, data: { state, message } });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to fetch meta status' });
    }
};

export const getTargets = async (req, res) => {
    try {
        const targets = await Target.find().populate('employeeId', 'profile.firstName profile.lastName email');
        res.json({ success: true, data: targets });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to fetch targets' });
    }
};

export const createTarget = async (req, res) => {
    try {
        const { employeeId, targetType, targetValue, periodStart, periodEnd } = req.body;
        
        if (employeeId === 'TEAM') {
            const employees = await User.find({ role: 'EMPLOYEE' });
            if (employees.length === 0) {
                return res.status(400).json({ success: false, message: 'No employees found to assign targets.' });
            }
            
            const dividedTarget = Math.floor(targetValue / employees.length);
            
            const targetsToCreate = employees.map(emp => ({
                employeeId: emp._id,
                targetType,
                targetValue: dividedTarget,
                achievedValue: 0,
                periodStart: new Date(periodStart),
                periodEnd: new Date(periodEnd),
                status: 'ACTIVE'
            }));
            
            await Target.insertMany(targetsToCreate);
            return res.status(201).json({ success: true, message: `Targets created and divided seamlessly among ${employees.length} employees.` });
        } else {
            const target = await Target.create({
                employeeId,
                targetType,
                targetValue,
                achievedValue: 0,
                periodStart: new Date(periodStart),
                periodEnd: new Date(periodEnd),
                status: 'ACTIVE'
            });

            return res.status(201).json({ success: true, message: "Target created", data: target });
        }
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to create target: ' + error.message });
    }
};

export const updateTarget = async (req, res) => {
    try {
        const { id } = req.params;
        const updates = req.body;
        const target = await Target.findByIdAndUpdate(id, updates, { new: true });
        res.json({ success: true, message: "Target updated", data: target });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Failed to update target' });
    }
};

export const getAnalytics = async (req, res) => {
    try {
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        // Aggregate daily calls for the last 7 days
        const sevenDaysAgo = new Date();
        sevenDaysAgo.setDate(today.getDate() - 6);
        sevenDaysAgo.setHours(0, 0, 0, 0);

        const callLogs = await CallLog.find({ timestamp: { $gte: sevenDaysAgo } });

        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const dailyCallsMap = {};
        for (let i = 0; i < 7; i++) {
            const d = new Date(sevenDaysAgo);
            d.setDate(d.getDate() + i);
            dailyCallsMap[days[d.getDay()]] = 0;
        }

        callLogs.forEach(log => {
            if (log.timestamp) {
                const dayName = days[log.timestamp.getDay()];
                if (dailyCallsMap[dayName] !== undefined) {
                    dailyCallsMap[dayName]++;
                }
            }
        });

        const dailyCalls = Object.keys(dailyCallsMap).map(key => ({
            date: key,
            calls: dailyCallsMap[key]
        }));

        // Employee Rankings
        const employees = await User.find({ role: 'EMPLOYEE' }).select('profile email');
        
        const activeTargets = await Target.find({
            status: 'ACTIVE',
            periodStart: { $lte: new Date() },
            periodEnd: { $gte: new Date() }
        });

        const callsTodayLogs = await CallLog.find({ timestamp: { $gte: today } });

        const rankings = employees.map(emp => {
            const empCallsToday = callsTodayLogs.filter(log => log.employeeId && log.employeeId.toString() === emp._id.toString()).length;
            const target = activeTargets.find(t => t.employeeId.toString() === emp._id.toString());
            
            let targetProgress = 0;
            if (target && target.targetValue > 0) {
                targetProgress = Math.min(target.achievedValue / target.targetValue, 1);
            }

            return {
                _id: emp._id.toString(),
                name: (emp.profile?.firstName || 'Unknown') + ' ' + (emp.profile?.lastName || ''),
                callsToday: empCallsToday,
                targetProgress: targetProgress,
                trend: empCallsToday > 0 ? 'UP' : 'DOWN'
            };
        }).sort((a, b) => b.callsToday - a.callsToday);

        res.json({
            success: true,
            data: {
                dailyCalls,
                rankings
            }
        });
    } catch (error) {
        console.error('Analytics Error:', error);
        res.status(500).json({ success: false, message: 'Failed to fetch analytics' });
    }
};

export const getCalls = async (req, res) => {
    try {
        const calls = await CallLog.find().sort({ timestamp: -1 }).populate('employeeId', 'profile.firstName profile.lastName email profile.phone');
        
        const formattedCalls = calls.map(c => ({
            _id: c._id.toString(),
            employeeName: c.employeeId ? `${c.employeeId.profile?.firstName || ''} ${c.employeeId.profile?.lastName || ''}`.trim() : 'Unknown',
            clientPhone: c.clientPhoneHash || 'Unknown',
            durationSeconds: c.durationSeconds || 0,
            timestamp: c.timestamp,
            callType: c.callType || 'UNKNOWN'
        }));
        
        res.json({ success: true, data: formattedCalls });
    } catch (error) {
        console.error('Get Calls Error:', error);
        res.status(500).json({ success: false, message: 'Failed to fetch calls' });
    }
};
