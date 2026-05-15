import CallLog from '../models/CallLog.js';
import User from '../models/User.js';
import Target from '../models/Target.js';

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

        const recentCalls = recentLogs.map(log => {
            const h = Math.floor(log.durationSeconds / 3600);
            const m = Math.floor((log.durationSeconds % 3600) / 60);
            const s = log.durationSeconds % 60;
            let durationStr = '';
            if (h > 0) durationStr += `${h}h `;
            if (m > 0 || h > 0) durationStr += `${m}m `;
            durationStr += `${s}s`;

            return {
                _id: log._id.toString(),
                contactName: log.clientPhoneHash,
                duration: durationStr.trim(),
                timestamp: log.timestamp || new Date()
            };
        });

        // Fetch active daily target
        const activeTarget = await Target.findOne({
            employeeId: userId,
            status: 'ACTIVE',
            periodEnd: { $gte: new Date() }
        }).sort({ createdAt: -1 });

        const targetValue = activeTarget ? activeTarget.targetValue : 50;
        const callsTrend = activeTarget ? (callsToday >= activeTarget.targetValue ? "Target Achieved" : "On track") : "On track";

        res.json({
            success: true,
            message: "Employee dashboard stats fetched successfully",
            data: {
                callsToday,
                target: targetValue,
                callsTrend: callsTrend,
                recentCalls
            }
        });
    } catch (error) {
        console.error('Employee Dashboard Stats Error:', error);
        res.status(500).json({ success: false, message: 'Error fetching employee dashboard stats' });
    }
};
