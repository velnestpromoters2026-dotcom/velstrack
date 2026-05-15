import mongoose from 'mongoose';

const callLogSchema = new mongoose.Schema({
    employeeId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, index: true },
    clientPhoneHash: { type: String },
    durationSeconds: { type: Number, required: true },
    callType: { type: String, enum: ['OUTGOING', 'INCOMING', 'MISSED'], required: true },
    timestamp: { type: Date, required: true, index: true },
    callFingerprint: { type: String, unique: true, index: true },
    isVelstrackCall: { type: Boolean, required: true, default: true },
    syncStatus: { type: String, enum: ['PENDING', 'SYNCED'], default: 'SYNCED' }
}, { timestamps: true });

callLogSchema.index({ employeeId: 1, timestamp: -1 }); // Compound index for fast queries

const CallLog = mongoose.model('CallLog', callLogSchema);
export default CallLog;
