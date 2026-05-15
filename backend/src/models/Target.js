import mongoose from 'mongoose';

const targetSchema = new mongoose.Schema({
    employeeId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, index: true },
    targetType: { type: String, enum: ['DAILY', 'WEEKLY', 'MONTHLY'], required: true },
    targetValue: { type: Number, required: true }, // E.g., 50 calls
    achievedValue: { type: Number, default: 0 },
    periodStart: { type: Date, required: true },
    periodEnd: { type: Date, required: true },
    status: { type: String, enum: ['ACTIVE', 'COMPLETED', 'DISABLED'], default: 'ACTIVE' }
}, { timestamps: true });

targetSchema.index({ employeeId: 1, targetType: 1, status: 1 });

const Target = mongoose.model('Target', targetSchema);
export default Target;
