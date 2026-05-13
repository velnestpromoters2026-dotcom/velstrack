import CallLog from '../models/CallLog.js';
import User from '../models/User.js';
import MetaCampaign from '../models/MetaCampaign.js';

export const getDashboardStats = async (req, res) => {
    try {
        // 1. Aggregate Employee Call Data
        const totalCalls = await CallLog.countDocuments();
        const callDurationAggr = await CallLog.aggregate([
            { $group: { _id: null, totalDuration: { $sum: "$durationSeconds" } } }
        ]);
        const totalDuration = callDurationAggr.length > 0 ? callDurationAggr[0].totalDuration : 0;
        const activeEmployees = await User.countDocuments({ role: 'EMPLOYEE', isActive: true });

        // 2. Fetch the latest Meta Ads Marketing insights
        const metaData = await MetaCampaign.findOne({ campaignId: process.env.META_AD_ACCOUNT_ID });

        res.json({
            calls: {
                totalCount: totalCalls,
                totalDurationSeconds: totalDuration,
                activeEmployees
            },
            marketing: metaData ? {
                accountId: metaData.campaignId,
                name: metaData.name,
                spend: metaData.dailyBudget // Simple mapping representing total spend
            } : null
        });

    } catch (error) {
        console.error('Dashboard Stats Error:', error);
        res.status(500).json({ message: 'Error aggregating dashboard stats' });
    }
};
