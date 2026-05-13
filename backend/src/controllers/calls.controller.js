import CallLog from '../models/CallLog.js';

export const syncCalls = async (req, res) => {
    const { calls } = req.body;
    const employeeId = req.user._id;

    if (!calls || !Array.isArray(calls)) {
        return res.status(400).json({ message: 'Invalid payload' });
    }

    try {
        const formattedCalls = calls.map(call => ({
            employeeId,
            clientPhoneHash: call.clientPhoneHash,
            durationSeconds: call.durationSeconds,
            callType: call.callType,
            timestamp: new Date(call.timestamp),
            syncStatus: 'SYNCED'
        }));

        await CallLog.insertMany(formattedCalls);
        
        res.status(201).json({ message: 'Calls synced successfully', syncedCount: formattedCalls.length });
    } catch (error) {
        console.error('Sync Error:', error);
        res.status(500).json({ message: 'Error syncing calls' });
    }
};
