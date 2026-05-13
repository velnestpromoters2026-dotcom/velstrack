import mongoose from 'mongoose';

const metaCampaignSchema = new mongoose.Schema({
    campaignId: { type: String, required: true, unique: true, index: true },
    name: { type: String, required: true },
    status: { type: String },
    dailyBudget: { type: Number }
}, { timestamps: true });

const MetaCampaign = mongoose.model('MetaCampaign', metaCampaignSchema);
export default MetaCampaign;
