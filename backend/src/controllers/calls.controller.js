import CallLog from '../models/CallLog.js';

export const syncCalls = async (req, res) => {
    const { calls } = req.body;
    const employeeId = req.user._id;

    if (!calls || !Array.isArray(calls)) {
        return res.status(400).json({ success: false, message: 'Invalid payload', data: null });
    }

    try {
        const validCalls = calls.filter(call => call.isVelstrackCall === true);

        if (validCalls.length === 0 && calls.length > 0) {
            return res.status(400).json({ success: false, message: 'Only Velstrack initiated calls are allowed', data: null });
        }

        const formattedCalls = validCalls.map(call => ({
            employeeId,
            clientPhoneHash: call.clientPhoneHash,
            durationSeconds: call.durationSeconds,
            callType: call.callType,
            timestamp: new Date(call.timestamp),
            isVelstrackCall: true,
            syncStatus: 'SYNCED'
        }));

        await CallLog.insertMany(formattedCalls);
        
        res.status(201).json({ 
            success: true, 
            message: 'Calls synced successfully', 
            data: { syncedCount: formattedCalls.length } 
        });
    } catch (error) {
        console.error('Sync Error:', error);
        res.status(500).json({ success: false, message: 'Error syncing calls: ' + error.message, data: null });
    }
};
