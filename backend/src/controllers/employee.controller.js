import CallLog from '../models/CallLog.js';
import User from '../models/User.js';

export const getEmployeeDashboardStats = async (req, res) => {
    try {
        const userId = req.user._id;
        
        const startOfDay = new Date();
        startOfDay.setHours(0, 0, 0, 0);

        const callsToday = await CallLog.countDocuments({
            employeeId: userId,
            timestamp: { $gte: startOfDay }
        });

        const recentLogs = await CallLog.find({ employeeId: userId }).sort({ timestamp: -1 }).limit(20);

        const recentCalls = recentLogs.map(log => ({
            _id: log._id.toString(),
            contactName: log.clientPhoneHash,
            duration: `${Math.floor(log.durationSeconds / 60)}m ${log.durationSeconds % 60}s`,
            timestamp: log.timestamp || new Date()
        }));

        res.json({
            success: true,
            message: "Employee dashboard stats fetched successfully",
            data: {
                callsToday,
                target: 50, // Default target
                callsTrend: "On track",
                recentCalls
            }
        });
    } catch (error) {
        console.error('Employee Dashboard Stats Error:', error);
        res.status(500).json({ success: false, message: 'Error fetching employee dashboard stats' });
    }
};
